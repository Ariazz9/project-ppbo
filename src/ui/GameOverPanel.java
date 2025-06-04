package ui;

import managers.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Game Over screen with animations and better visual design
 * Demonstrates advanced UI design and animation techniques
 */
public class GameOverPanel extends JPanel implements KeyListener, ActionListener {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;

    // Animation variables
    private Timer animationTimer;
    private int animationFrame = 0;
    private float titleAlpha = 0.0f;
    private float scoreAlpha = 0.0f;
    private float buttonAlpha = 0.0f;
    private boolean animationComplete = false;

    // Game data
    private int finalScore = 0;
    private int selectedIndex = 0;
    private List<String> menuOptions;

    // Visual elements
    private List<Particle> particles;
    private Font titleFont;
    private Font scoreFont;
    private Font buttonFont;
    private Font instructionFont;

    // Audio
    private SoundManager soundManager;

    // Action listener interface
    public interface GameOverActionListener {
        void onRestartGame();
        void onReturnToMainMenu();
    }

    private GameOverActionListener gameOverActionListener;

    public GameOverPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        soundManager = SoundManager.getInstance();

        initializeFonts();
        initializeMenuOptions();
        initializeParticles();

        animationTimer = new Timer(50, this);
    }

    /**
     * Initialize fonts for different UI elements
     */
    private void initializeFonts() {
        titleFont = new Font("Arial", Font.BOLD, 48);
        scoreFont = new Font("Arial", Font.BOLD, 32);
        buttonFont = new Font("Arial", Font.BOLD, 24);
        instructionFont = new Font("Arial", Font.PLAIN, 14);
    }

    /**
     * Initialize menu options
     */
    private void initializeMenuOptions() {
        menuOptions = new ArrayList<>();
        menuOptions.add("RESTART GAME");
        menuOptions.add("MAIN MENU");
    }

    /**
     * Initialize particle effects for background
     */
    private void initializeParticles() {
        particles = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle());
        }
    }

    /**
     * Set the final score and prepare for display
     */
    public void setFinalScore(int score) {
        this.finalScore = score;
    }

    /**
     * Set the game over action listener
     */
    public void setGameOverActionListener(GameOverActionListener listener) {
        this.gameOverActionListener = listener;
    }

    /**
     * Start the entrance animation
     */
    public void startAnimation() {
        animationFrame = 0;
        titleAlpha = 0.0f;
        scoreAlpha = 0.0f;
        buttonAlpha = 0.0f;
        animationComplete = false;
        selectedIndex = 0;

        // Reset particles
        for (Particle particle : particles) {
            particle.reset();
        }

        animationTimer.start();
    }

    /**
     * Stop animation and clean up
     */
    public void cleanup() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw animated background particles
        drawParticles(g2d);

        // Draw game over title with fade-in effect
        drawTitle(g2d);

        // Draw final score with fade-in effect
        drawScore(g2d);

        // Draw menu options with fade-in effect
        drawMenuOptions(g2d);

        // Draw instructions
        drawInstructions(g2d);
    }

    /**
     * Draw animated background particles
     */
    private void drawParticles(Graphics2D g2d) {
        for (Particle particle : particles) {
            particle.render(g2d);
        }
    }

    /**
     * Draw animated game over title
     */
    private void drawTitle(Graphics2D g2d) {
        if (titleAlpha <= 0) return;

        g2d.setFont(titleFont);
        String title = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(title)) / 2;
        int y = 120;

        // Create pulsing effect
        float pulse = (float) (0.8f + 0.2f * Math.sin(animationFrame * 0.1));
        int alpha = (int) (titleAlpha * 255 * pulse);

        // Draw title with glow effect
        g2d.setColor(new Color(255, 100, 100, Math.min(alpha / 2, 100)));
        g2d.drawString(title, x + 3, y + 3);

        g2d.setColor(new Color(255, 255, 255, alpha));
        g2d.drawString(title, x, y);
    }

    /**
     * Draw final score with animation
     */
    private void drawScore(Graphics2D g2d) {
        if (scoreAlpha <= 0) return;

        g2d.setFont(scoreFont);
        String scoreText = "FINAL SCORE: " + finalScore;
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(scoreText)) / 2;
        int y = 200;

        int alpha = (int) (scoreAlpha * 255);
        g2d.setColor(new Color(255, 255, 100, alpha));
        g2d.drawString(scoreText, x, y);

        // Draw score rank
        String rank = getScoreRank(finalScore);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(rank)) / 2;
        y = 230;
        g2d.setColor(new Color(100, 255, 100, alpha));
        g2d.drawString(rank, x, y);
    }

    /**
     * Get score rank based on final score
     */
    private String getScoreRank(int score) {
        if (score >= 1000) return "LEGENDARY PILOT!";
        else if (score >= 500) return "ACE PILOT!";
        else if (score >= 200) return "SKILLED PILOT";
        else if (score >= 100) return "ROOKIE PILOT";
        else return "CADET";
    }

    /**
     * Draw menu options with selection highlighting
     */
    private void drawMenuOptions(Graphics2D g2d) {
        if (buttonAlpha <= 0) return;

        g2d.setFont(buttonFont);
        FontMetrics fm = g2d.getFontMetrics();

        int startY = 320;
        int spacing = 60;

        for (int i = 0; i < menuOptions.size(); i++) {
            String option = menuOptions.get(i);
            int x = (PANEL_WIDTH - fm.stringWidth(option)) / 2;
            int y = startY + (i * spacing);

            int alpha = (int) (buttonAlpha * 255);

            // Highlight selected option
            if (i == selectedIndex && animationComplete) {
                // Draw selection background
                g2d.setColor(new Color(0, 255, 255, (int)(alpha * 0.3f)));
                g2d.fillRect(x - 20, y - fm.getAscent() - 5, fm.stringWidth(option) + 40, fm.getHeight() + 10);

                // Draw selection border
                g2d.setColor(new Color(0, 255, 255, alpha));
                g2d.drawRect(x - 20, y - fm.getAscent() - 5, fm.stringWidth(option) + 40, fm.getHeight() + 10);

                // Draw arrow indicators
                g2d.drawString(">", x - 40, y);
                g2d.drawString("<", x + fm.stringWidth(option) + 20, y);

                g2d.setColor(new Color(255, 255, 255, alpha));
            } else {
                g2d.setColor(new Color(200, 200, 200, alpha));
            }

            g2d.drawString(option, x, y);
        }
    }

    /**
     * Draw control instructions
     */
    private void drawInstructions(Graphics2D g2d) {
        if (!animationComplete) return;

        g2d.setFont(instructionFont);
        g2d.setColor(Color.GRAY);

        String[] instructions = {
                "Use UP/DOWN arrows to navigate",
                "Press ENTER to select"
        };

        int y = PANEL_HEIGHT - 60;
        for (String instruction : instructions) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (PANEL_WIDTH - fm.stringWidth(instruction)) / 2;
            g2d.drawString(instruction, x, y);
            y += 20;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        animationFrame++;

        // Update particle animations
        for (Particle particle : particles) {
            particle.update();
        }

        // Control fade-in animation timing
        if (animationFrame > 20 && titleAlpha < 1.0f) {
            titleAlpha += 0.05f;
        }
        if (animationFrame > 40 && scoreAlpha < 1.0f) {
            scoreAlpha += 0.05f;
        }
        if (animationFrame > 60 && buttonAlpha < 1.0f) {
            buttonAlpha += 0.05f;
        }

        // Mark animation as complete
        if (animationFrame > 80) {
            animationComplete = true;
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!animationComplete) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedIndex = (selectedIndex - 1 + menuOptions.size()) % menuOptions.size();
                soundManager.playSound(SoundManager.MENU_HOVER);
                break;
            case KeyEvent.VK_DOWN:
                selectedIndex = (selectedIndex + 1) % menuOptions.size();
                soundManager.playSound(SoundManager.MENU_HOVER);
                break;
            case KeyEvent.VK_ENTER:
                selectCurrentOption();
                break;
        }
        repaint();
    }

    /**
     * Execute action for currently selected option
     */
    private void selectCurrentOption() {
        soundManager.playSound(SoundManager.MENU_SELECT);
        animationTimer.stop();

        if (gameOverActionListener != null) {
            switch (selectedIndex) {
                case 0: // RESTART GAME
                    gameOverActionListener.onRestartGame();
                    break;
                case 1: // MAIN MENU
                    gameOverActionListener.onReturnToMainMenu();
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * Particle class for background animation effects
     */
    private class Particle {
        private float x, y;
        private float velocityX, velocityY;
        private float size;
        private Color color;
        private float alpha;
        private float life;
        private float maxLife;

        public Particle() {
            reset();
        }

        public void reset() {
            x = (float) (Math.random() * PANEL_WIDTH);
            y = (float) (Math.random() * PANEL_HEIGHT);
            velocityX = (float) (Math.random() * 2 - 1);
            velocityY = (float) (Math.random() * 2 - 1);
            size = (float) (Math.random() * 4 + 1);

            // Random colors for particles
            int r = (int) (Math.random() * 100 + 155);
            int g = (int) (Math.random() * 100 + 155);
            int b = (int) (Math.random() * 100 + 155);
            color = new Color(r, g, b);

            maxLife = (float) (Math.random() * 200 + 100);
            life = maxLife;
            alpha = 1.0f;
        }

        public void update() {
            x += velocityX;
            y += velocityY;
            life--;
            alpha = life / maxLife;

            // Reset particle when it dies or goes off screen
            if (life <= 0 || x < 0 || x > PANEL_WIDTH || y < 0 || y > PANEL_HEIGHT) {
                reset();
            }
        }

        public void render(Graphics2D g2d) {
            int alphaValue = (int) (alpha * 100);
            if (alphaValue > 0) {
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaValue));
                g2d.fillOval((int) x, (int) y, (int) size, (int) size);
            }
        }
    }
}
