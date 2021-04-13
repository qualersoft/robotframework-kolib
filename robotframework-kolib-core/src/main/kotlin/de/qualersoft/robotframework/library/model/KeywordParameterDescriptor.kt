package de.qualersoft.robotframework.library.model

import de.qualersoft.robotframework.library.annotation.KwdArg
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.cast
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

class KeywordParameterDescriptor(val param: KParameter) {

  val annotation = (param.annotations.firstOrNull { it is KwdArg } ?: KwdArg::class.createInstance()) as KwdArg

  /**
   * **Remark**: first parameter in function is `this`-ref
   * -> first "real" parameter is at 1
   */
  val position = param.index

  /**
   * If the parameter can be omitted.
   */
  val optional = param.isOptional

  /**
   * Name of the parameter as retrieved by kotlin.
   * If no name could be retrieved 'arg'+<position>. E.g. arg0, arg1, and so on.
   */
  val name = param.name ?: "arg${position}"


  /**
   * Used to get TypeParameter information of generics
   */
  private val _type = param.type
  val type = determineType()

  val kind = annotation.kind

  val default = determineDefault()

  val documentation: String by lazy {
    val typeName = type.simpleName
    val desc = annotation.doc.trim()
    val defVal = if (default.isNullOrBlank()) {
      ""
    } else {
      "\n\tDEFAULT: ${default.trim()}"
    }
    return@lazy "$name [$typeName] $desc$defVal"
  }

  val robotArgumentDescriptor by lazy {
    val argName = when (kind) {
                    ParameterKind.VALUE -> ""
                    ParameterKind.VARARG -> "*"
                    ParameterKind.KWARG -> "**"
                  } + name
    val res = mutableListOf<Any>(argName)
    // VARARG & KWARG is automatically assumed optional by RF
    if (optional && kind == ParameterKind.VALUE) {
      res.add(type.cast(null))
    }
    return@lazy res
  }

  // just validation
  init {
    // for technical reasons we can not support varargs
    if (param.isVararg) {
      throw IllegalArgumentException(
        "Parameter $name is a vararg-parameter, which is not supported! " +
        "Use List-type and mark it with 'KwdArg.kind = VARARG' to solve this."
      )
    }

    if (ParameterKind.VARARG == kind) {
      // Type must be List-Compatible
      if (!type.isSubclassOf(List::class)) {
        throw IllegalArgumentException(
          "The parameter $name is marked as vararg but it's type is not a " +
          "subclass of List!"
        )
      }
    } else if (ParameterKind.KWARG == kind && !(
          type.isSubclassOf(Map::class) &&
          String::class == (_type.arguments.first().type!!.classifier as KClass<*>)
      )) {
      throw IllegalArgumentException(
        "The parameter $name is marked as kwarg but it's type is not a " +
        "subclass of Map or its key-type parameter is not String"
      )
    } // no further else required VALUE has no restrictions (ATM ;) :P)
  }

  private fun determineType(): KClass<*> {
    return if (!Nothing::class.isSubclassOf(annotation.type)) {
      // 1. annotation has highest precedence
      annotation.type
    } else if (_type.classifier is KClass<*>) {
      // 2. try to determine from param itself
      _type.classifier as KClass<*>
    } else {
      // 3. should not occur but better safe than sorrow ;)
      Any::class
    }
  }

  private fun determineDefault(): String? {
    return if (KwdArg.NULL_STRING == annotation.default) {
      null
    } else {
      annotation.default
    }
  }
}