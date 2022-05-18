package de.qualersoft.robotframework.library

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.annotation.KwdArg
import de.qualersoft.robotframework.library.model.ParameterKind
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.assertAll
import org.springframework.context.annotation.ComponentScan
import java.io.File
import javax.annotation.ManagedBean

@ComponentScan(basePackageClasses = [RobotLibTest::class])
class RobotLibTest : FreeSpec({
  "Get Keyword names" - {
    "from annotation" {
      val origin = getLibTestKwdNames()
      origin shouldContain "publicKeywordWithNameFromAnnotation"
    }
    "from function" {
      val origin = getLibTestKwdNames()
      origin shouldContain "publicKeywordFromFunction"
    }
    "from protected methods are not considered" {
      val origin = getLibTestKwdNames()
      origin shouldNotContainAnyOf listOf("iAmProtected", "iAmProtectedKwd")
    }
    "from private methods are not considered" {
      val origin = getLibTestKwdNames()
      origin shouldNotContainAnyOf listOf("iAmPrivate", "iAmNotAlsoNotAvailable", "alsoNotIn")
    }
    "unannotated methods are not considered" {
      val origin = getLibTestKwdNames()
      origin shouldNotContainAnyOf listOf("notAKeyWord", "publicFunNotIn", "iAmProtected", "alsoNotIn")
    }
  }
  "When getting Arguments" - {
    val sut = RobotLib(root = RobotLibTest::class)
    "also not annotated args work" {
      val args = sut.getKeywordArguments("notAnnotated")
      args shouldContainAll listOf(listOf("first"), listOf("second"))
    }
    "unannotated args with default on non nullable type will be mapped to required" {
      val args = sut.getKeywordArguments("withUnannotatedNotNullableDefault")
      assertAll(
        { args shouldHaveSize 1 },
        { args[0] shouldHaveSize 2 },
        { args[0] shouldContainExactly listOf("arg", null) }
      )
    }
    "unannotated args with default on nullable type defaults to null" {
      val args = sut.getKeywordArguments("withUnannotatedNullableDefault")
      assertAll(
        { args shouldHaveSize 1 },
        { args[0] shouldHaveSize 2 },
        { args[0] shouldContainExactly listOf("f", null) }
      )
    }
    "varargs get marked" {
      val args = sut.getKeywordArguments("withVarArg")
      val arg = args.first()

      assertAll(
        { arg.first() as String shouldStartWith "*" },
        { arg.first() as String shouldNotStartWith "**" }
      )
    }
  }
  "Generating documentation" - {
    val sut = RobotLib(root = RobotLibTest::class)
    "of intro should give empty string" {
      sut.getKeywordDocumentation("__intro__").shouldBeEmpty()
    }
    "of init should give empty string" {
      sut.getKeywordDocumentation("__init__").shouldBeEmpty()
    }
    "of keyword with" - {
      "a single summary line" {
        val doc = sut.getKeywordDocumentation("singleSummaryLine")
        doc shouldBe """*Summary*:
          |
          |Just a single line""".trimMargin()
      }
      "multiple summary lines" {
        val doc = sut.getKeywordDocumentation("multiSummaryLine")
        doc shouldBe """*Summary*:
          |
          |Now I'm on a single line.""".trimMargin()
      }
      "leading and trailing whites in summary lines get trimmed" {
        val doc = sut.getKeywordDocumentation("multiSummaryLinesWithWhitespaces")
        doc shouldBe """*Summary*:
          |
          |<This two whitespaces will be trimmed as well as the two trailing whitespaces> <tabs at front and end of an entry are trimmed as well> <Newlines are also gone>""".trimMargin()
      }
      // <<------------------>>
      "a single details line" {
        val doc = sut.getKeywordDocumentation("justSingleDetailsLine")
        doc shouldBe """*Details*:
          |
          |Only one details line""".trimMargin()
      }
      "multiple details lines" {
        val doc = sut.getKeywordDocumentation("multipleDetailsLine")
        doc shouldBe """*Details*:
          |
          |I'm a details documentation
          |with multiple
          |lines""".trimMargin()
      }
    }
  }

  "Getting from subpackage also work" {
    val origin = getLibTestKwdNames()
    origin shouldContain "subpackageFunction"
  }

  "Get keyword types of noargs keyword" {
    val sut = RobotLib(root = RobotLibTest::class)
    val actual = sut.getKeywordTypes("publicKeywordWithNameFromAnnotation")
    actual should beEmpty()
  }

  "Get keyword types of mappable types" {
    val sut = RobotLib(root = RobotLibTest::class)
    val actual = sut.getKeywordTypes("withAnnotatedArgsNoReturn")
    actual shouldBe mapOf("first" to "str()", "second" to "int")
  }

  "Get keyword source" {
    val sut = RobotLib(root = RobotLibTest::class)
    val actual = sut.getKeywordSource("withAnnotatedArgsNoReturn")
    actual shouldNot beNull()
    actual should endWith(KeywordArgs::class.qualifiedName!!.replace('.', File.separatorChar))
  }
})

