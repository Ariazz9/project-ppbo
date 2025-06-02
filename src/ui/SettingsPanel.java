package ui;

import audio.AudioManager;
import enums.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Settings panel for audio and game configuration
 */
public class SettingsPanel extends JPanel {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;

    private AudioManager audioManager;
    private MenuPanel.GameStateListener gameStateListener;
    private JSlider volumeSlider;
    private JCheckBox muteCheckBox;

    public SettingsPanel(MenuPanel.GameStateListener listener) {
        this.gameStateListener = listener;
        this.audioManager = AudioManager.getInstance();

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("SETTINGS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Volume control
        JLabel volumeLabel = new JLabel("Master Volume:");
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        volumeLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(volumeLabel, gbc);

        volumeSlider = new JSlider(0, 100, (int)(audioManager.getMasterVolume() * 100));
        volumeSlider.setBackground(Color.BLACK);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100.0f;
            audioManager.setMasterVolume(volume);
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(volumeSlider, gbc);

        // Mute checkbox
        muteCheckBox = new JCheckBox("Mute All Sounds", audioManager.isMuted());
        muteCheckBox.setFont(new Font("Arial", Font.BOLD, 18));
        muteCheckBox.setForeground(Color.WHITE);
        muteCheckBox.setBackground(Color.BLACK);
        muteCheckBox.addActionListener(e -> audioManager.toggleMute());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(muteCheckBox, gbc);

        // Back button
        JButton backButton = new JButton("BACK TO MENU");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.DARK_GRAY);
        backButton.addActionListener(e -> {
            audioManager.playSound(AudioManager.MENU_CLICK);
            gameStateListener.onGameStateChange(GameState.MENU);
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(backButton, gbc);
    }
}
