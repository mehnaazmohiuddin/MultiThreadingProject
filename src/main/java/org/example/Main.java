package org.example;

import org.example.helper.ShutdownService;
import org.example.pipeline.EventValidator;
import org.example.pipeline.ReaderThread;
import org.example.pipeline.Transformer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.*;


public class Main {
    private static  ThreadPoolExecutor exec;
    private static final ArrayBlockingQueue<String> eventsQueue = new ArrayBlockingQueue(100);
    private static final ArrayBlockingQueue<String> enrichedQueue = new ArrayBlockingQueue(100);
    private static final String POISON = "---END---" ;

    public static ReaderThread startReader() throws URISyntaxException, InterruptedException {
        FileResource pathResource = getGetFileResource( "2.csv");
        ReaderThread readerThread = new ReaderThread(pathResource.resourcePath(), eventsQueue, pathResource.POISON(),Runtime.getRuntime().availableProcessors());
        readerThread.start();
        ShutdownService.tellShutDown(readerThread);
        return readerThread;

    }

    public static  void startTransformers() throws InterruptedException, IOException, URISyntaxException {
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        exec  = new ThreadPoolExecutor(
                numberOfThreads, numberOfThreads,
                30L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(2000));
        CountDownLatch latch  = new CountDownLatch(numberOfThreads);
        Transformer transformer = new Transformer( numberOfThreads,eventsQueue, exec,
                POISON,   latch , enrichedQueue);
        transformer.start();
        FileResource pathResource = getGetFileResource( "3.csv");
        EventValidator eventValidator = new EventValidator(enrichedQueue,pathResource.resourcePath(),numberOfThreads, pathResource.POISON);
        eventValidator.startEventValidation();
        ShutdownService.tellShutDown(eventValidator.getValidatorThread());
        latch.await();
        enrichedQueue.put(POISON);
        eventValidator.getValidatorThread().join();

    }


    public static void runPipeline() throws URISyntaxException, InterruptedException, IOException {
        ReaderThread r = startReader();
        startTransformers();
      //  r.join();
        exec.shutdown();
    }

    private static FileResource getGetFileResource(String fileName) throws URISyntaxException {

        URI resourceUri = Objects.requireNonNull(Main.class.getClassLoader().getResource(fileName)).toURI();
        Path resourcePath = Paths.get(resourceUri);
        System.out.println("this is the path" +resourceUri.getPath());

        return new FileResource(resourcePath, POISON);
    }

    private record FileResource(Path resourcePath, String POISON) {
    }

    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        System.out.println(System.nanoTime());
        ShutdownService.registerShutdown(exec);
        runPipeline();

    }
}