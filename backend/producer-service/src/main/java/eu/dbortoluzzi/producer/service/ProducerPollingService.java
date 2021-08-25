package eu.dbortoluzzi.producer.service;

import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.utils.CommonUtils;
import eu.dbortoluzzi.producer.config.InstanceConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProducerPollingService {

    // TODO: add configuration via .properties
    public static final int BUFFER_SIZE = 512;
    public static final int MAX_ELEMENT_OF_CHUNK = 50;

    private final InstanceConfiguration instanceConfiguration;

    private final ProducerFragmentService producerFragmentService;

    public ProducerPollingService(InstanceConfiguration instanceConfiguration, ProducerFragmentService producerFragmentService) {
        this.instanceConfiguration = instanceConfiguration;
        this.producerFragmentService = producerFragmentService;
    }

    public void runPolling() throws IOException, InterruptedException {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(instanceConfiguration.getPollingPath());
        // create folder if not exists
        try {
            File file = path.toFile();
            file.mkdirs();
            file.setWritable(true, false);
            file.setReadable(true, false);
            file.setExecutable(true, false);
        } catch (Exception e) {
            log.error("error creating path", e);
        }

        // register for creation file event
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");

                Object context = event.context();
                if (context instanceof Path) {
                    Path p = (Path) context;
                    CommonUtils.waitingForCopyCompleted(p);
                    System.out.println("Finished creating file!");

                    // read chunks
                    readUsingChunks(new File(path.toString() + "/" + p.getFileName().toString()));
                }
            }
            key.reset();
        }
    }

    private void readUsingChunks(File file) throws IOException {
        Date timestamp = new Date();
        log.info("reading chunks of " + file.getAbsolutePath());
        try (
                InputStream inputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long fileLength = file.length();
            long totalFragment = (long) Math.ceil((double) fileLength / BUFFER_SIZE);
            long read;
            long fragmentWithSuccess = 0;
            long counter = 1;

            while ((read = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
                List<CompletableFuture<Boolean>> completableFutureList = new ArrayList<>();
                producerFragmentService.addElaborationToCompletableFutures(file, timestamp, buffer, totalFragment, counter, completableFutureList);
                counter++;

                int elementsOfChunk = 0;
                while (elementsOfChunk<= MAX_ELEMENT_OF_CHUNK && (read = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
                    producerFragmentService.addElaborationToCompletableFutures(file, timestamp, buffer, totalFragment, counter, completableFutureList);
                    elementsOfChunk++;
                    counter++;
                }
                List<Boolean> results = CommonUtils.allOfCompletableFutures(completableFutureList).join();
                fragmentWithSuccess += results.stream().filter(b -> b).count();
            }
            bufferedInputStream.close();
            if (fragmentWithSuccess == totalFragment) {
                log.info("deleting file {} because elaboration is SUCCESS", file.getName());
                file.delete();
            } else {
                log.error("file with errors: {} fragmentWithSuccess != {} totalFragment", fragmentWithSuccess, totalFragment);
                // TODO: recover file undeleted: move to a directory to recover with a timestamp suffix (to use for fragment)
            }
        }
    }

}
