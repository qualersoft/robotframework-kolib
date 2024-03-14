package de.qualersoft.robotframework.dummypack.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("dummy")
data class AppConfig(val value: String)
