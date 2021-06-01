package de.qualersoft.robotframework.dummypack.keywords.simple

import de.qualersoft.robotframework.library.annotation.Keyword
import javax.annotation.ManagedBean

@ManagedBean
class IntegTests {
  
  @Keyword
  fun plainArg(name: String) {
    println("Got name $name")
  }
}