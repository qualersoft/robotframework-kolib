package de.qualersoft.robotframework.functioncall.keywords

import de.qualersoft.robotframework.library.annotation.Keyword
import javax.annotation.ManagedBean

@ManagedBean
class KeywordSource {

  @Keyword
  fun plainArg(name: String) {
    log("Got name $name")
  }

  @Keyword
  fun morePlainArgs(firstName: String, lastName: String) {
    log("Got firstname $firstName, lastname $lastName")
  }

  @Keyword
  fun withVarargs(vararg names: String) {
    log("Got names $names")
  }

  @Keyword
  fun withVarargsAndNormal(age: Int, vararg names: String) {
    log("Got an age of $age for names $names")
  }

  private fun log(message: String, vararg args:Any) {
    System.out.printf(message, *args)
  }
}