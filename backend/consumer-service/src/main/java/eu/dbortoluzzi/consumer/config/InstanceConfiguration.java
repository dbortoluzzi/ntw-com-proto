package eu.dbortoluzzi.consumer.config;

import eu.dbortoluzzi.consumer.model.RoutingElement;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public interface InstanceConfiguration {

    public String getConsumersFragmentUrl();

    public String getConsumersPort();

    public String getInstanceName();

    public RestTemplate restTemplate();

    public List<RoutingElement> otherConsumers();
}
