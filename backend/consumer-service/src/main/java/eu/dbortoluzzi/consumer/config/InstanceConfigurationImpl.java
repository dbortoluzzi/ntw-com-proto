package eu.dbortoluzzi.consumer.config;

import eu.dbortoluzzi.consumer.ConsumerRoutingTable;
import eu.dbortoluzzi.consumer.model.RoutingElement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class InstanceConfigurationImpl implements InstanceConfiguration{
    @Value("${consumers.url.fragment}")
    private String consumersFragmentsUrl;

    @Value("${consumers.port.fragment}")
    private String consumersPortFragment;

    @Autowired
    ConsumerRoutingTable consumerRoutingTable;

    @Override
    public String getConsumersFragmentUrl() {
        return consumersFragmentsUrl;
    }

    @Override
    public String getConsumersPort() {
        return consumersPortFragment;
    }

    @SneakyThrows
    public String getInstanceName() {
        return findRoutingElement().getName();
    }

    @Bean
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return new RestTemplate(factory);
    }

    @SneakyThrows
    public List<RoutingElement> otherConsumers() {
        RoutingElement routingElement = findRoutingElement();
        return consumerRoutingTable.getRoutingTable()
                .stream()
                .filter(e -> !e.getName().equals(routingElement.getName()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public RoutingElement findRoutingElement() {
        InetAddress localHost = InetAddress.getLocalHost();
        return consumerRoutingTable.getRoutingTable()
                .stream()
                .filter(e -> {
                    try {
                        InetAddress searchContainerAddress = InetAddress.getByName(e.getName());
                        return searchContainerAddress.getHostAddress().equals(localHost.getHostAddress());
                    } catch (Exception exception) {
                        log.error("findRoutingElement error {}", e);
                        return false;
                    }
                })
                .findFirst().orElseThrow();
    }
}
