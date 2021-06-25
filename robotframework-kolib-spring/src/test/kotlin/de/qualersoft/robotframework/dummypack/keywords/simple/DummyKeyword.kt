package de.qualersoft.robotframework.dummypack.keywords.simple

import de.qualersoft.robotframework.library.annotation.Keyword
import org.springframework.stereotype.Component

@Component
open class DummyKeyword {

  @Keyword
  fun getSimple(): String = "Simple"
}
