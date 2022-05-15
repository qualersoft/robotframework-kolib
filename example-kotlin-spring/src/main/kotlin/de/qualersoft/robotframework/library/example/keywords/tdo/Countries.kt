package de.qualersoft.robotframework.library.example.keywords.tdo

import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO
import de.qualersoft.robotframework.library.annotation.Keyword
import org.assertj.core.api.Assertions.assertThat
import java.lang.IllegalArgumentException
import java.util.Locale
import javax.annotation.ManagedBean

@ManagedBean
class Countries {

  @Keyword
  fun createDefaultCountry(countryCode: String) = when (countryCode.uppercase(Locale.getDefault())) {
    "GER" -> CountryTDO.DEFAULT_DE
    else -> throw IllegalArgumentException("No default for country code '$countryCode' present!")
  }

  @Keyword
  fun assertCountryListContains(list: List<CountryTDO>, country: CountryTDO) {
    assertThat(list).describedAs("Checking that $list contains $country")
      .contains(country)
  }
}
