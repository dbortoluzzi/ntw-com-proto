package eu.dbortoluzzi.producer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public interface InstanceConfiguration {

    public String getInstanceName();

    public String getPollingPath();

    public RestTemplate restTemplate();
}
