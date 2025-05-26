package Menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.awt.event.KeyEvent;

public class HighScoresMenu
{
    private final Rectangle border;
    private final Font font;
    private HighScore highScores[] = new HighScore[11];
    private HighScore highScoreToBeAdded;
    private final String nameMessage;
    private final String scoreMessage;
    private final String toContinueMessage;
    private String name;
    private int position;
    private long lineFlashTimer;
    private boolean triedAgain;
    private boolean entryIntoHighScores;
    private boolean alreadyAttemptedToEnterHighScores;
    private boolean finishedEnteringName;
    private static final int HORIZONTALOFFSET = 165;
    private static final int VERTICALOFFSET = 40;
    private static final String DASHES = "---";
    private final File file = new File("highscores.dat");
    public HighScoresMenu()
    {
        nameMessage = "Name";
        scoreMessage = "Score";
        toContinueMessage = "ESC / Enter to continue";
        border = new Rectangle(HORIZONTALOFFSET + 5, VERTICALOFFSET + 10, 260, 360);
        font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
        loadHighScores();
        resetEntry();
    }
    public void addHighScore(HighScore highScore)
    {
        highScoreToBeAdded = highScore;
        highScores[10] = highScore;
        for (int x = 0; x < 11; x++)
        {
            for (int y = 0; y < 10 - x; y++)
            {
                if (highScores[y + 1].getScore() > highScores[y].getScore())
                {
                    entryIntoHighScores = true;
                    highScore = highScores[y];
                    highScores[y] = highScores[y + 1];
                    highScores[y + 1] = highScore;
                    position = y;
                }
            }
        }
        alreadyAttemptedToEnterHighScores = true;
    }
    private boolean checkIfOKInput(final int k)
    {
        boolean isAcceptable = false;
        if (k >= KeyEvent.VK_A && k <= KeyEvent.VK_Z ||
                k >= KeyEvent.VK_0 && k <= KeyEvent.VK_9) isAcceptable = true;
        return isAcceptable;
    }
    /**
     *
     * @param whichOne true = create new high scores file, false = save high scores file
     */
    private void createOrSaveHighScoresFile(final boolean whichOne)
    {
        HighScore highScore;
        try
        {
            final FileOutputStream fo = new FileOutputStream(file);
            final ObjectOutputStream output = new ObjectOutputStream(fo);
            for (int i = 0; i < 10; i++)
            {
                if (whichOne)
                {
                    highScore = new HighScore(DASHES, 0);
                }
                else
                {
                    highScore = highScores[i];
                }
                highScores[i] = highScore;
                output.writeObject(highScores[i]);
            }
            output.close();
            fo.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        triedAgain = true;
    }
    public void draw(final Graphics2D g)
    {
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.fill(border);
        g.setColor(Color.WHITE);
        g.draw(border);
        g.setColor(Color.CYAN);
        g.drawString(nameMessage, HORIZONTALOFFSET + 65, VERTICALOFFSET + 45);
        g.drawString(scoreMessage, HORIZONTALOFFSET + 155, VERTICALOFFSET + 45);
        g.setColor(Color.GREEN);
        g.drawString(toContinueMessage, HORIZONTALOFFSET + 20, VERTICALOFFSET + 340);
        for (int i = 0; i < highScores.length - 1; i++)
        {
            g.setColor(Color.CYAN);
            g.drawString(i + 1 + "", HORIZONTALOFFSET + 25, VERTICALOFFSET + 90 + i * 22);
            g.setColor(Color.WHITE);
            g.drawString(highScores[i].getName(), HORIZONTALOFFSET + 65, VERTICALOFFSET + 90 + i * 22);
            g.setColor(Color.ORANGE);
            g.drawString(highScores[i].getScore() == 0 && highScores[i].getName().isEmpty() ? "" : highScores[i].getScore() + "", HORIZONTALOFFSET + 165, VERTICALOFFSET + 90 + i * 22);
        }
        g.setColor(Color.MAGENTA);
        g.drawString(name, HORIZONTALOFFSET + 65, VERTICALOFFSET + 90 + position * 22);
        if (entryIntoHighScores && !finishedEnteringName)
        {
            if ((System.nanoTime() - lineFlashTimer) % 1000000000 > 500000000 && name.length() < 8)
                g.drawLine(HORIZONTALOFFSET + 65 + 10 * name.length(), VERTICALOFFSET + 90 + 22 * position, HORIZONTALOFFSET + 76 + 10 * name.length(), VERTICALOFFSET + 90 + 22 * position);
        }
    }
    public boolean enteredHighScores()
    {
        return entryIntoHighScores;
    }
    public boolean hasAlreadyAttemptedToEnterHighScores()
    {
        return alreadyAttemptedToEnterHighScores;
    }
    public void keyPressed(final int k)
    {
        if (k >= KeyEvent.VK_A && k <= KeyEvent.VK_Z || k >= KeyEvent.VK_0 && k <= KeyEvent.VK_9 && !finishedEnteringName)
        {
            if (name.length() < 8) name += checkIfOKInput(k) ? (char)k : "";
        }
        else if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_ESCAPE)
        {
            highScoreToBeAdded = new HighScore(name, highScoreToBeAdded.getScore());
            highScores[position] = highScoreToBeAdded;
            createOrSaveHighScoresFile(false);
            entryIntoHighScores = false;
            finishedEnteringName = true;
            name = "";
        }
        else if (k == KeyEvent.VK_BACK_SPACE
        )
        {
            if (name.length() > 0) name = name.substring(0, name.length() - 1);
        }
    }
    private void loadHighScores()
    {
        try
        {
            final FileInputStream fi = new FileInputStream(file);
            final ObjectInputStream input = new ObjectInputStream(fi);
            for (int i = 0; i < 10; i++)
            {
                highScores[i] = (HighScore)input.readObject();
            }
            input.close();
            fi.close();
        }
        catch (final FileNotFoundException e)
        {
            tryToCreateNewFile();
        }
        catch (final IOException e)
        {
            tryToCreateNewFile();
        }
        catch (final ClassNotFoundException e)
        {
            tryToCreateNewFile();
        }
    }
    public void resetEntry()
    {
        entryIntoHighScores = alreadyAttemptedToEnterHighScores
                = triedAgain = finishedEnteringName = false;
        name = "";
    }
    private void tryToCreateNewFile()
    {
        if (!triedAgain)
        {
            createOrSaveHighScoresFile(true);
        }
        else
        {
            highScores[0] = new HighScore("ERROR", 0);
            for (int i = 1; i < 10; i++)
            {
                highScores[i] = new HighScore(DASHES, 0);
            }
        }
    }
}