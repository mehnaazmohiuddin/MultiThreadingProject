public class TroubleMaker {

    public static final Object lockA = new Object();
    public static final Object lockB = new Object();

    public void makeDeadLock(){
        Thread A = new Thread(()->{
            synchronized (lockA){
                System.out.println("Accuired Lock A --- -I am thread A");
                System.out.println("Trying to accire lock B --- I am thread A");
                synchronized (lockB){
                    System.out.println("Accuired Lock B --- -I am thread A");
                }
            }

        },"Thread A");

        Thread B = new Thread(()->{
            synchronized (lockB){
                System.out.println("Accuired Lock B --- -I am thread B");
                System.out.println("Trying to accire lock A --- I am thread B");
                synchronized (lockA){
                    System.out.println("Accuired Lock A --- -I am thread B");
                }
            }

        },"Thread B");
        A.start();
        B.start();


    }

    public void makeHeapOverflow(){


    }


}
