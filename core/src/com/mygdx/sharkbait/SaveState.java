package com.mygdx.sharkbait;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Stores game state.
 */
public class SaveState {
    private final Preferences preferences;
    private GameState gameState;

    /**
     * Creates a save state from a save file. 
     * 
     * @param filename the save file
     */
    public SaveState(String filename) {
        preferences = Gdx.app.getPreferences(filename);
        gameState = new GameState();
    }
    
    /**
     * Loads game data from the save file.
     * 
     * @return {@code true} if the data was loaded
     */
    public boolean load() {
        String gameStateString = preferences.getString("gameState", null);
        if (gameStateString == null) {
            return false;
        }
        try {
            gameState = (GameState)Serialization.fromString(gameStateString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Saves data to the save file.
     */
    public void save() {
        try {
            preferences.putString("gameState", Serialization.toString(gameState));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        preferences.flush();
    }

    /**
     * Clears all loaded data.
     */
    public void clear() {
        gameState = new GameState();
    }
    
    /**
     * Erases the save file.
     */
    public void erase() {
        preferences.clear();
        preferences.flush();
    }
    
    public World getWorld() {
        return gameState.world;
    }

    public void setWorld(World world) {
        gameState.world = world;
    }

    public int getState() {
        return gameState.state;
    }
    
    public void setState(int state) {
        gameState.state = state;
    }
    
    public int getLevel() {
        return gameState.level;
    }

    public void setLevel(int level) {
        gameState.level = level;
    }

    public int getLives() {
        return gameState.lives;
    }

    public void setLives(int lives) {
        gameState.lives = lives;
    }

    public float getRockChangeTick() {
        return gameState.rockChangeTick;
    }

    public void setRockChangeTick(float rockChangeTick) {
        gameState.rockChangeTick = rockChangeTick;
    }

    public float getGenerateMoveTick() {
        return gameState.generateMoveTick;
    }

    public void setGenerateMoveTick(float generateMoveTick) {
        gameState.generateMoveTick = generateMoveTick;
    }
    
    private static class GameState implements Serializable {
        static final long serialVersionUID = 1;
        
        World world;
        int state;
        int level;
        int lives;
        float rockChangeTick;
        float generateMoveTick;
    }
}
