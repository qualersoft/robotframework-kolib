package de.qualersoft.robotframework.library.conversion

import org.python.core.PyObject
import java.math.BigDecimal
import java.time.Duration
import java.time.temporal.TemporalAmount
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

object DurationConverter {

  @OptIn(ExperimentalContracts::class)
  fun convertToDuration(targetType: KClass<*>, value: Any): Any = when {
    (value is String) -> {
      if (value.toBigDecimalOrNull() == null) {
        // not a number
        Duration.parse(value)
      } else {
        numberToDuration(value.toBigDecimal())
      }
    }
    value is TemporalAmount -> {
      Duration.from(value)
    }
    value is Number -> {
      // if we have a number we assume seconds (as robot would do)
      val tmp = BigDecimal::class.safeCast(NumberConverter.convertToNumber(BigDecimal::class, value))!!
      numberToDuration(tmp)
    }
    isPythonObject(value, "timedelta") -> {
      val days = value.__findattr__("days").asLong()
      val sec = value.__findattr__("seconds").asLong()
      val micsec = value.__findattr__("microseconds").asLong()
      Duration.ofDays(days) + Duration.ofSeconds(sec) + Duration.ofNanos(micsec * MICRO_TO_NANO_FACTOR)
    }
    else -> {
      throw IllegalArgumentException("No conversion strategy of $value to $targetType")
    }
  }

  @ExperimentalContracts
  fun isPythonObject(value: Any, pyClassName: String, moduleName: String? = null): Boolean {
    contract {
      returns(true) implies (value is PyObject)
    }

    return (value is PyObject) && value.type.let { valType ->
      (pyClassName == valType.name) && moduleName?.run { return@run this == valType.module.toString() } ?: true
    }
  }

  private fun numberToDuration(seconds: BigDecimal): Duration {
    val integralPart = seconds.toBigInteger().toLong()
    val fractionalPart = ((seconds - integralPart.toBigDecimal()) * BigDecimal(SECONDS_FRACTION_TO_NANO_FACTOR)).toLong()
    return Duration.ofSeconds(integralPart) + Duration.ofNanos(fractionalPart)
  }

  /**
   * Factor to convert seconds fraction part to nanoseconds.
   */
  private const val SECONDS_FRACTION_TO_NANO_FACTOR = 1e9

  /**
   * Factor to convert microseconds to nanoseconds.
   */
  private const val MICRO_TO_NANO_FACTOR = 1000
}
