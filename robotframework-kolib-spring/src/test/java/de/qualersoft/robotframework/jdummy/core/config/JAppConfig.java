package de.qualersoft.robotframework.jdummy.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dummy")
public class JAppConfig {
  private final String value;

  public JAppConfig(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
