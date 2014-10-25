package com.mygdx.sharkbait.entities;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.sharkbait.utils.Assets;
import com.mygdx.sharkbait.utils.Position;

public class Rock extends TileSprite implements Serializable {
    private static final long serialVersionUID = 1;

    public static final int Z_INDEX = 2;
    
    public static final int SUBMERGED = 0;
    public static final int SUBMERGING = 1;
    public static final int SURFACING = 2;
    public static final int SURFACED = 3;
    
    private int state;

    public Rock(Position position) {
        super(position);
        zIndex = Z_INDEX;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void draw(Batch batch, float tileWidth) {
        switch (state) {
        case SUBMERGED:
            return; // Draw nothing.
        case SURFACED:
            keyFrame = Assets.rock;
            break;
        case SURFACING:
            keyFrame = Assets.rockSurface.getKeyFrame(stateTime);
            break;
        case SUBMERGING:
            keyFrame = Assets.rockSink.getKeyFrame(stateTime);
            break;
        }

        super.draw(batch, tileWidth);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        switch (state) {
        case SURFACED:
        case SUBMERGED:
            stateTime = 0;
            break;
        case SURFACING:
            if (Assets.rockSurface.isAnimationFinished(stateTime)) {
                state = SURFACED;
            }
            break;
        case SUBMERGING:
            if (Assets.rockSink.isAnimationFinished(stateTime)) {
                state = SUBMERGED;
            }
            break;
        }
    }
}
