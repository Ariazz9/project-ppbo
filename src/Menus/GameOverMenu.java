package Menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

public class GameOverMenu
{
    private final String[] options = {"", "Start Over", "Main Menu"};
    private final Font optionsFont;
    private final Rectangle optionsRectangle;
    private int currentChoice;
    private BufferedImage gameOverImage;
    private static final int VERTICALOFFSET = 0;
    private static final int HORIZONTALOFFSET = 125;
    private final Color green = new Color(0, 102, 0);
    public GameOverMenu()
    {
        try
        {
//            gameOverImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Backgrounds/game_over.jpg")));
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        optionsFont = new Font("Arial", Font.PLAIN, 24);
        optionsRectangle = new Rectangle(HORIZONTALOFFSET + 117, VERTICALOFFSET + 242, 200, 150);
    }
    public void decrementChoice()
    {
        currentChoice--;
        if (currentChoice == -1) currentChoice = options.length - 1;
    }
    public void draw(final Graphics2D g)
    {
        //draw menu options
        g.drawImage(gameOverImage, HORIZONTALOFFSET + 10, VERTICALOFFSET, null);
        g.setColor(Color.BLACK);
        g.fill(optionsRectangle);
        g.setColor(green);
        g.draw(optionsRectangle);
        g.setFont(optionsFont);
        for (int i = 0; i < options.length; i++)
        {
            if (i == currentChoice)
            {
                g.setColor(Color.RED);
            }
            else
            {
                g.setColor(green);
            }
            g.drawString(options[i], HORIZONTALOFFSET + 155, VERTICALOFFSET + 295 + i * 30);
        }
    }
    public int getChoice()
    {
        return currentChoice;
    }
    public void incrementChoice()
    {
        currentChoice++;
        if (currentChoice == options.length) currentChoice = 0;
    }
    public void reset()
    {
        currentChoice = 0;
    }
    public void setContinues(final int cont)
    {
        options[0] = "Continue (" + cont + ")";
    }
}