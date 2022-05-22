package de.qualersoft.robotframework.library.conversion

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.time.temporal.UnsupportedTemporalTypeException
import java.util.stream.Stream

class DurationConverterTest {

  @ParameterizedTest(name = "{0} - <{1}> -> <{2}>")
  @MethodSource("durationProvider")
  fun testDurationConversion(caseName: String, value: Any, expected: Any) {
    val res = DurationConverter.convertToDuration(value)
    res shouldBe expected
  }

  @ParameterizedTest(name = "{0}: {1}")
  @MethodSource("invalidValues")
  fun testInvalidInputs(caseName: String, value: Any, expectedErrorMsgParts: List<String>) {
    val ex = assertThrows<Exception> {
      val res = DurationConverter.convertToDuration(value)
      println(res)
    }

    assertAll(expectedErrorMsgParts.map {
      { ex.message shouldContain it }
    })
  }

  companion object {
    @JvmStatic
    fun durationProvider(): Stream<Arguments> = Stream.of(
      args("String-Number", "5.5", dur(sec = 5, ms = 500)),
      args("String-duration", "PT2h42m5s", dur(2, 42, 5)),
      args("Plain number (integral)", 110, dur(0, 1, 50)),
      args("Plain number (decimal-ms)", 3642.33, dur(1, 0, 42, 330)),
      args("Plain number (decimal-ns)", 3670.000000003, dur(1, 1, 10, 0, 3)),
      args("Period", Tmp(2, 4), dur(2, 4)),
      args(
        "timedelta", PyObjUtil.create(
          """from datetime import timedelta
            |res = timedelta(minutes=8, seconds=5)""".trimMargin()
        ), dur(0, 8, 5)
      )
    )

    @JvmStatic
    fun invalidValues(): Stream<Arguments> = Stream.of(
      args("Invalid format", "T1H", listOf("Text cannot be parsed to a Duration")),
      args("NaN string", "NaN", listOf("Text cannot be parsed to a Duration")),
      args("NaN num", Double.NaN, listOf("Character N is neither", "number", "point", "exponential mark")),
      args("Invalid source type", listOf("Im", "no", "duration"), listOf("No conversion strategy", "to Duration"))
    )

    private fun args(vararg args: Any): Arguments = Arguments.of(*args)

    private fun dur(hours: Long = 0, minutes: Long = 0, sec: Long = 0, ms: Long = 0, ns: Long = 0): Duration {
      return Duration.ofHours(hours) +
        Duration.ofMinutes(minutes) +
        Duration.ofSeconds(sec) +
        Duration.ofMillis(ms) +
        Duration.ofNanos(ns)
    }

    private class Tmp(hours: Long = 0, minutes: Long = 0, sec: Long = 0, ms: Long = 0, ns: Long = 0) : TemporalAmount {

      val units = mutableMapOf<TemporalUnit, Long>(
        ChronoUnit.HOURS to hours,
        ChronoUnit.MINUTES to minutes,
        ChronoUnit.SECONDS to sec,
        ChronoUnit.MILLIS to ms,
        ChronoUnit.NANOS to ns
      )

      override fun get(unit: TemporalUnit?): Long {
        requireNotNull(unit) { "Unit must not be null" }
        return if (unit in units) {
          units.getValue(unit)
        } else {
          throw UnsupportedTemporalTypeException("Unsupported unit: $unit! Must be one of ${units.keys}")
        }
      }

      override fun getUnits(): MutableList<TemporalUnit> {
        return units.keys.toMutableList()
      }

      override fun addTo(temporal: Temporal): Temporal {
        var result = temporal
        for (e in units) {
          result = result.plus(e.value, e.key)
        }
        return result
      }

      override fun subtractFrom(temporal: Temporal): Temporal {
        var result = temporal
        for (e in units) {
          result = result.minus(e.value, e.key)
        }
        return result
      }

      override fun toString(): String {
        return units.filterValues { 0L != it }
          .map { "${it.key}: ${it.value}" }
          .joinToString(prefix = "Tmp[", postfix = "]")
      }
    }


  }
}
