package de.qualersoft.robotframework.library.example.impl.ws.restcountries

import de.qualersoft.robotframework.library.example.impl.config.CountriesApiProperties
import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO
import java.util.HashMap

class WsCountriesByName(countryProps: CountriesApiProperties) : WsRestCountriesBase(countryProps) {

  var name: String? = null

  fun setName(country: CountryTDO) {
    name = country.name
  }

  override fun getRequestUrl(): String = "${baseUrl}/name/{name}"

  override fun getPathParameters(): Map<String, String> = HashMap<String,String>().apply {
    name?.let { this["name"] = it }
  }
}
