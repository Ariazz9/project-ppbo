package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Audio.AudioPlayer;
import Entity.EnemyShip;
import Entity.Explosion;
import Entity.ExplosionType;
import Entity.Missile;
import Entity.Player;
import Entity.ScoreBubble;
import Main.GamePanel;
import Menus.HighScore;
import Menus.MenuManager;

/*
 * to do:
 * resize JPanel and accomodate graphics and speeds
 * explosion sound effects
 * pausing is causing music to start
 */
public class Game {
    //map objects
    private final ArrayList<EnemyShip> enemies;
    private final ArrayList<Missile> missiles;
    private final ArrayList<Missile> enemyMissiles;
    private final ArrayList<Point> bottomEnemyPoints;
    private final ArrayList<Rectangle> barriers;
    private final ArrayList<Explosion> explosions;
    private final ArrayList<ScoreBubble> scoreBubbles;
    private EnemyShip strayEnemy;
    private final Player player;

    //sound effects
    private final AudioPlayer laserSound;
    private final AudioPlayer enemyLaserSound;
    private final AudioPlayer explosionSound;
    private final AudioPlayer explosionStrayEnemySound;
    private final AudioPlayer explosionPlayerSound;
    private final AudioPlayer music;

    private final HUD hud;
    private final Background background;
    //private final AudioPlayer bgMusic;
    private final Font countDownFont;
    private final Font messagesFont;
    private final MenuManager menuManager;
    private final TimerManager timerManager;

    private int kills;
    private int continues;
    private int countDownNumber;
    private int timePassBetweenEnemyShooting;

    private boolean isGameOver;
    private boolean pauseMode;
    private boolean cheatMode;
    private boolean startMenuMode;
    private boolean strayEnemyMode;
    private boolean initiateNewGameMode;
    private boolean strayEnemyFromLeft;
    private boolean pauseCountDownMode;
    private boolean playerExplosionMode;

    private String settingChangeMessage;
    private int settingChangeXPos;
    private boolean showSettingChangeMode;

    private static boolean showHelpDialog;
    private static boolean showHighScoresMenu;

    private static final String enabledMessage = "enabled";
    private static final String disabledMessage = "disabled";

    private enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public Game() {
        barriers = new ArrayList<Rectangle>(); //Rectangle[4][60];
        missiles = new ArrayList<Missile>();
        enemyMissiles = new ArrayList<Missile>();
        enemies = new ArrayList<EnemyShip>();
        bottomEnemyPoints = new ArrayList<Point>();
        explosions = new ArrayList<Explosion>();
        scoreBubbles = new ArrayList<ScoreBubble>();
        menuManager = new MenuManager();
        timerManager = new TimerManager();
        background = new Background("/Backgrounds/black.jpg", -1.5, 0);
        laserSound = new AudioPlayer("/SFX/laser.wav");
        enemyLaserSound = new AudioPlayer("/SFX/laser_enemy.wav");
        explosionSound = new AudioPlayer("/SFX/explosion.wav");
        explosionStrayEnemySound = new AudioPlayer("/SFX/explosion_stray_enemy.wav");
        explosionPlayerSound = new AudioPlayer("/SFX/explosion_player.wav");
        music = new AudioPlayer("/SFX/music.wav");
        countDownFont = new Font("Arial", Font.PLAIN, 60);
        messagesFont = new Font("Ariel", Font.PLAIN, 12);
        player = new Player();
        hud = new HUD(player);
        startMenuMode = true;
    }

    private void checkForHighScores() {
        if (continues == 0) {
            if (!menuManager.getHighScoresMenu().hasAlreadyAttemptedToEnterHighScores()) {
                menuManager.getHighScoresMenu().addHighScore(new HighScore("", player.getScore()));
            }
            if (menuManager.getHighScoresMenu().enteredHighScores()) {
                showHighScoresMenu = true;
            }
        }
    }

