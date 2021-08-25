package eu.dbortoluzzi.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.utils.FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.MD5FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.StringUtils;
import eu.dbortoluzzi.consumer.ConsumerRoutingTable;
import eu.dbortoluzzi.consumer.config.InstanceConfiguration;
import eu.dbortoluzzi.consumer.model.MongoFragment;
import eu.dbortoluzzi.consumer.model.RoutingElement;
import eu.dbortoluzzi.consumer.model.Sync;
import eu.dbortoluzzi.consumer.repository.FragmentRepository;
import eu.dbortoluzzi.consumer.repository.SyncRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConsumerSyncService {

    public static final String SYNC_ID = "id";
    private final ObjectMapper objectMapper;
    final FragmentRepository fragmentRepository;
    private final FragmentValidationStrategy fragmentValidationStrategy;
    final ConsumerRoutingTable consumerRoutingTable;
    final InstanceConfiguration instanceConfiguration;
    final SyncRepository syncRepository;

    public ConsumerSyncService(FragmentRepository fragmentRepository, ObjectMapper objectMapper, ConsumerRoutingTable consumerRoutingTable, InstanceConfiguration instanceConfiguration, SyncRepository syncRepository) {
        this.fragmentRepository = fragmentRepository;
        this.objectMapper = objectMapper;
        this.fragmentValidationStrategy = new MD5FragmentValidationStrategy();
        this.consumerRoutingTable = consumerRoutingTable;
        this.instanceConfiguration = instanceConfiguration;
        this.syncRepository = syncRepository;
    }

    // TODO: now its synchronous -> it will be async
    public void syncProcess(Date syncDate) {
        try {
            Optional<Sync> oldSync = syncRepository.findById(SYNC_ID);
            if (oldSync.isPresent()) {
                if (oldSync.get().getElaborating()) {
                    log.info("sync is elaborating");
                    return;
                }
            }
            // START elaborating
            syncRepository.save(new Sync(SYNC_ID, true, syncDate));
            log.info("started syncProcess {}", syncDate);
            List<MongoFragment> mongoFragmentsToSync = fragmentRepository.getNotSynced(syncDate, 500); // TODO: add configuration
            log.info("founded {} fragments to sync", mongoFragmentsToSync.size());
            for (MongoFragment mongoFragment: mongoFragmentsToSync) {
                log.info("sync fragment {} {}",mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                try {
                    boolean allSuccess = true;
                    for (RoutingElement routingElement: otherConsumers()) {
                        log.info("call {} for {} {}",routingElement.getName(), mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                        boolean success = sendToConsumer(routingElement, mongoFragment);
                        if (!success) {
                            allSuccess = false;
                        }
                    }
                    if (allSuccess) {
                        log.info("sync fragment with SUCCESS {} {}",mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                        fragmentRepository.save(new MongoFragment(mongoFragment, true));//TODO: update only synced
                    } else {
                        log.warn("sync fragment with ERROR {} {}",mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                    }
                }catch (Exception e) {
                    log.error("error for sync {} {}", mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                }
            }
            // END elaborating
            syncRepository.save(new Sync(SYNC_ID, false, syncDate));
        }catch (Exception e) {
            log.error("error in sync process", e);
        }
    }

    public String consumerFragmentUrl(RoutingElement routingElement) {
        return "http://" +
                routingElement.getName() +
                ":" +
                instanceConfiguration.getConsumersPort() +
                "/" +
                instanceConfiguration.getConsumersFragmentUrl();
    }

    public List<RoutingElement> otherConsumers() {
        return consumerRoutingTable.getRoutingTable().stream().filter(e -> !e.getName().equals(instanceConfiguration.getInstanceName())).collect(Collectors.toList());
    }

    private boolean sendToConsumer(RoutingElement routingElement, Fragment fragment) {
        String url = consumerFragmentUrl(routingElement);
        String result = instanceConfiguration.restTemplate().postForObject(url.concat("/CHECKSUM"), StringUtils.encodeHexString(toJsonString(fragment).getBytes(StandardCharsets.UTF_8)), String.class);
        log.info("RESULT CONSUMER: {}", result);
        return "OK".equals(result);
    }

    @SneakyThrows
    private String toJsonString(Fragment fragment) {
        return objectMapper.writeValueAsString(fragment);
    }
}
