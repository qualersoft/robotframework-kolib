package de.qualersoft.robotframework.library.example.keywords.functions

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.annotation.KwdArg
import de.qualersoft.robotframework.library.model.ParameterKind
import jakarta.inject.Named

@Named
open class FunctionKwds {

  @Keyword
  fun fnCallWithVarargs(
    posArg: String,
    @KwdArg(kind = ParameterKind.VARARG)
    varargs: List<String>
  ) {
    println("Mandatory posArg: $posArg")

    if (varargs.isEmpty()) {
      println("No varargs passed.")
    } else {
      println("Got varargs: $varargs")
    }
  }

  @Keyword
  fun fnCallWithKwargs(
    posArg: String,
    @KwdArg(kind = ParameterKind.KWARG)
    kwargs: Map<String, String>
  ) {
    println("Mandatory posArg: $posArg")

    if (kwargs.isEmpty()) {
      println("No kwargs passed.")
    } else {
      println("Got kwargs: $kwargs")
    }
  }

  @Keyword
  fun fnCallWithVarAndKwArgs(
    posArg: String,
    @KwdArg(kind = ParameterKind.VARARG)
    varargs: List<String>,
    @KwdArg(kind = ParameterKind.KWARG)
    kwargs: Map<String, String>
  ) {
    println("Mandatory posArg: $posArg")

    if (varargs.isEmpty()) {
      println("No varargs passed.")
    } else {
      println("Got varargs: $varargs")
    }

    if (kwargs.isEmpty()) {
      println("No kwargs passed.")
    } else {
      println("Got kwargs: $kwargs")
    }
  }
}