    private void determineNewSpeed() {
        //60 enemies
        if (cheatMode) return;
        if (kills < 11) EnemyShip.setMoveSpeed(0.4);
        else if (kills < 21) EnemyShip.setMoveSpeed(0.8);
        else if (kills < 31) EnemyShip.setMoveSpeed(1.2);
        else if (kills < 41) EnemyShip.setMoveSpeed(1.6);
        else if (kills < 51) EnemyShip.setMoveSpeed(2.0);
        else if (kills < 56) EnemyShip.setMoveSpeed(3.0);
        else if (kills < 57) EnemyShip.setMoveSpeed(4.0);
        else if (kills < 58) EnemyShip.setMoveSpeed(7.0);
        else if (kills < 59) EnemyShip.setMoveSpeed(9.0);
        else if (kills < 60) EnemyShip.setMoveSpeed(12.0);
    }

    public void draw(final Graphics2D g) {
        if (startMenuMode) {
            if (music.isRunning()) music.stop();
            menuManager.getStartMenu().draw(g);
            if (showHelpDialog) {
                menuManager.getHelpMenu().draw(g);
            } else if (showHighScoresMenu) {
                menuManager.getHighScoresMenu().draw(g);
                drawMessage(g);
                return;
            }
            drawMessage(g);
            return;
        }
        if (showHelpDialog) {
            menuManager.getHelpMenu().draw(g);
            drawMessage(g);
            return;
        }
        background.draw(g);
        if (initiateNewGameMode) {
            g.setColor(Color.RED);
            g.setFont(countDownFont);
            //System.out.println(countDownNumber);
            int time = pauseCountDownMode ? countDownNumber : (int) (((System.nanoTime() - timerManager.getTimer(Timer.INITIATE_NEW_GAME)) / 1000000000));
            time = 3 - time;
            if (time > 0) {
                g.drawString(time + "...", 280, 230);
            } else {
                player.incrementLevel();
                newGame();
            }
        }
        for (EnemyShip enemyShip : enemies) {
            enemyShip.draw(g);
        }
        if (Settings.enabledExplosions()) {
            for (Explosion e : explosions) {
                e.draw(g);
            }
        }
        if (Settings.enabledScoreBubbles()) {
            for (ScoreBubble sb : scoreBubbles) {
                sb.draw(g);
            }
        }
        if (strayEnemyMode) {
            if (!strayEnemy.isDead()) strayEnemy.draw(g);
        }
        g.setColor(Color.RED);
        if (!barriers.isEmpty())  //concurrent modification error, added this, hopefully goes away
        {
            for (Rectangle b : barriers) {
                g.draw(b);
            }
        }
        for (Missile missile : missiles) {
            g.drawImage(Missile.getMissileSprite(), (int) (missile.getx() - missile.getWidth() / 2), (int) (missile.gety() - missile.getHeight() / 2), null);
            g.draw(missile.getRectangle());

        }
        for (Missile enemyMissile : enemyMissiles) {
            g.drawImage(Missile.getEnemyMissileSprite(), (int) (enemyMissile.getx() - enemyMissile.getWidth() / 2), (int) (enemyMissile.gety() - enemyMissile.getHeight() * 6), null);
            g.draw(enemyMissile.getRectangle());
        }
        for (EnemyShip enemy : enemies) {
            g.draw(enemy.getRectangle());
        }
        hud.draw(g);
        drawMessage(g);
        if (player.getFlinching() && !isGameOver && !pauseMode) {
            if (player.getElapsed() % 2 == 0) {
                if (!playerExplosionMode) drawPlayerSprite(g);
                return;
            }
        } else {
            if (!playerExplosionMode) drawPlayerSprite(g);
            else {
                g.draw(player.getRectangle());
            }
        }
        if (isGameOver) {
            if (music.isRunning()) music.stop();
            menuManager.getGameOverMenu().draw(g);
            if (showHighScoresMenu) {
                menuManager.getHighScoresMenu().draw(g);
                return;
            }
        }
    }

