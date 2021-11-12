package de.qualersoft.robotframework.library

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.context.TypeExcludeFilter
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootConfiguration
@ComponentScan(
  excludeFilters = [
    ComponentScan.Filter(
      type = FilterType.CUSTOM,
      classes = [TypeExcludeFilter::class]
    )
  ]
)
annotation class SpringLibMarker
