package de.qualersoft.robotframework.library

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.annotation.KwdArg
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.string.shouldBeEmpty
import org.junit.jupiter.api.assertAll
import org.springframework.context.annotation.ComponentScan

@ComponentScan(basePackageClasses = [RobotLibTest::class])
class RobotLibTest : FreeSpec({
  "Get Keyword names" - {
    "from annotation" {
      val origin = getKwdNames()
      origin shouldContain "publicKeywordWithNameFromAnnotation"
    }
    "from function" {
      val origin = getKwdNames()
      origin shouldContain "publicKeywordFromFunction"
    }
    "from protected methods are not considered" {
      val origin = getKwdNames()
      origin shouldNotContainAnyOf listOf("iAmProtected", "iAmProtectedKwd")
    }
    "from private methods are not considered" {
      val origin = getKwdNames()
      origin shouldNotContainAnyOf listOf("iAmPrivate", "iAmNotAlsoNotAvailable", "alsoNotIn")
    }
    "unannotated methods are not considered" {
      val origin = getKwdNames()
      origin shouldNotContainAnyOf listOf("notAKeyWord", "publicFunNotIn", "iAmProtected", "alsoNotIn")
    }
  }
  "When getting Arguments" - {
    val sut = RobotLib(root = RobotLibTest::class)
    "annotations win" {
      val args = sut.getKeywordArguments("withAnnotatedArgsNoReturn")
      args shouldContainAll listOf(listOf("firstArg"), listOf("snd"))
    }
    "also not annotated args work" {
      val args = sut.getKeywordArguments("notAnnotated")
      args shouldContainAll listOf(listOf("first"), listOf("second"))
    }
    "defaults will be retrieved" {
      val args = sut.getKeywordArguments("withDefaultValues")
      assertAll(
        { args shouldHaveSize 2 },
        { args[0] shouldContainExactly listOf("first", "A") },
        { args[1] shouldContainExactly listOf("second", "5") }
      )
    }
    "unannotated args with default on non nullable type will be mapped to required" {
      val args = sut.getKeywordArguments("withUnannotatedNotNullableDefault")
      assertAll(
        { args shouldHaveSize 1 },
        { args[0] shouldHaveSize 1 },
        { args[0] shouldContainExactly listOf("arg") }
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
        doc shouldBe "Just a single line"
      }
      "multiple summary lines" {
        val doc = sut.getKeywordDocumentation("multiSummaryLine")
        doc shouldBe "Now I'm on a single line."
      }
      "leading and trailing whites in summary lines get trimmed" {
        val doc = sut.getKeywordDocumentation("multiSummaryLinesWithWhitespaces")
        doc shouldBe "<This two whitespaces will be trimmed as well as the two trailing whitespaces> <tabs at front and end of an entry are trimmed as well> <Newlines are also gone>"
      }
      // <<------------------>>
      "a single details line" {
        val doc = sut.getKeywordDocumentation("justSingleDetailsLine")
        doc shouldBe "Only one details line"
      }
      "multiple details lines" {
        val doc = sut.getKeywordDocumentation("multipleDetailsLine")
        doc shouldBe """I'm a details documentation
          |with multiple
          |lines""".trimMargin()
      }
    }
  }
})

fun getKwdNames() = RobotLib(root = RobotLibTest::class).getKeywordNames()

//<editor-fold desc="keyword discovery test classes">
@Suppress("unused")
open class KwdNameClass {
  @Keyword(name = "publicKeywordWithNameFromAnnotation")
  fun publicKeywordWithName() {
  }

  fun notAKeyWord() {}
}

@Suppress("unused")
open class KwdFunctionNameClass {
  @Keyword
  fun publicKeywordFromFunction() {
  }
}

@Suppress("unused")
open class ProtectedStuff {
  protected fun iAmProtected() {}

  @Keyword
  protected fun iAmProtectedKwd() {
  }
}

@Suppress("unused")
open class PrivateStuff {
  private fun iAmPrivate() {}

  @Keyword
  private fun iAmNotAlsoNotAvailable() {
  }
}

@Suppress("unused")
open class NoBeanAtAll {
  private fun alsoNotIn() {}
  fun publicFunNotIn() {}
}
//</editor-fold>

@Suppress("unused", "unused_parameter")
open class KeywordArgs {
  @Keyword
  fun withAnnotatedArgsNoReturn(
    @KwdArg(name = "firstArg") first: String,
    @KwdArg(name = "snd") second: Int
  ) {
  }

  @Keyword
  fun notAnnotated(first: String, second: Int) {
  }

  @Keyword
  fun withDefaultValues(
    @KwdArg(name = "first", default = "A", optional = true, type = String::class) f: String = "A",
    @KwdArg(name = "second", default = "5", optional = true, type = Int::class) s: Int = 5
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
  fun withVarArg(@KwdArg vararg variable: String) {
  }
}

@Suppress("unused")
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