package de.qualersoft.robotframework.library.conversion

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BooleanConverterTest {

  @ParameterizedTest(name = "<{0}> -> {1}")
  @MethodSource("generateValidData")
  fun testSuccessfulConversion(value: Any, expected: Boolean) {
    val result = BooleanConverter.convertToBoolean(value)
    result shouldBe expected
  }

  @ParameterizedTest
  @MethodSource("generateInvalidData")
  fun testInvalidConversion(value: Any, expectedErrorMsgParts: List<String>) {
    val ex = assertThrows<IllegalArgumentException> {
      BooleanConverter.convertToBoolean(value)
    }

    assertAll(expectedErrorMsgParts.map {
      { ex.message shouldContain it }
    })
  }

  @Suppress("unused")
  companion object {
    @JvmStatic
    fun generateValidData(): Stream<Arguments> = Stream.of(
      //<editor-fold desc="String conversion">
      //<editor-fold desc="to true">
      arg("yes", true),
      arg("YES", true),
      arg("true", true),
      arg("on", true),
      arg("ok", true),
      arg("1", true),
      arg("-0.5", true),
      //</editor-fold>
      //<editor-fold desc="to false">
      arg("", false),
      arg(" ", false),
      arg("no", false),
      arg("nO", false),
      arg("No", false),
      arg("NO", false),
      arg("false", false),
      arg("off", false),
      arg("nok", false),
      arg("0", false),
      arg("0.0", false),
      arg(".0", false),
      //</editor-fold>
      //</editor-fold>

      //<editor-fold desc="Number conversion">
      arg(1, true),
      arg(-1, true),
      arg(0.5, true),
      arg(-0.5, true),
      arg(1.0, true),
      arg(.1, true),
      arg(0, false),
      arg(0.0, false),
      arg(.0, false)
      //</editor-fold>
    )

    @JvmStatic
    fun generateInvalidData(): Stream<Arguments> = Stream.of(
      arg(
        "nop",
        listOf("Unable to convert String <nop>", "Valid values for `true` are", "'yes'", "and for `false` are", "'no'")
      ),
      arg(
        "yup",
        listOf("Unable to convert String <yup>", "Valid values for `true` are", "'yes'", "and for `false` are", "'no'")
      ),
      arg(
        "doh",
        listOf("Unable to convert String <doh>", "Valid values for `true` are", "'yes'", "and for `false` are", "'no'")
      ),
      arg(
        "ja",
        listOf("Unable to convert String <ja>", "Valid values for `true` are", "'yes'", "and for `false` are", "'no'")
      ),

      // yep, this should be handled earlier and not in converter
      arg(true, listOf("Unexpected type for value <true>", "Only Numbers", "some Strings"))
    )

    private fun arg(vararg arguments: Any): Arguments = Arguments.of(*arguments)
  }
}
