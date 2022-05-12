package de.qualersoft.robotframework.library.example.impl.ws.restcountries

import de.qualersoft.robotframework.library.example.impl.config.CountriesApiProperties
import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO
import java.util.HashMap

class WsCountriesByRegion(countryProps: CountriesApiProperties) : WsRestCountriesBase(countryProps) {

  var region: String? = null

  fun setRegion(country: CountryTDO) {
    region = country.region
  }

  override fun getRequestUrl(): String = "${baseUrl}/region/{region}"

  override fun getPathParameters(): Map<String, String> = HashMap<String,String>().apply {
    region?.let { this["region"] = it }
  }
}
