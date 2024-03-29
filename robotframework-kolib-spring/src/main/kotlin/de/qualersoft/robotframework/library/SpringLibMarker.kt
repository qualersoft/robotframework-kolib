package de.qualersoft.robotframework.library

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties
@ConfigurationPropertiesScan
@ComponentScan
annotation class SpringLibMarker
