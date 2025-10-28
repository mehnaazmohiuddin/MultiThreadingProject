1st Project demonstrate how 2 threads communicate.

## Learning 1 ArrayBlockingQueue<String> , 
We have used ArrayBlockingQueue<String> 

`  private static final ArrayBlockingQueue<String> eventsQueue = new ArrayBlockingQueue(100);
`

here the Thread is blocked when the queue is empty or full, depending on 
if you are a producer or consumer.

We made sure that its Bounded. 
We communicated the intent by putting poison in the queue. 

## Learning  2 poll(100, TimeUnit.MILLISECONDS), 
how much to wait
`enrichedQueue.poll(100, TimeUnit.MILLISECONDS);`

This tells to wait for 100 milliseconds else you will get Null.
This is good for something which is expected in that much time.



## Learning 4 Poison Pill

Inorder to say we have ended or you end your work. in the queue put a poison pill.


## Learning 3         CountDownLatch latch 

`CountDownLatch latch  = new CountDownLatch(numberOfThreads); `

This When we assign in Main And then pass it to the threads. each having the same reference.
Will should say latch.countDown(). so that we can shutdown the executor service after the work was
done or we can  latch.await() and then signal something else. Like putting poison pill. 

how do we shut down our pool?


# ShutDown How do we handle ShutDown book-keeping

`exec  = new ThreadPoolExecutor(
numberOfThreads, numberOfThreads,
30L, TimeUnit.MILLISECONDS,
new ArrayBlockingQueue<Runnable>(2000));`
`Runtime.getRuntime().addShutdownHook( ()->())
    ShutdownService.registerShutdown(exec);`



`cpuPool.shutdown();
Register the running threads. and call each one;s thread.inteerupt.
Let them handle their task what should happen when ur are suddenly interrupted. you want to write the on-going work 
to some thing. etc. `