package eu.dbortoluzzi.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.model.Metadata;
import eu.dbortoluzzi.commons.model.Payload;
import eu.dbortoluzzi.commons.model.RoutingElement;
import eu.dbortoluzzi.commons.utils.FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.MD5FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.StringUtils;
import eu.dbortoluzzi.producer.config.InstanceConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

@Service
@Slf4j
public class ProducerFragmentService {

    private final ObjectMapper objectMapper;

    final RestTemplate restTemplate;

    @Value("${producer.fragments.url}")
    String fragmentsUrl;

    @Value("${producer.fragments.port}")
    String fragmentsPort;

    final InstanceConfiguration instanceConfiguration;

    private final FragmentValidationStrategy fragmentValidationStrategy;

    public ProducerFragmentService(ObjectMapper objectMapper, RestTemplate restTemplate, InstanceConfiguration instanceConfiguration) {
        this.fragmentValidationStrategy = new MD5FragmentValidationStrategy();
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.instanceConfiguration = instanceConfiguration;
    }

    public boolean isValidFragment(Fragment fragment) {
        return fragmentValidationStrategy.isValid(fragment);
    }

    public byte[] decodeFragment(Fragment fragment) {
        return fragmentValidationStrategy.decodeFragment(fragment);
    }

    @SneakyThrows
    public String toJsonString(Fragment fragment) {
        return objectMapper.writeValueAsString(fragment);
    }

    @SneakyThrows
    public Fragment createFragment(Integer index, Integer total, String instance, String filename, Date timestamp, byte[] text) {
        String textEncoded = StringUtils.encodeHexString(text);
        return Fragment.builder()
                .filename(filename)
                .index(index)
                .total(total)
                .timestamp(timestamp)
                .payload(
                        Payload.builder()
                                .text(textEncoded)
                                .metadata(
                                        Metadata.builder()
                                                .instance(instance)
                                                .checksum(StringUtils.encodeHexString(MessageDigest.getInstance("MD5").digest(textEncoded.getBytes(StandardCharsets.UTF_8))))
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    public boolean sendToConsumer(Fragment fragment) {
        for (RoutingElement routingElement: instanceConfiguration.consumers()){
            String url = "";
            try {
                url = new StringBuilder()
                                .append("http://")
                                .append(routingElement.getName())
                                .append(":")
                                .append(fragmentsPort)
                                .append("/")
                                .append(fragmentsUrl).toString();
                String result = restTemplate.postForObject(url.concat("/CHECKSUM"), StringUtils.encodeHexString(toJsonString(fragment).getBytes(StandardCharsets.UTF_8)), String.class);
                log.info("RESULT CONSUMER for {} : {}", routingElement.getName(), result);
                if ("OK".equals(result)) {
                    return true;
                } else {
                    throw new IllegalStateException("Error consumer " + routingElement.getName());
                }
            }catch (Exception e) {
                log.info("RESULT CONSUMER KO for {} and url {}", routingElement.getName(), url, e);
            }
        }
        return false;
    }

}
