package de.qualersoft.robotframework.library.example.keywords.ws;

import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO;
import de.qualersoft.robotframework.library.example.impl.ws.BaseWebservice;
import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsCountriesByCurrency;
import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsCountriesByName;
import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsCountriesByRegion;
import de.qualersoft.robotframework.library.example.impl.ws.restcountries.WsRestCountriesBase;
import de.qualersoft.robotframework.library.annotation.Keyword;
import de.qualersoft.robotframework.library.example.impl.config.CountriesApiProperties;

import jakarta.inject.Named;

import java.util.List;

@Named
public class WsCountriesEu {

  private final CountriesApiProperties countryProps;

  public WsCountriesEu(CountriesApiProperties countryProps) {
    this.countryProps = countryProps;
  }

  @Keyword
  public BaseWebservice getCountriesByName(String name) {
    var result = new WsCountriesByName(countryProps);
    result.setName(name);
    result.send();
    return result;
  }

  @Keyword
  public BaseWebservice getCountriesByRegion(String region) {
    var result = new WsCountriesByRegion(countryProps);
    result.setRegion(region);
    result.send();
    return result;
  }

  @Keyword
  public BaseWebservice getCountriesByCurrency(String currencyCode) {
    var result = new WsCountriesByCurrency(countryProps);
    result.setCurrency(currencyCode);
    result.send();
    return result;
  }

  @Keyword
  public List<CountryTDO> getCountriesFromResponse(WsRestCountriesBase wsCountry) {
    return wsCountry.getResponseCountries();
  }
}
