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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ProducerFragmentService {

    public static final int TIMEOUT_SEND_TO_CONSUMERS = 30000; //TODO: add configuration
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
    public Fragment createFragment(Long index, Long total, String instance, String filename, Date timestamp, byte[] text) {
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

    @Async
    public CompletableFuture<Boolean> sendToConsumerParallel(Fragment fragment) {
        return CompletableFuture.supplyAsync(() -> sendToConsumer(fragment));
    }

    public boolean sendToConsumer(Fragment fragment) {
        int i = 0;
        boolean done = false;
        long start = new Date().getTime();
        while (!done && new Date().getTime() < start + TIMEOUT_SEND_TO_CONSUMERS) {
            RoutingElement routingElement = instanceConfiguration.consumers().get(i);
            String url = "";
            try {
                String postData = StringUtils.encodeHexString(toJsonString(fragment).getBytes(StandardCharsets.UTF_8));
                url = prepareConsumerFragmentUrl(routingElement, StringUtils.md5sum(postData));
                String result = restTemplate.postForObject(url, postData, String.class);
                log.info("RESULT CONSUMER for {} : {}", routingElement.getName(), result);
                if ("OK".equals(result)) {
                    done = true;
                } else {
                    throw new IllegalStateException("Error consumer " + routingElement.getName());
                }
            } catch (Exception e) {
                log.info("RESULT CONSUMER KO for {} and url {}", routingElement.getName(), url, e);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
            i = (i + 1) % instanceConfiguration.consumers().size();
        }
        return done;
    }

    public void addElaborationToCompletableFutures(File file, Date timestamp, byte[] buffer, long totalFragment, long counter, List<CompletableFuture<Boolean>> completableFutureList) {
        Fragment fragment = createFragment(counter, totalFragment, instanceConfiguration.getInstanceName(), file.getName(), timestamp, buffer);
        CompletableFuture<Boolean> completableFuture = sendToConsumerParallel(fragment);
        completableFutureList.add(completableFuture);
    }

    private String prepareConsumerFragmentUrl(RoutingElement routingElement, String checksum) {
        String url;
        url = MessageFormat.format("http://{0}:{1}/{2}/{3}",
                routingElement.getName(),
                fragmentsPort,
                fragmentsUrl,
                checksum);
        return url;
    }

}
