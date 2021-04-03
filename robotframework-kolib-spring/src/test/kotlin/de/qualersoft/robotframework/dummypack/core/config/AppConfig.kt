package de.qualersoft.robotframework.dummypack.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("dummy")
@ConstructorBinding
data class AppConfig(val value: String)