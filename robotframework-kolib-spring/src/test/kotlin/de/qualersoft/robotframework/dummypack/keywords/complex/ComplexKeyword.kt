package de.qualersoft.robotframework.dummypack.keywords.complex

import de.qualersoft.robotframework.dummypack.core.config.AppConfig
import de.qualersoft.robotframework.library.annotation.Keyword
import javax.annotation.ManagedBean

@ManagedBean
class ComplexKeyword(val cfg: AppConfig) {

  @Keyword
  fun getConfigDummy() = "Hello from ${cfg.value}"
}
