package de.qualersoft.robotframework.library.model

import de.qualersoft.robotframework.library.annotation.Keyword
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions

class KotlinKeywordDescriptorTest {
  
  @Test
  fun testSimpleFunctionOneArg() {
    val fnc = getFunctionBy<FunctionsHolderClass>("simpleFunctionOneArg")
    val desc = KeywordDescriptor(fnc)
    assertAll(
      { desc.name shouldBe "simpleFunctionOneArg" },
      { desc.description shouldBe """*Parameters*:
        |- test [String]
      """.trimMargin() },
      { desc.robotArguments should haveSize(1) },
      { desc.robotArguments.first() should haveSize(1) }
    )
  }

  @ParameterizedTest(name = "[{index}] {0} -> {1}")
  @MethodSource("typeFactory")
  fun testTypeConversion(name: String, type: String) {
    val fnc = getFunctionBy<FunctionsHolderClass>("nativeType$name")
    val desc = KeywordDescriptor(fnc)
    "${desc.robotArgumentTypes["test"]}" shouldBe type
  }
  
  
  private inline fun <reified T> getFunctionBy(name: String): KFunction<*> {
    val kFunction = T::class.functions.firstOrNull { it.name == name }
    kFunction shouldNot beNull()
    return kFunction!!
  }
  
  companion object {
    @Suppress("unused")
    @JvmStatic
    fun typeFactory(): Stream<Arguments> = Stream.of(
      Arguments.of("Byte", "int"),
      Arguments.of("Short", "int"),
      Arguments.of("Int", "int"),
      Arguments.of("Long", "int"),
      Arguments.of("Float", "float"),
      Arguments.of("Double", "float"),
      Arguments.of("BigDecimal", "class java.math.BigDecimal"),
      Arguments.of("Boolean", "bool"),
      Arguments.of("String", "class kotlin.String"),
      Arguments.of("Date", "datetime"),
      Arguments.of("Temporal", "datetime"),
      Arguments.of("Duration", "timedelta"),
      Arguments.of("ByteArray", "bytearray"),
      Arguments.of("Other", "class de.qualersoft.robotframework.library.model.KotlinKeywordDescriptorTest\$FunctionsHolderClass")
    )
  }
  
  @Suppress("unused", "unused_parameter")
  class FunctionsHolderClass {

    @Keyword
    fun simpleFunctionOneArg(test: String) {}

    //<editor-fold desc="Type tests">
    //<editor-fold desc="Numbers">
    @Keyword
    fun nativeTypeByte(test: Byte) {}
    @Keyword
    fun nativeTypeShort(test: Short) {}
    @Keyword
    fun nativeTypeInt(test: Int) {}
    @Keyword
    fun nativeTypeLong(test: Long) {}
    @Keyword
    fun nativeTypeFloat(test: Float) {}
    @Keyword
    fun nativeTypeDouble(test: Double) {}
    @Keyword
    fun nativeTypeBigDecimal(test: BigDecimal) {}
    //</editor-fold>
    @Keyword
    fun nativeTypeBoolean(test: Boolean) {}
    @Keyword
    fun nativeTypeString(test: String) {}
    @Keyword
    fun nativeTypeDate(test: Date) {}
    @Keyword
    fun nativeTypeTemporal(test: LocalDateTime) {}
    @Keyword
    fun nativeTypeDuration(test: Duration) {}
    @Keyword
    fun nativeTypeByteArray(test: ByteArray) {}
    @Keyword
    fun nativeTypeOther(test: FunctionsHolderClass) {}
    //</editor-fold>
  }
}
