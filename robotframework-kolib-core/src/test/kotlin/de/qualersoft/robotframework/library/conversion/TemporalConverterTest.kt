package de.qualersoft.robotframework.library.conversion

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.stream.Stream
import kotlin.reflect.KClass

class TemporalConverterTest {

  @MethodSource("dateProvider")
  @ParameterizedTest
  fun testDateConversion(value: Any, targetType: KClass<*>, expected: Any) {
    val res = TemporalConverter.convertToTemporal(targetType, value)
    assertAll(
      { res should beInstanceOf(targetType) },
      { res shouldBe expected }
    )
  }

  @Test
  fun testInvalidTargetType() {
    val ex = assertThrows<IllegalArgumentException> {
      TemporalConverter.convertToTemporal(Int::class, buildUtcDate(2021, 8, 15))
    }

    assertAll(
      { ex.message shouldContain "No temporal converter defined" },
      { ex.message shouldContain "2021-08-15" },
      { ex.message shouldContain "kotlin.Int" }
    )
  }
  
  @Test
  fun testInvalidValueType() {
    val value = Instant.now().epochSecond
    val ex = assertThrows<IllegalArgumentException> {
      TemporalConverter.convertToTemporal(Timestamp::class, value)
    }

    assertAll(
      { ex.message shouldContain "Could not find a matching temporal converter" },
      { ex.message shouldContain "$value" },
      { ex.message shouldContain "java.sql.Timestamp" }
    )
  }

  companion object {
    @Suppress("unused")
    @JvmStatic
    fun dateProvider(): Stream<Arguments> = Stream.of(
      args(
        Timestamp(buildUtcDate(2021, 1, 1, 13, 5, 28).toEpochMilli()),
        Timestamp::class,
        Timestamp.from(buildUtcDate(2021, 1, 1, 13, 5, 28))
      ),
      args(
        buildUtcDate(2021, 1, 1, 0, 8, 15),
        Timestamp::class,
        Timestamp.from(buildUtcDate(2021, 1, 1, 0, 8, 15))
      ),

      args(
        Timestamp(buildUtcDate(2021, 2, 1, 0, 0, 0).toEpochMilli()),
        Date::class,
        Date.from(buildUtcDate(2021, 2, 1))
      ),
      args(
        "2021-01-01",
        Date::class,
        Date.from(buildZonedDate(2021, 1, 1, zone = ZoneId.systemDefault()))
      ),

      args(
        Timestamp(buildUtcDate(2021, 2, 2, 2, 0, 0).toEpochMilli()),
        LocalDate::class,
        LocalDate.ofInstant(buildUtcDate(2021, 2, 2, 2), ZoneOffset.UTC)
      ),
      args(
        "2021-01-02 02:00:00",
        LocalDate::class,
        LocalDate.ofInstant(buildUtcDate(2021, 1, 2, 2), ZoneOffset.UTC)
      ),

      args(
        Timestamp(buildUtcDate(2021, 2, 3, 14, 3, 42).toEpochMilli()),
        LocalTime::class,
        LocalTime.ofInstant(buildUtcDate(2021, 2, 3, 14, 3, 42), ZoneOffset.UTC)
      ),
      args(
        "14:03:42",
        LocalTime::class,
        LocalTime.ofInstant(buildUtcDate(2021, 1, 3, 14, 3, 42), ZoneOffset.UTC)
      ),

      args(
        Timestamp(buildUtcDate(2021, 2, 4, 8, 12, 3).toEpochMilli()),
        LocalDateTime::class,
        LocalDateTime.ofInstant(buildUtcDate(2021, 2, 4, 8, 12, 3), ZoneOffset.UTC)
      ),
      args(
        "2021-01-04 09:15:01",
        LocalDateTime::class,
        LocalDateTime.ofInstant(buildUtcDate(2021, 1, 4, 9, 15, 1), ZoneOffset.UTC)
      ),

      args(
        Timestamp(buildUtcDate(2021, 2, 5, 21, 42, 8).toEpochMilli()),
        ZonedDateTime::class,
        ZonedDateTime.ofInstant(buildUtcDate(2021, 2, 5, 21, 42, 8), ZoneOffset.UTC)
      ),
      args(
        "2021-01-05 22:41:24",
        ZonedDateTime::class,
        // Workaround to get rid of zone display name
        ZonedDateTime.from(
          OffsetDateTime.ofInstant(
            buildZonedDate(2021, 1, 5, 22, 41, 24),
            ZoneOffset.systemDefault()
          )
        )
      ),

      args(
        Timestamp(buildUtcDate(2021, 2, 6, 18, 7, 3).toEpochMilli()),
        OffsetTime::class,
        OffsetTime.ofInstant(buildUtcDate(2021, 2, 6, 18, 7, 3), ZoneOffset.UTC)
      ),
      args(
        "19:08:03",
        OffsetTime::class,
        OffsetTime.ofInstant(buildZonedDate(2021, 1, 6, 19, 8, 3), ZoneOffset.systemDefault())
      ),

      args(
        Timestamp(buildUtcDate(2021, 2, 7, 9, 53, 36).toEpochMilli()),
        OffsetDateTime::class,
        OffsetDateTime.ofInstant(buildUtcDate(2021, 2, 7, 9, 53, 36), ZoneOffset.UTC)
      ),
      args(
        "2021-01-07 09:53:36",
        OffsetDateTime::class,
        OffsetDateTime.ofInstant(buildZonedDate(2021, 1, 7, 9, 53, 36), ZoneOffset.systemDefault())
      ),
      args(
        "2021-01-08 09:53:36+02:00",
        ZonedDateTime::class,
        ZonedDateTime.ofInstant(buildUtcDate(2021, 1, 8, 7, 53, 36), ZoneOffset.ofHours(2))
      ),
      args(
        "2021-09-12T15:47:05.123",
        LocalDateTime::class,
        LocalDateTime.ofInstant(buildUtcDate(2021, 9, 12, 15, 47, 5, 123), ZoneOffset.UTC)
      ),
      args(
        "2021-09-15T15:47:05",
        LocalDateTime::class,
        LocalDateTime.ofInstant(buildUtcDate(2021, 9, 15, 15, 47, 5), ZoneOffset.UTC)
      ),
      // should give zero o'clock no matter of zone because it is local
      args(
        "2021-09-17+03:00",
        LocalDateTime::class,
        LocalDateTime.ofInstant(buildUtcDate(2021, 9, 17), ZoneOffset.UTC)
      ),
      args(
        "2021-09-18+03:00", Date::class,
        Date.from(buildZonedDate(2021, 9, 18, zone = ZoneId.of("+3")))
      )
    )

    private fun args(vararg args: Any): Arguments = Arguments.of(*args)

    private fun buildZonedDate(
      year: Int,
      month: Int,
      day: Int,
      hour: Int = 0,
      minute: Int = 0,
      sec: Int = 0,
      millis: Int = 0,
      zone: ZoneId = ZoneId.systemDefault()
    ): Instant = Calendar.Builder()
      .setTimeZone(TimeZone.getTimeZone(zone))
      .setDate(year, month - 1, day)
      .setTimeOfDay(hour, minute, sec, millis)
      .build().toInstant()

    private fun buildUtcDate(
      year: Int,
      month: Int,
      day: Int,
      hour: Int = 0,
      minute: Int = 0,
      sec: Int = 0,
      millis: Int = 0
    ): Instant = buildZonedDate(year, month, day, hour, minute, sec, millis, ZoneOffset.UTC)
  }
}
