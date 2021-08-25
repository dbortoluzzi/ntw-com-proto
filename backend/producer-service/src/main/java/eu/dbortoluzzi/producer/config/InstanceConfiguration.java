package eu.dbortoluzzi.producer.config;

import eu.dbortoluzzi.commons.model.RoutingElement;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public interface InstanceConfiguration {

    public String getInstanceName();

    public String getPollingPath();

    public RestTemplate restTemplate();

    public List<RoutingElement> otherProducers();

    public List<RoutingElement> consumers();
}
