package de.qualersoft.robotframework.library.subpackage

import de.qualersoft.robotframework.library.annotation.Keyword
import javax.annotation.ManagedBean

@ManagedBean
class SubkeywordClass {

  @Keyword
  fun subpackageFunction() {}
}