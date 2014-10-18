package com.mygdx.sharkbait;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Holds menu and game screen resources (images, sounds, animations, etc.).
 * This class has methods which can be used to load additional resources but
 * calling {@code dispose} releases only the resources that are declared here.
 */
public final class Assets {
    public static Animation water;
    public static Animation rockSurface;
    public static Animation rockSink;
    public static Animation playerDodge;
    public static Animation playerMove;
    public static Animation shark;
    public static Animation sharkSurface;
    public static Animation sharkSubmerge;
    public static Animation life;
    public static Animation caffeine;
    public static Animation slow;

    public static TextureRegion rock;
    public static TextureRegion player;
    public static TextureRegion island;

    public static Texture titleTexture;
    public static Texture menuTexture;
    public static Texture pauseTexture;
    public static Texture resumeTexture;
    public static Texture soundTexture;
    public static Texture muteTexture;
    public static Texture lifeTexture;
    public static Texture caffeineTexture;
    public static Texture slowTexture;
    public static Texture waterTexture;
    public static Texture rockTexture;
    public static Texture playerLeapTexture;
    public static Texture playerDodgeTexture;
    public static Texture sharkTexture;
    public static Texture sharkAttackTexture;
    public static Texture islandTexture;

    public static Sound selectSound;
    public static Sound jumpSound;
    public static Sound lifeSound;
    public static Sound slowSound;
    public static Sound caffeineSound;
    public static Sound gameOverSound;
    public static Sound chompSound;
    public static Sound splashSound;
    public static Sound winSound;
    
    private Assets() {}

    /**
     * Loads menu and game screen assets.
     */
    public static void load() {        
        titleTexture = loadTexture("data/title.png");
        pauseTexture = loadTexture("data/pause.png");
        resumeTexture = loadTexture("data/resume.png");
        menuTexture = loadTexture("data/menu.png");
        soundTexture = loadTexture("data/sound.png");
        muteTexture = loadTexture("data/mute.png");

        islandTexture = loadTexture("data/island.png");
        island = new TextureRegion(islandTexture, 32, 32);
        
        lifeTexture = loadTexture("data/life.png");
        TextureRegion[] lifeFrames = split(lifeTexture, 32, 32);
        life = new Animation(0.1f, lifeFrames);
        life.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        caffeineTexture = loadTexture("data/caffeine.png");
        TextureRegion[] caffeineFrames = split(caffeineTexture, 32, 32);
        caffeine = new Animation(0.1f, caffeineFrames);
        caffeine.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        slowTexture = loadTexture("data/slow.png");
        TextureRegion[] slowFrames = split(slowTexture, 32, 32);
        slow = new Animation(0.1f, slowFrames);
        slow.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        waterTexture = loadTexture("data/water.png");
        TextureRegion[] waterFrames = split(waterTexture, 32, 32);
        water = new Animation(0.5f, waterFrames);
        water.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        rockTexture = loadTexture("data/rock.png");
        TextureRegion[] rockFrames = split(rockTexture, 25, 25);
        rockSurface = new Animation(1f / 30, rockFrames);
        rockSink = new Animation(1f / 30, rockFrames);
        rockSink.setPlayMode(Animation.PlayMode.REVERSED);
        rock = rockFrames[rockFrames.length - 1];

        playerLeapTexture = loadTexture("data/player_leap.png");
        TextureRegion[] leapFrames = split(playerLeapTexture, 50, 50);
        playerMove = new Animation(0.5f / leapFrames.length, leapFrames);
        player = leapFrames[0];
        
        playerDodgeTexture = loadTexture("data/player_dodge.png");
        TextureRegion[] dodgeFrames = split(playerDodgeTexture, 50, 50);
        playerDodge = new Animation(0.25f / dodgeFrames.length, dodgeFrames);

        sharkTexture = loadTexture("data/shark.png");
        TextureRegion[] sharkFrames = split(sharkTexture, 50, 50);
        shark = new Animation(1f / 15, sharkFrames);
        shark.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        sharkAttackTexture = loadTexture("data/shark_attack.png");
        TextureRegion[] sharkSurfaceFrames = split(sharkAttackTexture, 50, 50);
        sharkSurface = new Animation(0.53f / sharkSurfaceFrames.length, sharkSurfaceFrames);
        sharkSubmerge = new Animation(0.53f / sharkSurfaceFrames.length, sharkSurfaceFrames);
        sharkSubmerge.setPlayMode(Animation.PlayMode.REVERSED);

        selectSound = loadSound("data/select.wav");
        jumpSound = loadSound("data/jump.wav");
        lifeSound = loadSound("data/life.wav");
        slowSound = loadSound("data/slow.wav");
        caffeineSound = loadSound("data/caffeine.wav");
        gameOverSound = loadSound("data/game_over.wav");
        chompSound = loadSound("data/chomp.wav");
        splashSound = loadSound("data/splash.wav");
        winSound = loadSound("data/win.wav");
    }

    /**
     * Loads a texture from a file.
     * 
     * @param  filename the texture file
     * @return the loaded texture
     */
    public static Texture loadTexture(String filename) {
        return new Texture(Gdx.files.internal(filename));
    }

    /**
     * Splits a texture into smaller frames.
     * 
     * @param  texture the texture to split
     * @param  tileWidth the width of a frame
     * @param  tileHeight the height of a frame
     * @return a one-dimensional array of frames
     */
    public static TextureRegion[] split(Texture texture, int tileWidth, int tileHeight) {
        return TextureRegion.split(texture, tileWidth, tileHeight)[0];
    }

    /**
     * Loads a sound from a file.
     * 
     * @param  filename the sound file
     * @return the loaded sound
     */
    public static Sound loadSound(String filename) {
        return Gdx.audio.newSound(Gdx.files.internal(filename));
    }
    
    /**
     * Plays a sound if sound is enabled.
     * 
     * @param sound the sound to play
     */
    public static void playSound(Sound sound) {
        if (Settings.sound()) {
            sound.play();
        }
    }
    
    /**
     * Releases native resources.
     */
    public static void dispose() {
        titleTexture.dispose();
        menuTexture.dispose();
        pauseTexture.dispose();
        resumeTexture.dispose();
        soundTexture.dispose();
        muteTexture.dispose();
        lifeTexture.dispose();
        caffeineTexture.dispose();
        slowTexture.dispose();
        waterTexture.dispose();
        rockTexture.dispose();
        playerLeapTexture.dispose();
        playerDodgeTexture.dispose();
        sharkTexture.dispose();
        sharkAttackTexture.dispose();
        islandTexture.dispose();
        selectSound.dispose();
        jumpSound.dispose();
        lifeSound.dispose();
        slowSound.dispose();
        caffeineSound.dispose();
        gameOverSound.dispose();
        chompSound.dispose();
        splashSound.dispose();
        winSound.dispose();
    }
}
