import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import javax.sound.sampled.*;

public class Eggs extends JPanel implements Runnable {

    Thread th;
    Graphics2D g;
    int dx, dy;
    Bowl b = new Bowl();
    int NUM = 5;
    Egg egg[] = new Egg[NUM];
    EggMover movers[] = new EggMover[NUM];
    boolean inside = false;
    Timer timer[] = new Timer[NUM];
    static int FWIDTH = 500, FHEIGHT = 400;
    int in = 0;
    int speed = 10;
    int lifes = 10;
    static Color c1 = Color.orange, c2 = Color.yellow;
    BufferedImage backgroundImage;
    BufferedImage chicken;
    boolean paused = false;
    boolean gameRunning = false;
    // for start screen
    boolean showStartScreen = true;

    // for the pause button
    Clip startScreenMusic;

    public Eggs() {

        // Existing code...

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/Background.jpg"));
        } catch (IOException ex) {
            System.out.println("Error loading background image: " + ex.getMessage());
        }

        // music

        b.setMx(30);
        b.setMy(200);
        setOpaque(false);
        try {
            setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
                    new Font("Tahoma", 1, 24), new Color(204, 0, 0)));
            for (LookAndFeelInfo inf : UIManager.getInstalledLookAndFeels()) {
                if (inf.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(inf.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        // Listener Starts//

        for (int i = 0; i < egg.length; ++i) {
            egg[i] = new Egg();
            egg[i].setMy(30);
            egg[i].reset();
        }

        for (in = 0; in < egg.length; ++in) {
            movers[in] = new EggMover(egg[in], b);
            movers[in].setInitialDelay((in + 1) * 1500);
            movers[in].move();

        }
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                dx = (int) me.getPoint().getX();
                if (b.contains(me.getPoint())) {
                    inside = true;
                }

            }

            public void mouseReleased(MouseEvent me) {
                inside = false;
            }
        };
        addMouseListener(ml);

        MouseMotionListener mll = new MouseAdapter() {
            public void mouseDragged(MouseEvent me) {
                if (inside == true) {
                    b.setMx((int) me.getPoint().getX());
                }
            }
        };
        addMouseMotionListener(mll);

        th = new Thread(this);
        th.start();

        // Add KeyListener for space key
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!gameRunning) {
                        // Start game logic
                        for (int i = 0; i < movers.length; ++i) {
                            movers[i].move();
                        }
                        gameRunning = true;
                        showStartScreen = false;
                        startScreenMusic.stop();
                    } else {
                        // Pause game logic
                        for (int i = 0; i < movers.length; ++i) {
                            movers[i].stop();
                        }
                        gameRunning = false;
                        paused = true;

                    }
                }
            }

        });
        setFocusable(true);
    }

    public void paint(Graphics g2) {
        g = (Graphics2D) g2;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.paint(g);

        // Draw background image

        if (showStartScreen) {
            drawStartScreen(g);
        } else {
            GradientPaint gpp = new GradientPaint(getWidth() / 2, 0, c1, getWidth() / 2, getHeight(), c2);
            g.setPaint(gpp);
            g.fillRect(0, 0, getWidth(), getHeight());
            b.setMy(getHeight() - 35);
            Polygon poly = new Polygon();
            poly.addPoint((getWidth() / 3) / 2, getHeight() - 40);
            poly.addPoint(getWidth() - (getWidth() / 3) / 2, getHeight() - 40);
            poly.addPoint(getWidth() - 10, getHeight() - 10);
            poly.addPoint(10, getHeight() - 10);
            GradientPaint gp = new GradientPaint(getWidth() / 2, getHeight(), Color.BLACK, getWidth() / 2,
                    getHeight() - 40, Color.DARK_GRAY);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g.setPaint(gp);
            g.fill(poly);
            b.drawOn(g);
            for (int i = 0; i < egg.length; ++i) {
                egg[i].drawOn(g);
            }
            repaint();
        }
    }

    // for strt screen
    private void drawStartScreen(Graphics2D g) {
        try {
            chicken = ImageIO.read(getClass().getResource("/Anda.png"));
        } catch (IOException ex) {
            System.out.println("Error loading background image: " + ex.getMessage());
        }
        // Draw start screen
        // g.setColor(Color.BLACK);

        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.drawImage(chicken, 0, 0, getWidth(), getHeight(), this);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String message = "Press Enter to start";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, getWidth() / 2 - messageWidth / 2, getHeight() / 2);

    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(speed);
                if (b.lifes == 0) {
                    for (int i = 0; i < egg.length; ++i) {
                        egg[i].reset();
                        movers[i].stop();
                    }
                    JOptionPane.showMessageDialog(null, "Your Score Was " + b.score);
                    int a = JOptionPane.showConfirmDialog(null, "Do You Want To Restart The Game", "Game Over",
                            JOptionPane.OK_CANCEL_OPTION);

                    if (a == JOptionPane.OK_OPTION) {
                        for (int i = 0; i < egg.length; ++i) {
                            movers[i].setInitialDelay((i + 1) * 1500);
                            movers[i].move();
                        }
                        b.score = -10;
                        b.lifes = 10;
                        b.updateScore();

                    }
                    if (a == JOptionPane.CANCEL_OPTION) {
                        System.exit(0);
                    }
                }
                repaint();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void main(String args[]) {

        JFrame jfm = new JFrame();
        jfm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfm.setLayout(new BorderLayout());
        jfm.getContentPane().setBackground(c1);
        JLabel l = new JLabel("Your Score Along With Life Is Displayed Here");
        l.setBorder(BorderFactory.createTitledBorder(null, "Eggs!!", TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
                new Font("Tahoma", 1, 24), Color.WHITE));
        jfm.add(l, BorderLayout.NORTH);
        final Eggs eg = new Eggs();
        eg.b.setOutputComponent(l);
        KeyListener kl = new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
                    eg.b.setMx(eg.b.MX + 2);
                }
                if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
                    eg.b.setMx(eg.b.MX - 2);
                }
            }
        };
        jfm.addKeyListener(kl);
        jfm.add(eg);

        jfm.setSize(FWIDTH, FHEIGHT);
        jfm.setVisible(true);
    }
}

