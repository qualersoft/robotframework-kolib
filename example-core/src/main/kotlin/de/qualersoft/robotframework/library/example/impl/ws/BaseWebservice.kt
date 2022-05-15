package de.qualersoft.robotframework.library.example.impl.ws

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

abstract class BaseWebservice {

  private val template = RestTemplate()
  protected var response: ResponseEntity<String>? = null
    private set

  /**
   * Gets the url the request shall be send to.
   *
   * The url may contain path-parameter pattern in the form `{key}` but must not contain query-parameters!
   *
   * @see getPathParameters
   * @see getQueryParameters
   */
  abstract fun getRequestUrl(): String

  /**
   * Gets the methode used for sending the request.
   *
   * @see HttpMethod
   */
  abstract fun getRequestMethod(): HttpMethod

  /**
   * Gets the path parameter.
   *
   * Multiple occurrences of the same key will properly handles.
   * If returns `null`, no replacement takes place and potential placeholders will be kept as is!
   *
   * Defaults to `null`.
   */
  open fun getPathParameters(): Map<String, String>? = null

  /**
   * Gets the queryparameter.
   *
   * Defaults to `null`.
   */
  open fun getQueryParameters(): MultiValueMap<String, String>? = null

  /**
   * Gets the requests body.
   *
   * Defaults to `null`.
   */
  open fun getRequestBody(): String? = null

  fun send() {
    val url = buildRequestUrl()
    val method = getRequestMethod()
    val body = getRequestBody()
    val request = HttpEntity<String?>(body)
    response = template.exchange(url, method, request, String::class.java)
  }

  /**
   * `True` if the response succeed (2xx-status code), else `false`.
   *
   * **Attention**: May return `null` if no response exists!
   */
  fun isResponseOk(): Boolean? = response?.statusCode?.is2xxSuccessful

  /**
   * Gets the status code of the response.
   *
   * **Attention**: May return `null` if no response exists!
   */
  fun getResponseStatus(): Int? = response?.statusCode?.value()

  private fun buildRequestUrl(): URI {
    val builder = UriComponentsBuilder.fromUriString(getRequestUrl())
      .also {
        getQueryParameters()?.forEach { (k, v) -> it.queryParam(k, v) }
      }
    var result = builder.build()
    val pathParams = getPathParameters()
    if (null != pathParams) {
      result = result.expand(pathParams)
    }
    return result.toUri()
  }
}
