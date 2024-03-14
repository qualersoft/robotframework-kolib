package de.qualersoft.robotframework.jdummy.keywords;

import de.qualersoft.robotframework.library.annotation.Keyword;
import org.springframework.stereotype.Component;

@Component
public class JDummyKeyword {

  @Keyword
  public String getSimpleJ() {
    return "Simple";
  }
}
