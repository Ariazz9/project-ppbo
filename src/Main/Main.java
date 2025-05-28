package Main;
import java.awt.image.BufferedImage;
/*
 * need to do:
 * explosion animation
 * save file for settings
 * make default background image static?
 */



import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Entity.EnemyShip;
import Entity.Explosion;
import Entity.Missile;
import Entity.Player;
import Game.Background;
import Game.Settings;
import Main.GamePanel;
public class Main
{
    public static void main(String[] args)
    {
        final JFrame window = new JFrame("Space Invaders");
        window.setContentPane(new GamePanel());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        loadSprites();
        Settings.loadSettingsFile();
    }
    private static void loadSprites()
    {
        try
        {
            BufferedImage spritesheet = ImageIO.read(Main.class.getResourceAsStream("/Sprites/Weapons/missile.png"));
            Missile.setMissileSprite(spritesheet.getSubimage(0, 0, 3, 15));
            spritesheet = ImageIO.read(Main.class.getResourceAsStream("/Sprites/Weapons/missile_enemy.png"));
            Missile.setEnemyMissileSprite(spritesheet.getSubimage(0, 0, 3, 15));
            spritesheet = ImageIO.read(Main.class.getResourceAsStream("/Sprites/Player/player.png"));
            Player.setPlayerSprite(spritesheet.getSubimage(0, 0, 35, 35));
            spritesheet = ImageIO.read(Main.class.getResourceAsStream("/Sprites/Enemies/enemy_ships.png"));
            BufferedImage sprites[];
            for (int x = 0; x < 4; x++)
            {
                sprites = new BufferedImage[10];
                for (int i = 0; i < sprites.length; i++)
                {
                    sprites[i] = spritesheet.getSubimage(i * 30, 30 + 30 * x, 30, 30);
                }
                EnemyShip.setEnemyShipSprites(x, sprites);
            }
            sprites = new BufferedImage[3];
            for (int i = 0; i < sprites.length; i++)
            {
                sprites[i] = spritesheet.getSubimage(i * 30, 0, 30, 30);
            }
            EnemyShip.setEnemyShipSprites(-1, sprites);
            sprites = new BufferedImage[7];
            spritesheet = ImageIO.read(Main.class.getResourceAsStream("/Sprites/Explosions/explosion.gif"));
            for (int i = 0; i < sprites.length; i++)
            {
                sprites[i] = spritesheet.getSubimage(i * 30, 0, 30, 30);
            }
            Explosion.setExplosionShipSprites(sprites);
            sprites = new BufferedImage[25];
            spritesheet = ImageIO.read(Main.class.getResourceAsStream("/Sprites/Explosions/explosion_stray.gif"));
            for (int i = 0; i < sprites.length; i++)
            {
                sprites[i] = spritesheet.getSubimage(i * 30, 0, 30, 30);
            }
            Explosion.setExplosionStraySprites(sprites);
            sprites = new BufferedImage[14];
            spritesheet = ImageIO.read(Main.class.getResourceAsStream("/Sprites/Explosions/explosion_player.gif"));
            for (int i = 0; i < sprites.length; i++)
            {
                sprites[i] = spritesheet.getSubimage(i * 30, 0, 30, 30);
            }
            Explosion.setExplosionPlayerSprites(sprites);
            Background.setBlackImage(ImageIO.read(Main.class.getResourceAsStream("/Backgrounds/black.jpg")));
        }
        catch (final Exception e)
        {
            System.out.println("error mann");
            e.printStackTrace();
        }
    }
}