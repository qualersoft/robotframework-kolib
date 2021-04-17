package de.qualersoft.robotframework.library.conversion

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigInteger
import java.util.stream.Stream
import kotlin.reflect.KClass

class NumberConverterTest {
  companion object {
    @JvmStatic
    fun numberProvider() = Stream.of(
      Arguments.of(1, Byte::class, 1.toByte()),
      Arguments.of("2", Byte::class, 2.toByte()),
      Arguments.of(3, Short::class, 3.toShort()),
      Arguments.of("4", Short::class, 4.toShort()),
      Arguments.of(5, Int::class, 5),
      Arguments.of("6", Int::class, 6),
      Arguments.of(7, Long::class, 7.toLong()),
      Arguments.of("8", Long::class, 8.toLong()),
      Arguments.of(9.9, Float::class, 9.9.toFloat()),
      Arguments.of("10.1", Float::class, 10.1.toFloat()),
      Arguments.of(11.8, Double::class, 11.8),
      Arguments.of("12.2", Double::class, 12.2),
      Arguments.of(123456, BigInteger::class, 123456.toBigInteger()),
      Arguments.of("1234567", BigInteger::class, 1234567.toBigInteger())
    )
  }

  @MethodSource("numberProvider")
  @ParameterizedTest
  fun testNumberTypeConversion(value: Any, targetType: KClass<*>, expected: Number) {
    val res = NumberConverter.convertToNumber(targetType, value)
    assertAll(
      { res should beInstanceOf(targetType) },
      { res shouldBe expected }
    )
  }
}