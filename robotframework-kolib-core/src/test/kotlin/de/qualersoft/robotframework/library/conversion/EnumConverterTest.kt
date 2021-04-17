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
import java.util.stream.Stream

class EnumConverterTest {

  companion object {
    @JvmStatic
    fun enumProvider() = Stream.of(
      Arguments.of("GREEN", MyColor.GREEN), // Exact match
      Arguments.of("red", MyColor.RED), // case insensitive
      Arguments.of("dark green", MyColor.DARK_GREEN), // with spaces
      Arguments.of("light_red", MyColor.`LIGHT RED`) // with underscore
    )
  }

  @MethodSource("enumProvider")
  @ParameterizedTest
  fun testEnumConversion(value: Any, expected: MyColor) {
    val res = EnumConverter.convertToEnum(MyColor::class, value)
    assertAll(
      { res should beInstanceOf<MyColor>() },
      { res shouldBe expected }
    )
  }

  @Test
  fun testInvalidEnum() {
    val ex = assertThrows<ClassCastException> {
      EnumConverter.convertToEnum(MyColor::class, "Unknown")
    }
    assertAll(
      { ex.message shouldContain "'Unknown'" }
    )
  }

  @Test
  fun testMultipleEnums() {
    val ex = assertThrows<ClassCastException> {
      EnumConverter.convertToEnum(MyColor::class, "A  B")
    }
    assertAll(
      { ex.message shouldContain "'A  B'" },
      { ex.message shouldContain "multiple candidates found" }
    )
  }

  enum class MyColor {
    RED,
    GREEN,
    DARK_GREEN,
    `LIGHT RED`,
    `A_ B`,
    `A _B`
  }
}