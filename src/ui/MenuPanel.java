package ui;

import audio.AudioManager;
import enums.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Main menu panel with animated background and sound effects
 * Demonstrates event handling and UI design
 */
public class MenuPanel extends JPanel {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;

    private List<MenuButton> buttons;
    private AudioManager audioManager;
    private GameStateListener gameStateListener;
    private Timer animationTimer;
    private List<Star> stars;
    private Font titleFont;
    private Font buttonFont;

    // Interface for communicating with main frame
    public interface GameStateListener {
        void onGameStateChange(GameState newState);
    }

    public MenuPanel(GameStateListener listener) {
        this.gameStateListener = listener;
        this.audioManager = AudioManager.getInstance();

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null);

        initializeFonts();
        initializeStars();
        initializeButtons();
        startAnimation();
    }

    private void initializeFonts() {
        titleFont = new Font("Arial", Font.BOLD, 48);
        buttonFont = new Font("Arial", Font.BOLD, 20);
    }

    private void initializeStars() {
        stars = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            stars.add(new Star());
        }
    }

    private void initializeButtons() {
        buttons = new ArrayList<>();

        // Start Game button
        MenuButton startButton = new MenuButton("START GAME", 400, 250, 200, 50);
        startButton.addActionListener(e -> {
            audioManager.playSound(AudioManager.MENU_CLICK);
            gameStateListener.onGameStateChange(GameState.PLAYING);
        });
        buttons.add(startButton);

        // Settings button
        MenuButton settingsButton = new MenuButton("SETTINGS", 400, 320, 200, 50);
        settingsButton.addActionListener(e -> {
            audioManager.playSound(AudioManager.MENU_CLICK);
            gameStateListener.onGameStateChange(GameState.SETTINGS);
        });
        buttons.add(settingsButton);

        // Exit button
        MenuButton exitButton = new MenuButton("EXIT", 400, 390, 200, 50);
        exitButton.addActionListener(e -> {
            audioManager.playSound(AudioManager.MENU_CLICK);
            System.exit(0);
        });
        buttons.add(exitButton);

        // Add buttons to panel
        for (MenuButton button : buttons) {
            add(button);
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            updateStars();
            repaint();
        });
        animationTimer.start();
    }

    private void updateStars() {
        for (Star star : stars) {
            star.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw animated starfield
        drawStarfield(g2d);

        // Draw title
        drawTitle(g2d);

        // Draw version info
        drawVersionInfo(g2d);
    }

    private void drawStarfield(Graphics2D g2d) {
        for (Star star : stars) {
            star.render(g2d);
        }
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(titleFont);
        g2d.setColor(Color.CYAN);

        String title = "SPACE SHOOTER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(title)) / 2;
        int y = 150;

        // Draw title with glow effect
        g2d.setColor(new Color(0, 255, 255, 100));
        g2d.drawString(title, x + 2, y + 2);
        g2d.setColor(Color.CYAN);
        g2d.drawString(title, x, y);
    }

    private void drawVersionInfo(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.GRAY);
        g2d.drawString("Version 1.0 - Enhanced Edition", 10, PANEL_HEIGHT - 10);

        // Volume indicator
        String volumeText = audioManager.isMuted() ? "MUTED (M to toggle)" :
                "Volume: " + (int)(audioManager.getMasterVolume() * 100) + "% (M to mute, +/- to adjust)";
        g2d.drawString(volumeText, PANEL_WIDTH - 300, PANEL_HEIGHT - 10);
    }

    public void cleanup() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    // Inner class for menu buttons
    private class MenuButton extends JButton {
        private boolean hovered = false;

        public MenuButton(String text, int x, int y, int width, int height) {
            super(text);
            setBounds(x, y, width, height);
            setFont(buttonFont);
            setForeground(Color.WHITE);
            setBackground(new Color(0, 0, 0, 0));
            setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
            setFocusPainted(false);
            setContentAreaFilled(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    audioManager.playSound(AudioManager.MENU_HOVER);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hovered) {
                g2d.setColor(new Color(0, 255, 255, 50));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.CYAN);
            } else {
                g2d.setColor(Color.WHITE);
            }

            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;

            g2d.drawString(getText(), x, y);
        }
    }

    // Inner class for animated stars
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
            speed = (float) (Math.random() * 2 + 0.5);
            size = (int) (Math.random() * 3 + 1);

            int brightness = (int) (Math.random() * 155 + 100);
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
