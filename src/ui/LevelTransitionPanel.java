package ui;

import managers.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Level transition screen with animations and visual effects
 * Shows between levels to provide smooth gameplay transitions
 */
public class LevelTransitionPanel extends JPanel implements ActionListener {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;
    private static final int TRANSITION_DURATION = 3000; // 3 seconds

    // Animation variables
    private Timer animationTimer;
    private int animationFrame = 0;
    private float textAlpha = 0.0f;
    private float backgroundAlpha = 0.0f;
    private boolean fadeIn = true;
    private boolean transitionComplete = false;

    // Level data
    private int currentLevel = 1;

    // Visual elements
    private List<TransitionStar> stars;
    private Font levelFont;
    private Font messageFont;
    private Font instructionFont;

    // Audio
    private SoundManager soundManager;

    // Transition callback
    private Runnable onTransitionComplete;

    // Action listener interface
    public interface TransitionActionListener {
        void onTransitionComplete();
    }

    private TransitionActionListener transitionActionListener;

    public LevelTransitionPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        soundManager = SoundManager.getInstance();

        initializeFonts();
        initializeStars();

        animationTimer = new Timer(50, this);
    }

    /**
     * Initialize fonts for different UI elements
     */
    private void initializeFonts() {
        levelFont = new Font("Arial", Font.BOLD, 64);
        messageFont = new Font("Arial", Font.BOLD, 24);
        instructionFont = new Font("Arial", Font.PLAIN, 16);
    }

    /**
     * Initialize animated stars for background
     */
    private void initializeStars() {
        stars = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            stars.add(new TransitionStar());
        }
    }

    /**
     * Set the current level for display
     */
    public void setLevel(int level) {
        this.currentLevel = level;
    }

    /**
     * Set the transition action listener
     */
    public void setTransitionActionListener(TransitionActionListener listener) {
        this.transitionActionListener = listener;
    }

    /**
     * Start the level transition animation
     */
    public void startTransition(Runnable callback) {
        this.onTransitionComplete = callback;

        // Reset animation state
        animationFrame = 0;
        textAlpha = 0.0f;
        backgroundAlpha = 0.0f;
        fadeIn = true;
        transitionComplete = false;

        // Reset stars
        for (TransitionStar star : stars) {
            star.reset();
        }

        // Play transition sound
        soundManager.playSound(SoundManager.MENU_SELECT);

        animationTimer.start();
    }

    /**
     * Clean up resources
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

        // Draw animated background
        drawBackground(g2d);

        // Draw animated stars
        drawStars(g2d);

        // Draw level information
        drawLevelInfo(g2d);

        // Draw progress indicator
        drawProgressIndicator(g2d);
    }

    /**
     * Draw animated background with gradient effect
     */
    private void drawBackground(Graphics2D g2d) {
        // Create gradient background
        int alpha = (int) (backgroundAlpha * 100);
        if (alpha > 0) {
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 50, 100, alpha),
                    PANEL_WIDTH, PANEL_HEIGHT, new Color(0, 0, 50, alpha)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }
    }

    /**
     * Draw animated stars
     */
    private void drawStars(Graphics2D g2d) {
        for (TransitionStar star : stars) {
            star.render(g2d);
        }
    }

    /**
     * Draw level information with animations
     */
    private void drawLevelInfo(Graphics2D g2d) {
        if (textAlpha <= 0) return;

        int alpha = (int) (textAlpha * 255);

        // Draw "LEVEL" text
        g2d.setFont(messageFont);
        String levelText = "LEVEL";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(levelText)) / 2;
        int y = 200;

        g2d.setColor(new Color(255, 255, 255, alpha));
        g2d.drawString(levelText, x, y);

        // Draw level number with glow effect
        g2d.setFont(levelFont);
        String numberText = String.valueOf(currentLevel);
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(numberText)) / 2;
        y = 300;

        // Create pulsing glow effect
        float pulse = (float) (0.7f + 0.3f * Math.sin(animationFrame * 0.2));
        int glowAlpha = (int) (alpha * pulse * 0.5f);

        // Draw glow
        g2d.setColor(new Color(0, 255, 255, glowAlpha));
        g2d.drawString(numberText, x + 4, y + 4);

        // Draw main number
        g2d.setColor(new Color(255, 255, 255, alpha));
        g2d.drawString(numberText, x, y);

        // Draw motivational message
        g2d.setFont(messageFont);
        String message = getLevelMessage(currentLevel);
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(message)) / 2;
        y = 380;

        g2d.setColor(new Color(255, 255, 100, alpha));
        g2d.drawString(message, x, y);

        // Draw preparation instruction
        g2d.setFont(instructionFont);
        String instruction = "Get ready for increased difficulty!";
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(instruction)) / 2;
        y = 420;

        g2d.setColor(new Color(200, 200, 200, alpha));
        g2d.drawString(instruction, x, y);
    }

    /**
     * Get motivational message based on level
     */
    private String getLevelMessage(int level) {
        String[] messages = {
                "Here we go!",
                "Getting warmer!",
                "Nice progress!",
                "You're on fire!",
                "Impressive!",
                "Outstanding!",
                "Legendary!",
                "Unstoppable!",
                "Godlike!",
                "MAXIMUM POWER!"
        };

        int index = Math.min(level - 1, messages.length - 1);
        return messages[index];
    }

    /**
     * Draw progress indicator
     */
    private void drawProgressIndicator(Graphics2D g2d) {
        if (textAlpha <= 0) return;

        int alpha = (int) (textAlpha * 255);
        g2d.setColor(new Color(255, 255, 255, alpha));

        // Draw loading bar
        int barWidth = 300;
        int barHeight = 8;
        int barX = (PANEL_WIDTH - barWidth) / 2;
        int barY = 500;

        // Background bar
        g2d.setColor(new Color(100, 100, 100, alpha));
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // Progress bar
        float progress = (float) animationFrame / (TRANSITION_DURATION / 50);
        progress = Math.min(progress, 1.0f);
        int progressWidth = (int) (barWidth * progress);

        g2d.setColor(new Color(0, 255, 255, alpha));
        g2d.fillRect(barX, barY, progressWidth, barHeight);

        // Progress text
        g2d.setFont(instructionFont);
        String progressText = "Loading... " + (int)(progress * 100) + "%";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (PANEL_WIDTH - fm.stringWidth(progressText)) / 2;
        int textY = barY + barHeight + 20;

        g2d.setColor(new Color(200, 200, 200, alpha));
        g2d.drawString(progressText, textX, textY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        animationFrame++;

        // Update stars
        for (TransitionStar star : stars) {
            star.update();
        }

        // Control fade-in and fade-out timing
        int halfDuration = TRANSITION_DURATION / 100; // Convert to frames

        if (fadeIn) {
            if (backgroundAlpha < 1.0f) backgroundAlpha += 0.02f;
            if (animationFrame > 10 && textAlpha < 1.0f) textAlpha += 0.05f;

            if (animationFrame > halfDuration) {
                fadeIn = false;
            }
        } else {
            if (textAlpha > 0.0f) textAlpha -= 0.05f;
            if (backgroundAlpha > 0.0f) backgroundAlpha -= 0.02f;

            if (textAlpha <= 0 && backgroundAlpha <= 0) {
                transitionComplete = true;
            }
        }

        // Complete transition
        if (transitionComplete) {
            animationTimer.stop();
            if (onTransitionComplete != null) {
                onTransitionComplete.run();
            }
        }

        repaint();
    }

    /**
     * TransitionStar class for animated background effects
     */
    private class TransitionStar {
        private float x, y;
        private float velocityX, velocityY;
        private float size;
        private Color color;
        private float alpha;
        private float rotationSpeed;
        private float rotation;

        public TransitionStar() {
            reset();
        }

        public void reset() {
            x = (float) (Math.random() * PANEL_WIDTH);
            y = (float) (Math.random() * PANEL_HEIGHT);
            velocityX = (float) (Math.random() * 4 - 2);
            velocityY = (float) (Math.random() * 4 - 2);
            size = (float) (Math.random() * 6 + 2);

            // Bright colors for transition effect
            int brightness = (int) (Math.random() * 100 + 155);
            color = new Color(brightness, brightness, 255);

            alpha = (float) Math.random();
            rotationSpeed = (float) (Math.random() * 0.2 - 0.1);
            rotation = 0;
        }

        public void update() {
            x += velocityX;
            y += velocityY;
            rotation += rotationSpeed;

            // Pulsing alpha effect
            alpha += (float) (Math.sin(animationFrame * 0.1) * 0.01);
            alpha = Math.max(0.1f, Math.min(1.0f, alpha));

            // Wrap around screen edges
            if (x < 0) x = PANEL_WIDTH;
            if (x > PANEL_WIDTH) x = 0;
            if (y < 0) y = PANEL_HEIGHT;
            if (y > PANEL_HEIGHT) y = 0;
        }

        public void render(Graphics2D g2d) {
            int alphaValue = (int) (alpha * 200);
            if (alphaValue > 0) {
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaValue));

                // Draw star shape
                Graphics2D g2dRotated = (Graphics2D) g2d.create();
                g2dRotated.translate(x, y);
                g2dRotated.rotate(rotation);

                // Simple star shape using lines
                int halfSize = (int) (size / 2);
                g2dRotated.drawLine(-halfSize, 0, halfSize, 0);
                g2dRotated.drawLine(0, -halfSize, 0, halfSize);
                g2dRotated.drawLine(-halfSize/2, -halfSize/2, halfSize/2, halfSize/2);
                g2dRotated.drawLine(-halfSize/2, halfSize/2, halfSize/2, -halfSize/2);

                g2dRotated.dispose();
            }
        }
    }
}
