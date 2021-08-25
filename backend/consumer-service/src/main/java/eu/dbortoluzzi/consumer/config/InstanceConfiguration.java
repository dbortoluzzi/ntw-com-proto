package eu.dbortoluzzi.consumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public interface InstanceConfiguration {

    public String getConsumersFragmentUrl();

    public String getConsumersPort();

    public String getInstanceName();

    public RestTemplate restTemplate();
}
