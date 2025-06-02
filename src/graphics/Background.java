package graphics;

import java.awt.*;

/**
 * Background class for side-scrolling effect
 * Demonstrates encapsulation and separation of concerns
 */
public class Background {
    private double x1, x2;
    private int width, height;
    private double scrollSpeed;

    public Background(int width, int height) {
        this.width = width;
        this.height = height;
        this.x1 = 0;
        this.x2 = width;
        this.scrollSpeed = 1.0;
    }

    public void update() {
        // Move background to the left
        x1 -= scrollSpeed;
        x2 -= scrollSpeed;

        // Reset positions for infinite scrolling
        if (x1 <= -width) {
            x1 = width;
        }
        if (x2 <= -width) {
            x2 = width;
        }
    }

    public void render(Graphics g) {
        // Draw starfield background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // Draw moving stars
        g.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int starX = (int)((x1 + (i * 13) % width) % width);
            int starY = (i * 17) % height;
            g.fillOval(starX, starY, 2, 2);

            starX = (int)((x2 + (i * 13) % width) % width);
            g.fillOval(starX, starY, 2, 2);
        }
    }
}
