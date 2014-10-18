package com.mygdx.sharkbait;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Slow extends Powerup implements Serializable {
    private static final long serialVersionUID = 1;

    @Override
    public Powerup create() {
        return new Slow();
    }

    @Override
    public void draw(Batch batch, float tileWidth) {
        keyFrame = Assets.slow.getKeyFrame(stateTime);
        super.draw(batch, tileWidth);
    }
}
