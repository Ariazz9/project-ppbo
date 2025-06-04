package entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

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

    private BufferedImage sprite;
    private boolean useSprite = false;

    public Player(double x, double y) {
        super(x, y, 40, 30, Color.CYAN);
        this.health = MAX_HEALTH;
        this.shootTimer = 0;

        // Try to load player sprite
        loadSprite();
    }

    /**
     * Load player sprite from assets, fallback to rectangle if not found
     */
    private void loadSprite() {
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/assets/sprites/player.png"));
            useSprite = true;
            System.out.println("Player sprite loaded successfully");
        } catch (Exception e) {
            useSprite = false;
            System.out.println("Player sprite not found, using rectangle rendering");
        }
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (useSprite && sprite != null) {
            // Render sprite
            g2d.drawImage(sprite, (int)x, (int)y, (int)width, (int)height, null);
        } else {
            // Fallback to enhanced rectangle rendering
            // Main body
            g2d.setColor(Color.CYAN);
            g2d.fillRect((int)x, (int)y, (int)width, (int)height);

            // Cockpit
            g2d.setColor(Color.BLUE);
            g2d.fillRect((int)(x + width * 0.6), (int)(y + height * 0.3),
                    (int)(width * 0.3), (int)(height * 0.4));

            // Wings
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect((int)(x + width * 0.2), (int)(y - 5),
                    (int)(width * 0.4), 5);
            g2d.fillRect((int)(x + width * 0.2), (int)(y + height),
                    (int)(width * 0.4), 5);

            // Engine glow
            g2d.setColor(Color.ORANGE);
            g2d.fillOval((int)(x - 8), (int)(y + height * 0.3),
                    8, (int)(height * 0.4));
            g2d.setColor(Color.YELLOW);
            g2d.fillOval((int)(x - 6), (int)(y + height * 0.35),
                    6, (int)(height * 0.3));

            // Nose cone
            g2d.setColor(Color.WHITE);
            int[] xPoints = {(int)(x + width), (int)(x + width - 10), (int)(x + width)};
            int[] yPoints = {(int)(y + height/2), (int)(y), (int)(y + height)};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }

        // Health indicator (small bar above player) - always show regardless of sprite
        if (health < MAX_HEALTH) {
            g2d.setColor(Color.RED);
            g2d.fillRect((int)x, (int)(y - 8), (int)width, 3);
            g2d.setColor(Color.GREEN);
            g2d.fillRect((int)x, (int)(y - 8), (int)(width * health / MAX_HEALTH), 3);
        }
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

    public void setHealthToMax() {
        this.health = MAX_HEALTH;
    }
}
