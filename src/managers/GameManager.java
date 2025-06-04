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
    private int enemiesPerLevel;
    private int maxEnemiesPerLevel;
    private double enemySpeedMultiplier;
    private int currentLevelEnemyCount;


    public GameManager(int level) {
        this.score = 0;
        this.level = level;
        this.enemySpawnTimer = 0;
        this.enemySpawnRate = 120; // Spawn enemy every 2 seconds at 60 FPS
        this.random = new Random();

        // Initialize level-based properties
        calculateLevelProperties();
    }

    /**
     * Calculate level-based properties
     * Every 5 levels: reset enemy count and speed, but increase bullet damage
     */
    private void calculateLevelProperties() {
        int cycleLevel = ((level - 1) % 5) + 1; // 1-5 cycle within each difficulty tier

        // Base enemies per level increases within each 5-level cycle
        this.enemiesPerLevel = 3 + (cycleLevel - 1) * 2; // 3, 5, 7, 9, 11 enemies
        this.maxEnemiesPerLevel = enemiesPerLevel;

        // Speed multiplier increases within each 5-level cycle
        this.enemySpeedMultiplier = 1.0 + (cycleLevel - 1) * 0.3; // 1.0x to 2.2x speed

        // Spawn rate gets faster within each cycle
        this.enemySpawnRate = Math.max(30, 120 - (cycleLevel - 1) * 20); // 120 to 40 frames

        this.currentLevelEnemyCount = 0;

        System.out.println("Level " + level + " - Enemies: " + enemiesPerLevel +
                ", Speed: " + String.format("%.1f", enemySpeedMultiplier) + "x" +
                ", Spawn Rate: " + enemySpawnRate + " frames");
    }

    public void update(List<Enemy> enemies, int screenWidth, int screenHeight) {
        // Handle enemy spawning based on level requirements
        enemySpawnTimer++;
        if (enemySpawnTimer >= enemySpawnRate && currentLevelEnemyCount < maxEnemiesPerLevel) {
            spawnEnemy(enemies, screenWidth, screenHeight);
            enemySpawnTimer = 0;
            currentLevelEnemyCount++;
        }

        // Check for level progression
        updateLevel(enemies);
    }

    private void spawnEnemy(List<Enemy> enemies, int screenWidth, int screenHeight) {
        double y = random.nextInt(screenHeight - 50) + 25;
        enemies.add(new Enemy(screenWidth, y, level));
    }

    public void updateLevel(List<Enemy> enemies) {
        // Check if all enemies for current level have been spawned and destroyed
        if (currentLevelEnemyCount >= maxEnemiesPerLevel && enemies.isEmpty()) {
            level++;
            calculateLevelProperties();

            System.out.println("Advanced to Level " + level + "!");

            // Bonus score for completing level
            addScore(level * 10);
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

    public int getEnemiesPerLevel() {
        return enemiesPerLevel;
    }

    public int getCurrentLevelEnemyCount() {
        return currentLevelEnemyCount;
    }

    public int getMaxEnemiesPerLevel() {
        return maxEnemiesPerLevel;
    }

    public double getEnemySpeedMultiplier() {
        return enemySpeedMultiplier;
    }
}
