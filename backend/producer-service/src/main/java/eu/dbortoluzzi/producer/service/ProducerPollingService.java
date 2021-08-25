package eu.dbortoluzzi.producer.service;

import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.producer.config.InstanceConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
@Slf4j
public class ProducerPollingService {

    public static final int BUFFER_SIZE = 512;

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
                    // read chunks
                    readUsingChunks(new File(path.toString() + "/" + p.getFileName().toString()));
                }
            }
            key.reset();
        }
    }

    private void readUsingChunks(File file) throws IOException {
        log.info("reading chunks of " + file.getAbsolutePath());
        try (
                InputStream inputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long fileLength = file.length();
            log.info("fileLength {}", fileLength);
            int totalFragment = (int) Math.ceil((double) fileLength / BUFFER_SIZE);
            int read;
            int fragmentWithSuccess = 0;
            int counter = 1;
            while ((read = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
//                log.info("reading: " + new String(buffer));
                Fragment fragment = producerFragmentService.createFragment(counter, totalFragment, instanceConfiguration.getInstanceName(), file.getName(), buffer);
//                log.info("prepared: {}", fragment.toString());
//
//                log.info("isValid = {}", producerFragmentService.isValidFragment(fragment));
//
//                log.info("decoded: {}", new String(producerFragmentService.decodeFragment(fragment)));

                boolean successFragment = producerFragmentService.sendToConsumer(fragment);
                if (successFragment) {
                    fragmentWithSuccess++;
                }
                counter++;
            }
            bufferedInputStream.close();
            if (fragmentWithSuccess == totalFragment) {
                file.delete();
            } else {
                log.error("file with errors: {} fragmentWithSuccess != {} totalFragment", fragmentWithSuccess, totalFragment);
                // TODO: recover file undeleted
            }
        }
    }

}
