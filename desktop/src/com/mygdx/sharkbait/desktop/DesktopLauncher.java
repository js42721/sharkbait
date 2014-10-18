package com.mygdx.sharkbait.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.sharkbait.SharkBait;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Shark Bait";
        config.width = SharkBait.WIDTH;
        config.height = SharkBait.HEIGHT;
        new LwjglApplication(new SharkBait(), config);
    }
}
