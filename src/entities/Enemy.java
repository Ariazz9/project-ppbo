package entities;

import java.awt.*;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Enemy class inheriting from GameObject
 * Demonstrates inheritance and polymorphism
 */
public class Enemy extends GameObject {
    private static final double BASE_SPEED = 2.0;
    private static final int SHOOT_COOLDOWN = 60;

    private int shootTimer;
    private int scoreValue;
    private Random random;
    private BufferedImage sprite;
    private boolean useSprite = false;

    // Level-based properties
    private double speed;
    private int bulletDamage;
    private int level;

    public Enemy(double x, double y, int level) {
        super(x, y, 60, 40, Color.RED);
        this.shootTimer = 0;
        this.scoreValue = 10;
        this.random = new Random();
        this.level = level;

        // Calculate level-based properties
        calculateLevelProperties();

        this.velocityX = -speed;

        // Try to load enemy sprite
        loadSprite();
    }

    /**
     * Calculate enemy properties based on level
     * Every 5 levels: reset speed/count but increase bullet damage
     */
    private void calculateLevelProperties() {
        int cycleLevel = ((level - 1) % 5) + 1; // 1-5 cycle
        int damageMultiplier = (level - 1) / 5; // Increases every 5 levels

        // Speed increases within each 5-level cycle
        this.speed = BASE_SPEED + (cycleLevel - 1) * 0.5;

        // Bullet damage increases every 5 levels
        this.bulletDamage = 1 + damageMultiplier;

        // Score value increases with level
        this.scoreValue = 10 + (level - 1) * 2;
    }

    /**
     * Load enemy sprite from assets, fallback to rectangle if not found
     */
    private void loadSprite() {
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/assets/sprites/enemy.png"));
            useSprite = true;
            System.out.println("Enemy sprite loaded successfully");
        } catch (Exception e) {
            useSprite = false;
            System.out.println("Enemy sprite not found, using rectangle rendering");
        }
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (useSprite && sprite != null) {
            // Render sprite
            g2d.drawImage(sprite, (int)x, (int)y, (int)width, (int)height, null);

            // Add level indicator for sprite
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("L" + level, (int)(x + width + 2), (int)(y + 10));
        } else {
            // Fallback to enhanced rectangle rendering with level-based colors
            Color enemyColor = getLevelColor();

            // Main body
            g2d.setColor(enemyColor);
            g2d.fillRect((int)x, (int)y, (int)width, (int)height);

            // Dark details
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect((int)(x + width * 0.1), (int)(y + height * 0.3),
                    (int)(width * 0.3), (int)(height * 0.4));

            // Wings
            g2d.setColor(Color.GRAY);
            g2d.fillRect((int)(x + width * 0.4), (int)(y - 3),
                    (int)(width * 0.4), 3);
            g2d.fillRect((int)(x + width * 0.4), (int)(y + height),
                    (int)(width * 0.4), 3);

            // Engine glow
            g2d.setColor(enemyColor.brighter());
            g2d.fillOval((int)(x + width), (int)(y + height * 0.3),
                    6, (int)(height * 0.4));
            g2d.setColor(Color.ORANGE);
            g2d.fillOval((int)(x + width + 1), (int)(y + height * 0.35),
                    4, (int)(height * 0.3));

            // Weapon systems
            g2d.setColor(Color.YELLOW);
            g2d.fillRect((int)x, (int)(y + height * 0.2), 3, 3);
            g2d.fillRect((int)x, (int)(y + height * 0.7), 3, 3);

            // Nose cone
            g2d.setColor(Color.ORANGE);
            int[] xPoints = {(int)x, (int)(x + 8), (int)x};
            int[] yPoints = {(int)(y + height/2), (int)y, (int)(y + height)};
            g2d.fillPolygon(xPoints, yPoints, 3);

            // Level indicator
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("L" + level, (int)(x + width + 2), (int)(y + 10));
        }
    }

    /**
     * Get enemy color based on level for visual distinction
     */
    private Color getLevelColor() {
        int cycleLevel = ((level - 1) % 5) + 1;
        switch (cycleLevel) {
            case 1: return Color.RED;
            case 2: return Color.ORANGE;
            case 3: return Color.YELLOW;
            case 4: return Color.MAGENTA;
            case 5: return Color.CYAN;
            default: return Color.RED;
        }
    }

    // Enemy shooting method with level-based damage
    public EnemyBullet shoot() {
        if (shootTimer <= 0 && random.nextInt(100) < 2) { // 2% chance per frame
            shootTimer = SHOOT_COOLDOWN;
            return new EnemyBullet(x, y + height/2 - 2, -6, 0, bulletDamage);
        }
        return null;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getBulletDamage() {
        return bulletDamage;
    }

    public int getLevel() {
        return level;
    }

    public double getSpeed() {
        return speed;
    }
}
