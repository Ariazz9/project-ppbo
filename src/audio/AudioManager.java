package audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager class handles all audio operations in the game
 * Demonstrates singleton pattern and resource management
 */
public class AudioManager {
    private static AudioManager instance;
    private Map<String, Clip> soundClips;
    private Map<String, AudioInputStream> audioStreams;
    private float masterVolume = 0.7f;
    private boolean muted = false;

    // Sound effect names
    public static final String PLAYER_SHOOT = "player_shoot";
    public static final String ENEMY_EXPLOSION = "enemy_explosion";
    public static final String BACKGROUND_MUSIC = "background_music";
    public static final String MENU_HOVER = "menu_hover";
    public static final String MENU_CLICK = "menu_click";

    private AudioManager() {
        soundClips = new HashMap<>();
        audioStreams = new HashMap<>();
        loadSounds();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            // Load sound effects
            loadSound(PLAYER_SHOOT, "/assets/sounds/player_shoot.wav");
            loadSound(ENEMY_EXPLOSION, "/assets/sounds/enemy_explosion.wav");
            loadSound(BACKGROUND_MUSIC, "/assets/sounds/background_music.wav");
            loadSound(MENU_HOVER, "/assets/sounds/menu_hover.wav");
            loadSound(MENU_CLICK, "/assets/sounds/menu_click.wav");
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
            // Create silent clips as fallback
            createSilentClips();
        }
    }

    private void loadSound(String name, String path) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(path);
            if (audioSrc == null) {
                System.err.println("Could not find audio file: " + path);
                createSilentClip(name);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            soundClips.put(name, clip);
            audioStreams.put(name, audioInputStream);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound " + name + ": " + e.getMessage());
            createSilentClip(name);
        }
    }

    private void createSilentClips() {
        for (String soundName : new String[]{PLAYER_SHOOT, ENEMY_EXPLOSION, BACKGROUND_MUSIC, MENU_HOVER, MENU_CLICK}) {
            createSilentClip(soundName);
        }
    }

    private void createSilentClip(String name) {
        try {
            // Create a very short silent audio clip as fallback
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            byte[] silentData = new byte[4410]; // 0.1 second of silence
            AudioInputStream silentStream = new AudioInputStream(
                    new java.io.ByteArrayInputStream(silentData), format, silentData.length / format.getFrameSize());

            Clip clip = AudioSystem.getClip();
            clip.open(silentStream);
            soundClips.put(name, clip);

        } catch (Exception e) {
            System.err.println("Could not create silent clip for " + name);
        }
    }

    public void playSound(String soundName) {
        if (muted) return;

        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            clip.setFramePosition(0); // Reset to beginning
            setVolume(clip, masterVolume);
            clip.start();
        }
    }

    public void playBackgroundMusic() {
        if (muted) return;

        Clip clip = soundClips.get(BACKGROUND_MUSIC);
        if (clip != null) {
            clip.setFramePosition(0);
            setVolume(clip, masterVolume * 0.5f); // Background music at lower volume
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        Clip clip = soundClips.get(BACKGROUND_MUSIC);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    private void setVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float gain = min + (max - min) * volume;
                volumeControl.setValue(gain);
            } catch (IllegalArgumentException e) {
                // Volume control not supported
            }
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        // Update volume for currently playing clips
        for (Clip clip : soundClips.values()) {
            if (clip != null) {
                setVolume(clip, masterVolume);
            }
        }
    }

    public void toggleMute() {
        muted = !muted;
        if (muted) {
            stopAllSounds();
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

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
