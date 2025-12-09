/**
 *
 * @author 6713230_Natwarit Chuboonsub
 * @author 6713229_Nutthapat Techapornhiran
 * @author 6713235_Teetath Prapasanon
 * @author 6713239_Nitich Uanjityanon
 * @author 6713243_Puttipong Chutipongwanit
 */
package Project3_6713230;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MenuSystem extends JFrame {

    private JLabel backgroundLabel;
    private String[] backgrounds = {GameConstants.FILE_CASTLE_BG, GameConstants.FILE_FOREST1_BG, GameConstants.FILE_FOREST2_BG, GameConstants.FILE_RAIN_BG};

    private int currentBgIndex = 0;
    private Timer bgTimer;
    private boolean isRandomMode = true;

    private int frameheight = GameConstants.FRAME_HEIGHT;
    private int framewidth = GameConstants.FRAME_WIDTH;

    private GameSoundEffect MenuTheme;
    private float MenuVolume = 0.5f;
    private String gameDifficulty = "Normal";

    public MenuSystem() {
        setTitle("ROCK & SLASH - Main Menu");
        setSize(framewidth, frameheight);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        backgroundLabel = new JLabel();
        updateBackground();
        backgroundLabel.setLayout(null);

        MenuTheme = new GameSoundEffect(GameConstants.FILE_MENU_THEME);
        MenuTheme.playLoop();
        MenuTheme.setVolume(MenuVolume);

        // Animated background timer
        bgTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentBgIndex = (currentBgIndex + 1) % backgrounds.length;
                updateBackground();
            }
        });
        bgTimer.start();

        // Title
        JLabel titleLabel = new JLabel();
        ImageIcon animatedIcon = AssetLoader.loadGifIcon(GameConstants.FILE_TITLE);
        if (animatedIcon == null) {
            System.err.println("Failed to load background image: " + GameConstants.FILE_TITLE);
            return;
        }
        Image title = animatedIcon.getImage();
        titleLabel.setIcon(new ImageIcon(title));
        titleLabel.setBounds(230, -100, 500, 500);
        backgroundLabel.add(titleLabel);

        // Play Button
        JButton playButton = createMenuButton("PLAY");
        playButton.setBounds(380, 280, 200, 50);
        playButton.addActionListener(e -> startGame());
        backgroundLabel.add(playButton);

        // Options Button 
        JButton optionsButton = createMenuButton("OPTIONS");
        optionsButton.setBounds(380, 350, 200, 50);
        optionsButton.addActionListener(e -> openOptions());
        backgroundLabel.add(optionsButton);

        // Credits Button 
        JButton creditsButton = createMenuButton("CREDITS");
        creditsButton.setBounds(380, 420, 200, 50);
        creditsButton.addActionListener(e -> openCredits());
        backgroundLabel.add(creditsButton);

        // Exit Button
        JButton exitButton = createMenuButton("EXIT");
        exitButton.setBounds(380, 490, 200, 50);
        exitButton.addActionListener(e -> System.exit(0));
        backgroundLabel.add(exitButton);

        add(backgroundLabel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Serif", Font.BOLD, 24));
        button.setForeground(Color.WHITE);

        button.setBackground(new Color(50, 50, 60, 200));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 60, 80, 230));
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(50, 50, 60, 200));
                button.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2));
                button.repaint();
            }
        });

        return button;
    }

    private void updateBackground() {
        ImageIcon animatedIcon = AssetLoader.loadGifIcon(backgrounds[currentBgIndex]);
        if (animatedIcon == null) {
            System.err.println("Failed to load background image: " + backgrounds[currentBgIndex]);
            return;
        }
        Image bg = animatedIcon.getImage();
        bg = bg.getScaledInstance(framewidth, frameheight, Image.SCALE_DEFAULT);
        backgroundLabel.setIcon(new ImageIcon(bg));
    }

    private void startGame() {
        bgTimer.stop();
        if (MenuTheme != null) {
            MenuTheme.stop();
        }
        dispose();
        new GameFrame(this.gameDifficulty);
    }

    private void openOptions() {
        OptionsDialog dialog = new OptionsDialog(this, MenuVolume, MenuTheme, gameDifficulty, false, getCurrentBackgroundIndex());
        dialog.setVisible(true);
        MenuVolume = dialog.getCurrentVolume();
        gameDifficulty = dialog.getSelectedDifficulty();
    }

    public int getCurrentBackgroundIndex() {
        if (isRandomMode) {
            return 4; // Random mode
        }
        return currentBgIndex;
    }

    public void changeBackground(int i) {
        if (i < 4) {
            bgTimer.stop();
            isRandomMode = false;
            currentBgIndex = i;
            updateBackground();
        } else if (i == 4) {
            isRandomMode = true;
            bgTimer.start();
        }
    }

    private void openCredits() {
        CreditsDialog dialog = new CreditsDialog(this);
        dialog.setVisible(true);
    }
}
