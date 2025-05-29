package Entity;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Game.Settings;
public class Player extends Entity
{
    private int health;
    private int score;
    private int level;
    private long flinchTimer;
    private boolean dead;
    private boolean flinching;
    private static BufferedImage playerSprite;
    public Player()
    {
        super();
        width = 35;
        height = 35;
        cwidth = 25;
        cheight = 30;
        level = 0;
    }
    public void addToFlinchTimer(final long time)
    {
        flinchTimer += time;
    }
    public void draw(final Graphics2D g)
    {
        g.drawImage(playerSprite, (int)(x - width / 2), (int)(y - height / 2), null);
        //super.draw(g);
    }
    public long getElapsed()
    {
        return (System.nanoTime() - flinchTimer) / 100000000;
    }
    public boolean getFlinching()
    {
        return flinching;
    }
    public int getHealth()
    {
        return health;
    }
    public int getLevel()
    {
        return level;
    }
    private void getNextPosition()
    {
        if (down)
        {
            y += Settings.getPlayerSensitivity();
            if (y > 650) y = 650;
        }
        else if (up)
        {
            y -= Settings.getPlayerSensitivity();
            if (y < 210) y = 210;
        }
    }
    public static BufferedImage getPlayerSprite()
    {
        return playerSprite;
    }
    public int getScore()
    {
        return score;
    }
    public void hit()
    {
        if (flinching) return;
        health--;
        dead = true;
        flinching = true;
        flinchTimer = System.nanoTime();
    }
    public void incrementLevel()
    {
        level++;
    }
    public void incrementScore(final int points)
    {
        score += points;
    }
    public boolean isDead()
    {
        return dead;
    }
    public void resetHealth()
    {
        health = 3;
        flinching = false;
    }
    public void resetScoreAndLevel()
    {
        score = 0;
        level = 1;
    }
    public static void setPlayerSprite(final BufferedImage sprite)
    {
        playerSprite = sprite;
    }
    public void update()
    {
        if (dead)
        {
            if (System.nanoTime() - flinchTimer > 2000000000)
            {
                dead = false;
            }
        }
        getNextPosition();
        if (flinching)
        {
            long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
            if (elapsed > 2000)
            {
                flinching = false;
            }
        }
    }
}