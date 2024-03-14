package de.qualersoft.robotframework.library.subpackage

import de.qualersoft.robotframework.library.annotation.Keyword
import jakarta.inject.Named

@Named
open class SubkeywordClass {

  @Keyword
  fun subpackageFunction() {}
}
