package de.qualersoft.robotframework.library.model

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.annotation.KeywordTag
import de.qualersoft.robotframework.library.annotation.KwdArg
import de.qualersoft.robotframework.library.conversion.PyObjUtil
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
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
      {
        desc.description shouldBe """*Parameters*
          |
          |test [String]""".trimMargin()
      },
      { desc.robotArguments should haveSize(1) },
      { desc.robotArguments.first() should haveSize(1) }
    )
  }

  //<editor-fold desc="function name override">
  @Test
  fun testFunctionNameCanBeOverwritten() {
    val desc = getDescriptor<FunctionsHolderClass>("overrideFunctionName")
    desc.name shouldBe "newName"
  }

  @Test
  fun testEmptySuppliedNameIsIgnored() {
    val desc = getDescriptor<FunctionsHolderClass>("suppliedNameEmpty")
    desc.name shouldBe "suppliedNameEmpty"
  }

  @Test
  fun testBlankSuppliedNameIsIgnored() {
    val desc = getDescriptor<FunctionsHolderClass>("suppliedNameBlank")
    desc.name shouldBe "suppliedNameBlank"
  }
  //</editor-fold>

  @ParameterizedTest(name = "[{index}] {0} -> {1}")
  @MethodSource("typeFactory")
  fun testTypeConversion(name: String, type: String) {
    val fnc = getFunctionBy<FunctionsHolderClass>("nativeType$name")
    val desc = KeywordDescriptor(fnc)
    "${desc.robotArgumentTypes["test"]}" shouldBe type
  }

  //<editor-fold desc="Keyword call tests">
  @Test
  fun testSimpleCall() {
    val res = exec<InvokeHolderClass>("simpleCall", listOf("simple"))
    res shouldBe "Result simple"
  }

  @Test
  fun testNormalAndVarargsCall() {
    val res = exec<InvokeHolderClass>("normalAndVarargsCall", listOf("first", "varg1", "varg2"))
    res shouldBe "Result first <varg1, varg2>"
  }

  @Test
  fun testJustVarargsCall() {
    val res = exec<InvokeHolderClass>("justVarargsCall", listOf("a1", "b2", "c3"))
    res shouldBe "Result: <a1, b2, c3>"
  }

  @Test
  fun testVarargsCallWithIntType() {
    val res = exec<InvokeHolderClass>("varargsCallWithIntType", listOf(1, 2, 3))
    res shouldBe "Result: <1, 2, 3>"
  }

  @Test
  fun testKeywordArgsCall() {
    val res = exec<InvokeHolderClass>(
      "keywordArgsCall", listOf(), mapOf(
        "age" to 42,
        "name" to "Testy",
        "orderDate" to LocalDate.of(2021, 6, 24)
      )
    )
    res shouldBe """Result: {
      |"age": 42,
      |"name": "Testy",
      |"orderDate": "2021-06-24"
      |}""".trimMargin()
  }

  @Test
  fun testCallWithDifferentTypes() {
    val res = exec<InvokeHolderClass>(
      "callWithDifferentTypes", listOf(
        1,
        "42",
        "2021-06-24",
        "check",
        Duration.ofSeconds(1)
      )
    )
    res shouldBe """Result: {
      | "acceptTerms": true,
      | "age": 42,
      | "orderDate": "2021-06-24",
      | "payment": "CHECK",
      | "ttl": "PT1S"
      |}""".trimMargin()
  }

  @Test
  fun testCallWithDifferentTypesBooleanString() {
    val res = exec<InvokeHolderClass>(
      "callWithDifferentTypes", listOf(
        "false",
        "42",
        "2021-06-24",
        "check",
        Duration.ofSeconds(1)
      )
    )
    res shouldBe """Result: {
      | "acceptTerms": false,
      | "age": 42,
      | "orderDate": "2021-06-24",
      | "payment": "CHECK",
      | "ttl": "PT1S"
      |}""".trimMargin()
  }

  @Test
  fun testCallWithNamedArgs() {
    val res = exec<InvokeHolderClass>(
      "callWithDifferentTypes", listOf(), mapOf(
        "acceptTerms" to "false",
        "age" to "42",
        "orderDate" to "2021-06-24",
        "type" to "check",
        "ttl" to PyObjUtil.create(
          """from datetime import timedelta
            |res = timedelta(minutes=8, seconds=5)""".trimMargin()
        )
      )
    )
    res shouldBe """Result: {
      | "acceptTerms": false,
      | "age": 42,
      | "orderDate": "2021-06-24",
      | "payment": "CHECK",
      | "ttl": "PT8M5S"
      |}""".trimMargin()
  }

  @Test
  fun testCallWithNamedArgsAndKwargs() {
    val res = exec<InvokeHolderClass>(
      "callWithNamedArgsAndKwargs", listOf(), mapOf(
        "a1" to "1",
        "a2" to "2",
        "ka1" to "9",
        "ka2" to "8"
      )
    )
    res shouldBe """Result: {
      |  "a1": "1",
      |  "a2": "2",
      |  "ka1": "9",
      |  "ka2": "8"
      |}""".trimMargin()
  }

  @Test
  fun testCallWithPosButNoKwArgsGiven() {
    val res = exec<InvokeHolderClass>(
      "callWithNamedArgsAndKwargs", listOf("1", "2"), mapOf()
    )
    res shouldBe """Result: {
      |  "a1": "1",
      |  "a2": "2"
      |}""".trimMargin()
  }
  //</editor-fold>

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("docuFactory")
  fun testDocumentation(fncName: String, expected: String) {
    val desc = getDescriptor<DocumentationHolderClass>(fncName)
    desc.description shouldBe expected
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("tagFactory")
  fun testTags(fncName: String, expectedTags: List<String>) {
    val desc = getDescriptor<FunctionsHolderClass>(fncName)
    desc.tags shouldContainExactlyInAnyOrder expectedTags
  }

  //<editor-fold desc="function access and discoverability">
  @Test
  fun testPrivateFunThrows() {
    val fnc = getFunctionBy<ValidationHolderClass>("privateFun")
    val ex = assertThrows<IllegalArgumentException> {
      KeywordDescriptor(fnc)
    }
    ex.message shouldContain "'privateFun' is not accessible"
  }

  @Test
  fun testProtectedFunThrows() {
    val fnc = getFunctionBy<ValidationHolderClass>("protectedFun")
    val ex = assertThrows<IllegalArgumentException> {
      KeywordDescriptor(fnc)
    }
    ex.message shouldContain "'protectedFun' is not accessible"
  }

  @Test
  fun testAbstractFunThrows() {
    val fnc = getFunctionBy<ValidationHolderClass>("abstractFun")
    val ex = assertThrows<IllegalArgumentException> {
      KeywordDescriptor(fnc)
    }
    ex.message shouldContain "functions may not abstract! Make 'abstractFun'"
  }

  @Test
  fun testUnannotatedFunThrows() {
    val fnc = getFunctionBy<ValidationHolderClass>("unannotated")
    assertThrows<NoSuchElementException> {
      KeywordDescriptor(fnc)
    }
  }


  @Test
  fun testNoKeywordAnnotation() {
    val fnc = getFunctionBy<ValidationHolderClass>("noKeywordAnnotation")
    assertThrows<NoSuchElementException> {
      KeywordDescriptor(fnc)
    }
  }
  //</editor-fold>

  private inline fun <reified T> getFunctionBy(name: String): KFunction<*> {
    val kFunction = T::class.functions.firstOrNull { it.name == name }
    kFunction shouldNot beNull()
    return kFunction!!
  }

  private inline fun <reified T> getDescriptor(name: String) = assertDoesNotThrow {
    KeywordDescriptor(getFunctionBy<T>(name))
  }

  private inline fun <reified T> exec(name: String, args: List<Any?> = listOf(), kwArgs: Map<String, Any?> = mapOf()) =
    assertDoesNotThrow {
      val desc = getDescriptor<T>(name)
      val o = T::class.constructors.first().call() as Any
      desc.invoke(o, args, kwArgs)
    }

  companion object {
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
      Arguments.of("String", "str()"),
      Arguments.of("Date", "datetime.datetime"),
      Arguments.of("Temporal", "datetime.datetime"),
      Arguments.of("Duration", "timedelta"),
      Arguments.of("ByteArray", "bytearray"),
      Arguments.of(
        "Other",
        "class de.qualersoft.robotframework.library.model.KotlinKeywordDescriptorTest\$FunctionsHolderClass"
      )
    )

    @JvmStatic
    fun docuFactory(): Stream<Arguments> = Stream.of(
      Arguments.of("noDoc", ""),
      Arguments.of(
        "singleLineSummary", "Single line"
      ),
      Arguments.of(
        "multiLineSummary", """First line second line"""
      ),
      Arguments.of(
        "multiLineSummaryWithEmptyLine", "First line third line"
      ),
      Arguments.of(
        "multiLineSummaryWithEmptySpacedLine", "First line after ws line"
      ),
      Arguments.of(
        "singleDetailsLine", "First line"
      ),
      Arguments.of(
        "multiDetailsLine", """First line
        |Second line""".trimMargin()
      ),
      Arguments.of(
        "multiDetailsLineWithEmptyLine", """First line
        |
        |Third line""".trimMargin()
      ),
      Arguments.of(
        "summaryAndDetails", """I'm the summary
          |
          |I'm the details""".trimMargin()
      ),
      Arguments.of(
        "summaryAndArgs", """Just a summary
          |
          |*Parameters*
          |
          |age [Int]
        """.trimMargin()
      ),
      Arguments.of(
        "detailsAndArgs", """Just details
          |
          |*Parameters*
          |
          |age [Int]
        """.trimMargin()
      ),
      Arguments.of(
        "summaryDetailsAndArgs", """I'm the summary
          |
          |I'm the details
          |
          |*Parameters*
          |
          |age [Int]
        """.trimMargin()
      )
    )

    @JvmStatic
    fun tagFactory(): Stream<Arguments> = Stream.of(
      Arguments.of("simpleFunctionOneArg", emptyList<String>()),
      Arguments.of("singleTag", listOf("A TAG")),
      Arguments.of("tagWithSpaceNormalization", listOf("I M A TAG")),
      Arguments.of("multipleTags", listOf("T1", "T2", "T3")),
      Arguments.of("withEmptyTag", listOf("Some Tag")),
      Arguments.of("withBlankTags", listOf("Another tag")),
      Arguments.of("tagWithSurroundingSpaces", listOf("I have surrounding whitespaces"))
    )
  }

  @Suppress("unused", "unused_parameter")
  class FunctionsHolderClass {

    @Keyword
    fun simpleFunctionOneArg(test: String) {
    }

    @Keyword(name = "newName")
    fun overrideFunctionName() {
    }

    @Keyword(name = "")
    fun suppliedNameEmpty() {

    }

    @Keyword(name = " ")
    fun suppliedNameBlank() {

    }

    //<editor-fold desc="Type tests">
    //<editor-fold desc="Numbers">
    @Keyword
    fun nativeTypeByte(test: Byte) {
    }

    @Keyword
    fun nativeTypeShort(test: Short) {
    }

    @Keyword
    fun nativeTypeInt(test: Int) {
    }

    @Keyword
    fun nativeTypeLong(test: Long) {
    }

    @Keyword
    fun nativeTypeFloat(test: Float) {
    }

    @Keyword
    fun nativeTypeDouble(test: Double) {
    }

    @Keyword
    fun nativeTypeBigDecimal(test: BigDecimal) {
    }

    //</editor-fold>
    @Keyword
    fun nativeTypeBoolean(test: Boolean) {
    }

    @Keyword
    fun nativeTypeString(test: String) {
    }

    @Keyword
    fun nativeTypeDate(test: Date) {
    }

    @Keyword
    fun nativeTypeTemporal(test: LocalDateTime) {
    }

    @Keyword
    fun nativeTypeDuration(test: Duration) {
    }

    @Keyword
    fun nativeTypeByteArray(test: ByteArray) {
    }

    @Keyword
    fun nativeTypeOther(test: FunctionsHolderClass) {
    }
    //</editor-fold>

    @Keyword(tags = [KeywordTag("A TAG")])
    fun singleTag() {
    }

    @Keyword(tags = [KeywordTag("I  M\tA\t  TAG")])
    fun tagWithSpaceNormalization() {
    }

    @Keyword(tags = [KeywordTag("T1"), KeywordTag("T2"), KeywordTag("T3")])
    fun multipleTags() {
    }
    
    @Keyword(tags = [KeywordTag("Some Tag"), KeywordTag("")])
    fun withEmptyTag() {}

    @Keyword(tags = [KeywordTag("Another tag"), KeywordTag(" "), KeywordTag("\t")])
    fun withBlankTags() {}

    @Keyword(tags = [KeywordTag("  I have surrounding whitespaces\t")])
    fun tagWithSurroundingSpaces() {}
  }

  @Suppress("unused")
  class InvokeHolderClass {
    @Keyword
    fun simpleCall(value: String) = "Result $value"

    @Keyword
    fun normalAndVarargsCall(value: String, @KwdArg(kind = ParameterKind.VARARG) args: List<String>) =
      "Result $value " + args.joinToString(", ", "<", ">")

    @Keyword
    fun justVarargsCall(@KwdArg(kind = ParameterKind.VARARG) args: List<String>) =
      "Result: " + args.joinToString(", ", "<", ">")

    @Keyword
    fun varargsCallWithIntType(@KwdArg(kind = ParameterKind.VARARG) args: List<Int>) =
      "Result: " + args.joinToString(", ", "<", ">")

    @Keyword
    fun keywordArgsCall(@KwdArg(kind = ParameterKind.KWARG) kwargs: Map<String, Any?>) =
      "Result: " + kwargs.map { "\"${it.key}\": " + if (it.value is Number) it.value else "\"${it.value}\"" }
        .joinToString(",\n", "{\n", "\n}")

    @Keyword
    fun callWithDifferentTypes(
      acceptTerms: Boolean,
      age: Int,
      orderDate: LocalDate,
      type: PaymentType,
      ttl: Duration
    ): String {
      return """Result: {
        | "acceptTerms": $acceptTerms,
        | "age": $age,
        | "orderDate": "$orderDate",
        | "payment": "$type",
        | "ttl": "$ttl"
        |}""".trimMargin()
    }

    @Keyword
    fun callWithNamedArgsAndKwargs(
      a1: String,
      a2: String,
      @KwdArg(kind = ParameterKind.KWARG) kwargs: Map<String, Any?>
    ): String {
      return """Result: {
      |  "a1": "$a1",
      |  "a2": "$a2"
    """.trimMargin() + kwargs.map { "  \"${it.key}\": \"${it.value}\"" }
        .let { if (it.isEmpty()) "\n}" else it.joinToString(",\n", ",\n", "\n}") }
    }
  }

  @Suppress("unused")
  class DocumentationHolderClass {
    @Keyword
    fun noDoc() {
    }

    @Keyword(docSummary = ["Single line"])
    fun singleLineSummary() {
    }

    @Keyword(docSummary = ["First line", "second line"])
    fun multiLineSummary() {
    }

    @Keyword(docSummary = ["First line", "", "third line"])
    fun multiLineSummaryWithEmptyLine() {
    }

    @Keyword(docSummary = ["First line", " ", "after ws line"])
    fun multiLineSummaryWithEmptySpacedLine() {
    }

    @Keyword(docDetails = ["First line"])
    fun singleDetailsLine() {
    }

    @Keyword(docDetails = ["First line", "Second line"])
    fun multiDetailsLine() {
    }

    @Keyword(docDetails = ["First line", "", "Third line"])
    fun multiDetailsLineWithEmptyLine() {
    }

    @Keyword(
      docSummary = ["I'm the summary"],
      docDetails = ["I'm the details"]
    )
    fun summaryAndDetails() {
    }

    @Keyword(
      docSummary = ["Just a summary"]
    )
    fun summaryAndArgs(age: Int) {
    }

    @Keyword(
      docDetails = ["Just details"]
    )
    fun detailsAndArgs(age: Int) {
    }

    @Keyword(
      docSummary = ["I'm the summary"],
      docDetails = ["I'm the details"]
    )
    fun summaryDetailsAndArgs(age: Int) {
    }
  }

  @Suppress("unused")
  abstract class ValidationHolderClass {
    @Keyword
    private fun privateFun() {
    }

    @Keyword
    protected fun protectedFun() {
    }

    @Keyword
    abstract fun abstractFun()

    fun unannotated() {}

    @CustomAnnotation
    fun noKeywordAnnotation() {
    }
  }

  @Suppress("unused")
  enum class PaymentType {
    CHECK,
    CREDIT_CARD,
    DIRECT_DEBIT,
    ON_DELIVERY
  }

  @Target(AnnotationTarget.FUNCTION)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class CustomAnnotation
}
