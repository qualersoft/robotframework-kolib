package de.qualersoft.robotframework.library.conversion

import org.python.core.PyObject
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

object NumberConverter {
  fun convertToNumber(targetType: KClass<*>, value: Any): Any {
    if (value is String) {
      return numberOfString(targetType, value)
    } else if (value is Number) {
      return numberOfNumber(targetType, value)
    } else if ((value is PyObject) && value.isNumberType) {
      val tmp = value.__tojava__(Double::class.java) as Double
      return numberOfNumber(targetType, tmp)
    }
    throw UnsupportedOperationException("Couldn't find a matching number converter for `$value` to '$targetType'")
  }

  private fun numberOfString(targetType: KClass<*>, value: String): Number = when (targetType) {
    Byte::class -> value.toByte()
    Short::class -> value.toShort()
    Int::class -> value.toInt()
    Long::class -> value.toLong()
    Float::class -> value.toFloat()
    Double::class -> value.toDouble()
    BigInteger::class -> value.toBigInteger()
    BigDecimal::class -> value.toBigDecimal()
    else -> throw UnsupportedOperationException("No converter defined to convert string '$value' to type '$targetType'")
  }

  private fun numberOfNumber(targetType: KClass<*>, value: Number): Number = when (targetType) {
    Byte::class -> value.toByte()
    Short::class -> value.toShort()
    Int::class -> value.toInt()
    Long::class -> value.toLong()
    Float::class -> value.toFloat()
    Double::class -> value.toDouble()
    BigInteger::class -> BigInteger.valueOf(value.toLong())
    BigDecimal::class -> BigDecimal.valueOf(value.toDouble())
    else -> throw UnsupportedOperationException("No converter defined to convert number '$value' to type '$targetType'")
  }
}
