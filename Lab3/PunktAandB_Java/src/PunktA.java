import java.util.concurrent.atomic.AtomicInteger;

public class PunktA {

    private static int n = 5;
    private static int N = 50;

    public static void main(String[] argv){
        Pot pot = new Pot(N);
        Semaphor semaphore = new Semaphor(1);
        WinniePooh bear = new WinniePooh(pot);
        bear.start();
        for (int i = 0; i < n; i++) {
            new Bee(pot, semaphore, bear).start();
        }
    }
}

class Semaphor {
    private int maxCount;
    private AtomicInteger count;

    public Semaphor(int maxCount)
    {
        this.maxCount = maxCount;
        this.count = new AtomicInteger(this.maxCount);
    }

    public synchronized void acquire(){
        while (count.get() == 0){

        }
        count.decrementAndGet();
    }

    public void release()
    {
        if(count.get() != maxCount){
            count.incrementAndGet();
        }
    }

}

class Pot{
    private int currCapacity;
    private int maxCapacity;

    public Pot(int maxCapacity)
    {
        this.maxCapacity = maxCapacity;
    }

    public int getCapacity() {
        return currCapacity;
    }

    public void setCapacity(int capacity) {
        if(capacity <= maxCapacity && capacity >= 0)
        {
            this.currCapacity = capacity;
        }
    }

    public boolean isFull()
    {
        return currCapacity == maxCapacity;
    }

    public boolean isEmpty(){
        return currCapacity == 0;
    }
}

class Bee extends Thread{
    private Pot pot;
    private Semaphor semaphore;
    private WinniePooh bear;

    public Bee(Pot pot, Semaphor semaphore, WinniePooh bear)
    {
        this.pot = pot;
        this.semaphore = semaphore;
        this.bear = bear;
    }

    public void wakeUpBear(){
        System.out.println("Bear was waken up");
        bear.awake();
    }

    @Override
    public void run() {
        while (!interrupted()){
            semaphore.acquire();
            //System.out.println(Thread.currentThread().getName() + "acquired");
            if(!pot.isFull() && bear.isSleeping()){
                pot.setCapacity(pot.getCapacity() + 1);
                System.out.println("Bee named \"" + Thread.currentThread().getName() + "\" added portion " + pot.getCapacity());
//                try {
//                    sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            else{
                if(bear.isSleeping()){
                        wakeUpBear();
                    }
            }
            //System.out.println(Thread.currentThread().getName() + "released");
            semaphore.release();
        }
    }
}

class WinniePooh extends Thread{
    private Pot pot;
    private boolean sleep;

    public WinniePooh(Pot pot)
    {
        this.pot = pot;
        sleep = true;
    }

    public void awake(){
        sleep = false;
    }

    public void goToSleep(){
        System.out.println("Bear went to sleep");
        sleep = true;
    }

    public boolean isSleeping(){
        return sleep;
    }

    @Override
    public void run() {
        while (!interrupted()){
            if(!isSleeping()){
                if(!pot.isEmpty()){
                    pot.setCapacity(pot.getCapacity()-1);
                    System.out.println("Bear ate portion, now there are " + pot.getCapacity() + " portions in pot");
//                    try {
//                        sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
                else {
                    goToSleep();
                }
            }
        }
    }
}