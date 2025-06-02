package ui;

import java.awt.*;

public class HUD {
    private Font font;
    private Font instructionFont;

    public HUD() {
        this.font = new Font("Arial", Font.BOLD, 16);
        this.instructionFont = new Font("Arial", Font.PLAIN, 12);
    }

    public void render(Graphics g, int score, int level, int health) {
        g.setFont(font);
        g.setColor(Color.WHITE);

        // Draw score
        g.drawString("Score: " + score, 10, 25);

        // Draw level
        g.drawString("Level: " + level, 10, 50);

        // Draw health bar
        g.drawString("Health: ", 10, 75);
        g.setColor(Color.RED);
        g.fillRect(80, 60, health * 2, 15);
        g.setColor(Color.WHITE);
        g.drawRect(80, 60, 200, 15);

        // Instructions
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(instructionFont);
        g.drawString("WASD/Arrow Keys: Move | Space: Shoot", 10, 590);
    }
}