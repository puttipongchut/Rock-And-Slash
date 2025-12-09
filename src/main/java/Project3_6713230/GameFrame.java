/**
 *
 * @author 6713230_Natwarit Chuboonsub
 * @author 6713229_Nutthapat Techapornhiran
 * @author 6713235_Teetath Prapasanon
 * @author 6713239_Nitich Uanjityanon
 * @author 6713243_Puttipong Chutipongwanit
 */
package Project3_6713230;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.ArrayList;
import java.util.Random;

public class GameFrame extends JFrame {

    private JPanel contentpane;
    private JLabel drawpane;
    private JLabel hpLabel, staminaLabel;
    private JProgressBar hpBar, staminaBar;
    private JLabel potionLabel, potion;
    private GameIcon potionIcon;
    private JLabel timerLabel;
    private long lastTime;
    private double elaspedSecond;
    private GameSoundEffect themeSound;
    private GameSoundEffect bossTheme;
    private GameSoundEffect VictorySound;
    private Timer timer;
    private int staminaCounter = 0;

    private String[] backgroundFilePaths;
    private GameIcon currentBackgroundIcon;

    private ArrayList<Monsters> monsters = new ArrayList<>();
    private Golem golemBoss;
    private ArrayList<Rock> rocks = new ArrayList<>();
    private GameMap gameMap = new GameMap();
    private String gameDifficulty;
    private int currentBgIndex = gameMap.getCurrentLevel();

    private Knight mc;
    private GameFrame currentFrame;

    private boolean movingLeft = false;
    private boolean movingRight = false;

    private int framewidth = GameConstants.FRAME_WIDTH;
    private int frameheight = GameConstants.FRAME_HEIGHT;

    private boolean isPaused = false;
    private JDialog pauseDialog;
    private JDialog gameOverDialog;

    private float currentVolume = 0.2f;

    public JLabel getDrawPane() {
        return drawpane;
    }

    public int getStaminaCounter() {
        return staminaCounter;
    }

