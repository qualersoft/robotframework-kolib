package de.qualersoft.robotframework.library.example.impl.ws.restcountries

import de.qualersoft.robotframework.library.example.impl.config.CountriesApiProperties
import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO
import java.util.HashMap

class WsCountriesByCurrency(countryProps: CountriesApiProperties) : WsRestCountriesBase(countryProps) {

  var currency: String? = null

  fun setCurrency(country: CountryTDO) {
    currency = country.currencies?.firstOrNull()?.code
  }

  override fun getRequestUrl(): String = "${baseUrl}/currency/{region}"

  override fun getPathParameters(): Map<String, String> = HashMap<String,String>().apply {
    currency?.let { this["region"] = it }
  }
}
