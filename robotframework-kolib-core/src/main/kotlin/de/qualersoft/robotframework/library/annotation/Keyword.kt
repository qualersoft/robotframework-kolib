package de.qualersoft.robotframework.library.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Keyword(
  /**
   * External name of the keyword.
   */
  val name: String = "",

  /**
   * Lines of summary documentation
   * When parsed:
   * - Leading and trailing whitespaces should be trimmed.
   * - Empty lines should be suppressed (Otherwise `libdoc` will take it into details-section)
   *
   * Hint: When using kotlin you should prefer multi-line-string instead
   */
  val docSummary: Array<String> = [],

  /**
   * Lines of documentation details
   *
   * Hint: When using kotlin you should prefer multi-line-string instead
   */
  val docDetails: Array<String> = [],

  /**
   * List of tags present by default onto the keyword
   */
  val tags: Array<KeywordTag> = []
)
