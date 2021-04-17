package de.qualersoft.robotframework.library.conversion

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.assertAll
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
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.KClass

class TemporalConverterTest {

  companion object {

    @JvmStatic
    fun dateProvider() = Stream.of(
      Arguments.of(
        Timestamp(buildUtcDate(2021, 2, 1, 0, 0, 0).toEpochMilli()),
        Date::class,
        Date.from(buildUtcDate(2021, 2, 1))
      ),
      Arguments.of("2021-01-01", Date::class, Date.from(buildUtcDate(2021, 1, 1))),

      Arguments.of(
        Timestamp(buildUtcDate(2021, 2, 2, 2, 0, 0).toEpochMilli()),
        LocalDate::class,
        LocalDate.ofInstant(buildUtcDate(2021, 2, 2, 2), ZoneOffset.UTC)
      ),
      Arguments.of(
        "2021-01-02 02:00:00",
        LocalDate::class,
        LocalDate.ofInstant(buildUtcDate(2021, 1, 2, 2), ZoneOffset.UTC)
      ),

      Arguments.of(
        Timestamp(buildUtcDate(2021, 2, 3, 14, 3, 42).toEpochMilli()),
        LocalTime::class,
        LocalTime.ofInstant(buildUtcDate(2021, 2, 3, 14, 3, 42), ZoneOffset.UTC)
      ),
      Arguments.of(
        "14:03:42",
        LocalTime::class,
        LocalTime.ofInstant(buildUtcDate(2021, 1, 3, 14, 3, 42), ZoneOffset.UTC)
      ),

      Arguments.of(
        Timestamp(buildUtcDate(2021, 2, 4, 8, 12, 3).toEpochMilli()),
        LocalDateTime::class,
        LocalDateTime.ofInstant(buildUtcDate(2021, 2, 4, 8, 12, 3), ZoneOffset.UTC)
      ),
      Arguments.of(
        "2021-01-04 09:15:01",
        LocalDateTime::class,
        LocalDateTime.ofInstant(buildUtcDate(2021, 1, 4, 9, 15, 1), ZoneOffset.UTC)
      ),

      Arguments.of(
        Timestamp(buildUtcDate(2021, 2, 5, 21, 42, 8).toEpochMilli()),
        ZonedDateTime::class,
        ZonedDateTime.ofInstant(buildUtcDate(2021, 2, 5, 21, 42, 8), ZoneOffset.UTC)
      ),
      Arguments.of(
        "2021-01-05 22:41:24",
        ZonedDateTime::class,
        ZonedDateTime.ofInstant(buildUtcDate(2021, 1, 5, 22, 41, 24), ZoneOffset.UTC)
      ),

      Arguments.of(
        Timestamp(buildUtcDate(2021, 2, 6, 18, 7, 3).toEpochMilli()),
        OffsetTime::class,
        OffsetTime.ofInstant(buildUtcDate(2021, 2, 6, 18, 7, 3), ZoneOffset.UTC)
      ),
      Arguments.of(
        "19:08:03",
        OffsetTime::class,
        OffsetTime.ofInstant(buildUtcDate(2021, 1, 6, 19, 8, 3), ZoneOffset.UTC)
      ),

      Arguments.of(
        Timestamp(buildUtcDate(2021, 2, 7, 9, 53, 36).toEpochMilli()),
        OffsetDateTime::class,
        OffsetDateTime.ofInstant(buildUtcDate(2021, 2, 7, 9, 53, 36), ZoneOffset.UTC)
      ),
      Arguments.of(
        "2021-01-07 09:53:36",
        OffsetDateTime::class,
        OffsetDateTime.ofInstant(buildUtcDate(2021, 1, 7, 9, 53, 36), ZoneOffset.UTC)
      )
    )

    private fun buildUtcDate(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, sec: Int = 0): Instant =
      Calendar.Builder()
        .setTimeZone(TimeZone.getTimeZone("UTC"))
        .setDate(year, month - 1, day)
        .setTimeOfDay(hour, minute, sec)
        .build().toInstant()

  }

  @MethodSource("dateProvider")
  @ParameterizedTest
  fun testDateConversion(value: Any, targetType: KClass<*>, expected: Any) {
    val res = TemporalConverter.convertToTemporal(targetType, value)
    assertAll(
      { res should beInstanceOf(targetType) },
      { res shouldBe expected }
    )
  }

}