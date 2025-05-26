package Entity;
import java.awt.Rectangle;
public abstract class Entity
{
    protected double x, y;  //position
    protected int width, height;  //dimensions of sprite
    protected int cwidth, cheight;  //collision width and height
    protected boolean left, right;  //movement
    public int getHeight()
    {
        return height;
    }
    public Rectangle getRectangle()
    {
        return new Rectangle((int)x - cwidth / 2, (int)y - cheight / 2, cwidth, cheight);
    }
    public int getWidth()
    {
        return width;
    }
    public int getx()
    {
        return (int)x;
    }
    public int gety()
    {
        return (int)y;
    }
    public boolean intersects(final Entity other)
    {
        final Rectangle rect1 = getRectangle();
        final Rectangle rect2 = other.getRectangle();
        return rect1.intersects(rect2);
    }
    public void setLeft(final boolean bool)
    {
        left = bool;
    }
    public void setPosition(final double x, final double y)
    {
        this.x = x;
        this.y = y;
    }
    public void setRight(final boolean bool)
    {
        right = bool;
    }
}