package eu.dbortoluzzi.producer.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@Configuration
public class InstanceConfigurationImpl implements InstanceConfiguration{

    @Value("${producer.polling.folder}")
    private String folderPath;

    @SneakyThrows
    public String getInstanceName() {
        InetAddress localHost = InetAddress.getLocalHost();
        return "instance_" + localHost.getHostName();
    }

    public String getPollingPath() {
        return folderPath + "/"+getInstanceName();
    }

    @Bean
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return new RestTemplate(factory);
    }
}
