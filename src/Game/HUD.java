package Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Entity.Player;
public class HUD
{
    private final Player player;
    private final Rectangle[] healthRectangles;
    private final Font font;
    public HUD(final Player player)
    {
        this.player = player;
        font = new Font("Ariel", Font.PLAIN, 12);
        healthRectangles = new Rectangle[20];
        for (int x = 0; x < 4; x++)
        {
            healthRectangles[x] = new Rectangle(53 + 17 * x, 425, 15, 13);
        }
    }
    public void draw(final Graphics2D g)
    {
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Level: " + player.getLevel(), 10, 405);
        g.drawString("Score: " + player.getScore(), 10, 420);
        g.drawString("Health:", 10, 435);
        g.setColor(Color.RED);
        for (int x = 0; x < 4; x++)
        {
            if (player.getHealth() > x - 1)
            {
                g.fill(healthRectangles[x]);
            }
        }
    }
}