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
 * Main menu screen with keyboard navigation and audio feedback
 * Demonstrates proper UI design and event handling
 */
public class MainMenu extends JPanel implements KeyListener, ActionListener {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;

    // Menu options
    private List<String> menuOptions;
    private int selectedIndex = 0;

    // Fonts and colors
    private Font titleFont;
    private Font menuFont;
    private Font instructionFont;

    // Animation variables
    private Timer animationTimer;
    private List<Star> backgroundStars;
    private int titleGlowPhase = 0;

    // Audio manager
    private SoundManager soundManager;

    // Menu action listener
    private MenuActionListener menuActionListener;

    /**
     * Interface for handling menu actions
     */
    public interface MenuActionListener {
        void onStartGame();
        void onShowOptions();
        void onExitGame();
    }

    public MainMenu() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        soundManager = SoundManager.getInstance();

        initializeFonts();
        initializeMenuOptions();
        initializeBackground();
        startAnimation();
    }

    /**
     * Initialize fonts for different UI elements
     */
    private void initializeFonts() {
        titleFont = new Font("Arial", Font.BOLD, 48);
        menuFont = new Font("Arial", Font.BOLD, 24);
        instructionFont = new Font("Arial", Font.PLAIN, 14);
    }

    /**
     * Set up menu options
     */
    private void initializeMenuOptions() {
        menuOptions = new ArrayList<>();
        menuOptions.add("START GAME");
        menuOptions.add("OPTIONS");
        menuOptions.add("EXIT");
    }

    /**
     * Initialize animated background stars
     */
    private void initializeBackground() {
        backgroundStars = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            backgroundStars.add(new Star());
        }
    }

    /**
     * Start animation timer for background effects
     */
    private void startAnimation() {
        animationTimer = new Timer(50, this);
        animationTimer.start();
    }

    /**
     * Set the menu action listener
     */
    public void setMenuActionListener(MenuActionListener listener) {
        this.menuActionListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw animated background
        drawBackground(g2d);

        // Draw title
        drawTitle(g2d);

        // Draw menu options
        drawMenuOptions(g2d);

        // Draw instructions
        drawInstructions(g2d);

        // Draw audio status
        drawAudioStatus(g2d);
    }

    /**
     * Draw animated starfield background
     */
    private void drawBackground(Graphics2D g2d) {
        for (Star star : backgroundStars) {
            star.render(g2d);
        }
    }

    /**
     * Draw game title with glow effect
     */
    private void drawTitle(Graphics2D g2d) {
        String title = "Earth Counter 99";
        g2d.setFont(titleFont);

        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(title)) / 2;
        int y = 120;

        // Create glow effect
        int glowIntensity = (int) (50 + 30 * Math.sin(titleGlowPhase * 0.1));
        g2d.setColor(new Color(0, 255, 255, glowIntensity));
        g2d.drawString(title, x + 3, y + 3);

        // Draw main title
        g2d.setColor(Color.CYAN);
        g2d.drawString(title, x, y);
    }

    /**
     * Draw menu options with selection highlighting
     */
    private void drawMenuOptions(Graphics2D g2d) {
        g2d.setFont(menuFont);
        FontMetrics fm = g2d.getFontMetrics();

        int startY = 250;
        int spacing = 60;

        for (int i = 0; i < menuOptions.size(); i++) {
            String option = menuOptions.get(i);
            int x = (PANEL_WIDTH - fm.stringWidth(option)) / 2;
            int y = startY + (i * spacing);

            // Highlight selected option
            if (i == selectedIndex) {
                // Draw selection background
                g2d.setColor(new Color(0, 255, 255, 50));
                g2d.fillRect(x - 20, y - fm.getAscent() - 5, fm.stringWidth(option) + 40, fm.getHeight() + 10);

                // Draw selection border
                g2d.setColor(Color.CYAN);
                g2d.drawRect(x - 20, y - fm.getAscent() - 5, fm.stringWidth(option) + 40, fm.getHeight() + 10);

                // Draw arrow indicators
                g2d.drawString(">", x - 40, y);
                g2d.drawString("<", x + fm.stringWidth(option) + 20, y);

                g2d.setColor(Color.WHITE);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
            }

            g2d.drawString(option, x, y);
        }
    }

    /**
     * Draw control instructions
     */
    private void drawInstructions(Graphics2D g2d) {
        g2d.setFont(instructionFont);
        g2d.setColor(Color.GRAY);

        String[] instructions = {
                "Use UP/DOWN arrows to navigate",
                "Press ENTER to select",
                "Press M to toggle mute, +/- to adjust volume"
        };

        int y = PANEL_HEIGHT - 80;
        for (String instruction : instructions) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (PANEL_WIDTH - fm.stringWidth(instruction)) / 2;
            g2d.drawString(instruction, x, y);
            y += 20;
        }
    }

    /**
     * Draw current audio status
     */
    private void drawAudioStatus(Graphics2D g2d) {
        g2d.setFont(instructionFont);
        g2d.setColor(Color.YELLOW);

        String volumeText = soundManager.isMuted() ?
                "AUDIO: MUTED" :
                "VOLUME: " + (int)(soundManager.getMasterVolume() * 100) + "%";

        g2d.drawString(volumeText, 10, PANEL_HEIGHT - 10);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                moveSelectionUp();
                break;
            case KeyEvent.VK_DOWN:
                moveSelectionDown();
                break;
            case KeyEvent.VK_ENTER:
                selectCurrentOption();
                break;
            case KeyEvent.VK_M:
                soundManager.toggleMute();
                break;
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_EQUALS:
                adjustVolume(0.1f);
                break;
            case KeyEvent.VK_MINUS:
                adjustVolume(-0.1f);
                break;
        }
        repaint();
    }

    /**
     * Move selection up with wraparound
     */
    private void moveSelectionUp() {
        selectedIndex = (selectedIndex - 1 + menuOptions.size()) % menuOptions.size();
        soundManager.playSound(SoundManager.MENU_HOVER);
    }

    /**
     * Move selection down with wraparound
     */
    private void moveSelectionDown() {
        selectedIndex = (selectedIndex + 1) % menuOptions.size();
        soundManager.playSound(SoundManager.MENU_HOVER);
    }

    /**
     * Execute action for currently selected option
     */
    private void selectCurrentOption() {
        soundManager.playSound(SoundManager.MENU_SELECT);

        if (menuActionListener != null) {
            switch (selectedIndex) {
                case 0: // START GAME
                    menuActionListener.onStartGame();
                    break;
                case 1: // OPTIONS
                    menuActionListener.onShowOptions();
                    break;
                case 2: // EXIT
                    menuActionListener.onExitGame();
                    break;
            }
        }
    }

    /**
     * Adjust master volume
     */
    private void adjustVolume(float delta) {
        float newVolume = soundManager.getMasterVolume() + delta;
        soundManager.setMasterVolume(newVolume);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Update animation
        titleGlowPhase++;

        // Update background stars
        for (Star star : backgroundStars) {
            star.update();
        }

        repaint();
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
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * Inner class for animated background stars
     */
    private class Star {
        private float x, y;
        private float speed;
        private int size;
        private Color color;

        public Star() {
            reset();
        }

        private void reset() {
            x = (float) (Math.random() * PANEL_WIDTH);
            y = (float) (Math.random() * PANEL_HEIGHT);
            speed = (float) (Math.random() * 3 + 1);
            size = (int) (Math.random() * 3 + 1);

            int brightness = (int) (Math.random() * 128 + 127);
            color = new Color(brightness, brightness, brightness);
        }

        public void update() {
            x -= speed;
            if (x < -size) {
                x = PANEL_WIDTH + size;
                y = (float) (Math.random() * PANEL_HEIGHT);
            }
        }

        public void render(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillOval((int) x, (int) y, size, size);
        }
    }
}
