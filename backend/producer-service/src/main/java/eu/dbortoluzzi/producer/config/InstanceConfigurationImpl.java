package eu.dbortoluzzi.producer.config;

import eu.dbortoluzzi.commons.model.RoutingElement;
import eu.dbortoluzzi.producer.ConsumerRoutingTable;
import eu.dbortoluzzi.producer.ProducerRoutingTable;
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

    @Value("${producer.polling.folder}")
    private String folderPath;

    @SneakyThrows
    public String getInstanceName() {
        return findRoutingElement().getName();
    }

    @Autowired
    ProducerRoutingTable producerRoutingTable;

    @Autowired
    ConsumerRoutingTable consumerRoutingTable;

    public String getPollingPath() {
        return folderPath + "/instance_"+getInstanceName();
    }

    @Bean
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return new RestTemplate(factory);
    }

    @Override
    public List<RoutingElement> otherProducers() {
        RoutingElement routingElement = findRoutingElement();
        return producerRoutingTable.getProducerRoutingTable()
                .stream()
                .filter(e -> !e.getName().equals(routingElement.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoutingElement> consumers() {
        return consumerRoutingTable.getConsumerRoutingTable();
    }

    @SneakyThrows
    public RoutingElement findRoutingElement() {
        InetAddress localHost = InetAddress.getLocalHost();
        return producerRoutingTable.getProducerRoutingTable()
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
