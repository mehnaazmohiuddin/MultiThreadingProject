package org.example.pipeline;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EventValidator {
    ArrayBlockingQueue<String> enrichedQueue;
    Path filePath ;
    CountDownLatch latch;
    Thread validatorThread ;
    String poisionPill;
    public EventValidator(ArrayBlockingQueue<String> enrichedQueue, Path validFilePath, int numOfWorkerThreads, String POISON) throws IOException {
        this.enrichedQueue = enrichedQueue;
        this.filePath = validFilePath;
        this.latch = new CountDownLatch(numOfWorkerThreads);
        this.poisionPill = POISON;
    }
    public  Thread getValidatorThread(){
        return validatorThread;
    }

    public void startEventValidation(){
        System.out.println("Valdation begins");

        validatorThread = new Thread(()->{
                try (var out = java.nio.file.Files.newBufferedWriter(
                        filePath,
                        java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.APPEND)){
                    System.out.println("File is "+filePath.getParent());
                    while (true){
                     String event =   enrichedQueue.poll(100, TimeUnit.MILLISECONDS);
                        System.out.println("waited for even for 100 millis"+event);
                     if( this.latch.getCount() == 0){
                         break;
                     }
                     if(event.endsWith("1")){
                         System.out.println("got one event with one at last"+event);
                         out.write(event+"\n");
                     }
                     if(event.equals(this.poisionPill)){
                         this.latch.countDown();
                         break;
                     }
                    }

                }catch (Exception e){
                    System.out.println(e);
                }

            });
        validatorThread.start();
    }



}
