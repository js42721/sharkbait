package com.mygdx.sharkbait;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Stores settings.
 */
public final class Settings {
    private static Configuration config = new Configuration();
    private static final Preferences preferences = Gdx.app.getPreferences(SharkBait.SETTINGS);

    private Settings() {}
    
    /**
     * Loads settings from the settings file.
     */
    public static void load() {
        /* Prepares default values in case of failure. */
        config = new Configuration();
        String configString = preferences.getString("config", null);
        if (configString == null) {
            return;
        }
        try {
            config = (Configuration)Serialization.fromString(configString);
        } catch (Exception e) {}
    }

    /**
     * Saves settings to the settings file.
     */
    public static void save() {
        try {
            preferences.putString("config", Serialization.toString(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        preferences.flush();
    }

    public static boolean sound() {
        return config.sound;
    }
    
    public static void setSound(boolean sound) {
        config.sound = sound;
    }
    
    public static int getHighestLevel() {
        return config.highestLevel;
    }
    
    public static void setHighestLevel(int highestLevel) {
        config.highestLevel = highestLevel;
    }
    
    private static class Configuration implements Serializable {
        static final long serialVersionUID = 1;
        
        boolean sound = true;
        int highestLevel;
    }
}
