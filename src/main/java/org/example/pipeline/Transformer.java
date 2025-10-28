package org.example.pipeline;

import java.util.concurrent.*;

public class Transformer {
    ExecutorService cpuPool ;
    ArrayBlockingQueue<String> eventsQueue;
    int numberOfThreads ;
    String poison;
    CountDownLatch latch ;
    ArrayBlockingQueue<String> enrichedQueue;

    public Transformer(int numberOfThreads, ArrayBlockingQueue<String> eventsQueue, ExecutorService cpuPool,
                       String poison, CountDownLatch latch, ArrayBlockingQueue<String> enrichedQueue) {
            this.cpuPool = cpuPool;
            this.eventsQueue = eventsQueue;
            this.numberOfThreads=numberOfThreads;
            this.poison = poison;
            this.latch = latch;
            this.enrichedQueue = enrichedQueue;
    }

    public void start() {

        for(int i =0 ; i<numberOfThreads;i++){
            cpuPool.submit(()->{
                String event = null;
                try {
                    while (true){
                        event = eventsQueue.poll(100, TimeUnit.MILLISECONDS);
                        if(event==null || event.equals(poison)){
                            String poisonLable ="";
                            if( poison.equals(event)){
                                poisonLable = ", poison taken";
                            }
                            System.out.println("Work done "+poisonLable);
                            break;
                        }
                        System.out.println("Consuming event "+event);
                        // store it as some pleace or whatever
                        enrichedQueue.offer(event, 100,TimeUnit.MILLISECONDS);
                    }
                } catch (InterruptedException e) {
                    System.out.println("in interupted "+event);
                    Thread.currentThread().interrupt();

                }
                finally { latch.countDown(); }

            });
        }

    }
}
