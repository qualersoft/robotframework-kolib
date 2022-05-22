package de.qualersoft.robotframework.library.annotation

/**
 * Annotation to mark functions in a class as keyword.
 *
 * Its arguments can be used to provide information on the keyword to robot framework.
 * - Normally the name of a keyword will be the name of the function this annotation is applied to.
 *    With the [name] argument you can override the default.
 * - The keywords' documentation can be defined through [docSummary] and [docDetails]
 * - and with [tags] you can expose keyword-tags to robot framework. Whitespaces inside a tag will be space-normalised.
 *    Meaning, any consecutive whitespaces are folded to a single space.
 *
 * **Note on documentation of a keyword**
 *
 * This library implements the following pattern:
 *
 * [docSummary]?&nbsp;&lt;optParagraph&gt;&nbsp;[docDetails]?&nbsp;&lt;optParagraph&gt;&nbsp;[argumentsDoc][KwdArg]
 *
 * _&lt;optParagraph&gt;_: We automatically insert two linebreaks between each none-empty block, resulting
 * in a new paragraph.
 *
 * **Remarks**:
 *
 * The implementation provided by this library use the here mentioned defaults for all the arguments.
 *
 * The use of these arguments as well as the here descript behavior, lies with the implementing library.
 * Implementing libraries **must explicitly** indicate deviations from the behavior described here.
 */
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
   *
   * *Remarks*:
   *  - Leading and trailing whitespaces will be removed.
   *  - All consecutive whitespaces will be folded to a single space!
   */
  val tags: Array<KeywordTag> = []
)
