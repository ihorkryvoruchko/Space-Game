import model.GameModel;

import javax.swing.*;

public class GameWindow extends JFrame {

    private int width = 800, height = 800;
    private GamePanel gamePanel;
    private GameModel game;

    public GameWindow(GameModel game) {
        super(":: I WANNA BELIEVE ::");
        this.game = game;

        this.setSize(width, height);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocation(400,200);
        this.setResizable(false);

        this.setVisible(true);

        int pixelTitleHeight = this.getInsets().top;
        int pixelBorderRight = this.getInsets().right;
        int pixelBorderLeft = this.getInsets().left;
        int pixelBorderBottom = this.getInsets().bottom;

        this.gamePanel = new GamePanel(
                this.width - pixelBorderRight - pixelBorderLeft ,
                this.height - pixelTitleHeight - pixelBorderBottom,
                this.game
        );
        this.add(this.gamePanel);
    }

    public Timer getTimer() {
        return gamePanel.getTimer();
    }
}
