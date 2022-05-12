package de.qualersoft.robotframework.library.example.keywords.tdo;

import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO;
import de.qualersoft.robotframework.library.annotation.Keyword;

import java.lang.IllegalArgumentException;
import java.util.List;
import java.util.Locale;
import javax.annotation.ManagedBean;

import static org.assertj.core.api.Assertions.assertThat;

@ManagedBean
public class Countries {

  @Keyword
  public CountryTDO createDefaultCountry(String countryCode) {
    return switch (countryCode.toUpperCase(Locale.getDefault())) {
      case "GER" -> CountryTDO.Companion.getDEFAULT_DE();
      default ->
          throw new IllegalArgumentException(String.format("No default for country code '%s' present!", countryCode));
    };
  }

  @Keyword
  public void assertCountryListContains(List<CountryTDO> list, CountryTDO country) {
    assertThat(list).describedAs("Checking that $list contains $country").contains(country);
  }
}
