package eu.dbortoluzzi.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.model.Metadata;
import eu.dbortoluzzi.commons.model.Payload;
import eu.dbortoluzzi.commons.utils.FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.MD5FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

    private final FragmentValidationStrategy fragmentValidationStrategy;

    public ProducerFragmentService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.fragmentValidationStrategy = new MD5FragmentValidationStrategy();
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
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
    public Fragment createFragment(Integer index, Integer total, String instance, byte[] text) {
        String textEncoded = StringUtils.encodeHexString(text);
        return Fragment.builder()
                .index(index)
                .total(total)
                .timestamp(new Date())
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
        String result = restTemplate.postForObject(fragmentsUrl.concat("/CHECKSUM"), StringUtils.encodeHexString(toJsonString(fragment).getBytes(StandardCharsets.UTF_8)), String.class);
        log.info("RESULT CONSUMER: {}", result);
        return true;
    }

}
