import managers.SoundManager;
import ui.GamePanel;
import ui.MainMenu;
import ui.OptionsMenu;
import ui.GameOverPanel;
import ui.LevelTransitionPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Enhanced Main class with menu system and audio integration
 * Demonstrates state management and proper resource handling
 */
public class Main extends JFrame implements MainMenu.MenuActionListener,
        OptionsMenu.OptionsActionListener,
        GamePanel.GameActionListener,
        GameOverPanel.GameOverActionListener,
        LevelTransitionPanel.TransitionActionListener {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // UI Panels
    private MainMenu mainMenu;
    private OptionsMenu optionsMenu;
    private GamePanel gamePanel;
    private GameOverPanel gameOverPanel;
    private LevelTransitionPanel levelTransitionPanel;

    // Audio manager
    private SoundManager soundManager;

    // Panel identifiers
    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String OPTIONS_MENU = "OPTIONS_MENU";
    private static final String GAME_PANEL = "GAME_PANEL";
    private static final String GAME_OVER_PANEL = "GAME_OVER_PANEL";
    private static final String LEVEL_TRANSITION_PANEL = "LEVEL_TRANSITION_PANEL";

    public Main() {
        setTitle("Earth Counter 99 - Enhanced Edition");
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

        gameOverPanel = new GameOverPanel();
        gameOverPanel.setGameOverActionListener(this);

        levelTransitionPanel = new LevelTransitionPanel();
        levelTransitionPanel.setTransitionActionListener(this);

        // Add panels to card layout
        mainPanel.add(mainMenu, MAIN_MENU);
        mainPanel.add(optionsMenu, OPTIONS_MENU);
        mainPanel.add(gamePanel, GAME_PANEL);
        mainPanel.add(gameOverPanel, GAME_OVER_PANEL);
        mainPanel.add(levelTransitionPanel, LEVEL_TRANSITION_PANEL);

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

    /**
     * Show game over panel with final score
     */
    private void showGameOverPanel(int finalScore) {
        gameOverPanel.setFinalScore(finalScore);
        cardLayout.show(mainPanel, GAME_OVER_PANEL);
        gameOverPanel.requestFocusInWindow();
        gameOverPanel.startAnimation();
    }

    /**
     * Show level transition panel
     */
    private void showLevelTransition(int level) {
        levelTransitionPanel.setLevel(level);
        cardLayout.show(mainPanel, LEVEL_TRANSITION_PANEL);
        levelTransitionPanel.startTransition(() -> {
            showGamePanel();
        });
    }

    // MainMenu.MenuActionListener implementation
    @Override
    public void onStartGame() {
        gamePanel.resetAll();
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
        showGameOverPanel(finalScore);
    }

    // GameOverPanel.GameOverActionListener implementation
    @Override
    public void onRestartGame() {
        gamePanel.resetAll();
        showGamePanel();
    }

    @Override
    public void onReturnToMainMenu() {
        showMainMenu();
    }

    // LevelTransitionPanel.TransitionActionListener implementation
    @Override
    public void onTransitionComplete() {
        showGamePanel();
    }

    @Override
    public void onLevelTransition(int level) {
        showLevelTransition(level);
    }

    /**
     * Clean up resources before shutdown
     */
    private void cleanup() {
        if (mainMenu != null) {
            mainMenu.cleanup();
        }
        if (gameOverPanel != null) {
            gameOverPanel.cleanup();
        }
        if (levelTransitionPanel != null) {
            levelTransitionPanel.cleanup();
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
