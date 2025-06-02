package ui;

import managers.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Options menu for audio and game settings
 */
public class OptionsMenu extends JPanel implements KeyListener {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;

    private SoundManager soundManager;
    private OptionsActionListener optionsActionListener;
    private int selectedIndex = 0;
    private final int MAX_OPTIONS = 3;

    private Font titleFont;
    private Font optionFont;

    public interface OptionsActionListener {
        void onBackToMenu();
    }

    public OptionsMenu() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        soundManager = SoundManager.getInstance();

        titleFont = new Font("Arial", Font.BOLD, 36);
        optionFont = new Font("Arial", Font.BOLD, 20);
    }

    public void setOptionsActionListener(OptionsActionListener listener) {
        this.optionsActionListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw title
        g2d.setFont(titleFont);
        g2d.setColor(Color.CYAN);
        String title = "OPTIONS";
        FontMetrics titleFm = g2d.getFontMetrics();
        int titleX = (PANEL_WIDTH - titleFm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 100);

        // Draw options
        g2d.setFont(optionFont);
        int startY = 200;
        int spacing = 80;

        // Volume control
        drawOption(g2d, "MASTER VOLUME: " + (int)(soundManager.getMasterVolume() * 100) + "%",
                0, startY, selectedIndex == 0);

        // Mute toggle
        drawOption(g2d, "MUTE: " + (soundManager.isMuted() ? "ON" : "OFF"),
                0, startY + spacing, selectedIndex == 1);

        // Back option
        drawOption(g2d, "BACK TO MENU",
                0, startY + spacing * 2, selectedIndex == 2);

        // Instructions
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(Color.GRAY);
        String[] instructions = {
                "UP/DOWN: Navigate options",
                "LEFT/RIGHT: Adjust volume",
                "ENTER: Toggle/Select",
                "ESC: Back to menu"
        };

        int instrY = PANEL_HEIGHT - 120;
        for (String instruction : instructions) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (PANEL_WIDTH - fm.stringWidth(instruction)) / 2;
            g2d.drawString(instruction, x, instrY);
            instrY += 20;
        }
    }

    private void drawOption(Graphics2D g2d, String text, int x, int y, boolean selected) {
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (PANEL_WIDTH - fm.stringWidth(text)) / 2;

        if (selected) {
            // Draw selection background
            g2d.setColor(new Color(0, 255, 255, 50));
            g2d.fillRect(textX - 20, y - fm.getAscent() - 5, fm.stringWidth(text) + 40, fm.getHeight() + 10);

            // Draw selection border
            g2d.setColor(Color.CYAN);
            g2d.drawRect(textX - 20, y - fm.getAscent() - 5, fm.stringWidth(text) + 40, fm.getHeight() + 10);

            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
        }

        g2d.drawString(text, textX, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedIndex = (selectedIndex - 1 + MAX_OPTIONS) % MAX_OPTIONS;
                soundManager.playSound(SoundManager.MENU_HOVER);
                break;
            case KeyEvent.VK_DOWN:
                selectedIndex = (selectedIndex + 1) % MAX_OPTIONS;
                soundManager.playSound(SoundManager.MENU_HOVER);
                break;
            case KeyEvent.VK_LEFT:
                if (selectedIndex == 0) { // Volume control
                    adjustVolume(-0.1f);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (selectedIndex == 0) { // Volume control
                    adjustVolume(0.1f);
                }
                break;
            case KeyEvent.VK_ENTER:
                handleSelection();
                break;
            case KeyEvent.VK_ESCAPE:
                if (optionsActionListener != null) {
                    soundManager.playSound(SoundManager.MENU_SELECT);
                    optionsActionListener.onBackToMenu();
                }
                break;
        }
        repaint();
    }

    private void adjustVolume(float delta) {
        float newVolume = soundManager.getMasterVolume() + delta;
        soundManager.setMasterVolume(newVolume);
        soundManager.playSound(SoundManager.MENU_HOVER);
    }

    private void handleSelection() {
        soundManager.playSound(SoundManager.MENU_SELECT);

        switch (selectedIndex) {
            case 0: // Volume - no action needed, use left/right
                break;
            case 1: // Mute toggle
                soundManager.toggleMute();
                break;
            case 2: // Back to menu
                if (optionsActionListener != null) {
                    optionsActionListener.onBackToMenu();
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
