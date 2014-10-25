package com.mygdx.sharkbait;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.sharkbait.utils.Assets;

public class SharkBait extends Game {
    public static final int WIDTH = 320;
    public static final int HEIGHT = 480;
    
    public static final String SAVE = ".sharkbait_save";
    public static final String SETTINGS = ".sharkbait_settings";
    
    public SpriteBatch batch;
    public BitmapFont font;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        Assets.load();
        Settings.load();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
        Assets.dispose();
        batch.dispose();
        font.dispose();
    }
}
