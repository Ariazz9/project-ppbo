package entities;

import java.awt.*;

/**
 * Abstract base class for all game objects
 * Demonstrates abstraction and provides common functionality
 */
public abstract class GameObject {
    protected double x, y;
    protected double width, height;
    protected double velocityX, velocityY;
    protected Color color;

    public GameObject(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.velocityX = 0;
        this.velocityY = 0;
    }

    // Abstract methods demonstrating polymorphism
    public abstract void update();
    public abstract void render(Graphics g);

    // Collision detection method
    public boolean collidesWith(GameObject other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }

    // Encapsulation: Getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }
}