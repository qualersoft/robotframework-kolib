package de.qualersoft.robotframework.library.model

import de.qualersoft.robotframework.library.annotation.KwdArg
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.converter.JavaTimeConversionPattern
import java.util.Optional
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters

class KotlinKeywordParameterDescriptorTest {

  //<editor-fold desc="plain (no annotation) tests">
  @Test
  fun testPlainNullableParameter() {
    val param = getParameterOf<PlainFunctionHolderClass>("plainNullableParameter")
    val descriptor = KeywordParameterDescriptor(param)
    assertAll(
      { descriptor.type shouldBe String::class },
      { descriptor.optional shouldBe false },
      { descriptor.name shouldBe "test" },
      { descriptor.kind shouldBe ParameterKind.VALUE }
    )
  }

  @Test
  fun testPlainNonNullableParameter() {
    val param = getParameterOf<PlainFunctionHolderClass>("plainNonNullableParameter")
    val descriptor = KeywordParameterDescriptor(param)
    assertAll(
      { descriptor.type shouldBe String::class },
      { descriptor.optional shouldBe false },
      { descriptor.name shouldBe "test" },
      { descriptor.kind shouldBe ParameterKind.VALUE }
    )
  }

  @Test
  fun testPlainNullableWithDefault() {
    val param = getParameterOf<PlainFunctionHolderClass>("plainNullableWithDefault")
    val descriptor = KeywordParameterDescriptor(param)
    assertAll(
      { descriptor.type shouldBe String::class },
      { descriptor.optional shouldBe true },
      { descriptor.name shouldBe "test" },
      { descriptor.kind shouldBe ParameterKind.VALUE }
    )
  }

  @Test
  fun testPlainNonNullableWithDefault() {
    val param = getParameterOf<PlainFunctionHolderClass>("plainNonNullableWithDefault")
    val descriptor = KeywordParameterDescriptor(param)
    assertAll(
      { descriptor.type shouldBe String::class },
      { descriptor.optional shouldBe true },
      { descriptor.name shouldBe "test" },
      { descriptor.kind shouldBe ParameterKind.VALUE }
    )
  }

  @Test
  fun testPlainVarargShouldThrow() {
    val param = getParameterOf<PlainFunctionHolderClass>("plainVararg")
    val ex = assertThrows<IllegalArgumentException> { KeywordParameterDescriptor(param) }
    assertAll(
      { ex.message shouldContain "Use List-type" },
      { ex.message shouldContain "VARARG" }
    )
  }

  @Test
  fun testDocumentationOfPlain() {
    val param = getParameterOf<PlainFunctionHolderClass>("plainNonNullableWithDefault")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.documentation shouldBe "test [String]"
  }
  //</editor-fold>

