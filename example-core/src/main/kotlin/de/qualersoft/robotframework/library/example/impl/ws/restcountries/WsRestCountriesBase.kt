package de.qualersoft.robotframework.library.example.impl.ws.restcountries

import de.qualersoft.robotframework.library.example.impl.config.CountriesApiProperties
import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO
import de.qualersoft.robotframework.library.example.impl.tdo.CurrencyTDO
import de.qualersoft.robotframework.library.example.impl.ws.BaseWebservice
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.http.HttpMethod

abstract class WsRestCountriesBase(countryProps: CountriesApiProperties) : BaseWebservice() {

  protected val baseUrl = countryProps.baseEndpoint()

  override fun getRequestMethod(): HttpMethod = HttpMethod.GET

  fun getResponseCountries(): List<CountryTDO>? {
    // yes `true ==` because it can be null
    return if (true == isResponseOk()) {
      val body = response!!.body
      val mainJson = JSONArray(body)
      mainJson.map { parseCountryEntry(it as JSONObject) }
    } else null
  }

  private fun parseCountryEntry(json: JSONObject) = CountryTDO.Builder().apply {
    val joName = json.optJSONObject(NAME_KEY)
    name(joName.optString(COMMON_NAME_KEY))
    officialName(joName.optString(OFFICIAL_NAME_KEY))
    region(json.optString(REGION_KEY))
    alpha3Code(json.optString(ALPHA3CODE_KEY))
    callingCodes(retrieveCallingCodes(json.optJSONObject(CALLING_CODES_KEY)))
    borders(json.optJSONArray(BORDERS_KEY)?.let { entry ->
      entry.map { it as String }
    })
    currencies(json.optJSONObject(CURRENCIES_KEY)?.let { entry ->
      entry.keySet().map { parseCurrency(it, entry.getJSONObject(it)) }
    })
  }.build()

  private fun retrieveCallingCodes(json: JSONObject): List<String> {
    // get rid of '+' or leading zeros
    val root = json.optString(CALLING_CODES_ROOT_KEY).toInt().toString()
    val suffixes = json.optJSONArray(CALLING_CODES_SUFFIX_KEY)
    return (0 until suffixes.length()).map {
      root + suffixes.getString(it)
    }
  }

  private fun parseCurrency(key: String, json: JSONObject) = CurrencyTDO.Builder()
    .code(key)
    .name(json.optString(CUR_NAME_KEY))
    .symbol(json.optString(CUR_SYMBOL_KEY))
    .build()

  companion object {
    const val NAME_KEY = "name"
    const val COMMON_NAME_KEY = "common"
    const val REGION_KEY = "region"
    const val ALPHA3CODE_KEY = "cca3"

    const val CALLING_CODES_KEY = "idd"
    const val CALLING_CODES_ROOT_KEY = "root"
    const val CALLING_CODES_SUFFIX_KEY = "suffixes"

    const val OFFICIAL_NAME_KEY = "official"
    const val BORDERS_KEY = "borders"
    const val CURRENCIES_KEY = "currencies"

    const val CUR_NAME_KEY = "name"
    const val CUR_SYMBOL_KEY = "symbol"
  }
}
