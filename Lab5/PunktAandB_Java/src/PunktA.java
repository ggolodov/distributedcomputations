import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PunktA {


    public static void main(String[] argv){
        int N = 150;
        int n = 3;
        Recruit[] recruits = new Recruit[N];
        Random random = new Random();
        CyclicBarrier barrier = new CyclicBarrier(n, new RecruitShowing(recruits));
        for (int i = 0; i < N; i++) {
            recruits[i] = new Recruit();
            boolean rand = random.nextBoolean();
            if (rand){
                recruits[i].turnLeft();
            } else {
                recruits[i].turnRight();
            }
        }

        new Sorter(1,0,50, recruits, barrier).start();
        new Sorter(2,50,100, recruits, barrier).start();
        new Sorter(3,100,N, recruits, barrier).start();
    }
}

class Recruit{
    private boolean leftTurn;
    private boolean rightTurn;

    Recruit(){
        leftTurn = false;
        rightTurn = false;
    }

    public boolean isLeftTurned() {
        return leftTurn;
    }

    public boolean isRightTurned() {
        return rightTurn;
    }

    public void turnLeft(){
        this.leftTurn = true;
        this.rightTurn = false;
    }

    public void turnRight() {
        this.rightTurn = true;
        this.leftTurn = false;
    }
}

class Sorter extends Thread{
    private int startSort;
    private int endSort;
    private Recruit[] recruits;
    private CyclicBarrier barrier;
    private int id;

    Sorter(int id, int startSort, int endSort, Recruit[] recruits, CyclicBarrier barrier){
        this.startSort = startSort;
        this.endSort = endSort;
        this.recruits = recruits;
        this.barrier = barrier;
        this.id = id;
    }

    @Override
    public void run() {
        int changes;
        while (!interrupted()){
            try {
                changes = 0;
                for (int i = startSort; i < endSort-1; i++) {
                    if(recruits[i].isRightTurned() && recruits[i+1].isLeftTurned()){
                        recruits[i].turnLeft();
                        //System.out.println("Recruit " + i + " turned left");
                        recruits[i+1].turnRight();
                        //System.out.println("Recruit " + (i+1) + " turned right");
                        changes++;
                    }
                }

                if (changes == 0) {
                    System.out.println("Sorter #" + id + " finished sorting");
                    barrier.await();
                    break;


//                    synchronized (recruits){
//                        if(endSort != recruits.length){
//                            if (recruits[endSort-1].isRightTurned() && recruits[endSort].isLeftTurned()){
//                                recruits[endSort-1].turnLeft();
//                                recruits[endSort].turnRight();
//                                recruits[endSort].modify();
//                                resorting = true;
//                                continue;
//                            }
//                        }
//                    }
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}

class RecruitShowing implements Runnable{
    private Recruit[] recruits;

    RecruitShowing(Recruit[] recruits){
        this.recruits = recruits;
    }

    @Override
    public void run() {
        String s = "|";
        for (int i = 0; i < recruits.length; i++) {
            if (recruits[i].isLeftTurned()){
                s += " q |";
            } else if(recruits[i].isRightTurned()){
                s += " p |";
            }
        }
        System.out.println("Rank now looks like:");
        System.out.println(s);
    }
}