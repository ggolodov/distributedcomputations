import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class PunktB {
    public static void main(String[] argv){
        AtomicBoolean isDone = new AtomicBoolean(false);
        AtomicIntegerArray symbolCounter = new AtomicIntegerArray(4);
        Barrier barrier = new Barrier(4, new StopSignal(symbolCounter, isDone));
        new StringChanger(0, barrier, symbolCounter, isDone, "ABCDADA").start();
        new StringChanger(1, barrier, symbolCounter, isDone, "ABCDBCABC").start();
        new StringChanger(2, barrier, symbolCounter, isDone, "ACDCACDADCC").start();
        new StringChanger(3, barrier, symbolCounter, isDone, "ABADBCDBCDACA").start();
    }
}

class Barrier extends Thread {
    private int maxCount;
    private AtomicInteger count;
    private Runnable runnable;
    private volatile boolean isDone;

    public Barrier(int maxCount, Runnable runnable)
    {
        this.maxCount = maxCount;
        this.count = new AtomicInteger(0);
        this.runnable = runnable;
        this.isDone = false;
    }

    public void await() throws InterruptedException, BrokenBarrierException{
        isDone = false;
        count.incrementAndGet();
        while (count.get() % maxCount != 0){

        }
        synchronized (runnable){
            if (!isDone){
                isDone = true;
                new Thread(runnable).start();
            }
        }
    }


}

class StopSignal implements Runnable{
    private AtomicIntegerArray symbolCounter;
    private AtomicBoolean isDone;


    StopSignal(AtomicIntegerArray symbolCounter, AtomicBoolean isDone){
        this.symbolCounter = symbolCounter;
        this.isDone = isDone;
    }

    @Override
    public void run() {
        int counter1 = 0;
        System.out.println("");
        for (int j = 0; j < symbolCounter.length()-1; j++) {
            if(symbolCounter.get(0) == symbolCounter.get(j+1)){
                counter1++;
            }
        }

        int counter2 = 0;
        for (int j = 0; j < symbolCounter.length(); j++) {
            if(symbolCounter.get(1) == symbolCounter.get(j)){
                if (j != 1){
                    counter2++;
                }
            }
        }

        if (counter1 >= 2 || counter2 >= 2){
            isDone.set(true);
            System.out.println("All the threads were stopped");
        }else {
            System.out.println("Number of equal quantities of As and Bs < 3");
        }
    }
}

class StringChanger extends Thread{
    private int id;
    private Barrier barrier;
    private String stringToChange;
    private Random random;
    private AtomicIntegerArray symbolCounter;
    private AtomicBoolean isDone;

    StringChanger(int id, Barrier barrier, AtomicIntegerArray symbolCounter, AtomicBoolean isDone, String stringToChange){
        this.id = id;
        this.barrier = barrier;
        this.symbolCounter = symbolCounter;
        this.isDone = isDone;
        this.stringToChange = stringToChange;
        random = new Random();
    }

    void changeSymbol() {
        int randSymbolIndex = random.nextInt(stringToChange.length());
        //sleep(randSymbolIndex*1000);
        StringBuilder tempString = new StringBuilder();
        for (int i = 0; i < stringToChange.length(); i++) {
            char symbol = stringToChange.charAt(i);
            if(i == randSymbolIndex){
                String symbolToChange = String.valueOf(symbol);
                switch (symbolToChange) {
                    case "A":
                        tempString.append("C");
                        break;
                    case "C":
                        tempString.append("A");
                        break;
                    case "B":
                        tempString.append("D");
                        break;
                    case "D":
                        tempString.append("B");
                        break;
                }
            }else{
                tempString.append(symbol);
            }

        }
        stringToChange = tempString.toString();
    }

    int countAB(){
        int counter = 0;
        for (int i = 0; i < stringToChange.length(); i++) {
            if (String.valueOf(stringToChange.charAt(i)).equals("A") || String.valueOf(stringToChange.charAt(i)).equals("B")){
                counter++;
            }
        }
        return counter;
    }

    @Override
    public void run() {
        while ( !interrupted()){
            try {
                sleep(1000);
                if (isDone.get()){
                    break;
                }
                changeSymbol();
                int count = countAB();
                System.out.println("Quantity of As and Bs of stringChanger #" + id + " = " + count);
                symbolCounter.set(id, count);
                barrier.await();

            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
