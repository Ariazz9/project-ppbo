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
        g.setColor(color);
        g.fillOval((int)x, (int)y, (int)width, (int)height);
    }
}