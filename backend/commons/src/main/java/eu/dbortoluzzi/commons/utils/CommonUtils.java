package eu.dbortoluzzi.commons.utils;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommonUtils {

    public static void waitingForCopyCompleted(Path p) throws InterruptedException {
        boolean isGrowing;
        Long initialWeight;
        Long finalWeight;

        do {
            initialWeight = p.toFile().length();
            Thread.sleep(1000);
            finalWeight = p.toFile().length();
            isGrowing = initialWeight < finalWeight;

        } while (isGrowing);
    }

    public static <T> CompletableFuture<List<T>> allOfCompletableFutures(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult =
                CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
        return allFuturesResult.thenApply(v ->
                futuresList.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
    }
}
