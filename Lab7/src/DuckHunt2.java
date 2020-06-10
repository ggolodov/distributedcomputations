import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class DuckHunt2 {
    static JLabel background;
    static JLabel hunter;
    static JLabel bullets;
    private static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
    static JFrame jf;

    DuckHunt2() throws IOException, InterruptedException {
        initGUI();
    }

    public void initGUI() throws IOException, InterruptedException {
        background = new JLabel();
        background.setIcon(new ImageIcon("images/background.png"));
        background.setLayout(new BorderLayout(400, 270));

        JButton btn = new JButton("START");

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                background.removeAll();
                background.revalidate();
                background.setLayout(new GridLayout(5, 2));

                for (int i = 0; i < 4; i++) {
                    new Duck().start();
                }
                bullets = new JLabel();
                bullets.setLayout(new GridLayout(1, 5));
                new Bullet().start();
                new Bullet().start();
                new Bullet().start();
                new Bullet().start();
                new Bullet().start();
                hunter = new JLabel();
                hunter.setIcon(new ImageIcon("images/hunter.png"));
                background.add(hunter);
            }
        });

        background.add(Box.createGlue(), BorderLayout.NORTH);
        background.add(Box.createGlue(), BorderLayout.SOUTH);
        background.add(Box.createGlue(), BorderLayout.WEST);
        background.add(Box.createGlue(), BorderLayout.EAST);
        background.add(btn, BorderLayout.CENTER);
    }

    static class Bullet extends Thread{
        JLabel bullet;
        AtomicBoolean flying;
        boolean start;
        boolean inhand;

        Bullet(){
            bullet = new JLabel();
            bullet.setIcon(new ImageIcon("images/bullet.png"));
            flying = new AtomicBoolean(false);
            start = true;
            inhand = false;
            bulletList.add(this);
            background.add(bullet);
        }

        public void setInHand(boolean inhand) {
            this.inhand = inhand;
        }

        public boolean isInHand() {
            return inhand;
        }

        public void setStart(boolean start) {
            this.start = start;
        }

        public boolean isInStart() {
            return start;
        }

        void setFlying(boolean flying){
            this.flying.set(flying);
        }

        boolean isFlying(){
            return flying.get();
        }

        void setStartPos(){
            if (bullet.getX() < hunter.getX()){
                while (bullet.getX() < hunter.getX()+38){
                    bullet.setLocation(bullet.getX()+1, bullet.getY());
                }
            } else if (bullet.getX() > hunter.getX()){
                while (bullet.getX() > hunter.getX()+38){
                    bullet.setLocation(bullet.getX()-1, bullet.getY());
                }
            }

            if (bullet.getY() < hunter.getY()){
                while (bullet.getY() < hunter.getY()){
                    bullet.setLocation(bullet.getX(), bullet.getY()+1);
                }
            } else if (bullet.getY() > hunter.getY()){
                while (bullet.getY() > hunter.getY()){
                    bullet.setLocation(bullet.getX(), bullet.getY()-1);
                }
            }

            bullet.repaint();
        }

        JLabel getLabel(){
            return bullet;
        }

        @Override
        public void run() {
            while (!interrupted()){
                try {
                    if(!isFlying() && !isInStart()){
                        while (bullet.getX() > -20){
                            bullet.setLocation(bullet.getX()-1, bullet.getY());
                        }
                        bullet.repaint();
                        setStart(true);
                    } else if (isFlying() && !isInHand()){
                        setStartPos();
                        setStart(false);
                        setInHand(true);
                    }

                    if (bullet.getY() > -35 && isFlying() && isInHand()){
                        sleep(25);
                        bullet.setLocation(bullet.getX(), bullet.getY()-10);
                        bullet.repaint();
                    }

                    if(bullet.getY() <= -35) {
                        setFlying(false);
                        setStart(false);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Duck extends Thread{
        JLabel duck;
        Random random;
        boolean moveLeft;
        boolean isDead;

        Duck(){
            init();
        }

        void init(){
            duck = new JLabel();
            duck.setIcon(new ImageIcon("images/duckR.gif"));
            duck.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    isDead = true;
                    duck.setIcon(new ImageIcon("images/deadDuck.png"));
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            background.add(duck);
            moveLeft = true;
            isDead = false;
        }

        void move(){
            //borders x:960 , y:577

            if (moveLeft){
                if (duck.getX() > 850){
                    moveLeft = false;
                    duck.setIcon(new ImageIcon("images/duckL.gif"));
                } else {
                    if(duck.getY() < 0){
                        duck.setLocation(duck.getX() + random.nextInt(20), duck.getY()+ random.nextInt(20));
                    } else if (duck.getY() > 400){
                        duck.setLocation(duck.getX() + random.nextInt(20), duck.getY()- random.nextInt(20));
                    } else {
                        duck.setLocation(duck.getX() + random.nextInt(20), duck.getY()+ random.nextInt(40)-20);
                    }
                }
            } else {
                if (duck.getX() < 20){
                    moveLeft = true;
                    duck.setIcon(new ImageIcon("images/duckR.gif"));
                } else {
                    if(duck.getY() < 0){
                        duck.setLocation(duck.getX() - random.nextInt(20), duck.getY() + random.nextInt(20));
                    } else if (duck.getY() > 350){
                        duck.setLocation(duck.getX() - random.nextInt(20), duck.getY() - random.nextInt(20));
                    }else {
                        duck.setLocation(duck.getX() - random.nextInt(20), duck.getY() + random.nextInt(40)-20);
                    }
                }
            }
        }

        synchronized void checkLife(){
            for (int i = 0; i < bulletList.size(); i++) {
                if (duck.getX() <= bulletList.get(i).getLabel().getX() && bulletList.get(i).getLabel().getX() <= duck.getX()+100){
                    if (duck.getY() <= bulletList.get(i).getLabel().getY() && bulletList.get(i).getLabel().getY() <= duck.getY()+50){
                        isDead = true;
                        duck.setIcon(new ImageIcon("images/deadDuck.png"));
                        bulletList.get(i).setStart(false);
                        bulletList.get(i).setFlying(false);
                        bulletList.get(i).setInHand(false);
                    }
                }
            }
        }

        @Override
        public void run() {
            random = new Random();
            int side = random.nextInt(10);
            while (!interrupted()){
                try {
                    sleep(100);
                    if(!isDead){
                        move();
                        checkLife();
                    } else {
                        if (duck.getY() < 470){
                            duck.setLocation(duck.getX(), duck.getY() + 10);
                        } else {
                            duck.setIcon(new ImageIcon("images/duckR.gif"));
                            duck.setLocation(0, 0);
                            isDead = false;
                            duck.repaint();
                        }
                    }
                    duck.repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] argv) throws IOException, InterruptedException {
        jf = new JFrame("Duck Hunt");
        jf.setContentPane(new DuckHunt2().background);
        jf.setLocation(300, 100);
        jf.setFocusable(true);
        jf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_RIGHT){
                    if (hunter.getX() < 860)
                    hunter.setLocation(hunter.getX() + 10, hunter.getY());
                } else if(code == KeyEvent.VK_LEFT){
                    if (hunter.getX() > 0){
                        hunter.setLocation(hunter.getX() - 10, hunter.getY());
                    }
                } else if (code == KeyEvent.VK_SPACE){
                    for (int i = 0; i < bulletList.size(); i++) {
                        if (!bulletList.get(i).isFlying()){
                            bulletList.get(i).setStartPos();
                            bulletList.get(i).setFlying(true);
                            bulletList.get(i).setInHand(true);
                            break;
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        jf.setMinimumSize(new Dimension(400, 400));
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.pack();
        jf.setVisible(true);
    }
}

