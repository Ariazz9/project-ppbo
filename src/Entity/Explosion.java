package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Explosion extends Entity
{
    private static BufferedImage[] explosionShipSprites = new BufferedImage[7];
    private static BufferedImage[] explosionStraySprites = new BufferedImage[25];
    private static BufferedImage[] explosionPlayerSprites = new BufferedImage[14];
    private int explosionFrameCounter;
    private long explosionFrameTimer;
    private boolean isDone;
    private ExplosionType type;
    private int length;
    public Explosion(final ExplosionType type)
    {
        super();
        width = 30;  //tile sheet
        height = 30;
        isDone = false;
        this.type = type;
        if (type == ExplosionType.STRAY)
            length = 25;
        else if (type == ExplosionType.PLAYER)
            length = 14;
        else
            length = 7;
        explosionFrameCounter = -1;
    }
    public void draw(final Graphics2D g)
    {
        BufferedImage bi;
        if (type == ExplosionType.STRAY)
            bi = explosionStraySprites[explosionFrameCounter];
        else if (type == ExplosionType.PLAYER)
            bi = explosionPlayerSprites[explosionFrameCounter];
        else
            bi = explosionShipSprites[explosionFrameCounter];
        g.drawImage(bi, (int)(x - width / 2), (int)(y - height / 2), null);
    }
    public boolean isDone()
    {
        return isDone;
    }
    public static void setExplosionPlayerSprites(final BufferedImage[] sprites)
    {
        explosionPlayerSprites = sprites;
    }
    public static void setExplosionShipSprites(final BufferedImage[] sprites)
    {
        explosionShipSprites = sprites;
    }
    public static void setExplosionStraySprites(final BufferedImage[] sprites)
    {
        explosionStraySprites = sprites;
    }
    public void updateFrame()
    {
        if (System.nanoTime() - explosionFrameTimer > 50000000)
        {
            explosionFrameTimer = System.nanoTime();
            explosionFrameCounter++;
            if (explosionFrameCounter == length)
            {
                isDone = true;
            }
        }
    }
}