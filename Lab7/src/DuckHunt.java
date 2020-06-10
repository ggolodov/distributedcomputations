import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Random;

public class DuckHunt {
    JLabel background;
    JLabel score;

    DuckHunt() throws IOException, InterruptedException {
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
                background.setLayout(new GridLayout(7, 0));

//                JPanel panel = new JPanel();
//                panel.setOpaque(false);
//                JLabel scoreTxt = new JLabel("Score: ");
//                scoreTxt.setFont(new Font("TimesRoman", Font.PLAIN, 22));
//                score = new JLabel();
//                score.setFont(new Font("TimesRoman", Font.PLAIN, 22));
//                panel.add(scoreTxt);
//                panel.add(score);
//                background.add(panel);

                for (int i = 0; i < 6; i++) {
                    new Duck().start();
                }
            }
        });

        background.add(Box.createGlue(), BorderLayout.NORTH);
        background.add(Box.createGlue(), BorderLayout.SOUTH);
        background.add(Box.createGlue(), BorderLayout.WEST);
        background.add(Box.createGlue(), BorderLayout.EAST);
        background.add(btn, BorderLayout.CENTER);
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
                    } else if (duck.getY() > 400){
                        duck.setLocation(duck.getX() - random.nextInt(20), duck.getY() - random.nextInt(20));
                    }else {
                        duck.setLocation(duck.getX() - random.nextInt(20), duck.getY() + random.nextInt(40)-20);
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
        JFrame jf = new JFrame("Duck Hunt");
        jf.setContentPane(new DuckHunt().background);
        jf.setLocation(300, 100);
        jf.setMinimumSize(new Dimension(400, 400));
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.pack();
        jf.setVisible(true);
    }
}

