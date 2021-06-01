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
    else -> throw UnsupportedOperationException("Could not find a matching temporal converter for `$value` to `$targetType`!")
  }

  private fun temporalOfString(targetType: KClass<*>, value: String): Any {
    var tmp = temporalFormatter.parseBest(value, ZonedDateTime::from, LocalDateTime::from)
    if (tmp is LocalDateTime) {
      tmp = tmp.atOffset(ZoneOffset.UTC)
    }
    return temporalOfInstant(targetType, Instant.from(tmp))
  }

  private fun temporalOfInstant(targetType: KClass<*>, value: Instant): Any {
    val zone = retrieveZoneId(value)
    return when (targetType) {
      Timestamp::class -> Timestamp(value.toEpochMilli())
      Date::class -> Date(value.toEpochMilli())
      LocalDate::class -> LocalDate.ofInstant(value, zone)
      LocalTime::class -> LocalTime.ofInstant(value, zone)
      LocalDateTime::class -> LocalDateTime.ofInstant(value, zone)
      ZonedDateTime::class -> ZonedDateTime.ofInstant(value, zone)
      OffsetTime::class -> OffsetTime.ofInstant(value, zone)
      OffsetDateTime::class -> OffsetDateTime.ofInstant(value, zone)

      else -> throw UnsupportedOperationException("No temporal converter defined to convert instant '$value' to type '$targetType'")
    }
  }

  private fun retrieveZoneId(instant: Instant): ZoneId {
    return try {
      ZoneId.from(instant)
    } catch (ignored: DateTimeException) {
      ZoneId.of(ZoneOffset.UTC.id)
    }
  }

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
      .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false)
      .optionalEnd()
      .optionalEnd()
      .optionalEnd()
      // Zone stuff
      .optionalStart()
      .appendZoneOrOffsetId()
      .optionalEnd()
      // Defaults
      .parseDefaulting(ChronoField.YEAR_OF_ERA, 1900)
      .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
      .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
      .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
      .toFormatter()
  }
}