    private void setMessageSettings(final String message, final int xPos) {
        timerManager.setToSystemNanoTime(Timer.SETTING_CHANGE_MESSAGE);
        showSettingChangeMode = true;
        settingChangeMessage = message;
        settingChangeXPos = xPos;
    }

    private void drawMessage(final Graphics2D g) {
        g.setFont(messagesFont);
        if (showSettingChangeMode) {
            g.setColor(Color.WHITE);
            if (System.nanoTime() - timerManager.getTimer(Timer.SETTING_CHANGE_MESSAGE) < 1500000000) {
                g.drawString(settingChangeMessage, settingChangeXPos, 430);
            } else {
                showSettingChangeMode = false;
            }
        }
    }

    private void drawPlayerSprite(final Graphics2D g) {
        BufferedImage sprite = Player.getPlayerSprite();

        // Buat transformasi ROTASI 90 derajat searah jarum jam
        AffineTransform transform = new AffineTransform();
        transform.translate(sprite.getHeight() / 2.0, sprite.getWidth() / 2.0); // tengah
        transform.rotate(Math.toRadians(90));
        transform.translate(-sprite.getWidth() / 2.0, -sprite.getHeight() / 2.0);

        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedSprite = op.filter(sprite, null);

        // Gambar sprite yang sudah diputar di posisi yang normal
        g.drawImage(rotatedSprite, (int)(player.getx()),
                (int)(player.gety()), null);
    }


