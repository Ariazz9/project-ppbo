package Entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class ScoreBubble extends Entity
{
    private int counter;
    private long scoreBubbleTimer;
    private boolean isDone;
    private int score;
    private Font font;
    public ScoreBubble(final int score)
    {
        super();
        width = 30;  //tile sheet
        height = 30;
        isDone = false;
        this.score = score;
        font = new Font("Arial", Font.PLAIN, 10);
    }
    public void draw(final Graphics2D g)
    {
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(score + "", (int)(x - width / 2), (int)(y - height / 2));
    }
    public boolean isDone()
    {
        return isDone;
    }
    public void update()
    {
        if (System.nanoTime() - scoreBubbleTimer > 50000000)
        {
            scoreBubbleTimer = System.nanoTime();
            y -= 1;
            counter++;
            if (counter == 15) isDone = true;
        }
    }
}