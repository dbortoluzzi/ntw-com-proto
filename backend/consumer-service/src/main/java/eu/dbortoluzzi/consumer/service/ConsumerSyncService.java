package eu.dbortoluzzi.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.model.RoutingElement;
import eu.dbortoluzzi.commons.utils.CommonUtils;
import eu.dbortoluzzi.commons.utils.StringUtils;
import eu.dbortoluzzi.consumer.config.InstanceConfiguration;
import eu.dbortoluzzi.consumer.model.MongoFragment;
import eu.dbortoluzzi.consumer.model.Sync;
import eu.dbortoluzzi.consumer.repository.FragmentRepository;
import eu.dbortoluzzi.consumer.repository.SyncRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConsumerSyncService {

    public static final String SYNC_ID = "id";
    public static final int MAX_NUM_FRAGMENTS_TO_SYNC = 1000; // TODO: add configuration

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
    public void syncProcess(Date syncDate, boolean firstSync) {
        try {
            Optional<Sync> oldSync = syncRepository.findById(SYNC_ID);
            if (!firstSync && oldSync.isPresent()) {
                if (oldSync.get().getElaborating()) {
                    log.info("sync is elaborating, skipping");
                    return;
                }
            }
            // START elaborating
            long startElaboration = new Date().getTime();
            syncRepository.save(new Sync(SYNC_ID, true, syncDate));

            log.info("started syncProcess {}", syncDate);
            List<MongoFragment> mongoFragmentsToSync = fragmentRepository.getNotSynced(syncDate, MAX_NUM_FRAGMENTS_TO_SYNC);
            log.info("founded {} fragments to sync", mongoFragmentsToSync.size());
            List<CompletableFuture<Void>> completableFutureList = mongoFragmentsToSync.stream().map(this::syncFragmentInAsyncWay).collect(Collectors.toList());
            CommonUtils.allOfCompletableFutures(completableFutureList).join();
            // END elaborating
            syncRepository.save(new Sync(SYNC_ID, false, syncDate));
            log.info("syncProcess ended, elaborationTime = {}ms", new Date().getTime() - startElaboration);
        } catch (Exception e) {
            log.error("error in sync process", e);
        }
    }

    @Async
    public CompletableFuture<Void> syncFragmentInAsyncWay(MongoFragment mongoFragment) {
        return CompletableFuture.runAsync(() -> syncFragmentInSynchronousWay(mongoFragment));
    }

    private void syncFragmentInSynchronousWay(MongoFragment mongoFragment) {
        log.info("sync fragment = {}, index = {}", mongoFragment.getUniqueFileName(), mongoFragment.getIndex());
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

    private boolean sendToConsumer(RoutingElement routingElement, Fragment fragment) {
        String postData = StringUtils.encodeHexString(toJsonString(fragment).getBytes(StandardCharsets.UTF_8));
        String url = prepareConsumerFragmentUrl(routingElement, StringUtils.md5sum(postData));
        String result = instanceConfiguration.restTemplate().postForObject(url, postData, String.class);
        log.info("RESULT CONSUMER: {}", result);
        return "OK".equals(result);
    }

    public String prepareConsumerFragmentUrl(RoutingElement routingElement, String checksum) {
        return MessageFormat.format("http://{0}:{1}/{2}/{3}/{4}",
                routingElement.getName(),
                instanceConfiguration.getConsumersPort(),
                instanceConfiguration.getConsumersFragmentUrl(),
                instanceConfiguration.getInstanceName(),
                checksum);
    }

    @SneakyThrows
    private String toJsonString(Fragment fragment) {
        return objectMapper.writeValueAsString(fragment);
    }
}
