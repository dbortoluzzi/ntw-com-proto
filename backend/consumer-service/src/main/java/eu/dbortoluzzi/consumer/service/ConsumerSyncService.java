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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConsumerSyncService {

    public static final String SYNC_ID = "id";

    private final ObjectMapper objectMapper;
    private final FragmentRepository fragmentRepository;
    private final InstanceConfiguration instanceConfiguration;
    private final SyncRepository syncRepository;
    private final List<RoutingElement> otherConsumers;

    public ConsumerSyncService(FragmentRepository fragmentRepository, ObjectMapper objectMapper, InstanceConfiguration instanceConfiguration, SyncRepository syncRepository) {
        this.fragmentRepository = fragmentRepository;
        this.objectMapper = objectMapper;
        this.instanceConfiguration = instanceConfiguration;
        this.syncRepository = syncRepository;
        this.otherConsumers = instanceConfiguration.otherConsumers();
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
            long startElaboration = new Date().getTime();
            syncRepository.save(new Sync(SYNC_ID, true, syncDate));
            log.info("started syncProcess {}", syncDate);
            List<MongoFragment> mongoFragmentsToSync = fragmentRepository.getNotSynced(syncDate, 1000); // TODO: add configuration
            log.info("founded {} fragments to sync", mongoFragmentsToSync.size());
            for (MongoFragment mongoFragment : mongoFragmentsToSync) {
                log.info("sync fragment {} {}", mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                List<String> syncedWithSuccess = new ArrayList<>();
                List<String> instancesAlreadySynced = Optional.ofNullable(mongoFragment.getInstancesSynced()).orElse(new ArrayList<>());
                boolean synced = false;
                try {
                    List<RoutingElement> instancesToSync = otherConsumers.stream().filter(oc -> !instancesAlreadySynced.contains(oc.getName())).collect(Collectors.toList());

                    for (RoutingElement routingElement : instancesToSync) {
                        log.info("call {} for {} {}", routingElement.getName(), mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                        boolean successSingleSync = sendToConsumer(routingElement, mongoFragment);
                        if (successSingleSync) {
                            syncedWithSuccess.add(routingElement.getName());
                        }
                    }
                    if (syncedWithSuccess.size() == instancesToSync.size()) {
                        log.info("syncing, set fragment with SUCCESS {} {}", mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                        synced = true;
                    } else {
                        log.warn("syncing, set fragment with ERROR {} {}", mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                    }
                } catch (Exception e) {
                    log.error("error for sync {} {}", mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                } finally {
                    List<String> newInstancesSynced = new ArrayList<>();
                    newInstancesSynced.addAll(instancesAlreadySynced);
                    newInstancesSynced.addAll(syncedWithSuccess);
                    log.info("save fragment SUCCESS={} instances={}, with {} {}", synced, String.join(" ", newInstancesSynced), mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
                    fragmentRepository.save(new MongoFragment(mongoFragment, synced, newInstancesSynced));
                }
            }
            // END elaborating
            syncRepository.save(new Sync(SYNC_ID, false, syncDate));
            log.info("syncProcess ended, elaborationTime = {}ms", new Date().getTime() - startElaboration);
        } catch (Exception e) {
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
