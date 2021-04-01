package de.qualersoft.robotframework.library.model

import de.qualersoft.robotframework.library.annotation.KwdArg
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

class KeywordArgDto(private val kParameter: KParameter) {

  /**
   * The [KwdArg] annotation of the parameter. May be `null`
   */
  val argAnnotation = kParameter.annotations.firstOrNull { it.annotationClass == KwdArg::class } as? KwdArg

  /**
   * Position of the argument within the methods argument list
   */
  val position = kParameter.index - 1 // first is always the instance-argument

  /**
   * Denote if this parameter is nullable
   */
  val nullable: Boolean = kParameter.type.isMarkedNullable

  /**
   * Name of the argument.
   * If it cannot determined from [KwdArg] annotation nor from reflection, `'arg' + [position]` will be returned.
   */
  val name: String = if ((null != argAnnotation) && argAnnotation.name.isNotBlank()) {
    argAnnotation.name
  } else {
    kParameter.name ?: "arg${position}"
  }

  /**
   * Type of the argument
   */
  val type: KClass<*> = detectType()

  val kind = detectKind()

  /**
   * The documentation of the argument.
   *
   * Generation pattern is: `<`[name]`> <type>['='<default>][':' `<`[KwdArg.doc]`>]`
   *  - type is at least Any if it cannot be determined
   *  - default is determende from [KwdArg.default] if the argument is optional. Either by using [KwdArg.optional] or reflection.
   *  - [KwdArg.doc] will only be added if it is not `blank`
   *
   * **Known limitations:**
   *
   * It is possible to create an invalid documentation if [KwdArg.optional] is `true` but the parameter is not optional!
   *
   * Example:
   *  ```
   *    @Keyword
   *    public fun error(@KwdArg(optional=true, default="IamWrong") arg:String)
   *  ```
   * In respect to your users, you should avoid this.
   */
  val documentation = createDoc()

  fun toArgumentTuple(): List<Any?> {
    val resName = when (kind) {
      ParamKind.VALUE -> ""
      ParamKind.VARARG -> "*"
      ParamKind.KWARG -> "**"
    } + name

    return if (isDefault()) {
      val default = getDefault()
      val nullable = kParameter.type.isMarkedNullable
      val mappedDefault = if ("\u0000" == default) null else default
      if (!nullable && (null == mappedDefault)) {
        // non-nullable types has to be annotated!
        listOf(resName)
      } else {
        listOf(resName, mappedDefault)
      }
    } else {
      listOf(resName)
    }
  }

  /**
   * Create the argument documentation with the following schema
   * &lt;name&gt; &lt;type&gt;`[`'='&lt;default&gt;`][`':' &lt;doc&gt;`]`
   * Details see [detectType], [getDefault] and last but not least [documentation]
   */
  private fun createDoc(): String {
    val type = detectType().simpleName
    val default = getDefault()

    return "$name $type" + if (null != default) "=$default" else "" + if (null != argAnnotation && argAnnotation.doc.isNotBlank()) ": " + argAnnotation.doc else ""
  }

  private fun isDefault() = argAnnotation?.optional ?: kParameter.isOptional

  /**
   * We cannot determine the default value at runtime therefore it must be set
   */
  private fun getDefault(): String? {
    return if (null != argAnnotation) {
      if ((argAnnotation.optional || kParameter.isOptional) && (argAnnotation.default != "\u0000")) {
        argAnnotation.default
      } else null
    } else null
  }

  private fun detectType(): KClass<*> = if ((null != argAnnotation) && (argAnnotation.type != Nothing::class)) {
    argAnnotation.type
  } else {
    kParameter.type.classifier as? KClass<*> ?: Any::class // no way to determine the type return 'any'
  }

  private fun detectKind(): ParamKind {
    return when {
      kParameter.isVararg -> {
        ParamKind.VARARG
      }
      isKwArg() -> {
        ParamKind.KWARG
      }
      else -> {
        ParamKind.VALUE
      }
    }
  }

  private fun isKwArg(): Boolean {
    val mainType = kParameter.type
    val isMarked: Boolean = (null != argAnnotation) && argAnnotation.kwArg
    return if (isMarked && mainType.classifier == Map::class) {
      val keyType = mainType.arguments.first().type
      keyType?.classifier == String::class
    } else {
      false
    }
  }

  enum class ParamKind {
    VALUE,
    VARARG,
    KWARG
  }
}