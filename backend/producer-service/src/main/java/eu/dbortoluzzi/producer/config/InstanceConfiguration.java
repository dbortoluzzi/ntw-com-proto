package eu.dbortoluzzi.producer.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public interface InstanceConfiguration {

    public String getInstanceName();

    public String getPollingPath();
}