class EggMover {

    ActionListener al;
    Timer timer;
    Egg egg;
    Bowl b;
    boolean allowed = true;

    public EggMover(Egg eg, Bowl bl) {
        egg = eg;
        b = bl;
        al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                egg.setMy(egg.MY + 1);
                if (isAllowed()) {
                    if (egg.fallsInBowl(b)) {
                        switch (egg.type) {
                            case REGULAR:
                                egg.reset();
                                b.updateScore();
                                break;
                            case GOLDEN:
                                egg.reset();
                                b.updateScore(20);
                                break;
                            case BOMB:
                                egg.reset();
                                // Do not decrement lifes for bomb egg
                                b.lifes = 0;
                                break;
                        }
                    }
                }
                if (egg.r.y + egg.r.height > b.r.y + b.r.height / 2 && !egg.fallsInBowl(b)) {
                    allowed = false;
                    if (egg.r.y + egg.r.height * 2 >= 275 && egg.type != Egg.EggType.BOMB) {
                        egg.reset();
                        b.lifes -= 1;
                        b.score -= 10;
                        b.updateLife();
                    }
                } else {
                    allowed = true;
                }
            }
        };
        timer = new Timer(10, al);
    }

    void setInitialDelay(int i) {
        timer.setInitialDelay(i);
    }

    boolean isAllowed() {
        return allowed;
    }

    void move() {
        timer.start();
    }

    void stop() {
        timer.stop();
    }
}

class Egg {

    enum EggType {
        REGULAR, GOLDEN, BOMB
    }

    int MX, MY;
    Rectangle r;
    Random rn = new Random();
    EggType type;

    Egg() {
        // Default to regular egg
        type = EggType.REGULAR;
    }

    void setType(EggType eggType) {
        type = eggType;
    }

    boolean fallsInBowl(Bowl b) {
        return r.intersects(b.r);
    }

    void setMx(int dx) {
        MX = dx;
    }

    void setMy(int dy) {
        MY = dy;
    }

    public void reset() {
        setMy(30);
        setMx(30 + rn.nextInt(Eggs.FWIDTH - 40));
        // Randomly assign egg type
        int random = rn.nextInt(100); // Random number between 0 and 99
        if (random < 5) {
            setType(EggType.GOLDEN); // 5% chance of golden egg
        } // else if (random < 15) {
          // // setType(EggType.ROCK); // 10% chance of rock
        else if (random < 25) {
            setType(EggType.BOMB); // 10% chance of bomb
        } else {
            setType(EggType.REGULAR); // 75% chance of regular egg
        }
    }

    void drawOn(Graphics2D g) {
        switch (type) {
            case REGULAR:
                g.setColor(Color.GRAY.brighter());
                g.fillOval(MX, MY, 10, 15);
                r = new Rectangle(MX, MY, 10, 15 / 2);
                break;
            case GOLDEN:
                g.setColor(Color.YELLOW);
                g.fillOval(MX, MY, 10, 15);
                r = new Rectangle(MX, MY, 10, 15 / 2);
                break;
            case BOMB:
                g.setColor(Color.RED);
                g.fillOval(MX, MY, 10, 15);
                r = new Rectangle(MX, MY, 10, 15);
                break;
        }
    }
}

class Bowl {

    int MX, MY;
    Rectangle r;
    JLabel l;
    int score = 0, lifes = 10;

    Bowl() {
    }

    void setMx(int dx) {
        MX = dx;
    }

    void setOutputComponent(JLabel lb) {
        l = lb;
    }

    void updateScore() {
        l.setText("Score = " + (score += 10) + "          Lifes = " + (lifes));
    }

    void updateScore(int bonus) {
        l.setText("Score = " + (score += bonus) + "          Lifes = " + (lifes));
    }

    void updateLife() {
        l.setText("Score = " + (score += 10) + "          Lifes = " + (lifes));
    }

    void setMy(int dy) {
        MY = dy;
    }

    boolean contains(Point p) {
        return r.contains(p);
    }

    void drawOn(Graphics2D g) {
        GradientPaint gp1 = new GradientPaint(MX - 15, MY - 10, Color.LIGHT_GRAY, MX - 15 + 30 / 2, MY - 10 + 30 / 2,
                Color.WHITE, true);
        g.setPaint(gp1);
        g.fillArc(MX - 15, MY - 10, 30, 30, 0, -180);
        GradientPaint gp2 = new GradientPaint(MX, MY, Color.WHITE, MX, MY + 10, Color.DARK_GRAY.brighter());
        g.setPaint(gp2);
        g.fillOval(MX - 15, MY, 30, 10);
        r = new Rectangle(MX - 15, MY, 30, 20);
    }
}
