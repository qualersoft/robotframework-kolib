package de.qualersoft.robotframework.library.example.keywords.ws

import de.qualersoft.robotframework.library.example.impl.ws.BaseWebservice
import de.qualersoft.robotframework.library.annotation.Keyword
import org.assertj.core.api.Assertions.assertThat
import javax.annotation.ManagedBean

@ManagedBean
class WsCommons {

  @Keyword(
    docSummary = ["Assert that a webservice request completed successfully."]
  )
  fun assertRequestSucceed(ws: BaseWebservice) {
    assertThat(ws.isResponseOk())
      .withFailMessage { "Expected request to succeed but got status code ${ws.getResponseStatus()}" }
      .isTrue()
  }
}
