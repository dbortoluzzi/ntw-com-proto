package eu.dbortoluzzi.consumer.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@Configuration
public class InstanceConfigurationImpl implements InstanceConfiguration{
    @Value("${consumers.url.fragment}")
    private String consumersFragmentsUrl;

    @Value("${consumers.port.fragment}")
    private String consumersPortFragment;

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
        InetAddress localHost = InetAddress.getLocalHost();
        return localHost.getHostName();
    }

    @Bean
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return new RestTemplate(factory);
    }
}
