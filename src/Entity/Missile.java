package Entity;

import java.awt.image.BufferedImage;

public class Missile extends Entity
{
    private static BufferedImage missileSprite;
    private static BufferedImage enemyMissileSprite;
    public Missile()
    {
        width = 3;
        height = 20;
        cwidth = 3;
        cheight = 14;
    }
    public static BufferedImage getEnemyMissileSprite()
    {
        return enemyMissileSprite;
    }
    public static BufferedImage getMissileSprite()
    {
        return missileSprite;
    }
    public static void setEnemyMissileSprite(final BufferedImage sprite)
    {
        enemyMissileSprite = sprite;
    }
    public static void setMissileSprite(final BufferedImage sprite)
    {
        missileSprite = sprite;
    }
}