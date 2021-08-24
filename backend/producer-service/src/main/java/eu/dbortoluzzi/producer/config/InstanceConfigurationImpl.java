package eu.dbortoluzzi.producer.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class InstanceConfigurationImpl implements InstanceConfiguration{

    @Value("${producer.consumer.folder}")
    private String folderPath;

    @SneakyThrows
    public String getInstanceName() {
        InetAddress localHost = InetAddress.getLocalHost();
        return "instance_" + localHost.getHostName();
    }

    public String getPollingPath() {
        return folderPath + "/"+getInstanceName();
    }
}
