package org.example.helper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

public class ShutdownService {
    static LinkedList<Thread> workerThreads = new LinkedList<>();
    public static void tellShutDown(Thread t){
        workerThreads.add(t);
    }

    public static void registerShutdown(ExecutorService cpuPool ){
        Runtime.getRuntime().addShutdownHook( new Thread (()->{
            System.out.println("App Shutting Down ...");
            if(cpuPool !=null)
                cpuPool.shutdown();
            for (Thread workerThread : workerThreads){
                workerThread.interrupt();
            }
            try{
                if (cpuPool !=null && !cpuPool.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                    cpuPool.shutdownNow();                // interrupt workers
                }

            }catch (InterruptedException e){
                if(cpuPool!=null)
                  cpuPool.shutdownNow();
            }
            finally {
                System.out.println("Ending time="+System.nanoTime());
            }
        }));
    }

}
