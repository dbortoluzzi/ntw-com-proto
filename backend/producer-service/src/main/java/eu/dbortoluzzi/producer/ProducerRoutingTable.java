package eu.dbortoluzzi.producer;

import eu.dbortoluzzi.commons.model.RoutingElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ProducerRoutingTable {

    private List<RoutingElement> producerRoutingTable;

}
