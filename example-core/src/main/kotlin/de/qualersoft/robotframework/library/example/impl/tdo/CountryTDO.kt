package de.qualersoft.robotframework.library.example.impl.tdo

/**
 * Test data object to work with rest-countries api
 */
data class CountryTDO(
  val name: String?,
  val region: String?,
  val alpha3Code: String?,
  val callingCodes: List<String>?,
  val officialName: String?,
  val borders: List<String>?,
  val currencies: List<CurrencyTDO>?
) {

  data class Builder(
    var name: String? = null,
    var region: String? = null,
    var alpha3Code: String? = null,
    var callingCodes: List<String>? = null,
    var officialName: String? = null,
    var borders: List<String>? = null,
    var currencies: List<CurrencyTDO>? = null
  ) {

    constructor(tdo: CountryTDO) : this(
      tdo.name,
      tdo.region,
      tdo.alpha3Code,
      tdo.callingCodes?.toMutableList(),
      tdo.officialName,
      tdo.borders?.toMutableList(),
      tdo.currencies
    )

    fun name(name: String?) = apply { this.name = name }
    fun region(region: String?) = apply { this.region = region }
    fun alpha3Code(alpha3Code: String?) = apply { this.alpha3Code = alpha3Code }
    fun callingCodes(callingCodes: List<String>?) = apply { this.callingCodes = callingCodes?.toMutableList() }
    fun officialName(officialName: String?) = apply { this.officialName = officialName }
    fun borders(borders: List<String>?) = apply { this.borders = borders?.toMutableList() }
    fun currencies(currency: List<CurrencyTDO>?) = apply { this.currencies = currency?.toMutableList() }

    fun build() = CountryTDO(
      name, region, alpha3Code, callingCodes?.toList(), officialName, borders?.toList(), currencies?.toList()
    )
  }

  companion object {
    val DEFAULT_DE = Builder(
      "Germany", "Europe", "DEU", listOf("49"), "Federal Republic of Germany",
      listOf("AUT", "BEL", "CZE", "DNK", "FRA", "LUX", "NLD", "POL", "CHE"),
      listOf(CurrencyTDO.EURO)
    ).build()

    val NOOP = Builder().build()
  }
}

data class CurrencyTDO(
  val code: String?,
  val name: String?,
  val symbol: String?
) {

  data class Builder(
    var code: String? = null,
    var name: String? = null,
    var symbol: String? = null
  ) {

    constructor(tdo: CurrencyTDO) : this(tdo.code, tdo.name, tdo.symbol)

    fun code(code: String?) = apply { this.code = code }
    fun name(name: String?) = apply { this.name = name }
    fun symbol(symbol: String?) = apply { this.symbol = symbol }

    fun build() = CurrencyTDO(code, name, symbol)
  }

  companion object {
    val EURO = Builder("EUR", "Euro", "€").build()
    val NOOP = Builder()
  }
}
