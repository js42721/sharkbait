package com.mygdx.sharkbait;

public interface WorldListener {
    void powerupFound(Powerup powerup);
    void playerHit();
    void levelEnd();
}
