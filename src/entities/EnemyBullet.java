package entities;

import java.awt.*;

/**
 * Enemy bullet class inheriting from GameObject
 * Demonstrates inheritance and polymorphism
 */
public class EnemyBullet extends GameObject {
    private int damage;

    public EnemyBullet(double x, double y, double velocityX, double velocityY, int damage) {
        super(x, y, 9, 5, Color.ORANGE);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
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
        g2d.setColor(Color.ORANGE);
        g2d.fillOval((int)x, (int)y, (int)width, (int)height);

        // Inner core
        g2d.setColor(Color.RED);
        g2d.fillOval((int)(x + 1), (int)(y + 1), (int)(width - 2), (int)(height - 2));

        // Trail effect
        g2d.setColor(new Color(255, 155, 0, 120));
        g2d.fillOval((int)(x + width), (int)(y - 1), 4, (int)(height + 2));
        g2d.setColor(new Color(255, 100, 0, 60));
        g2d.fillOval((int)(x + width + 3), (int)(y - 2), 3, (int)(height + 4));
    }

    public int getDamage() {
        return damage;
    }
}