    public GameFrame(String difficulty) {
        setTitle("Rock & Slash Game");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        currentFrame = this;
        contentpane = (JPanel) getContentPane();
        contentpane.setLayout(new BorderLayout());
        contentpane.setPreferredSize(new Dimension(framewidth, frameheight));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        this.gameDifficulty = difficulty;

        addComponent();
        spawnMonsters();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                    return;
                }
                if (isPaused) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    movingLeft = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    movingRight = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) {
                    mc.jump();
                    mc.isJumping = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_J) {
                    mc.attack();
                }
                if (e.getKeyCode() == KeyEvent.VK_K || e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    mc.roll();
                }
                if (e.getKeyCode() == KeyEvent.VK_L) {
                    mc.regenHp();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (isPaused) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    movingLeft = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    movingRight = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) {
                    mc.isJumping = false;
                }
            }
        });

        timer = new Timer(GameConstants.FRAME_RATE_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused) {
                    return;
                }

                if (mc.isRolling()) {
                    if (System.currentTimeMillis() - mc.getRollStartTime() > 300) {

                        if (!mc.getTurn() && movingLeft) {
                            mc.cancelRoll();
                        } else if (mc.getTurn() && movingRight) {
                            mc.cancelRoll();
                        }
                    }
                }

                if (movingLeft && !movingRight) {
                    mc.moveLeft();
                } else if (movingRight && !movingLeft) {
                    mc.moveRight();
                } else {
                    mc.stopMoving();
                }
                mc.updateLocation();

                for (Monsters m : monsters) {
                    m.updateBehavior(mc);
                    m.updateLocation();

                    if (m.isAttacking() && m.isHitboxActive()) {
                        Rectangle monsterAttackBox = m.getAttackLabel().getBounds();
                        Rectangle mcHitBox = mc.getHitBox();

                        if (monsterAttackBox.intersects(mcHitBox)) {
                            mc.damage(m.getAttackPoint());
                        }
                    }

                    if (!m.isDead() && mc.getHitBox().intersects(m.getHitBox())) {
                        mc.damage(m.getAttackPoint());
                    }

                    if (mc.isAttacking() && mc.getAttackHitBox().intersects(m.getHitBox())) {
                        m.damage(mc.getAttackPoint(), mc.getTurn());
                    }
                }

                if (golemBoss != null) {
                    golemBoss.updateBehavior(mc);
                    golemBoss.updateLocation();
                    // Boss Damage to Player
                    if (!golemBoss.isDead()) {
                        // Damage 1: Collision/Slide/Jump Damage (Move 2/3)
                        if (golemBoss.isCollisionDamageActive() && mc.getHitBox().intersects(golemBoss.getHitBox())) {
                            mc.damage(GameConstants.GOLEM_JUMP_SLIDE_DMG);
                        }
                        // Damage 2: Attack 1 Melee Hit
                        if (golemBoss.isAttack1Active() && mc.getHitBox().intersects(golemBoss.getAttack1HitBox())) {
                            mc.damage(GameConstants.GOLEM_MELEE_DMG);
                        }
                        // Damage 3: GroundHit Particle Damage
                        if (golemBoss.isParticleDamageActive() && mc.getHitBox().intersects(golemBoss.getParticleHitBox())) {
                            mc.damage(GameConstants.GOLEM_MELEE_DMG); // ใช้ดาเมจเบาสำหรับ particle
                        }
                        // Player Damage to Boss
                        if (mc.isAttacking() && mc.getAttackHitBox().intersects(golemBoss.getHitBox())) {
                            golemBoss.damage(mc.getAttackPoint());
                        }
                    }
                }
                ArrayList<Rock> rocksToRemove = new ArrayList<>();
                for (Rock r : rocks) {
                    r.updateLocation();
                    if (mc.isAttacking() && mc.getAttackHitBox().intersects(r.getHitBox())) {
                        if (!r.isDestroyed() && !r.isParticle()) {
                            r.destroyAndParticle();
                            System.out.println("Rock hit by Knight's attack!");
                        }
                    }
                    if (r.getHitBox().intersects(mc.getHitBox())) {
                        // Rock ชนผู้เล่น
                        if (!r.isDestroyed() && !r.isParticle()) {
                            mc.damage(GameConstants.GOLEM_ROCK_DMG); // สมมติว่ามี GOLEM_ROCK_DAMAGE
                            r.destroyAndParticle(); // เปลี่ยนเป็น Particle
                        }
                    }
                    if (r.isDestroyed()) {
                        rocksToRemove.add(r);
                    }
                }
                // Cleanup Golem
                if (golemBoss != null && golemBoss.isDeathAnimationFinished()) {
                    drawpane.remove(golemBoss);
                    drawpane.remove(golemBoss.getAttackLabel());
                    drawpane.remove(golemBoss.getParticleLabel());
                    drawpane.remove(golemBoss.getJumpLabel());
                    golemBoss = null;

                    if (gameOverDialog == null) {
                        showVictory();
                    }
                }

                // Cleanup Rocks
                for (Rock r : rocksToRemove) {
                    rocks.remove(r);
                    drawpane.remove(r);
                }

                monsters.removeIf(m -> {
                    if (m.isDead() && m.isDeathAnimationFinished()) {
                        drawpane.remove(m);
                        drawpane.remove(m.getAttackLabel());
                        return true;
                    }
                    return false;
                });

                hpLabel.setText("HP: " + mc.getHp());
                hpBar.setValue(mc.getHp());
                staminaLabel.setText("Stamina: " + mc.getStamina());
                staminaBar.setValue(mc.getStamina());
                potionLabel.setText("Potion: " + mc.getPotion());
                timerLabel.setText("Time: " + String.format("%.1f", elaspedSecond));

                // Check if player died
                if (mc.getHp() <= 0) {
                    if (gameOverDialog == null && mc.isDeathAnimetionFinished()) {
                        showGameOver();
                    }
                }

                if (mc.getX() > 910 && !gameMap.FinalLevel()) {
                    if (currentBgIndex == backgroundFilePaths.length - 1) {
                        movingRight = false;
                        mc.updateLocation();
                    } else {
                        gameMap.nextLevel();
                        gameMap.clearMapVisuals(drawpane);
                        mc.setStartPosition();

                        currentBgIndex++;
                        Image newBg = AssetLoader.loadImage(backgroundFilePaths[currentBgIndex]);
                        if (newBg == null) {
                            System.err.println("Failed to load background image: " + backgroundFilePaths[currentBgIndex]);
                            return;
                        }
                        newBg = newBg.getScaledInstance(framewidth, frameheight, Image.SCALE_SMOOTH);
                        currentBackgroundIcon = new GameIcon(newBg);

                        drawpane.setIcon(currentBackgroundIcon);

                        gameMap.drawMap(drawpane);
                        drawpane.remove(mc);
                        drawpane.add(mc);
                        spawnMonsters();
                    }
                }

                drawpane.revalidate();
                repaint();

                staminaCounter++;

                if (staminaCounter >= 188 && mc.getHp() > 0 && mc.getStamina() < 50) {
                    mc.regenStamina();
                    staminaCounter = 0;
                }
            }
        });
        timer.start();
    }

    /*====================Classes for Graphic====================*/
    class DialogButton extends JButton {

        private Color baseColor;
        private boolean isHovered = false;

        public DialogButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setFont(new Font("Serif", Font.BOLD, 20));
            setForeground(new Color(210, 205, 195));
            //set as ปุ่มใส
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            //Button background
            if (isHovered) {
                g2d.setColor(baseColor.brighter());
            } else {
                g2d.setColor(baseColor);
            }
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            //Button border
            if (isHovered) {
                g2d.setColor(new Color(150, 145, 135));
            } else {
                g2d.setColor(new Color(100, 95, 85));
            }
            g2d.setStroke(new BasicStroke(isHovered ? 3 : 2));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            super.paintComponent(g);
        }
    }

    //Pause
    class PauseBgPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            GradientPaint gradient = new GradientPaint(0, 0, new Color(8, 8, 12), 0, getHeight(), new Color(18, 16, 20));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            //Border
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(new Color(80, 75, 70, 180));
            g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
            //inner border
            g2d.setColor(new Color(120, 110, 100, 30));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(13, 13, getWidth() - 26, getHeight() - 26, 12, 12);
        }
    }

    //GameOver
    class GameOverBgPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(12, 5, 8),
                    0, getHeight(), new Color(20, 8, 10)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(new Color(100, 40, 45, 180));
            g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);

            g2d.setColor(new Color(150, 60, 70, 30));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(13, 13, getWidth() - 26, getHeight() - 26, 12, 12);
        }
    }

    //Victory
    class VictoryBgPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            GradientPaint gradient = new GradientPaint(0, 0, new Color(229, 187, 92), 0, getHeight(), new Color(236, 204, 133));

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            //Outer border
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(new Color(180, 140, 60, 200));
            g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);

            //Inner border
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(new Color(180, 140, 60, 200));
            g2d.drawRoundRect(13, 13, getWidth() - 26, getHeight() - 26, 12, 12);
        }
    }