fun getLibTestKwdNames() = RobotLib(root = RobotLibTest::class).getKeywordNames()

//<editor-fold desc="keyword discovery test classes">
@Suppress("unused")
@ManagedBean
open class KwdNameClass {
  @Keyword(name = "publicKeywordWithNameFromAnnotation")
  fun publicKeywordWithName() {
  }

  fun notAKeyWord() {}
}

@Suppress("unused")
@ManagedBean
open class KwdFunctionNameClass {
  @Keyword
  fun publicKeywordFromFunction() {
  }
}

@Suppress("unused")
@ManagedBean
open class ProtectedStuff {
  protected fun iAmProtected() {}

  @Keyword
  protected fun iAmProtectedKwd() {
  }
}

@Suppress("unused")
@ManagedBean
open class PrivateStuff {
  private fun iAmPrivate() {}

  @Keyword
  private fun iAmNotAlsoNotAvailable() {
  }
}

@Suppress("unused")
@ManagedBean
open class NoBeanAtAll {
  private fun alsoNotIn() {}
  fun publicFunNotIn() {}
}
//</editor-fold>

@Suppress("unused", "unused_parameter")
@ManagedBean
open class KeywordArgs {
  @Keyword
  fun withAnnotatedArgsNoReturn(
    @KwdArg first: String,
    @KwdArg second: Int
  ) {
  }

  @Keyword
  fun notAnnotated(first: String, second: Int) {
  }

  @Keyword
  fun withDefaultValues(
    @KwdArg(default = "A", type = String::class) first: String = "A",
    @KwdArg(default = "5", type = Int::class) second: Int = 5
  ) {
  }

  @Keyword
  fun withUnannotatedNotNullableDefault(
    arg: String = "42"
  ) {
  }

  @Keyword
  fun withUnannotatedNullableDefault(
    f: String? = null
  ) {
  }

  @Keyword
  fun withVarArg(@KwdArg(kind = ParameterKind.VARARG) variable: List<String>?) {
  }
}

@Suppress("unused")
@ManagedBean
open class KeywordDocumentation {

  @Keyword(docSummary = ["Just a single line"])
  fun singleSummaryLine() {
  }

  @Keyword(
    docSummary = [
      "Now I'm",
      "on a single",
      "line."
    ]
  )
  fun multiSummaryLine() {
  }

  // <This two whitespaces will be trimmed as well as the two trailing whitespaces> <tabs at front and end of an entry are trimmed as well> <Newlines are also gone>
  @Keyword(
    docSummary = [
      "  <This two whitespaces will be trimmed",
      "as well as the two trailing whitespaces>  ",
      "\t<tabs at front and end of an entry are trimmed as well>\t",
      "\n<Newlines are also gone>\n"
    ]
  )
  fun multiSummaryLinesWithWhitespaces() {
  }

  @Keyword(docDetails = ["Only one details line"])
  fun justSingleDetailsLine() {
  }

  @Keyword(
    docDetails = ["I'm a details documentation",
      "with multiple",
      "lines"
    ]
  )
  fun multipleDetailsLine() {
  }
}
