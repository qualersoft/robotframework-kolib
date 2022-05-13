package de.qualersoft.robotframework.library.example.keywords.ws;

import de.qualersoft.robotframework.library.example.impl.ws.BaseWebservice;
import de.qualersoft.robotframework.library.annotation.Keyword;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.ManagedBean;

@ManagedBean
public class WsCommons {

  @Keyword(
      docSummary = "Assert that a webservice request completed successfully."
  )
  public void assertRequestSucceed(BaseWebservice ws) {
    assertThat(ws.isResponseOk())
        .withFailMessage(() ->
            String.format("Expected request to succeed but got status code %s", ws.getResponseStatus())
        )
        .isTrue();
  }
}
