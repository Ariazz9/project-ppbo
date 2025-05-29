package Main;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import Game.Game;
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener
{
    public static final int WIDTH = 720;
    public static final int HEIGHT = 480;

    //game thread
    private Thread thread;
    private static final int FPS = 30;
    private static final long targetTime = 1000000000 / FPS;

    //image
    private final BufferedImage image;
    private final Graphics2D g;

    private final Game game;

    public GamePanel()
    {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D)image.getGraphics();
        game = new Game();
    }
    public void addNotify()
    {
        super.addNotify();
        if (thread == null)
        {
            thread = new Thread(this);
            addKeyListener(this);
            thread.start();
        }
    }
    private void draw()
    {
        game.draw(g);
        final Graphics g2 = getGraphics();
        g2.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
        g2.dispose();
    }
    public void keyPressed(final KeyEvent key)
    {
        game.keyPressed(key.getKeyCode());
    }
    public void keyReleased(final KeyEvent key)
    {
        game.keyReleased(key.getKeyCode());
    }
    public void keyTyped(final KeyEvent key)
    {
    }
    public void run()
    {
        long start, elapsed, wait;
        while(true)
        {
            start = System.nanoTime();
            if (!game.isPaused()) update();
            draw();
            elapsed = System.nanoTime() - start;
            wait = (targetTime - elapsed) / 1000000;
            try
            {
                Thread.sleep(wait < 0 ? 5 : wait);  //in case there's negative wait time
            }
            catch(final Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private void update()
    {
        game.update();
    }
}