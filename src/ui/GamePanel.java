package ui;

import entities.*;
import managers.GameManager;
import managers.SoundManager;
import graphics.BackgroundStarField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;
    private static final int DELAY = 16; // ~60 FPS

    private BackgroundStarField starField;
    private Timer timer;
    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<EnemyBullet> enemyBullets;
    private GameManager gameManager;
    private HUD hud;
    private SoundManager soundManager;

    // Input handling
    private boolean[] keys = new boolean[256];

    // Game action listener interface
    public interface GameActionListener {
        void onReturnToMenu();
        void onGameOver(int finalScore);
        void onLevelTransition(int level); // Add this new method
    }

    private GameActionListener gameActionListener;
    private int currentDisplayedLevel = 1;

    public GamePanel() {
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        // Initialize audio
        soundManager = SoundManager.getInstance();

        initializeGame();
    }

    private void initializeGame() {
        player = new Player(50, PANEL_HEIGHT / 2);
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        gameManager = new GameManager(currentDisplayedLevel);
        hud = new HUD();

        starField = new BackgroundStarField(120);

        timer = new Timer(DELAY, this);
    }

    public void setGameActionListener(GameActionListener listener) {
        this.gameActionListener = listener;
    }

    public void startGame() {
        // Reset game state
        enemies.clear();
        bullets.clear();
        enemyBullets.clear();
        gameManager = new GameManager(currentDisplayedLevel);
        clearKeys();

        timer.start();
    }

    public void startGameAudio() {
        soundManager.playBackgroundMusic();
    }

    public void stopGameAudio() {
        soundManager.stopBackgroundMusic();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        starField.update();
        handleInput();
        player.update();
        updateBullets();
        updateEnemies();
        updateEnemyBullets();
        checkCollisions();
        gameManager.update(enemies, PANEL_WIDTH, PANEL_HEIGHT);

        // Check for level transition
        checkLevelTransition();

        removeOffScreenObjects();
    }

    /**
     * Check if player has advanced to a new level and trigger transition
     */
    private void checkLevelTransition() {
        int currentLevel = gameManager.getLevel();
        if (currentLevel > currentDisplayedLevel) {
            currentDisplayedLevel = currentLevel;

            // Pause game temporarily for transition
            timer.stop();

            if (gameActionListener != null) {
                gameActionListener.onLevelTransition(currentLevel);
            }

            // Resume game after a short delay
            Timer resumeTimer = new Timer(3000, e -> {
                player.resetState();
                clearKeys();
                timer.start();
                ((Timer) e.getSource()).stop();
            });
            resumeTimer.setRepeats(false);
            resumeTimer.start();
        }
    }

    private void handleInput() {
        if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
            player.moveUp();
        }
        if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) {
            player.moveDown();
        }
        if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
            player.moveLeft();
        }
        if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
            player.moveRight();
        }
        if (keys[KeyEvent.VK_SPACE]) {
            Bullet bullet = player.shoot();
            if (bullet != null) {
                bullets.add(bullet);
                soundManager.playSound(SoundManager.PLAYER_SHOOT);
            }
        }
        if (keys[KeyEvent.VK_M]) {
            soundManager.toggleMute();
            keys[KeyEvent.VK_M] = false; // Prevent continuous toggling
        }
        if (keys[KeyEvent.VK_ESCAPE]) {
            pauseGame();
            keys[KeyEvent.VK_ESCAPE] = false;
        }
    }

    private void updateBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.getX() > PANEL_WIDTH) {
                bulletIterator.remove();
            }
        }
    }

    private void updateEnemies() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update();

            EnemyBullet enemyBullet = enemy.shoot();
            if (enemyBullet != null) {
                enemyBullets.add(enemyBullet);
            }

            if (enemy.getX() < -enemy.getWidth()) {
                enemyIterator.remove();
            }
        }
    }

    private void updateEnemyBullets() {
        Iterator<EnemyBullet> bulletIterator = enemyBullets.iterator();
        while (bulletIterator.hasNext()) {
            EnemyBullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.getX() < 0) {
                bulletIterator.remove();
            }
        }
    }

    private void checkCollisions() {
        // Player bullets vs enemies
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (bullet.collidesWith(enemy)) {
                    bulletIterator.remove();
                    enemyIterator.remove();
                    gameManager.addScore(enemy.getScoreValue());
                    soundManager.playSound(SoundManager.ENEMY_EXPLOSION);
                    break;
                }
            }
        }

        // Enemy bullets vs player
        Iterator<EnemyBullet> enemyBulletIterator = enemyBullets.iterator();
        while (enemyBulletIterator.hasNext()) {
            EnemyBullet bullet = enemyBulletIterator.next();
            if (bullet.collidesWith(player)) {
                enemyBulletIterator.remove();
                player.takeDamage(bullet.getDamage()); // Use bullet's damage value
                if (player.getHealth() <= 0) {
                    gameOver();
                }
            }
        }

        for (Enemy enemy : enemies){
            if (enemy.getX() < -50 && enemy == enemies.getLast()){
                gameOver();
            }
        }

        // Player vs enemies
        for (Enemy enemy : enemies) {
            if (player.collidesWith(enemy)) {
                player.takeDamage(2);
                if (player.getHealth() <= 0) {
                    gameOver();
                }
            }
        }
    }

    private void removeOffScreenObjects() {
        bullets.removeIf(bullet -> bullet.getX() > PANEL_WIDTH);
        enemyBullets.removeIf(bullet -> bullet.getX() < 0);
        enemies.removeIf(enemy -> enemy.getX() < -enemy.getWidth());
    }

    private void gameOver() {
        timer.stop();
        if (gameActionListener != null) {
            gameActionListener.onGameOver(gameManager.getScore());
        }
    }

    private void pauseGame() {
        timer.stop();
        soundManager.stopBackgroundMusic();

        int choice = JOptionPane.showConfirmDialog(this,
                "Game Paused\nReturn to menu?",
                "Paused",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION && gameActionListener != null) {
            gameActionListener.onReturnToMenu();
        } else {
            timer.start();
            soundManager.playBackgroundMusic();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        starField.render((Graphics2D) g);
        player.render(g);

        for (Bullet bullet : bullets) {
            bullet.render(g);
        }

        for (EnemyBullet bullet : enemyBullets) {
            bullet.render(g);
        }

        for (Enemy enemy : enemies) {
            enemy.render(g);
        }

        hud.render(g, gameManager.getScore(), gameManager.getLevel(), player.getHealth());
    }

    private void clearKeys() {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = false;
        }
    }

    public void resetAll(){
        // Reset game state
        enemies.clear();
        bullets.clear();
        enemyBullets.clear();
        player.setHealthToMax();
        player.resetState();
        currentDisplayedLevel = 1;
        gameManager = new GameManager(currentDisplayedLevel);
        clearKeys();

        timer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
