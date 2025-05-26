package Menus;

import java.io.Serializable;

public class HighScore implements Serializable
{
    private static final long serialVersionUID = 1L;
    public final String name;
    public final int score;
    public HighScore(final String name, final int score)
    {
        this.name = name;
        this.score = score;
    }
    public String getName()
    {
        return name;
    }
    public int getScore()
    {
        return score;
    }
}