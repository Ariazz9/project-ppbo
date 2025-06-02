package managers;

import entities.Enemy;
import java.util.List;
import java.util.Random;

/**
 * Game manager class handling game state, scoring, and level progression
 * Demonstrates encapsulation and single responsibility principle
 */
public class GameManager {
    private int score;
    private int level;
    private int enemySpawnTimer;
    private int enemySpawnRate;
    private Random random;

    public GameManager() {
        this.score = 0;
        this.level = 1;
        this.enemySpawnTimer = 0;
        this.enemySpawnRate = 120; // Spawn enemy every 2 seconds at 60 FPS
        this.random = new Random();
    }

    public void update(List<Enemy> enemies, int screenWidth, int screenHeight) {
        // Handle enemy spawning
        enemySpawnTimer++;
        if (enemySpawnTimer >= enemySpawnRate) {
            spawnEnemy(enemies, screenWidth, screenHeight);
            enemySpawnTimer = 0;
        }

        // Level progression
        updateLevel();
    }

    private void spawnEnemy(List<Enemy> enemies, int screenWidth, int screenHeight) {
        double x = screenWidth;
        double y = random.nextInt(screenHeight - 50) + 25;
        enemies.add(new Enemy(x, y));
    }

    private void updateLevel() {
        int newLevel = (score / 100) + 1;
        if (newLevel > level) {
            level = newLevel;
            // Increase difficulty by spawning enemies more frequently
            enemySpawnRate = Math.max(30, enemySpawnRate - 10);
        }
    }

    public void addScore(int points) {
        score += points;
    }

    // Encapsulation: Getters for score and level
    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }
}