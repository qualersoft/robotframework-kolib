package de.qualersoft.robotframework.library.example.keywords.tdo

import de.qualersoft.robotframework.library.example.impl.tdo.CountryTDO
import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.annotation.KwdArg
import org.assertj.core.api.Assertions.assertThat
import java.lang.IllegalArgumentException
import java.util.Locale
import javax.annotation.ManagedBean

@ManagedBean
class Countries {

  @Keyword(
    docSummary = ["Creates a default country for a given country code."],
    docDetails = ["""
      Actually the only supported countryCode is `GER`.
      
      The case of given country code does not matter."""]
  )
  fun createDefaultCountry(
    @KwdArg("""
      The code for which to get a default country object.
      Return NOOP if `None`. Defaults to `None`
    """,
      default = "None"
    )
    countryCode: String? = null
  ) = countryCode?.let {
    when (it.uppercase(Locale.getDefault())) {
      "GER" -> CountryTDO.DEFAULT_DE
      else -> throw IllegalArgumentException("No default for country code '$it' present!")
    }
  } ?: CountryTDO.NOOP

  @Keyword
  fun assertCountryListContains(list: List<CountryTDO>, country: CountryTDO) {
    assertThat(list).describedAs("Checking that $list contains $country")
      .contains(country)
  }
}