    private Point getClosestBottomMostEnemyPoint() {
        Point point;
        bottomEnemyPoints.clear();
        for (EnemyShip enemy : enemies) {
            point = new Point((int) enemy.getx(), (int) enemy.gety());
            bottomEnemyPoints.add(point);
            for (int y = 0; y < bottomEnemyPoints.size(); y++) {
                if (point.getX() == bottomEnemyPoints.get(y).getX() && point.getY() > bottomEnemyPoints.get(y).getY()) {
                    bottomEnemyPoints.remove(y);
                    y--;
                }
            }
        }
        double x = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < bottomEnemyPoints.size(); i++) {
            if (Math.abs(bottomEnemyPoints.get(i).x - player.getx()) < x) {
                x = Math.abs(bottomEnemyPoints.get(i).x - player.getx());
                index = i;
            }
        }
        //return bottomEnemyPoints.get((new Random()).nextInt(bottomEnemyPoints.size()));
        return bottomEnemyPoints.get(index);
    }

    @SuppressWarnings("incomplete-switch")
    private int getDirectionalMostEnemy(final Direction direction) {
        int ship = 0;
        double position = switch (direction) {
            case DOWN -> 640;
            case UP -> 210;
            default -> 0;
        };
        for (int y = 0; y < enemies.size(); y++) {
            switch (direction) {
                case UP:
                    if (enemies.get(y).gety() < position) {
                        position = enemies.get(y).gety();
                        ship = y;
                    }
                    break;
                case DOWN, LEFT:
                    if (enemies.get(y).gety() > position) {
                        position = enemies.get(y).gety();
                        ship = y;
                    }
                    break;
            }
        }
        return ship;
    }

    public boolean isPaused() {
        return pauseMode;
    }

    public void keyPressed(final int k) {
        if (!menuManager.getHighScoresMenu().enteredHighScores()) {
            if (k == KeyEvent.VK_B) {
                Settings.toggleBackgroundImage();
                setMessageSettings("Background image " + (Settings.enabledBackground() ? enabledMessage : disabledMessage), 435);
            } else if (k == KeyEvent.VK_Q) {
                Settings.toggleSound();
                setMessageSettings("Sound " + (Settings.enabledSound() ? enabledMessage : disabledMessage), 505);
            } else if (k == KeyEvent.VK_M) {
                Settings.toggleMusic();
                setMessageSettings("Music " + (Settings.enabledMusic() ? enabledMessage : disabledMessage), 505);
                if (Settings.enabledMusic() && !pauseMode) {
                    music.loop();
                } else {
                    music.stop();
                }
            } else if (k == KeyEvent.VK_E) {
                Settings.toggleExplosions();
                setMessageSettings("Explosions " + (Settings.enabledExplosions() ? enabledMessage : disabledMessage), 480);
            } else if (k == KeyEvent.VK_H) {
                Settings.toggleScoreBubbles();
                setMessageSettings("Score bubbles " + (Settings.enabledScoreBubbles() ? enabledMessage : disabledMessage), 460);
            } else if (k == KeyEvent.VK_2) {
                Settings.setBackgroundScrolling(true);
                setMessageSettings("Background scrolling enabled", 425);
            } else if (k == KeyEvent.VK_3) {
                Settings.setBackgroundScrolling(false);
                setMessageSettings("Background scrolling disabled", 425);
            } else if (k == KeyEvent.VK_K) {
                Settings.decrementPlayerSensitivity();
                setMessageSettings("Player sensitivity: " + Settings.getPlayerSensitivity(), 485);
            } else if (k == KeyEvent.VK_L) {
                Settings.incrementPlayerSensitivity();
                setMessageSettings("Player sensitivity: " + Settings.getPlayerSensitivity(), 485);
            }
        }
        if (showHighScoresMenu) {
            if (!menuManager.getHighScoresMenu().enteredHighScores() && (k == KeyEvent.VK_ESCAPE || (k == KeyEvent.VK_ENTER))) {
                showHighScoresMenu = false;
            } else if (menuManager.getHighScoresMenu().enteredHighScores()) {
                menuManager.getHighScoresMenu().keyPressed(k);
            }
        } else if (startMenuMode) {
            if (k == KeyEvent.VK_F1)
                showHelpDialog = !showHelpDialog;
            else if (k == KeyEvent.VK_ESCAPE) {
                if (showHelpDialog)
                    showHelpDialog = false;
                else
                    System.exit(0);
            } else if (k == KeyEvent.VK_ENTER && showHelpDialog)
                showHelpDialog = false;
            else {
                menuManager.getStartMenu().keyPressed(k);
//        if (showHighScoresMenu)
//        {
//          highScoresMenu.loadHighScores();
//        }
            }
        } else if (isGameOver) {
            switch (k) {
                case KeyEvent.VK_DOWN:
                    menuManager.getGameOverMenu().incrementChoice();
                    break;
                case KeyEvent.VK_UP:
                    menuManager.getGameOverMenu().decrementChoice();
                    break;
                case KeyEvent.VK_ENTER:
                    if (menuManager.getGameOverMenu().getChoice() == 2) {
                        menuManager.getStartMenu().reset();
                        startMenuMode = true;
                    } else if (menuManager.getGameOverMenu().getChoice() == 1) //start over
                    {
                        reset();
                    } else if (menuManager.getGameOverMenu().getChoice() == 0) //continue
                    {
                        if (continues > 0) {
                            music.reset();
                            continues--;
                            newGame();
                        }
                    }
                    menuManager.getGameOverMenu().reset();
            }
            return;
        } else if (showHelpDialog) {
            if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_ESCAPE || k == KeyEvent.VK_F1)
                showHelpDialog = false;
        } else if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
            player.setDown(true);
        } else if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
            player.setUp(true);
        } else if (k == KeyEvent.VK_SPACE && !initiateNewGameMode) {
            if (System.nanoTime() - timerManager.getTimer(Timer.MISSILE) > 350000000) {
                timerManager.setToSystemNanoTime(Timer.MISSILE);
                Missile m = new Missile();
                m.setPosition(player.getx() + 5, player.gety() + 22);
                missiles.add(m);
                if (Settings.enabledSound()) laserSound.play();
            }
        } else if (k == KeyEvent.VK_P) {
            boolean skip = false;
            final long sysTime = System.nanoTime();
            if (pauseMode && !initiateNewGameMode) {
                timerManager.updateForPauseTimer();
                player.addToFlinchTimer(sysTime - timerManager.getTimer(Timer.PAUSE));
                timerManager.setTimer(Timer.PAUSE, 0);
            }
            if (!pauseMode) timerManager.setToSystemNanoTime(Timer.PAUSE);
            if (!initiateNewGameMode) pauseMode = !pauseMode;
            if (initiateNewGameMode) {
                pauseCountDownMode = !pauseCountDownMode;
                if (pauseCountDownMode) {
                    countDownNumber = (int) (((System.nanoTime() - timerManager.getTimer(Timer.INITIATE_NEW_GAME))) / 1000000000);
                    timerManager.setToSystemNanoTime(Timer.PAUSE_COUNT_DOWN);
                    music.pause();
                } else {
                    timerManager.setTimer(Timer.INITIATE_NEW_GAME, timerManager.getTimer(Timer.INITIATE_NEW_GAME) + (sysTime - timerManager.getTimer(Timer.PAUSE_COUNT_DOWN)));
                    if (Settings.enabledMusic()) music.cont();
                }
                skip = true;
            }
            if (pauseMode) {
                music.pause();
                if (!pauseCountDownMode && skip) music.cont();
            } else {
                if (Settings.enabledMusic()) music.cont();
            }
        } else if (k == KeyEvent.VK_F2) {
            reset();
        } else if (k == KeyEvent.VK_ESCAPE) {
            menuManager.getStartMenu().reset();
            pauseMode = false;
            startMenuMode = true;
        } else if (k == KeyEvent.VK_8) {
            cheatMode = true;
            EnemyShip.increaseMoveSpeed();
        } else if (k == KeyEvent.VK_9) {
            cheatMode = true;
            EnemyShip.decreaseMoveSpeed();
        } else if (k == KeyEvent.VK_F1) {
            if (!isGameOver && !initiateNewGameMode) {
                showHelpDialog = !showHelpDialog;
                boolean toBeUnpaused = false;
                if (!pauseMode) {
                    toBeUnpaused = true;
                    pauseMode = true;
                }
                if (toBeUnpaused) pauseMode = false;
            }
        }
    }

    public void keyReleased(final int k) {
        if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
            player.setDown(false);
        } else if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
            player.setUp(false);
        }
    }

    private void newGame() {
        missiles.clear();
        enemyMissiles.clear();
        explosions.clear();
        scoreBubbles.clear();
        player.resetHealth();
        player.setPosition(60, 210);
        isGameOver = pauseMode = cheatMode = strayEnemyMode
                = initiateNewGameMode = showHelpDialog = playerExplosionMode = false;
        populateEnemies();
        populateBarriers();
        timerManager.setToSystemNanoTime(Timer.ENEMY_MISSILE);
        timerManager.resetTimer(Timer.PAUSE);
        kills = 0;
        menuManager.getHighScoresMenu().resetEntry();
        timePassBetweenEnemyShooting = (800000000 - player.getLevel() * 100000000);
        if (timePassBetweenEnemyShooting < 200000000) timePassBetweenEnemyShooting = 200000000;
        //music.setFramePosition();
        if (Settings.enabledMusic() && !music.isRunning()) music.cont();
    }

    private void populateBarriers() {
        barriers.clear();
        Rectangle rect;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 40; y++) {
                rect = new Rectangle();
                rect.setBounds(140 + 4 * y / 15,50 + x * 150 + (y % 20) * 4 , 4, 4);
                barriers.add(rect);
            }
            timerManager.setToSystemNanoTime(Timer.STRAY_ENEMY);
        }
    }

    private void populateEnemies() {
        enemies.clear();
        EnemyShip ship;

        int columns = 5;
        int rows = 10;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                int whichOne = Math.min(y, 3);
                ship = new EnemyShip(whichOne);
                ship.setPosition(x * 60 + 430, 50 + 40 * y);
                enemies.add(ship);
            }
        }
        EnemyShip.setMoveSpeed(EnemyShip.MOVESPEED);
    }

    private void reset() {
        continues = 3; //change back to 3
        player.resetHealth();
        player.resetScoreAndLevel();
        newGame();
    }

    public static void showHelpDialog() {
        showHelpDialog = true;
    }

    public static void showHighScoresMenu() {
        showHighScoresMenu = true;
    }

    public void update() {
        if (playerExplosionMode) {
            if (System.nanoTime() - timerManager.getTimer(Timer.PLAYER_EXPLOSION) > 1000000000) {
                menuManager.getGameOverMenu().setContinues(continues);
                isGameOver = true;
                checkForHighScores();
                playerExplosionMode = false;
            }
        }
        if (pauseMode) return;
        if (startMenuMode) {
            menuManager.getStartMenu().update();
            if (menuManager.getStartMenu().newGameRequested()) {
                startMenuMode = false;
                reset();
            }
        }
        if (startMenuMode || showHelpDialog || isGameOver) return;
        player.update();
        background.update();
        updateEnemyShipDirection();
        updateDeadEnemiesAndCheckForEnemyInvasion();
        if (enemies.isEmpty() && !initiateNewGameMode) {
            initiateNewGameMode = true;
            timerManager.setToSystemNanoTime(Timer.INITIATE_NEW_GAME);
            pauseMode = true;
        }
        updatePlayerMissileCollisionWithBarriersAndEnemies();
        updateEnemyMissileCollisionWithBarriersAndPlayer();
        updateEnemyShooting();
        updateMissilePositions();
        updateStrayEnemy();
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).updateFrame();
            if (explosions.get(i).isDone()) {
                explosions.remove(i);
                i--;
            }
        }
        for (int i = 0; i < scoreBubbles.size(); i++) {
            scoreBubbles.get(i).update();
            if (scoreBubbles.get(i).isDone()) {
                scoreBubbles.remove(i);
                i--;
            }
        }
    }

    private void updateDeadEnemiesAndCheckForEnemyInvasion() {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
            if (enemies.get(i).isDead()) {
                enemies.remove(i);
                i--;
            } else {
                if (enemies.get(i).getx() < 0) {
                    isGameOver = true;
                    menuManager.getGameOverMenu().setContinues(continues);
                    checkForHighScores();
                }
            }
        }
    }

    private void updateEnemyMissileCollisionWithBarriersAndPlayer() {
        boolean kill = false;
        int object = 0;
        for (int x = 0; x < enemyMissiles.size(); x++) {
            kill = false;
            for (int y = 0; y < barriers.size(); y++) {
                if (enemyMissiles.get(x).getRectangle().intersects(barriers.get(y))) {
                    kill = true;
                    object = y;
                }
            }
            if (kill) {
                enemyMissiles.remove(x);
                barriers.remove(object);
                continue;
            }
            if (player.intersects(enemyMissiles.get(x)) && !playerExplosionMode) {
                player.hit();
                if (player.getHealth() == -1) {
                    Explosion e = new Explosion(ExplosionType.PLAYER);
                    e.setPosition(player.getx(), player.gety());
                    explosions.add(e);
                    playerExplosionMode = true;
                    timerManager.setTimer(Timer.PLAYER_EXPLOSION, System.nanoTime());
                    if (Settings.enabledSound()) explosionPlayerSound.play();
                }
                enemyMissiles.remove(x);
            }
        }
    }

    private void updateEnemyShipDirection() {
        if (enemies.isEmpty()) return;
        if (!enemies.getFirst().getLeftMode()) {
            if (enemies.getFirst().getUp()) {
                if (enemies.get(getDirectionalMostEnemy(Direction.UP)).gety() < 20) {
                    for (EnemyShip e : enemies) {
                        e.setLeftMode();
                    }
                }
                ;
            } else {
                if (enemies.get(getDirectionalMostEnemy(Direction.DOWN)).gety() > 100) {
                    for (EnemyShip e : enemies) {
                        e.setLeftMode();
                    }
                }
                ;
            }
        }
        EnemyShip.updateFrame();
    }

    private void updateEnemyShooting() {
        if (System.nanoTime() - timerManager.getTimer(Timer.ENEMY_MISSILE) > timePassBetweenEnemyShooting && !enemies.isEmpty()) {
            timerManager.setToSystemNanoTime(Timer.ENEMY_MISSILE);
            Point point = getClosestBottomMostEnemyPoint();
            Missile m = new Missile();
            m.setPosition(point.getX(), point.getY());  //the -15 aligns it with enemy ship
            enemyMissiles.add(m);
            if (Settings.enabledSound()) enemyLaserSound.play();
        }
    }

    private void updateMissilePositions() {
        for (int x = 0; x < missiles.size(); x++) {
            Missile m = missiles.get(x);
            m.setPosition(m.getx() + 3, m.gety());
            if (m.getx() > GamePanel.WIDTH) missiles.remove(x);
        }

        for (int x = 0; x < enemyMissiles.size(); x++) {
            Missile m = enemyMissiles.get(x);
            m.setPosition(m.getx() - 3, m.gety());
            if (m.getx() < 0) enemyMissiles.remove(x);
        }
    }


    private void updatePlayerMissileCollisionWithBarriersAndEnemies() {
        boolean destroy = false;
        int object = 0;
        for (int x = 0; x < missiles.size(); x++) {
            destroy = false;
            for (int y = 0; y < barriers.size(); y++) {
                if (missiles.get(x).getRectangle().intersects(barriers.get(y))) {
                    destroy = true;
                    object = y;
                }
            }
            if (destroy) {
                missiles.remove(x);
                barriers.remove(object);
                continue;
            }
            for (int y = 0; y < enemies.size(); y++) {
                if (missiles.get(x).intersects(enemies.get(y))) {
                    destroy = true;
                    object = y;
                    Explosion e = new Explosion(ExplosionType.MOST);
                    e.setPosition(enemies.get(y).getx(), enemies.get(y).gety());
                    explosions.add(e);
                    if (Settings.enabledSound()) explosionSound.play();
                }
            }
            if (destroy) {
                int score = enemies.get(object).getPoints(player.getLevel());
                ScoreBubble sb = new ScoreBubble(score);
                sb.setPosition(enemies.get(object).getx() + 10, enemies.get(object).gety());
                scoreBubbles.add(sb);
                if (!cheatMode) player.incrementScore(score);
                missiles.remove(x);
                enemies.get(object).kill();
                kills++;
                determineNewSpeed();
            }
        }
    }

    private void updateStrayEnemy() {
        if ((System.nanoTime() - timerManager.getTimer(Timer.STRAY_ENEMY)) / 1000 > 20000000) {
            timerManager.setToSystemNanoTime(Timer.STRAY_ENEMY);
            strayEnemy = new EnemyShip(-1);
            int position = strayEnemyFromLeft ? -20 : GamePanel.WIDTH + 20;
            strayEnemyFromLeft = !strayEnemyFromLeft;
            if (strayEnemyFromLeft) {
                strayEnemy.setDown();
            } else strayEnemy.setUp();
            strayEnemy.setPosition(position, 20);
            strayEnemyMode = true;
        } else if (strayEnemyMode) {
            strayEnemy.update();
            if (strayEnemy.getx() > GamePanel.WIDTH + 20 || strayEnemy.getx() < -20) strayEnemyMode = false;
            for (int x = 0; x < missiles.size(); x++) {
                if (missiles.get(x).intersects(strayEnemy)) {
                    int score = strayEnemy.getPoints(player.getLevel());
                    strayEnemy.kill();
                    if (!cheatMode) player.incrementScore(score);
                    missiles.remove(x);
                    Explosion e = new Explosion(ExplosionType.STRAY);
                    e.setPosition(strayEnemy.getx(), strayEnemy.gety());
                    explosions.add(e);
                    ScoreBubble sb = new ScoreBubble(score);
                    sb.setPosition(strayEnemy.getx() + (strayEnemy.getx() < 300 ? 20 : -10), strayEnemy.gety() + 30);
                    scoreBubbles.add(sb);
                    if (Settings.enabledSound()) explosionStrayEnemySound.play();
                }
            }
        }
    }
}