package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EnemyShip extends Entity
{
    private boolean dead;
    private boolean leftMode;
    private int whichOne;
    private int leftModeCounter;
    private static BufferedImage[][] enemyShipSprites = new BufferedImage[4][10];
    private static BufferedImage[] strayEnemySprites = new BufferedImage[3];
    private static boolean enemyFrameDirectionForward = true;
    private static int enemyFrameCounter;
    private static long enemyFrameTimer;
    private static final double MOVESPEEDFORSTRAYSHIP = 1.0;
    public static final double MOVESPEED = 0.4;
    private static double moveSpeedForMostShips = MOVESPEED;
    public EnemyShip(final int whichOne)
    {
        super();
        width = 30;  //tile sheet
        height = 30;
        cwidth = 25;
        cheight = 30;
        this.whichOne = whichOne;
        down = true;
    }
    public static void decreaseMoveSpeed()
    {
        moveSpeedForMostShips -= MOVESPEED;
        if (moveSpeedForMostShips < 0) moveSpeedForMostShips = 0;
    }
    public void draw(final Graphics2D g)
    {
        BufferedImage image;
        if (whichOne == -1)
            image = strayEnemySprites[enemyFrameCounter / 4];
        else
            image = getEnemyShipSprites(whichOne)[enemyFrameCounter];
        g.drawImage(image, (int)(x - width / 2), (int)(y - height / 2), null);
    }
    public boolean getLeftMode()
    {
        return leftMode;
    }
    public static BufferedImage[] getEnemyShipSprites(final int which)
    {
        if (which == -1) return strayEnemySprites;
        return enemyShipSprites[which];
    }
    public boolean getDown()
    {
        return down;
    }
    public static double getMoveSpeed()
    {
        return moveSpeedForMostShips;
    }
    private void getNextPosition() {
        double moveSpeed = whichOne == -1 ? MOVESPEEDFORSTRAYSHIP : moveSpeedForMostShips;

        if (leftMode) {
            leftModeCounter++;
            if (leftModeCounter == 6) {
                leftModeCounter = 0;
                leftMode = false;
                if (up) {
                    up = false;
                    down = true;
                } else {
                    up = true;
                    down = false;
                }
                return;
            }
            x -= 2;
            return;
        }

        if (up) {
            y -= moveSpeed;
        } else if (down) {
            y += moveSpeed;
        }

    }
    public int getPoints(final int level)
    {
        if (whichOne == -1) return 170 + 30 * level;
        return (10 + level * 5) * (4 - whichOne);
    }
    public boolean getUp()
    {
        return up;
    }
    public static void increaseMoveSpeed()
    {
        moveSpeedForMostShips += MOVESPEED;
    }
    public boolean isDead()
    {
        return dead;
    }
    public void kill()
    {
        dead = true;
    }
    public void setLeftMode()
    {
        leftMode = true;
    }
    public static void setEnemyShipSprites(final int whichOne, final BufferedImage[] sprites)
    {
        if (whichOne == -1)
            strayEnemySprites = sprites;
        else
            enemyShipSprites[whichOne] = sprites;
    }
    public void setDown()
    {
        down = true;
        up = false;
    }
    public static void setMoveSpeed(final double ms)
    {
        moveSpeedForMostShips = ms;
    }
    public void setUp()
    {
        up = true;
        down = false;
    }
    public void update()
    {
        getNextPosition();
    }
    public static void updateFrame()
    {
        if (System.nanoTime() - enemyFrameTimer > 75000000)
        {
            enemyFrameTimer = System.nanoTime();
            if (enemyFrameDirectionForward)
            {
                enemyFrameCounter++;
                if (enemyFrameCounter > enemyShipSprites[0].length - 1)
                {
                    enemyFrameCounter = enemyShipSprites[0].length - 1;
                    enemyFrameDirectionForward = false;
                }
            }
            else
            {
                enemyFrameCounter--;
                if (enemyFrameCounter == -1)
                {
                    enemyFrameCounter = 1;
                    enemyFrameDirectionForward = true;
                }
            }
        }
    }
}