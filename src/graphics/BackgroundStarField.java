package graphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BackgroundStarField {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;

    private List<Star> backgroundStars;
    private int starCount;

    public BackgroundStarField(int starCount) {
        this.starCount = starCount;
        backgroundStars = new ArrayList<>();
        initializeStars();
    }

    private void initializeStars() {
        for (int i = 0; i < starCount; i++) {
            backgroundStars.add(new Star());
        }
    }

    public void update() {
        for (Star star : backgroundStars) {
            star.update();
        }
    }

    public void render(Graphics2D g2d) {
        for (Star star : backgroundStars) {
            star.render(g2d);
        }
    }

    private class Star {
        private float x, y;
        private float speed;
        private int size;
        private Color color;

        public Star() {
            reset();
        }

        private void reset() {
            x = (float) (Math.random() * PANEL_WIDTH);
            y = (float) (Math.random() * PANEL_HEIGHT);
            speed = (float) (Math.random() * 3 + 1);
            size = (int) (Math.random() * 3 + 1);

            int brightness = (int) (Math.random() * 128 + 127);
            color = new Color(brightness, brightness, brightness);
        }

        public void update() {
            x -= speed;
            if (x < -size) {
                x = PANEL_WIDTH + size;
                y = (float) (Math.random() * PANEL_HEIGHT);
            }
        }

        public void render(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillOval((int) x, (int) y, size, size);
        }
    }
}
