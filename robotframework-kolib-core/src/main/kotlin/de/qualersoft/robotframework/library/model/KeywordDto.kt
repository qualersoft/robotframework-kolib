package de.qualersoft.robotframework.library.model

import de.qualersoft.robotframework.library.annotation.Keyword
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaMethod

class KeywordDto(private val function: KFunction<*>) {
  private val kwdAnnotation = function.annotations.first { it.annotationClass == Keyword::class } as Keyword
  val arguments = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.map { KeywordArgDto(it) }
  val declaringClass = function.javaMethod!!.declaringClass
  val method = function

  val name: String
    get() {
      return kwdAnnotation.name.let {
        if (it.isNotBlank()) it
        else function.name
      }
    }
  val description: String = """${normalizeSummary(kwdAnnotation.docSummary)}
    |
    |${kwdAnnotation.docDetails.joinToString(System.lineSeparator())}
    |${createArgDoc()}""".trimMargin().trim()


  fun prepareArguments(args: List<Any?>, kwArgs: Map<String, Any?>): List<Any?> {
    val allArgs = args.toMutableList()
    if (kwArgs.isNotEmpty()) {
      allArgs += kwArgs
    }
    return emptyList()
  }

  private fun normalizeSummary(summary: Array<String>): String {
     return summary.map { it.trim() }.filter { it.isNotEmpty() }.joinToString( " ")
  }

  private fun createArgDoc() =
    if (arguments.isNotEmpty()) {
      "${System.lineSeparator()}Parameters:${System.lineSeparator()}" + arguments.joinToString(
        System.lineSeparator()
      ) { it.documentation }
    } else ""
}