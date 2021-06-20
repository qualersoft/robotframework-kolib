package de.qualersoft.robotframework.library.annotation

import de.qualersoft.robotframework.library.model.ParameterKind
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class KwdArg(

  /**
   * Documentation of the argument, will be put in generated documentation of the keyword.
   */
  val doc: String = "",

  /**
   * Only used for documentation.
   */
  val default: String = NULL_STRING,

  /**
   * Can mark an argument as vararg or keyword argument.
   * For details on this refer to the documentation.
   */
  val kind: ParameterKind = ParameterKind.VALUE,

  /**
   * Type of the argument.
   * Can be used to override the normal type
   */
  val type: KClass<*> = Nothing::class
) {
  companion object {
    const val NULL_STRING = "\u0000"
  }
}
