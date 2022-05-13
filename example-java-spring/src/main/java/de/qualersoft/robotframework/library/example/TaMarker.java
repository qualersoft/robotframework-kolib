package de.qualersoft.robotframework.library.example;

import de.qualersoft.robotframework.library.SpringLibMarker;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@ConfigurationPropertiesScan
@SpringLibMarker
public interface TaMarker {
  
}
