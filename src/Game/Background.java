package Game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Background
{
    private BufferedImage image;
    private static BufferedImage black;
    private double x, y, dx, dy;
    private double originaldx, originaldy;
    public Background(final String s, final double dx, final double dy)
    {
        this.dx = originaldx = dx;
        this.dy = originaldy = dy;
        try
        {
            image = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/space.jpg"));
        }
        catch(final Exception e)
        {
            e.printStackTrace();
        }
    }
    public void draw(final Graphics2D g)
    {
        final BufferedImage bi = Settings.enabledBackground() ? image : black;

        g.drawImage(bi, (int)x, 0, null);

        g.drawImage(bi, (int)x + bi.getWidth(), 0, null);

        if (x <= -bi.getWidth()) {
            x = 0;
        }
    }
    public static void setBlackImage(final BufferedImage image)
    {
        black = image;
    }
    public void update()
    {
        if (Settings.enabledBackgroundScrolling())
        {
            dx = originaldx;
            dy = originaldy;
        }
        else
        {
            dx = 0;
            dy = 0;
        }
        x += dx;
        // reset horizontal scroll
        if (x <= -image.getWidth()) {
            x = 0;
        }
    }
}