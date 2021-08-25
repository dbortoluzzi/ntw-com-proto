package eu.dbortoluzzi.consumer;

import eu.dbortoluzzi.consumer.model.RoutingElement;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ConsumerRoutingTable {

    private List<RoutingElement> routingTable;

}
