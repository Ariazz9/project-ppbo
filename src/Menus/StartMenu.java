package Menus;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Game.Background;
import Game.Game;

import java.awt.event.KeyEvent;

public class StartMenu
{
    private Background bg;
    private BufferedImage spaceInvadersTitle;
    private final String[] options = {"Start", "High Scores", "Help", "Quit"};
    private final Font font = new Font("Arial", Font.PLAIN, 24);
    private int currentChoice;
    private boolean newGame;
    private static final int HORIZONTALOFFSET = 125;
    private static final int VERTICALOFFSET = -15;
    public StartMenu()
    {
        try
        {
            bg = new Background("/Backgrounds/titlescreenbackground.jpg", -0.4, 0);
//            spaceInvadersTitle = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/titlescreen.jpg"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        newGame = false;
    }
    public void draw(final Graphics2D g)
    {
        bg.draw(g);
        g.drawImage(spaceInvadersTitle, HORIZONTALOFFSET + 40, VERTICALOFFSET + 10, null);
        g.setFont(font);
        for (int i = 0; i < options.length; i++)
        {
            g.setColor(Color.RED);
            if (i == currentChoice)
            {
                g.setColor(Color.ORANGE);
            }
            g.drawString(options[i], HORIZONTALOFFSET + (i == 1 ? 155 : 193), VERTICALOFFSET + 305 + i * 30);
        }
    }
    public void keyPressed(final int k)
    {
        switch(k)
        {
            case KeyEvent.VK_ENTER:
                select();
                break;
            case KeyEvent.VK_UP:
                currentChoice--;
                if (currentChoice == -1)
                {
                    currentChoice = options.length - 1;
                }
                break;
            case KeyEvent.VK_DOWN:
                currentChoice++;
                if (currentChoice == options.length)
                {
                    currentChoice = 0;
                }
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
        }
    }
    public boolean newGameRequested()
    {
        return newGame;
    }
    public void reset()
    {
        newGame = false;
    }
    private void select()
    {
        switch(currentChoice)
        {
            case 0:
                newGame = true;
                break;
            case 1:
                Game.showHighScoresMenu();
                break;
            case 2:
                Game.showHelpDialog();
                break;
            case 3:
                System.exit(0);
        }
    }
    public void update()
    {
        bg.update();
    }
}