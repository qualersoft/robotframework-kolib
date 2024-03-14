package de.qualersoft.robotframework.library.example.keywords.ws

import de.qualersoft.robotframework.library.example.impl.ws.BaseWebservice
import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.annotation.KwdArg
import org.assertj.core.api.Assertions.assertThat
import jakarta.inject.Named

@Named
open class WsCommons {

  @Keyword(
    docSummary = ["Assert that a webservice request completed successfully."],
    docDetails = [
      """Remark: Ensure that the webservice object was send!

      You can use any object that inherits from BaseWebservice.""",
    ]
  )
  fun assertRequestSucceed(
    @KwdArg("""
The webservice to check.
  - WebService must have been sent
  - Don't know
"""
    )
    ws: BaseWebservice
  ) {
    assertThat(ws.isResponseOk())
      .withFailMessage { "Expected request to succeed but got status code ${ws.getResponseStatus()}" }
      .isTrue()
  }
}
