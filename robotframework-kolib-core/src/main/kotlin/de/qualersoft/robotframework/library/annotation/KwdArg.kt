package de.qualersoft.robotframework.library.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class KwdArg(

  /**
   * Documentation of the argument, will be put in generated documentation of the keyword.
   */
  val doc: String = "",

  /**
   * Indicates whether the argument is optional.
   * Can be omitted if kotlin is used.
   */
  val optional: Boolean = false,

  /**
   * Mark this argument as a `keyword` argument.
   *
   * **Attention:**
   * - If `true`, the argument must be the last of the function
   * - If `true`, the argument must be of type Map<String, *>
   *
   * Except the type check, we do not perform any consistence-checks! (We trust you)
   *
   * Ok Example:
   * ```
   *  @Keyword
   *  fun runCommand(cmd: String, vararg args: String, @KwdArg(kwArg=true) env: Map<String, String>) {
   *    // some fancy code to run the command
   *  }
   * ```
   * The next ones will also work but may (most properly) result in unwanted behavior
   * ```
   *  fun wrongPosition(@KwdArg(kwArg=true) first: Map<String, String>, sensOfLife: Int)
   * ```
   */
  val kwArg: Boolean = false,

  /**
   * Name of the argument.
   */
  val name: String = "",

  /**
   * Default value (if any). May only apply to [optional] arguments.
   * Because default values can not be retrieved from meta-data you should define this property.
   * As of robot framework conversion restrictions and annotation restriction only 'primitive' values and 'enumerations' can be used!
   * All these can be converted from string
   */
  val default: String = "\u0000",

  /**
   * Type of the argument
   */
  val type: KClass<*> = Nothing::class
)