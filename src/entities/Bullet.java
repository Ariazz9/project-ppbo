package entities;

import java.awt.*;

/**
 * Player bullet class inheriting from GameObject
 * Demonstrates inheritance
 */
public class Bullet extends GameObject {
    public Bullet(double x, double y, double velocityX, double velocityY) {
        super(x, y, 8, 4, Color.YELLOW);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override
    public void update() {
        x += velocityX;
        y += velocityY;
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Main bullet body
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)x, (int)y, (int)width, (int)height);

        // Inner glow
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)(x + 1), (int)(y + 1), (int)(width - 2), (int)(height - 2));

        // Trail effect
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.fillOval((int)(x - 4), (int)(y - 1), 4, (int)(height + 2));
        g2d.setColor(new Color(255, 255, 0, 50));
        g2d.fillOval((int)(x - 8), (int)(y - 2), 4, (int)(height + 4));
    }
}
