package de.qualersoft.robotframework.library.example.keywords.ws;

import de.qualersoft.robotframework.library.annotation.KwdArg;
import de.qualersoft.robotframework.library.example.impl.ws.BaseWebservice;
import de.qualersoft.robotframework.library.annotation.Keyword;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.ManagedBean;

@ManagedBean
public class WsCommons {

  @Keyword(
      docSummary = "Assert that a webservice request completed successfully.",
      docDetails = """
          *Remark:* Ensure that the webservice object was send!
          
          You can use any object that inherits from BaseWebservice."""
  )
  public void assertRequestSucceed(
      @KwdArg(doc =
        """
         The webservice to check.
          - WebService must have been sent
          - Don't know"""
      )
      BaseWebservice ws
  ) {
    assertThat(ws.isResponseOk())
        .withFailMessage(() ->
            String.format("Expected request to succeed but got status code %s", ws.getResponseStatus())
        )
        .isTrue();
  }
}
