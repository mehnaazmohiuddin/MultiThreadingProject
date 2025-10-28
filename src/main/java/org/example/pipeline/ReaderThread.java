package org.example.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;

public class ReaderThread extends Thread{
    Path filePath;
    ArrayBlockingQueue<String> events;
    private final String POISON;
    int workerThreads;
    public ReaderThread(Path filePath, ArrayBlockingQueue<String> events, String poison, int workerThreads){
        this.filePath = filePath;
        this.events = events;
        POISON = poison;
        this.workerThreads = workerThreads;
    }
    @Override
    public void run() {
            try(BufferedReader bufferedReader = Files.newBufferedReader(filePath)){
                String line ;

                while ((line = bufferedReader.readLine()) != null){
                    events.put(line);
                    Thread.sleep(10);
                    System.out.println("Submiting Event to Q1"+line);
                }
                System.out.println("File is done");
                for(int i =0; i< workerThreads;i++)
                {
                    events.put(POISON);
                }

            }catch (IOException e){
                System.out.println("Exception IO");
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Got interrupted");
                throw new RuntimeException(e);
            }
        System.out.println("Reader run is done");
    }
}
