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

        // Draw level with cycle information
        int cycleLevel = ((level - 1) % 5) + 1;
        int tier = (level - 1) / 5 + 1;
        g.drawString("Level: " + level + " (Tier " + tier + "-" + cycleLevel + ")", 10, 50);

        // Draw health bar
        g.drawString("Health: ", 10, 75);
        g.setColor(Color.RED);
        g.fillRect(80, 60, health * 2, 15);
        g.setColor(Color.WHITE);
        g.drawRect(80, 60, 200, 15);

        // Draw level progression info
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        if (cycleLevel == 5) {
            g.drawString("Next: Tier " + (tier + 1) + " (Stronger bullets!)", 10, 100);
        } else {
            g.drawString("Next: More enemies & faster speed", 10, 100);
        }

        // Instructions
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(instructionFont);
        g.drawString("WASD/Arrow Keys: Move | Space: Shoot", 10, 590);
    }
}
