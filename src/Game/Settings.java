package Game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Settings
{
    private static boolean showBackground = true;
    private static boolean backgroundScrolling = true;
    private static boolean soundEnabled = true;
    private static boolean explosionsEnabled = true;
    private static boolean scoreBubblesEnabled = true;
    private static boolean musicEnabled = true;
    private static int playerSensitivity = 4;
    private static final File file = new File("settings.dat");
    public static void decrementPlayerSensitivity()
    {
        playerSensitivity -= 1;
        if (playerSensitivity < 1) playerSensitivity = 1;
        saveSettingsFile();
    }
    public static int getPlayerSensitivity()
    {
        return playerSensitivity;
    }
    public static boolean enabledExplosions()
    {
        return explosionsEnabled;
    }
    public static void incrementPlayerSensitivity()
    {
        playerSensitivity += 1;
        if (playerSensitivity > 15) playerSensitivity = 15;
        saveSettingsFile();
    }
    public static void loadSettingsFile()
    {
        try
        {
            final FileInputStream fi = new FileInputStream(file);
            final ObjectInputStream input = new ObjectInputStream(fi);
            showBackground = input.readBoolean();
            backgroundScrolling = input.readBoolean();
            soundEnabled = input.readBoolean();
            musicEnabled = input.readBoolean();
            explosionsEnabled = input.readBoolean();
            scoreBubblesEnabled = input.readBoolean();
            playerSensitivity = input.readInt();
            input.close();
            fi.close();
        }
        catch (final FileNotFoundException e)
        {
            saveSettingsFile();
        }
        catch (final IOException e)
        {
            saveSettingsFile();
        }
    }
    private static void saveSettingsFile()
    {
        try
        {
            final FileOutputStream fo = new FileOutputStream(file);
            final ObjectOutputStream output = new ObjectOutputStream(fo);
            output.writeBoolean(showBackground);
            output.writeBoolean(backgroundScrolling);
            output.writeBoolean(soundEnabled);
            output.writeBoolean(musicEnabled);
            output.writeBoolean(explosionsEnabled);
            output.writeBoolean(scoreBubblesEnabled);
            output.writeInt(playerSensitivity);
            output.close();
            fo.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    public static boolean enabledScoreBubbles()
    {
        return scoreBubblesEnabled;
    }
    public static void setBackgroundScrolling(final boolean on)
    {
        backgroundScrolling = on;
        saveSettingsFile();
    }
    public static boolean enabledBackground()
    {
        return showBackground;
    }
    public static boolean enabledBackgroundScrolling()
    {
        return backgroundScrolling;
    }
    public static boolean enabledMusic()
    {
        return musicEnabled;
    }
    public static boolean enabledSound()
    {
        return soundEnabled;
    }
    public static void toggleBackgroundImage()
    {
        showBackground = !showBackground;
        saveSettingsFile();
    }
    public static void toggleExplosions()
    {
        explosionsEnabled = !explosionsEnabled;
        saveSettingsFile();
    }
    public static void toggleMusic()
    {
        musicEnabled = !musicEnabled;
        saveSettingsFile();
    }
    public static void toggleScoreBubbles()
    {
        scoreBubblesEnabled = !scoreBubblesEnabled;
        saveSettingsFile();
    }
    public static void toggleSound()
    {
        soundEnabled = !soundEnabled;
        saveSettingsFile();
    }
}