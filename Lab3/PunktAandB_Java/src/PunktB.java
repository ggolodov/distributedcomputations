import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class PunktB {
    volatile static Semaphore semaphore = new Semaphore(1, true);
    public static void main(String[] argv) throws InterruptedException {

        Barber barber = new Barber(semaphore);
        barber.start();
        for (int i = 0; i < 5; i++) {
            new Client(semaphore, i, barber).start();
        }
        sleep(5000);
        new Client(semaphore, 6, barber).start();
    }
}

class Barber extends Thread{
    private Semaphore semaphore;
    private boolean sleep;
    private Client currentClient;

    public Barber(Semaphore semaphore){
        this.semaphore = semaphore;
        this.sleep = true;
    }

    public void setCurrentClient(Client client){
        currentClient = client;
    }

    public Client getCurrentClient(){
        return currentClient;
    }

    public void releaseCurrentClient(){
        currentClient = null;
    }

    public boolean isSleeping(){
        return sleep;
    }

    public void goToWork(){
        sleep = false;
    }

    public void goToSleep(){
        sleep = true;
    }

    public void makeCut(){
        currentClient.cut();
    }

    public void acquireClient(Semaphore semaphore) throws InterruptedException {
        semaphore.acquire();
    }

    public void releaseClient(Semaphore semaphore) throws InterruptedException{
        semaphore.release();
    }

    @Override
    public void run() {
        while (!interrupted()){
            if(!sleep){
                if(currentClient != null){
                    makeCut();
                    releaseCurrentClient();

                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(semaphore.availablePermits() == 1){
                    goToSleep();
                }

            }
        }
    }
}

class Client extends Thread{
    private Semaphore semaphore;
    private Barber barber;
    private boolean isCut;
    private int name;


    public Client(Semaphore semaphore, int name, Barber barber){
        this.semaphore = semaphore;
        this.isCut = false;
        this.name = name;
        this.barber = barber;
    }

    public void cut(){
        isCut = true;
    }

    public void awakeBarber(Barber barber){
        barber.goToWork();
        System.out.println("Barber was awaken");
    }

    @Override
    public void run() {
        try {
            barber.acquireClient(semaphore);
            barber.setCurrentClient(this);
            if(barber.isSleeping()){
                awakeBarber(barber);
            }

            System.out.println("Client #" + name + " is being cut");
            while (!isCut && barber.getCurrentClient() == null){

            }
            System.out.println("Client #" + name + " has gone");
            barber.releaseClient(semaphore);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }
}

