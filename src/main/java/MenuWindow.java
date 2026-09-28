import model.GameModel;
import model.UserModel;
import repository.GameRepository;
import repository.UserRepository;
import repository.api.ApiGameRepository;
import repository.api.ApiUserRepository;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

//Observer/Listener
public class MenuWindow extends JFrame implements ActionListener {

    private int width = 600;
    private int height = 400;

    private JButton toggleGame, toggleMusic;

    private JTextField usernameField;

    private GameWindow gameWindow;
    private boolean gameRunning, musicRunning;
    private Sequencer sequencer;

    private final UserRepository userRepository = new ApiUserRepository();
    private final GameRepository gameRepository = new ApiGameRepository();

    public MenuWindow() {
        this.setTitle("MenuWindow");
        this.setResizable(false);
        this.setLocation(800, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(width, height);

        this.viewInit();

        this.setVisible(true);
        //wer                             wo ist der Listener
        this.toggleGame.addActionListener(this);
        this.toggleMusic.addActionListener(this);
    }

    private void viewInit() {
        this.setLayout(new BorderLayout());
        JPanel panelNorth = new JPanel();
        panelNorth.setBackground(Color.BLACK);
        panelNorth.setLayout(new FlowLayout());

        JLabel nameLabel = new JLabel("Player Name:");
        nameLabel.setForeground(Color.WHITE);
        panelNorth.add(nameLabel);

        this.usernameField = new JTextField(10);
        panelNorth.add(this.usernameField);

        this.toggleGame = new JButton("Start Game");
        panelNorth.add(this.toggleGame);

        this.toggleMusic = new JButton("Start Music");
        panelNorth.add(this.toggleMusic);

        JPanel panelCenter = new JPanel();
        panelCenter.setBackground(Color.BLACK);

        JTextArea gameManual = new JTextArea();
        gameManual.setBackground(Color.BLACK);
        gameManual.setForeground(Color.WHITE);
        gameManual.setMargin(new Insets(30, 30, 30, 30));
        gameManual.setFont(new Font("Arial", Font.ITALIC, 20));

        gameManual.setText("Beware, Spock \r\n" +
                "It's Chewbacca \r\n" +
                "Set Phasers to 'StUnNiNg' \n\n" +
                "This is TREK WARS");

        panelCenter.add(gameManual);

        this.add(panelNorth, BorderLayout.NORTH);
        this.add(panelCenter, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.toggleGame) {
            if (!this.gameRunning) {
                String inputName = this.usernameField.getText().trim();

                if (inputName.isEmpty()) {
                    inputName = "Guest";
                }

                UserModel tmpUser = new UserModel();
                tmpUser.setName(inputName);

                UserModel user = this.userRepository.save(tmpUser);

                GameModel game = new GameModel();
                game.setUserModel(user);
                game.setScore(0);

                this.gameWindow = new GameWindow(
                        this.gameRepository.save(game)
                );
                gameWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        gameRunning = false;
                        toggleGame.setText("Start Game");
                        gameWindow.getTimer().stop();
                    }
                });

                this.gameRunning = true;
                this.toggleGame.setText("Stop Game");

            } else {
                this.gameWindow.dispose();
                this.toggleGame.setText("Start Game");
                this.gameRunning = false;
            }
        } else if (e.getSource() == this.toggleMusic) {
            System.out.println("Musik");

            if (!musicRunning) {
                try {
                    startMusicNow();

                    toggleMusic.setText("Stop Music");
                    musicRunning = true;

                } catch (MidiUnavailableException | InvalidMidiDataException | IOException ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                if (sequencer != null && sequencer.isRunning()) {
                    sequencer.stop();
                }
                toggleMusic.setText("Start Music");
                musicRunning = false;
            }
        }
    }

    private void startMusicNow() throws MidiUnavailableException, InvalidMidiDataException, IOException {
        sequencer = MidiSystem.getSequencer();
        var synthesizer = MidiSystem.getSynthesizer();

        synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());

        var singleSequence = MidiSystem.getSequence(new File("ImperialMarch.mid"));

        sequencer.open();
        sequencer.setSequence(singleSequence);
        sequencer.setTempoFactor(1.f);
        sequencer.start();
    }
}