//==============================================================================

    private void showVictory() {
        JDialog victoryDialog = new JDialog(this, "VICTORY", true);
        victoryDialog.setSize(400, 420);
        victoryDialog.setLocationRelativeTo(this);
        victoryDialog.setUndecorated(true);
        bossTheme.stop();

        new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movingLeft = false;
                movingRight = false;
                VictorySound.playOnce();
                ((Timer) e.getSource()).stop();

                VictoryBgPanel mainPanel = new VictoryBgPanel();
                mainPanel.setLayout(null);
                victoryDialog.setContentPane(mainPanel);

                // Title Label
                JLabel titleLabel = new JLabel("VICTORY!", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
                titleLabel.setForeground(new Color(80, 65, 35));
                titleLabel.setBounds(0, 40, 400, 70);
                mainPanel.add(titleLabel);

                JLabel timeLabel = new JLabel("Time: " + String.format("%.2f", elaspedSecond) + " seconds", SwingConstants.CENTER);
                timeLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
                timeLabel.setForeground(new Color(80, 65, 35));
                timeLabel.setBounds(0, 120, 400, 30);
                mainPanel.add(timeLabel);

                // Trophy/Victory Icon (optional - ใช้ emoji)
                JLabel trophyLabel = new JLabel("🏆", SwingConstants.CENTER);
                trophyLabel.setFont(new Font("Serif", Font.PLAIN, 50));
                trophyLabel.setBounds(0, 220, 400, 60);
                mainPanel.add(trophyLabel);

                // Main Menu Button
                DialogButton mainMenuButton = new DialogButton("MAIN MENU", new Color(80, 65, 35));
                mainMenuButton.setBounds(100, 310, 200, 50);
                mainMenuButton.addActionListener(ex -> {
                    victoryDialog.dispose();
                    timer.stop();
                    dispose();
                    MenuSystem menu = new MenuSystem();
                    menu.setVisible(true);
                });
                mainPanel.add(mainMenuButton);

                victoryDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                victoryDialog.setVisible(true);
            }
        }).start();
    }

    public void addComponent() {
        drawpane = new JLabel();

        backgroundFilePaths = new String[]{
            GameConstants.FILE_BG_MAP1,
            GameConstants.FILE_BG_MAP2,
            GameConstants.FILE_BG_MAP3,
            GameConstants.FILE_BG_MAP4,
            GameConstants.FILE_BG_MAP5,
            GameConstants.FILE_BG_MAP6
        };

        Image initialBg = AssetLoader.loadImage(backgroundFilePaths[currentBgIndex]);
        if (initialBg == null) {
            System.err.println("Failed to load background image: " + backgroundFilePaths[currentBgIndex]);
            return;
        }
        initialBg = initialBg.getScaledInstance(framewidth, frameheight, Image.SCALE_SMOOTH);
        currentBackgroundIcon = new GameIcon(initialBg);

        gameMap.drawMap(drawpane);
        drawpane.revalidate();
        drawpane.repaint();

        drawpane.setIcon(currentBackgroundIcon);
        drawpane.setLayout(null);

        mc = new Knight(currentFrame, gameMap, gameDifficulty);

//        Border hitbox = BorderFactory.createLineBorder(Color.RED, 2);
//        mc.setBorder(hitbox);
        hpLabel = new JLabel("HP: " + mc.getHp());
        hpLabel.setBounds(10, 40, 150, 30);
        hpLabel.setForeground(Color.WHITE);
        hpLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        hpBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, mc.getHp());
        hpBar.setBounds(10, 10, 200, 30);
        hpBar.setValue(mc.getHp());
        hpBar.setForeground(Color.RED);
        hpBar.setBackground(Color.DARK_GRAY);

        staminaLabel = new JLabel("Stamina: " + mc.getStamina());
        staminaLabel.setBounds(220, 40, 150, 30);
        staminaLabel.setForeground(Color.WHITE);
        staminaLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        staminaBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, mc.getStamina());
        staminaBar.setBounds(220, 10, 200, 30);
        staminaBar.setValue(mc.getStamina());
        staminaBar.setForeground(Color.ORANGE);
        staminaBar.setBackground(Color.DARK_GRAY);

        potionLabel = new JLabel("Potion: " + mc.getPotion());
        potionLabel.setBounds(480, 15, 150, 30);
        potionLabel.setForeground(Color.WHITE);
        potionLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        potionIcon = new GameIcon(GameConstants.FILE_POTION).resize(40, 40);
        potion = new JLabel();
        potion.setBounds(430, 10, 40, 40);
        potion.setIcon(potionIcon);

        timerLabel = new JLabel("Time: " + String.format("%.1f", elaspedSecond));
        timerLabel.setBounds(820, 15, 150, 30);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        drawpane.add(hpLabel);
        drawpane.add(hpBar);
        drawpane.add(staminaLabel);
        drawpane.add(staminaBar);
        drawpane.add(potionLabel);
        drawpane.add(potion);
        drawpane.add(timerLabel);

        drawpane.add(mc.getAttackLabel());
        drawpane.add(mc.getRollLabel());
        drawpane.add(mc);

        themeSound = new GameSoundEffect(GameConstants.FILE_THEME);
        themeSound.playLoop();
        themeSound.setVolume(GameSoundEffect.masterVolume * currentVolume);

        bossTheme = new GameSoundEffect(GameConstants.FILE_BOSS_THEME);
        bossTheme.setVolume(GameSoundEffect.masterVolume * currentVolume);

        VictorySound = new GameSoundEffect(GameConstants.FILE_VICTORY_SFX);
        VictorySound.setVolume(GameSoundEffect.masterVolume * currentVolume);

        contentpane.add(drawpane);
        startTimer();
        validate();
        repaint();
    }

    private void spawnMonsters() {
        for (Monsters m : monsters) {
            m.stopAllSounds();
            drawpane.remove(m.getAttackLabel());
            drawpane.remove(m);
        }
        monsters.clear();

        if (gameMap.getCurrentLevel() != 5 && golemBoss != null) {
            drawpane.remove(golemBoss);
            drawpane.remove(golemBoss.getAttackLabel());
            drawpane.remove(golemBoss.getParticleLabel());
            drawpane.remove(golemBoss.getParticleLabel2());
            drawpane.remove(golemBoss.getJumpLabel());
            golemBoss = null;
            bossTheme.stop();

            for (Rock m : rocks) {
                drawpane.remove(m);
            }
            rocks.clear();
        }

        Random rand = new Random();
        final int TILE_SIZE = GameConstants.TILE_SIZE;
        final int MIN_X = 50;
        final int MAX_X = 900;
        final int MIN_Y = 50;
        final int MAX_Y = 550;
        final int AIR_MAX_Y = 300;

        if (gameMap.getCurrentLevel() == 0) {
            monsters.add(new Skeleton(this, gameMap, 300, 400, gameDifficulty));
            monsters.add(new Skeleton(this, gameMap, 600, 400, gameDifficulty));
        } else if (gameMap.getCurrentLevel() == 1) {
            monsters.add(new Skeleton(this, gameMap, 600, 300, gameDifficulty));
        } else if (gameMap.getCurrentLevel() == 5) {
            themeSound.stop();
            bossTheme.playLoop();
            golemBoss = new Golem(this, gameMap, 300, 350, gameDifficulty);
        } else {
            // Random spawning for other levels
            int minMonsters = 4;
            int maxMonsters = 7;
            int numToSpawn = rand.nextInt(maxMonsters - minMonsters + 1) + minMonsters;

            for (int i = 0; i < numToSpawn; i++) {
                int monsterType = rand.nextInt(2);
                int startX = 0;
                int startY = 0;

                if (monsterType == 0) {
                    // Spawn Skeleton
                    int skeletonHeight = GameConstants.SKELETON_HEIGHT;
                    int skeletonWidth = GameConstants.SKELETON_WIDTH;

                    boolean validSpawnFound = false;
                    for (int attempt = 0; attempt < 100; attempt++) {
                        startX = rand.nextInt(MAX_X - MIN_X + 1) + MIN_X;

                        int minRow = MIN_Y / TILE_SIZE;
                        int maxRow = MAX_Y / TILE_SIZE;
                        int randomStandingRow = rand.nextInt(maxRow - minRow) + minRow;
                        int groundLevelY = randomStandingRow * TILE_SIZE;
                        startY = groundLevelY - skeletonHeight;

                        int checkY_below_ground = groundLevelY + 1;
                        boolean hasSupport = gameMap.isSolid(startX + 10, checkY_below_ground)
                                && gameMap.isSolid(startX + skeletonWidth - 10, checkY_below_ground);

                        boolean notCeiling = !gameMap.isSolid(startX + 10, startY + 1)
                                && !gameMap.isSolid(startX + skeletonWidth - 10, startY + 1);

                        boolean notInMap = !gameMap.isSolid(startX, startY)
                                && !gameMap.isSolid(startX + skeletonWidth - 1, startY)
                                && !gameMap.isSolid(startX, startY + skeletonHeight - 1)
                                && !gameMap.isSolid(startX + skeletonWidth - 1, startY + skeletonHeight - 1)
                                && !gameMap.isSolid(startX + skeletonWidth / 2, startY + skeletonHeight / 2);

                        boolean isLethalArea = gameMap.isLethal(startX, startY)
                                && gameMap.isLethal(startX + skeletonWidth - 1, startY)
                                && gameMap.isLethal(startX, startY + skeletonHeight - 1)
                                && gameMap.isLethal(startX + skeletonWidth - 1, startY + skeletonHeight - 1)
                                && gameMap.isLethal(startX + skeletonWidth / 2, startY + skeletonHeight / 2);

                        boolean isHazardArea = gameMap.isHazard(startX, startY)
                                && gameMap.isHazard(startX + skeletonWidth - 1, startY)
                                && gameMap.isHazard(startX, startY + skeletonHeight - 1)
                                && gameMap.isHazard(startX + skeletonWidth - 1, startY + skeletonHeight - 1)
                                && gameMap.isHazard(startX + skeletonWidth / 2, startY + skeletonHeight / 2);

                        if (hasSupport && !isLethalArea && notCeiling && notInMap && !isHazardArea) {
                            validSpawnFound = true;
                            break;
                        }
                    }

                    if (validSpawnFound) {
                        monsters.add(new Skeleton(this, gameMap, startX, startY, gameDifficulty));
                    }
                } else {
                    // Spawn Bat (if Bat class exists)
                    try {
                        while (true) {
                            startX = rand.nextInt(MAX_X - MIN_X + 1) + MIN_X;

                            if (rand.nextBoolean()) {
                                startY = rand.nextInt(AIR_MAX_Y - MIN_Y + 1) + MIN_Y;
                            } else {
                                startY = rand.nextInt(MAX_Y - AIR_MAX_Y + 1) + AIR_MAX_Y;
                            }

                            int checkX_center = startX + GameConstants.BAT_WIDTH / 2;
                            int checkY_center = startY + GameConstants.BAT_HEIGHT / 2;

                            boolean isColliding = gameMap.isSolid(checkX_center, checkY_center)
                                    || gameMap.isLethal(checkX_center, checkY_center)
                                    || gameMap.isHazard(checkX_center, checkY_center);

                            if (!isColliding) {
                                break;
                            }
                        }
                        monsters.add(new Bat(this, gameMap, startX, startY, gameDifficulty));
                    } catch (Exception ex) {
                        monsters.add(new Skeleton(this, gameMap, startX, startY, gameDifficulty));
                    }
                }
            }
        }

        for (Monsters m : monsters) {
            drawpane.add(m);
            drawpane.add(m.getAttackLabel());
        }
        drawpane.revalidate();
        drawpane.repaint();
    }

    public void spawnRock(int x, int y, int vx, int vy) {
        Rock newRock = new Rock(this, gameMap, x, y, vx, vy);
        rocks.add(newRock);
        drawpane.add(newRock);
    }

    private void togglePause() {
        if (gameOverDialog != null && gameOverDialog.isVisible()) {
            return;
        }

        isPaused = !isPaused;

        if (isPaused) {
            showPauseMenu();
        }
    }

    private void showPauseMenu() {
        movingLeft = false;
        movingRight = false;
        mc.stopAllSounds();
        for (Monsters m : monsters) {
            m.stopAllSounds();
        }
        if (gameMap.getCurrentLevel() == 5) {
            bossTheme.stop();
        } else {
            themeSound.stop();
        }

        pauseDialog = new JDialog(this, "PAUSED", true);
        pauseDialog.setSize(400, 380);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setUndecorated(true);

        // ใช้ Custom Background Panel
        PauseBgPanel mainPanel = new PauseBgPanel();
        mainPanel.setLayout(null);
        pauseDialog.setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("GAME PAUSED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(210, 205, 195));
        titleLabel.setBounds(0, 40, 400, 60);
        mainPanel.add(titleLabel);

        DialogButton resumeButton = new DialogButton("RESUME", new Color(60, 58, 62));
        resumeButton.setBounds(100, 130, 200, 50);
        resumeButton.addActionListener(e -> {
            isPaused = false;
            pauseDialog.dispose();
            pauseDialog = null;
            startTimer();
            if (gameMap.getCurrentLevel() == 5) {
                bossTheme.playLoop();
            } else {
                themeSound.playLoop();
            }
        });
        mainPanel.add(resumeButton);

        DialogButton optionsButton = new DialogButton("OPTIONS", new Color(60, 58, 62));
        optionsButton.setBounds(100, 195, 200, 50);
        optionsButton.addActionListener(e -> {
            pauseDialog.setVisible(false);
            openOptions();
            if (isPaused && pauseDialog != null) {
                pauseDialog.setVisible(true);
            }
        });
        mainPanel.add(optionsButton);

        DialogButton mainMenuButton = new DialogButton("MAIN MENU", new Color(60, 58, 62));
        mainMenuButton.setBounds(100, 260, 200, 50);
        mainMenuButton.addActionListener(e -> {
            pauseDialog.dispose();
            pauseDialog = null;
            timer.stop();
            dispose();
            if (gameMap.getCurrentLevel() == 5) {
                bossTheme.stop();
            }
            themeSound.stop();
            MenuSystem menu = new MenuSystem();
            menu.setVisible(true);
        });
        mainPanel.add(mainMenuButton);

        pauseDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isPaused = false;
                pauseDialog = null;
            }
        });

        pauseDialog.setVisible(true);
    }

    private void openOptions() {
        OptionsDialog dialog = new OptionsDialog(currentFrame, currentVolume, themeSound, gameDifficulty, true);
        dialog.setVisible(true);

        float newVolume = dialog.getCurrentVolume();

        if (currentVolume != newVolume) {
            currentVolume = newVolume;
            if (gameMap.getCurrentLevel() == 5 && bossTheme != null) {
                bossTheme.setVolume(currentVolume);
            } else if (themeSound != null) {
                themeSound.setVolume(currentVolume);
            }
        }
    }

    private void showGameOver() {
        isPaused = true;
        gameOverDialog = new JDialog(this, "GAME OVER", true);
        gameOverDialog.setSize(400, 380);
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setUndecorated(true);

        if (gameMap.getCurrentLevel() == 5) {
            bossTheme.stop();
        } else {
            themeSound.stop();
        }

        // ใช้ Custom Background Panel
        GameOverBgPanel mainPanel = new GameOverBgPanel();
        mainPanel.setLayout(null);
        gameOverDialog.setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("YOU DIED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(new Color(220, 80, 80));
        titleLabel.setBounds(0, 40, 400, 70);
        mainPanel.add(titleLabel);

        JLabel messageLabel = new JLabel("R.I.P. 💀 ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        messageLabel.setForeground(new Color(180, 170, 160));
        messageLabel.setBounds(0, 120, 400, 30);
        mainPanel.add(messageLabel);

        DialogButton restartButton = new DialogButton("RESTART", new Color(80, 40, 45));
        restartButton.setBounds(100, 180, 200, 50);
        restartButton.addActionListener(e -> {
            gameOverDialog.dispose();
            gameOverDialog = null;
            restartGame();
        });
        mainPanel.add(restartButton);

        DialogButton exitButton = new DialogButton("MAIN MENU", new Color(80, 40, 45));
        exitButton.setBounds(100, 250, 200, 50);
        exitButton.addActionListener(e -> {
            gameOverDialog.dispose();
            gameOverDialog = null;
            timer.stop();
            dispose();
            MenuSystem menu = new MenuSystem();
            menu.setVisible(true);
        });
        mainPanel.add(exitButton);

        gameOverDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameOverDialog.setVisible(true);
                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    private void startTimer() {
        Thread timerThread = new Thread() {
            public void run() {
                lastTime = System.nanoTime();

                while (!isPaused) {
                    if (golemBoss != null && golemBoss.isDead()) {
                        break;
                    }
                    long now = System.nanoTime();
                    double delta = (now - lastTime) / 1_000_000_000.0;
                    lastTime = now;

                    elaspedSecond += delta;
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timerThread.start();
    }

    private void restartGame() {
        isPaused = false;
        staminaCounter = 0;
        currentBgIndex = 0;
        bossTheme = new GameSoundEffect(GameConstants.FILE_BOSS_THEME);
        bossTheme.setVolume(currentVolume);
        themeSound = new GameSoundEffect(GameConstants.FILE_THEME);
        themeSound.playLoop();
        themeSound.setVolume(currentVolume);

        Image initialBg = AssetLoader.loadImage(backgroundFilePaths[currentBgIndex]);
        if (initialBg == null) {
            System.err.println("Failed to load background image: " + backgroundFilePaths[currentBgIndex]);
            return;
        }
        initialBg = initialBg.getScaledInstance(framewidth, frameheight, Image.SCALE_SMOOTH);
        currentBackgroundIcon = new GameIcon(initialBg);

        for (Monsters m : monsters) {
            m.stopAllSounds();
            drawpane.remove(m.getAttackLabel());
            drawpane.remove(m);
        }
        monsters.clear();

        if (golemBoss != null) {
            drawpane.remove(golemBoss);
            drawpane.remove(golemBoss.getAttackLabel());
            drawpane.remove(golemBoss.getParticleLabel());
            drawpane.remove(golemBoss.getParticleLabel2());
            drawpane.remove(golemBoss.getJumpLabel());
            golemBoss = null;

            for (Rock m : rocks) {
                drawpane.remove(m);
            }
            rocks.clear();
        }

        gameMap.clearMapVisuals(drawpane);
        gameMap = new GameMap();

        movingLeft = false;
        movingRight = false;
        mc.isJumping = false;
        drawpane.remove(mc.getAttackLabel());
        drawpane.remove(mc.getRollLabel());
        drawpane.remove(mc);
        mc = new Knight(currentFrame, gameMap, gameDifficulty);
//        Border hitbox = BorderFactory.createLineBorder(Color.RED, 2);
//        mc.setBorder(hitbox);

        drawpane.setIcon(currentBackgroundIcon);
        gameMap.drawMap(drawpane);

        drawpane.add(mc.getAttackLabel());
        drawpane.add(mc.getRollLabel());
        drawpane.add(mc);

        spawnMonsters();
        elaspedSecond = 0;
        startTimer();
        drawpane.revalidate();
        drawpane.repaint();
    }
}