  //<editor-fold desc="With annotation">
  @Test
  fun testWithListAsVarArg() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withListAsVarArg")
    val descriptor = KeywordParameterDescriptor(param)
    assertAll(
      { descriptor.kind shouldBe ParameterKind.VARARG },
      { descriptor.name shouldBe "tests" },
      { descriptor.optional shouldBe false }
    )
  }

  @Test
  fun testWithVarargNoListTypeThrow() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withVarargNoListType")
    val ex = assertThrows<IllegalArgumentException> { KeywordParameterDescriptor(param) }
    assertAll(
      { ex.message shouldContain "parameter tests" },
      { ex.message shouldContain "marked as vararg" },
      { ex.message shouldContain "subclass of List" }
    )
  }

  @Test
  fun testWithKeywordArgs() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withKeywordArgs")
    val descriptor = KeywordParameterDescriptor(param)
    assertAll(
      { descriptor.kind shouldBe ParameterKind.KWARG },
      { descriptor.name shouldBe "tests" },
      { descriptor.optional shouldBe false }
    )
  }

  @Test
  fun testWithKeywordArgsNoMapTypeThrow() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withKeywordArgsNoMapType")
    val ex = assertThrows<IllegalArgumentException> { KeywordParameterDescriptor(param) }
    assertAll(
      { ex.message shouldContain "parameter tests" },
      { ex.message shouldContain "marked as kwarg" },
      { ex.message shouldContain "subclass of Map" }
    )
  }

  @Test
  fun testWithKeywordArgsKeyTypeNoStringThrow() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withKeywordArgsKeyTypeNoString")
    val ex = assertThrows<IllegalArgumentException> { KeywordParameterDescriptor(param) }
    assertAll(
      { ex.message shouldContain "parameter tests" },
      { ex.message shouldContain "marked as kwarg" },
      { ex.message shouldContain "subclass of Map" }
    )
  }

  @Test
  fun testWithAnnotationButNotKwdArgs() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withAnnotationButNotKwdArgs")
    val descriptor = KeywordParameterDescriptor(param)
    assertAll(
      { descriptor.kind shouldBe ParameterKind.VALUE },
      { descriptor.name shouldBe "test" },
      { descriptor.optional shouldBe false }
    )
  }
  //</editor-fold>

  //<editor-fold desc="Documentation generation">
  @Test
  fun testDocumentationWithDescription() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withDocu")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.documentation shouldBe "test [String] A test value"
  }

  @Test
  fun testDocumentationWithDescriptionAndDefault() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withDocuAndDefault")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.documentation shouldBe "test (_DEFAULT_: `empty`) [String] A test value"
  }

  @Test
  fun testDocumentationWithDescriptionAndEmptyDefault() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withDocuAndEmptyDefault")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.documentation shouldBe "test (_DEFAULT_: ``) [String] A test value"
  }

  @Test
  fun testDocumentationWithDescriptionAndBlankDefault() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withDocuAndBlankDefault")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.documentation shouldBe "test (_DEFAULT_: ` `) [String] A test value"
  }
  //</editor-fold>

  //<editor-fold desc="Robot descriptor generation">
  @Test
  fun testRobotDescriptorValue() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withDocu")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.robotArgumentDescriptor shouldBe listOf("test")
  }

  @Test
  fun testRobotDescriptorValueOptional() {
    val param = getParameterOf<PlainFunctionHolderClass>("plainNonNullableWithDefault")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.robotArgumentDescriptor shouldBe listOf("test", null)
  }

  @Test
  fun testRobotDescriptorVararg() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withListAsVarArg")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.robotArgumentDescriptor shouldBe listOf("*tests")
  }

  @Test
  fun testRobotDescriptorVarargAndDefaultBehaveLikeWithoutDefault() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withListAsVarArgAndDefault")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.robotArgumentDescriptor shouldBe listOf("*tests")
  }

  @Test
  fun testRobotDescriptorKeywordArgs() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withKeywordArgs")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.robotArgumentDescriptor shouldBe listOf("**tests")
  }

  @Test
  fun testRobotDescriptorKeywordArgsAndDefaultBehaveLikeWithoutDefault() {
    val param = getParameterOf<AnnotatedFunctionsHolderClass>("withKeywordArgsAndDefault")
    val descriptor = KeywordParameterDescriptor(param)
    descriptor.robotArgumentDescriptor shouldBe listOf("**tests")
  }
  //</editor-fold>

  //<editor-fold desc="Type conversion">
  @Test
  fun testInputIntToTargetString() {
    val desc = getDescriptorFor<PlainFunctionHolderClass>("plainNullableParameter")
    val actual = desc.convertToTargetType(Optional.of(1))

    assertAll(
      { actual should beInstanceOf<String>() },
      { actual shouldBe "1" }
    )
  }

  @Test
  fun testInputIntListToTargetByteArray() {
    val desc = getDescriptorFor<PlainFunctionHolderClass>("byteArrayArgument")
    val range = IntRange(1, 8)
    val actual = desc.convertToTargetType(Optional.of(range.toList()))
    val expected = ByteArray(8) { (it + 1).toByte() }

    assertAll(
      { actual should beInstanceOf<ByteArray>() },
      { actual shouldBe expected }
    )
  }

  @Test
  fun testInputStringToTargetTypeExceptionByFallback() {
    val desc = getDescriptorFor<PlainFunctionHolderClass>("fallbackArgument")
    val value = "Test"
    val expected = Exception(value)
    val actual = desc.convertToTargetType(Optional.of(value))

    assertAll(
      { actual should beInstanceOf<Exception>() },
      { actual shouldBe expected }
    )
  }
  //</editor-fold>

  private inline fun <reified T> getDescriptorFor(method: String, pos: Int = 0) =
    KeywordParameterDescriptor(getParameterOf<T>(method, pos))

  private inline fun <reified T> getParameterOf(method: String, pos: Int = 0): KParameter {
    val kFunction = T::class.functions.first { it.name == method }
    kFunction shouldNot beNull()
    // pos + 1 because `this` param is always at 0
    return kFunction.valueParameters.first { it.index == pos + 1 }
  }

  @Suppress("unused", "unused_parameter")
  class PlainFunctionHolderClass {

    fun plainNullableParameter(test: String?) {}

    fun plainNonNullableParameter(test: String) {}

    fun plainNullableWithDefault(test: String? = null) {}

    fun plainNonNullableWithDefault(test: String = "empty") {}

    fun plainVararg(vararg tests: String) {}

    fun byteArrayArgument(test: ByteArray) {}

    fun fallbackArgument(test: Exception) {}
  }

  @Suppress("unused", "unused_parameter")
  class AnnotatedFunctionsHolderClass {

    fun withListAsVarArg(@KwdArg(kind = ParameterKind.VARARG) tests: List<String>) {}
    fun withVarargNoListType(@KwdArg(kind = ParameterKind.VARARG) tests: String) {}
    fun withListAsVarArgAndDefault(@KwdArg(kind = ParameterKind.VARARG) tests: List<String> = emptyList()) {}

    fun withKeywordArgs(@KwdArg(kind = ParameterKind.KWARG) tests: Map<String, Any>) {}
    fun withKeywordArgsAndDefault(@KwdArg(kind = ParameterKind.KWARG) tests: Map<String, Any> = emptyMap()) {}
    fun withKeywordArgsNoMapType(@KwdArg(kind = ParameterKind.KWARG) tests: String) {}
    fun withKeywordArgsKeyTypeNoString(@KwdArg(kind = ParameterKind.KWARG) tests: Map<Int, Any>) {}

    fun withDocu(@KwdArg(doc = "A test value") test: String) {}
    fun withDocuAndDefault(@KwdArg(doc = "A test value", default = "empty") test: String = "empty") {}
    fun withDocuAndEmptyDefault(@KwdArg(doc = "A test value", default = "") test: String = "") {}
    fun withDocuAndBlankDefault(@KwdArg(doc = "A test value", default = " ") test: String = " ") {}

    fun withAnnotationButNotKwdArgs(@JavaTimeConversionPattern("") test: String) {}
  }
}
