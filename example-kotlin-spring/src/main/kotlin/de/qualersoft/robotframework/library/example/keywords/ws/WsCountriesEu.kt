package de.qualersoft.robotframework.library.example.keywords.ws

import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsCountriesByCurrency
import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsCountriesByName
import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsCountriesByRegion
import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsRestCountriesBase
import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.example.impl.config.CountriesApiProperties
import jakarta.inject.Named

@Named
open class WsCountriesEu(private val countryProps: CountriesApiProperties) {

  @Keyword
  fun getCountriesByName(name: String) = WsCountriesByName(countryProps).also {
    it.name = name
    it.send()
  }

  @Keyword
  fun getCountriesByRegion(region: String) = WsCountriesByRegion(countryProps).also {
    it.region = region
    it.send()
  }

  @Keyword
  fun getCountriesByCurrency(currencyCode: String) = WsCountriesByCurrency(countryProps).also {
    it.currency = currencyCode
    it.send()
  }

  @Keyword
  fun getCountriesFromResponse(wsCountry: WsRestCountriesBase) = wsCountry.getResponseCountries()
}
