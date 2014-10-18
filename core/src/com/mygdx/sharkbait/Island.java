package com.mygdx.sharkbait;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Island extends TileSprite implements Serializable {
    private static final long serialVersionUID = 1;

    public Island(Position position) {
        super(position);
    }

    @Override
    public void draw(Batch batch, float tileWidth) {
        keyFrame = Assets.island;
        super.draw(batch, tileWidth);
    }
}
