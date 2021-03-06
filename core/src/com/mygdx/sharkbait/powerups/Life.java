package com.mygdx.sharkbait.powerups;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.sharkbait.utils.Assets;

public class Life extends Powerup implements Serializable {
    private static final long serialVersionUID = 1;

    @Override
    public Powerup create() {
        return new Life();
    }

    @Override
    public void draw(Batch batch, float tileWidth) {
        keyFrame = Assets.life.getKeyFrame(stateTime);
        super.draw(batch, tileWidth);
    }
}
