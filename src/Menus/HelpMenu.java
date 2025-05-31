package Menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class HelpMenu
{
    private final Rectangle border;
    private final Font font;
    private final String[] string = new String[17];
    private final String madeByMessage;
    private final String toContinueMessage;
    private static final int HORIZONTALOFFSET = 90;
    private static final int VERTICALOFFSET = 15;
    public HelpMenu()
    {
        string[0] = "S/Down arrow = move down";
        string[1] = "W/Up arrow = move up";
        string[2] = "space = shoot";
        string[3] = "B = toggle background";
        string[4] = "S = toggle sound";
        string[5] = "M = toggle music";
        string[6] = "P = pause";

        string[7] = "F1 = help";
        string[8] = "F2 = start new game";

        string[9] = "ESC = main menu";
        string[10] = "K/L = increase/decrease movement sensitivity";
        string[11] = "8/9 = increase/decrease enemy speed*";
        string[12] = "2/3 = start/stop background scrolling";
        string[13] = "E = toggle explosions";
        string[14] = "H = toggle score bubbles";
        string[15] = "*will turn off score accumulation";
        string[16] = "for the rest of that round";

        madeByMessage = "Made by Eric Sweeten (eric.sweeten@gmail.com)";
        toContinueMessage = "F1 / ESC / ENTER to continue";
        border = new Rectangle(HORIZONTALOFFSET + 5, VERTICALOFFSET + 11, 400, 385);
        font = new Font("Ariel", Font.PLAIN, 15);
    }
    public void draw(final Graphics2D g)
    {
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.fill(border);
        g.setColor(Color.WHITE);
        g.draw(border);
        g.setColor(Color.GREEN);
        for (int i = 0; i < string.length; i++)
        {
            g.drawString(string[i], HORIZONTALOFFSET + 40, VERTICALOFFSET + (i < 7 ? 37 : i < 9 ? 47 : i < 15 ? 57 : 67) + i * 16);
        }
        g.setColor(Color.RED);
        g.drawString(toContinueMessage, HORIZONTALOFFSET + 40, VERTICALOFFSET + 352);
        g.setColor(Color.CYAN);
        g.drawString(madeByMessage, HORIZONTALOFFSET + 40, VERTICALOFFSET + 377);
    }
}