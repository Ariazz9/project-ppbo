package entities;

import java.awt.*;

/**
 * Enemy bullet class inheriting from GameObject
 * Demonstrates inheritance and polymorphism
 */
public class EnemyBullet extends GameObject {
    public EnemyBullet(double x, double y, double velocityX, double velocityY) {
        super(x, y, 6, 3, Color.ORANGE);
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
        g.setColor(color);
        g.fillOval((int)x, (int)y, (int)width, (int)height);
    }
}