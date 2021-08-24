package eu.dbortoluzzi.producer;

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

    public ProducerPollingService(InstanceConfiguration instanceConfiguration) {
        this.instanceConfiguration = instanceConfiguration;
    }

    public void runPolling() throws IOException, InterruptedException {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(instanceConfiguration.getPollingPath());

        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");

                Object context = event.context();
                if (context instanceof Path) {
                    Path p = (Path) context;
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
            int read;
            while ((read = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
                // TODO: send to server
                log.info("reading: " + new String(buffer));
            }
        }
    }

}
