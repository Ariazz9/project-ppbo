package Audio;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer
{
    private Clip clip;
    private int framePosition;
    public AudioPlayer(final String filePathAndName)
    {
        try
        {
            InputStream audioStream = getClass().getResourceAsStream(filePathAndName);
            if (audioStream == null) {
                throw new RuntimeException("Audio file not found: " + filePathAndName);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioStream);
            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
            clip = AudioSystem.getClip();
            clip.open(dais);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    public void close()
    {
        stop();
        clip.close();
    }
    public boolean isRunning()
    {
        return clip != null && clip.isRunning();
    }
    public void pause()
    {
        stop();
        framePosition = clip.getFramePosition();
    }
    public void cont()
    {
        loop();
        clip.setFramePosition(framePosition);
    }
    public void loop()
    {
        play();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void play()
    {
        if (clip == null) return;
        stop();
        //clip.setFramePosition(Frame.NORMAL);
        clip.setFramePosition(0);
        clip.start();
    }
    public void reset()
    {
        clip.setFramePosition(0);
    }
    public void stop()
    {
        if (clip.isRunning()) clip.stop();
    }
}