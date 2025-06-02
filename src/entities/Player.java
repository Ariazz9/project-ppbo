package entities;

import java.awt.*;

/**
 * Player class inheriting from GameObject
 * Demonstrates inheritance and encapsulation
 */
public class Player extends GameObject {
    private static final double SPEED = 5.0;
    private static final int MAX_HEALTH = 100;
    private static final int SHOOT_COOLDOWN = 10;

    private int health;
    private int shootTimer;

    public Player(double x, double y) {
        super(x, y, 40, 30, Color.CYAN);
        this.health = MAX_HEALTH;
        this.shootTimer = 0;
    }

    @Override
    public void update() {
        // Update position based on velocity
        x += velocityX;
        y += velocityY;

        // Reset velocity (movement is handled by input)
        velocityX = 0;
        velocityY = 0;

        // Update shoot timer
        if (shootTimer > 0) {
            shootTimer--;
        }

        // Keep player within screen bounds
        if (y < 0) y = 0;
        if (y > 600 - height) y = 600 - height;
        if (x < 0) x = 0;
        if (x > 1000 - width) x = 1000 - width;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)width, (int)height);

        // Draw spaceship details
        g.setColor(Color.WHITE);
        int[] xPoints = {(int)(x + width), (int)(x + width - 10), (int)(x + width)};
        int[] yPoints = {(int)(y + height/2), (int)(y), (int)(y + height)};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    // Movement methods
    public void moveUp() {
        velocityY = -SPEED;
    }

    public void moveDown() {
        velocityY = SPEED;
    }

    public void moveLeft() {
        velocityX = -SPEED;
    }

    public void moveRight() {
        velocityX = SPEED;
    }

    // Shooting method
    public Bullet shoot() {
        if (shootTimer <= 0) {
            shootTimer = SHOOT_COOLDOWN;
            return new Bullet(x + width, y + height/2 - 2, 8, 0);
        }
        return null;
    }

    // Health management
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public int getHealth() {
        return health;
    }
}