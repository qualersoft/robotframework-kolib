package de.qualersoft.robotframework.library.conversion

import java.sql.Timestamp
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

object TemporalConverter {

  private const val MIN_NANO_WIDTH = 1
  private const val MAX_NANO_WIDTH = 6
  private const val DEFAULT_YEAR = 1900L
  private const val DEFAULT_MONTH = 1L
  private const val DEFAULT_DAY = 1L
  private const val DEFAULT_TIME = 0L

  // robots default to UTC -> meaning a value of 2021-05-13 15:00:00 will be interpreted as UTC
  // lazy companion so we have to create formatter only once and only if required
  private val temporalFormatter by lazy {
    DateTimeFormatterBuilder()
      // Date stuff
      .optionalStart()
        .appendPattern("yyyy-MM-dd")
      .optionalEnd()
      .optionalStart()
        .appendPattern("'T'")
      .optionalEnd()
      .optionalStart()
        .appendPattern(" ")
      .optionalEnd()
      // Time stuff
      .optionalStart()
        .appendPattern("HH:mm")
        .optionalStart()
          .appendPattern(":ss")
          .optionalStart()
            .appendPattern(".")
            .appendFraction(ChronoField.NANO_OF_SECOND, MIN_NANO_WIDTH, MAX_NANO_WIDTH, false)
          .optionalEnd()
        .optionalEnd()
      .optionalEnd()
      // Zone stuff
      .optionalStart()
      .appendZoneOrOffsetId()
      .optionalEnd()
      // Defaults
      .parseDefaulting(ChronoField.YEAR_OF_ERA, DEFAULT_YEAR)
      .parseDefaulting(ChronoField.MONTH_OF_YEAR, DEFAULT_MONTH)
      .parseDefaulting(ChronoField.DAY_OF_MONTH, DEFAULT_DAY)
      .parseDefaulting(ChronoField.HOUR_OF_DAY, DEFAULT_TIME)
      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, DEFAULT_TIME)
      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, DEFAULT_TIME)
      .parseDefaulting(ChronoField.NANO_OF_SECOND, DEFAULT_TIME)
      .toFormatter()
  }

  fun convertToTemporal(targetType: KClass<*>, value: Any): Any = when {
    value is String -> {
      temporalOfString(targetType, value)
    }
    Instant::class.isSuperclassOf(value::class) -> {
      temporalOfInstant(targetType, value as Instant)
    }
    Date::class.isSuperclassOf(value::class) -> {
      temporalOfInstant(targetType, (value as Date).toInstant())
    }
    else -> throw IllegalArgumentException(
      "Could not find a matching temporal converter for `$value` to `$targetType`!"
    )
  }

  private fun temporalOfString(targetType: KClass<*>, value: String): Any {
    var tmp = temporalFormatter.parseBest(value, ZonedDateTime::from, LocalDateTime::from)
    if (tmp is LocalDateTime) {
      // we didn't found a zone info -> as of ISO 8601 systems 'local' zone is assumed
      tmp = tmp.atZone(ZoneId.systemDefault())
    }
    // here we definitely have a zone info
    val zone = ZoneOffset.ofTotalSeconds(tmp.get(ChronoField.OFFSET_SECONDS)).normalized()
    // Instant.from gives us an instance in UTC
    return temporalOfInstant(targetType, Instant.from(tmp), zone)
  }

  private fun temporalOfInstant(targetType: KClass<*>, value: Instant, zoneId: ZoneId? = null): Any {
    val zone = zoneId ?: retrieveZoneId(value)
    return when (targetType) {
      Timestamp::class -> Timestamp.from(Timestamp(value.toEpochMilli()).toInstant().atZone(zone).toInstant())
      Date::class -> Date.from(Date(value.toEpochMilli()).toInstant().atZone(zone).toInstant())
      LocalDate::class -> LocalDate.ofInstant(value, zone)
      LocalTime::class -> LocalTime.ofInstant(value, zone)
      LocalDateTime::class -> LocalDateTime.ofInstant(value, zone)
      ZonedDateTime::class -> ZonedDateTime.ofInstant(value, zone)
      OffsetTime::class -> OffsetTime.ofInstant(value, zone)
      OffsetDateTime::class -> OffsetDateTime.ofInstant(value, zone)
      else -> throw IllegalArgumentException(
        "No temporal converter defined to convert instant '$value' to type '$targetType'"
      )
    }
  }

  private fun retrieveZoneId(instant: Instant): ZoneId {
    return try {
      ZoneId.from(instant)
    } catch (ignored: DateTimeException) {
      ZoneId.of(ZoneOffset.UTC.id)
    }
  }
}
