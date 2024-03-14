package de.qualersoft.robotframework.jdummy.keywords;

import de.qualersoft.robotframework.jdummy.core.config.JAppConfig;
import de.qualersoft.robotframework.library.annotation.Keyword;
import jakarta.inject.Named;

@Named
public class JComplexDummyKeyword {

  private final JAppConfig cfg;
  public JComplexDummyKeyword(final JAppConfig cfg) {
    this.cfg = cfg;
  }

  @Keyword
  public String getConfigDummyJ() {
    return "Hello from %s".formatted(cfg.getValue());
  }
}
