package de.qualersoft.robotframework.library.example.impl.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("countries-api")
data class CountriesApiProperties @ConstructorBinding constructor(val baseUrl: String, val version: String = "v3") {
  fun baseEndpoint(): String {
    return (if (!baseUrl.endsWith("/")) "$baseUrl/"
    else baseUrl) + "$version/"
  }
}
