package view;

import model.GameModel;
import repository.GameRepository;
import repository.api.ApiGameRepository;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    // Space
    private int gamePanelWidth, gamePanelHeight;

    // Ship
    private Image spaceShip;
    private Image batman;
    private int shipX = 100, shipY = 100;
    private int shipWidth = 60, shipHeight = 60;

    // Key
    private boolean leftPressed, rightPressed, upPressed, downPressed;

    // Logic
    private int speed = 10; // px als Schritte
    private int delay = 40; // ms
    private Timer timer;

    // Star
    private Color starColor = Color.YELLOW;
    private int starWidth = 15, starHeight = 15;
    private int starX = 60, starY = 60;
    private int starCountdown;
    private final int starSpeed = 70; //70 mal runterzählen pro delay

    private int points = 0;
    private boolean starDiscovered = false;

    private GameModel game;
    private final GameRepository gameRepository = new ApiGameRepository();


    public GamePanel(int w, int h, GameModel game) {

        this.gamePanelWidth = w;
        this.gamePanelHeight = h;
        this.timer = new Timer(this.delay, this);
        this.game = game;

        this.setBackground(Color.BLACK);

        this.setFocusable(true);
        this.requestFocusInWindow();

        try {
            this.batman = this.loadImage();

            this.spaceShip = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/demo.png")));

            this.timer.start();
            this.fly();

        } catch (IOException e) {
            System.out.println("Das bild wurde nicht gefunden!");
        }
    }

    public Timer getTimer() {
        return timer;
    }

    private void fly() {

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        leftPressed = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = true;
                        break;
                    case KeyEvent.VK_UP:
                        upPressed = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        downPressed = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        leftPressed = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = false;
                        break;
                    case KeyEvent.VK_UP:
                        upPressed = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        downPressed = false;
                        break;
                }
            }
        };
        this.addKeyListener(keyAdapter);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        g.drawString("(" + this.game.getUserModel().getName() + ") Erkundete Planeten: " + points, 5, 30);

        g.setColor(this.starColor);
        g.fillOval(this.starX, this.starY, this.starWidth, this.starHeight);

        g.drawImage(batman, shipX + 60, shipY + 60, 100, 100, null);

        if (this.rightPressed) {
            g.drawImage(this.spaceShip, this.shipX, this.shipY, this.shipWidth, this.shipHeight, null);
        } else {
            g.drawImage(this.spaceShip, this.shipX + this.shipWidth, this.shipY, -this.shipWidth, this.shipHeight, null);
        }

    }

    private boolean collision() {
        return shipX < starX && shipX + shipWidth > starX && shipY < starY && shipY + shipHeight > starY;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (collision()) {
            points++;
            starDiscovered = true;

            this.game.setScore(this.points);
            this.game = this.gameRepository.update(this.game);
        }

        //Sterne
        starCountdown -= 1;

        if (starCountdown < 0 || starDiscovered) {

            starDiscovered = false;

            Random random = new Random();
            starX = random.nextInt(gamePanelWidth - starWidth);
            starY = random.nextInt(gamePanelHeight - starHeight);
            starCountdown = starSpeed;
        }

        if (this.leftPressed) {
            this.shipX -= this.speed;
            if (this.shipX < 0) {
                this.shipX = this.gamePanelWidth;
            }
        }

        if (this.rightPressed) {
            this.shipX += this.speed;
            if (this.shipX > this.gamePanelWidth) {
                this.shipX = 0;
            }
        }

        if (this.upPressed) {
            this.shipY -= this.speed;
            if (this.shipY < 0) {
                this.shipY = this.gamePanelHeight;
            }
        }

        if (this.downPressed) {
            this.shipY += this.speed;
            if (this.shipY > this.gamePanelHeight) {
                this.shipY = 0;
            }
        }

        this.repaint();

    }

    public Image loadImage(){
        //build a Image

        final  int a = Color.black.getRGB();
        final  int b = Color.black.getRGB();
        final  int c = Color.yellow.getRGB();

        int [] imageData = {
                a,a,a,a,a,a,a,a,a,a,b,b,b,b,b,b,b,b,b,b,b,a,a,a,a,a,a,a,a,a,a,a,
                a,a,a,a,a,a,a,b,b,b,b,b,c,c,c,c,c,c,c,b,b,b,b,b,a,a,a,a,a,a,a,a,
                a,a,a,a,a,b,b,b,c,c,c,c,c,b,c,c,c,b,c,c,c,c,c,b,b,b,a,a,a,a,a,a,
                a,a,a,b,b,b,c,c,b,b,c,c,c,b,b,b,b,b,c,c,c,b,b,c,c,b,b,b,a,a,a,a,
                a,a,b,b,c,c,c,b,b,c,c,c,c,b,c,b,c,b,c,c,c,c,b,b,c,c,c,b,b,a,a,a,
                a,b,b,c,c,b,b,b,b,c,c,c,c,b,b,b,b,b,c,c,c,c,b,b,b,b,c,c,b,b,a,a,
                a,b,c,b,b,b,b,b,b,c,c,c,c,b,b,b,b,b,c,c,c,c,b,b,b,b,b,b,c,b,a,a,
                b,b,c,b,b,b,b,b,b,b,c,c,b,b,b,b,b,b,b,c,c,b,b,b,b,b,b,b,c,b,b,a,
                b,c,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,c,b,a,
                b,c,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,c,b,a,
                b,c,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,c,b,a,
                b,c,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,c,b,a,
                b,c,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,c,b,a,
                b,b,c,b,b,b,b,c,c,b,b,b,b,b,b,b,b,b,b,b,b,b,c,c,b,b,b,b,c,b,b,a,
                a,b,c,b,b,b,c,c,c,c,b,c,c,b,b,b,b,b,c,c,b,c,c,c,c,b,b,b,c,b,a,a,
                a,b,b,c,c,b,c,c,c,c,b,c,c,c,b,b,b,c,c,c,b,c,c,c,c,b,c,c,b,b,a,a,
                a,a,b,b,c,c,b,c,c,c,c,c,c,c,c,b,c,c,c,c,c,c,c,c,b,c,c,b,b,a,a,a,
                a,a,a,b,b,b,c,c,c,c,c,c,c,c,c,b,c,c,c,c,c,c,c,c,c,b,b,b,a,a,a,a,
                a,a,a,a,a,b,b,b,c,c,c,c,c,c,c,c,c,c,c,c,c,c,c,b,b,b,a,a,a,a,a,a,
                a,a,a,a,a,a,a,b,b,b,b,b,c,c,c,c,c,c,c,b,b,b,b,b,a,a,a,a,a,a,a,a,
                a,a,a,a,a,a,a,a,a,a,b,b,b,b,b,b,b,b,b,b,b,a,a,a,a,a,a,a,a,a,a,a
        };

        return createImage(
                new MemoryImageSource( 32, 21, imageData, 0, 32 ) );
    }

}
