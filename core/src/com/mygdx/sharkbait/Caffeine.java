package com.mygdx.sharkbait;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Caffeine extends Powerup implements Serializable {
    private static final long serialVersionUID = 1;

    @Override
    public Powerup create() {
        return new Caffeine();
    }

    @Override
    public void draw(Batch batch, float tileWidth) {
        keyFrame = Assets.caffeine.getKeyFrame(stateTime);
        super.draw(batch, tileWidth);
    }
}
