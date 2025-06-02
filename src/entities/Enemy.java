package entities;

import java.awt.*;
import java.util.Random;

/**
 * Enemy class inheriting from GameObject
 * Demonstrates inheritance and polymorphism
 */
public class Enemy extends GameObject {
    private static final double SPEED = 2.0;
    private static final int SHOOT_COOLDOWN = 60;

    private int shootTimer;
    private int scoreValue;
    private Random random;

    public Enemy(double x, double y) {
        super(x, y, 35, 25, Color.RED);
        this.shootTimer = 0;
        this.scoreValue = 10;
        this.random = new Random();
        this.velocityX = -SPEED;
    }

    @Override
    public void update() {
        // Move enemy left
        x += velocityX;

        // Simple AI: slight vertical movement
        y += Math.sin(x * 0.01) * 0.5;

        // Update shoot timer
        if (shootTimer > 0) {
            shootTimer--;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)width, (int)height);

        // Draw enemy spaceship details
        g.setColor(Color.YELLOW);
        g.fillRect((int)x, (int)(y + height/3), (int)width/2, (int)(height/3));
    }

    // Enemy shooting method
    public EnemyBullet shoot() {
        if (shootTimer <= 0 && random.nextInt(100) < 2) { // 2% chance per frame
            shootTimer = SHOOT_COOLDOWN;
            return new EnemyBullet(x, y + height/2 - 2, -6, 0);
        }
        return null;
    }

    public int getScoreValue() {
        return scoreValue;
    }
}