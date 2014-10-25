package com.mygdx.sharkbait.world;

import com.mygdx.sharkbait.powerups.Powerup;

public interface WorldListener {
    void powerupFound(Powerup powerup);
    void playerHit();
    void levelEnd();
}
