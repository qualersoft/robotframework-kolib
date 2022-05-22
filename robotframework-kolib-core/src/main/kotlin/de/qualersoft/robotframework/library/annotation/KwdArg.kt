package de.qualersoft.robotframework.library.annotation

import de.qualersoft.robotframework.library.model.ParameterKind
import kotlin.reflect.KClass

/**
 * Annotation to provide additional information on a [Keyword] annotated function.
 *
 * Mainly useful to mark special arguments as [VARARG][ParameterKind.VARARG] or [KWARG][ParameterKind.KWARG].
 *
 * But also to provide additional documentation on the argument without having the need to loosely couple it in
 * [Keyword]s documentation.
 *
 * Generated documentation pattern:
 *
 * <[Argument name][name]> +
 * ( " (&#95;DEFAULT&#95;: '" + <[Default value][default]> + "') " )?
 * " &#91;" + <[short typename][type]> + "&#93;" + ( " " + <[documentation][doc]> )?
 *
 * Multiple arguments are separated through double newlines, resulting in a 'paragraph'.
 *
 * Example:
 * ```
 * @KwdArg("The age", default = "18", name = "age", type = Int::class)
 * ```
 * Results in:
 * ```
 * age (_DEFAULT_: '18') [Int] The age
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class KwdArg(
  /**
   * Documentation of the argument, will be put in generated documentation of the [keyword][Keyword].
   */
  val doc: String = "",

  /**
   * Only used for documentation.
   *
   * ⚠ **Attention**:
   *
   * As this will be put in front of type, do not use long text or text containing linebreaks!
   * If more details on the default is required, put it in the normal documentation.
   */
  val default: String = NULL_STRING,

  /**
   * Can mark an argument as vararg or keyword argument.
   * For details on this refer to the documentation.
   */
  val kind: ParameterKind = ParameterKind.VALUE,

  /**
   * Type of the argument.
   * Can be used to override the normal type.
   * Defaults to [Nothing] class which indicates to use the real type.
   */
  val type: KClass<*> = Nothing::class,

  /**
   * Only used if dealing with java functions, where the name normally can not be retrieved.
   * Gives the user the possibility to avoid argument names like `arg1`.
   * Defaults to [NULL_STRING] which indicates to use the build-in detection mode.
   *
   * **Remark:**
   *
   * In case of java, you can use `-parameters` compiler flag to prevent parameter-name
   * information in compiled class files.
   */
  val name: String = NULL_STRING
) {
  companion object {
    const val NULL_STRING = "\u0000"
  }
}
