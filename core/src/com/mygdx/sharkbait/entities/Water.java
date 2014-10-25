package com.mygdx.sharkbait.entities;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.sharkbait.utils.Assets;
import com.mygdx.sharkbait.utils.Position;

public class Water extends TileSprite implements Serializable {
    private static final long serialVersionUID = 1;

    public Water(Position position) {
        super(position);
    }

    @Override
    public void draw(Batch batch, float tileWidth) {
        keyFrame = Assets.water.getKeyFrame(stateTime);
        super.draw(batch, tileWidth);;
    }
}
