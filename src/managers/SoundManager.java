package managers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager handles all audio operations in the game using javax.sound.sampled
 * Implements singleton pattern for global access and resource management
 */
public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private Map<String, AudioInputStream> audioStreams;
    private float masterVolume = 0.7f;
    private boolean muted = false;

    // Sound effect identifiers
    public static final String PLAYER_SHOOT = "player_shoot";
    public static final String ENEMY_EXPLOSION = "enemy_explosion";
    public static final String BACKGROUND_MUSIC = "background_music";
    public static final String MENU_HOVER = "menu_hover";
    public static final String MENU_SELECT = "menu_select";

    private SoundManager() {
        soundClips = new HashMap<>();
        audioStreams = new HashMap<>();
        loadAllSounds();
    }

    /**
     * Get singleton instance of SoundManager
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Load all sound files from resources
     */
    private void loadAllSounds() {
        try {
            // Load sound effects - using placeholder sounds if files don't exist
            loadSound(PLAYER_SHOOT, "/assets/sounds/player_shoot.wav");
            loadSound(ENEMY_EXPLOSION, "/assets/sounds/enemy_explosion.wav");
            loadSound(BACKGROUND_MUSIC, "/assets/sounds/background_music.wav");
            loadSound(MENU_HOVER, "/assets/sounds/menu_hover.wav");
            loadSound(MENU_SELECT, "/assets/sounds/menu_select.wav");
        } catch (Exception e) {
            System.err.println("Warning: Could not load some audio files. Creating silent clips.");
            createSilentClips();
        }
    }

    /**
     * Load individual sound file
     */
    private void loadSound(String soundName, String filePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            if (audioSrc == null) {
                System.err.println("Audio file not found: " + filePath + ". Creating silent clip.");
                createSilentClip(soundName);
                return;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            soundClips.put(soundName, clip);
            audioStreams.put(soundName, audioInputStream);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound " + soundName + ": " + e.getMessage());
            createSilentClip(soundName);
        }
    }

    /**
     * Create silent clips for all sounds as fallback
     */
    private void createSilentClips() {
        String[] soundNames = {PLAYER_SHOOT, ENEMY_EXPLOSION, BACKGROUND_MUSIC, MENU_HOVER, MENU_SELECT};
        for (String soundName : soundNames) {
            createSilentClip(soundName);
        }
    }

    /**
     * Create a silent audio clip as fallback when audio files are missing
     */
    private void createSilentClip(String soundName) {
        try {
            // Create minimal silent audio data
            AudioFormat format = new AudioFormat(22050, 16, 1, true, false);
            byte[] silentData = new byte[2205]; // 0.1 second of silence
            AudioInputStream silentStream = new AudioInputStream(
                    new java.io.ByteArrayInputStream(silentData),
                    format,
                    silentData.length / format.getFrameSize()
            );

            Clip clip = AudioSystem.getClip();
            clip.open(silentStream);
            soundClips.put(soundName, clip);

        } catch (Exception e) {
            System.err.println("Could not create silent clip for " + soundName);
        }
    }

    /**
     * Play a sound effect once
     */
    public void playSound(String soundName) {
        if (muted) return;

        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            // Stop and rewind clip if already playing
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            setClipVolume(clip, masterVolume);
            clip.start();
        }
    }

    /**
     * Play background music on loop
     */
    public void playBackgroundMusic() {
        if (muted) return;

        Clip clip = soundClips.get(BACKGROUND_MUSIC);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            setClipVolume(clip, masterVolume * 0.6f); // Background music at lower volume
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        Clip clip = soundClips.get(BACKGROUND_MUSIC);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Stop all currently playing sounds
     */
    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    /**
     * Set volume for a specific clip
     */
    private void setClipVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float gain = min + (max - min) * volume;
                volumeControl.setValue(gain);
            } catch (IllegalArgumentException e) {
                // Volume control not supported for this clip
            }
        }
    }

    /**
     * Set master volume (0.0 to 1.0)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));

        // Update volume for all currently loaded clips
        for (Clip clip : soundClips.values()) {
            if (clip != null) {
                setClipVolume(clip, masterVolume);
            }
        }
    }

    /**
     * Get current master volume
     */
    public float getMasterVolume() {
        return masterVolume;
    }

    /**
     * Toggle mute state
     */
    public void toggleMute() {
        muted = !muted;
        if (muted) {
            stopAllSounds();
        }
    }

    /**
     * Check if audio is muted
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * Clean up resources when shutting down
     */
    public void cleanup() {
        stopAllSounds();
        for (Clip clip : soundClips.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        soundClips.clear();
        audioStreams.clear();
    }
}
