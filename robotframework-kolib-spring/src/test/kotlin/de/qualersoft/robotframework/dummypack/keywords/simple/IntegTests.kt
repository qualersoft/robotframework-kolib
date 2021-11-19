package de.qualersoft.robotframework.dummypack.keywords.simple

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.annotation.KwdArg
import de.qualersoft.robotframework.library.model.ParameterKind
import javax.annotation.ManagedBean

@ManagedBean
class IntegTests {

  @Keyword
  fun plainArg(name: String): String {
    return "Got name $name"
  }

  @Keyword
  fun noKwargKeyword(kwarg: Map<String, Any?>): String {
    return "$kwarg"
  }

  @Keyword
  fun dynamic(
    @KwdArg(kind = ParameterKind.VARARG) args: List<String?> = listOf(),
    @KwdArg(kind = ParameterKind.KWARG) kwargs: Map<String, Any?> = mapOf()
  ): String {
    val posArgs = args.filterNotNull()
    return "$posArgs, $kwargs"
  }

  @Keyword
  fun fullKeyword(
    pos1: String,
    pos2: String = "default",
    @KwdArg(kind = ParameterKind.VARARG) args: List<String?> = listOf(),
    @KwdArg(kind = ParameterKind.KWARG) kwargs: Map<String, Any?> = mapOf()
  ): String {
    val posArgs = "($pos1, $pos2)"
    val varArgs = args.filterNotNull()
    return "$posArgs, $varArgs, $kwargs"
  }
}
