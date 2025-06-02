import managers.SoundManager;
import ui.GamePanel;
import ui.MainMenu;
import ui.OptionsMenu;

import javax.swing.*;
import java.awt.*;

/**
 * Enhanced Main class with menu system and audio integration
 * Demonstrates state management and proper resource handling
 */
public class Main extends JFrame implements MainMenu.MenuActionListener,
        OptionsMenu.OptionsActionListener,
        GamePanel.GameActionListener {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // UI Panels
    private MainMenu mainMenu;
    private OptionsMenu optionsMenu;
    private GamePanel gamePanel;

    // Audio manager
    private SoundManager soundManager;

    // Panel identifiers
    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String OPTIONS_MENU = "OPTIONS_MENU";
    private static final String GAME_PANEL = "GAME_PANEL";

    public Main() {
        setTitle("Space Shooter - Enhanced Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Initialize audio system
        soundManager = SoundManager.getInstance();

        initializeComponents();
        setupFrame();

        // Start with main menu
        showMainMenu();
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Setup card layout for panel switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels
        mainMenu = new MainMenu();
        mainMenu.setMenuActionListener(this);

        optionsMenu = new OptionsMenu();
        optionsMenu.setOptionsActionListener(this);

        gamePanel = new GamePanel();
        gamePanel.setGameActionListener(this);

        // Add panels to card layout
        mainPanel.add(mainMenu, MAIN_MENU);
        mainPanel.add(optionsMenu, OPTIONS_MENU);
        mainPanel.add(gamePanel, GAME_PANEL);

        add(mainPanel);
    }

    /**
     * Setup main frame properties
     */
    private void setupFrame() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Show main menu and ensure it has focus
     */
    private void showMainMenu() {
        cardLayout.show(mainPanel, MAIN_MENU);
        mainMenu.requestFocusInWindow();
    }

    /**
     * Show options menu and ensure it has focus
     */
    private void showOptionsMenu() {
        cardLayout.show(mainPanel, OPTIONS_MENU);
        optionsMenu.requestFocusInWindow();
    }

    /**
     * Show game panel and start the game
     */
    private void showGamePanel() {
        cardLayout.show(mainPanel, GAME_PANEL);
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
        gamePanel.startGameAudio();
    }

    // MainMenu.MenuActionListener implementation
    @Override
    public void onStartGame() {
        showGamePanel();
    }

    @Override
    public void onShowOptions() {
        showOptionsMenu();
    }

    @Override
    public void onExitGame() {
        // Clean up resources before exit
        cleanup();
        System.exit(0);
    }

    // OptionsMenu.OptionsActionListener implementation
    @Override
    public void onBackToMenu() {
        showMainMenu();
    }

    // GamePanel.GameActionListener implementation
    @Override
    public void onReturnToMenu() {
        gamePanel.stopGameAudio();
        showMainMenu();
    }

    @Override
    public void onGameOver(int finalScore) {
        gamePanel.stopGameAudio();

        // Show game over dialog
        int choice = JOptionPane.showConfirmDialog(this,
                "Game Over!\nFinal Score: " + finalScore + "\n\nReturn to Main Menu?",
                "Game Over",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            showMainMenu();
        } else {
            cleanup();
            System.exit(0);
        }
    }

    /**
     * Clean up resources before shutdown
     */
    private void cleanup() {
        if (mainMenu != null) {
            mainMenu.cleanup();
        }
        if (soundManager != null) {
            soundManager.cleanup();
        }
    }

    /**
     * Override dispose to ensure proper cleanup
     */
    @Override
    public void dispose() {
        cleanup();
        super.dispose();
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel if system L&F fails
        }

        // Create and show the game
        SwingUtilities.invokeLater(() -> new Main());
    }
}
