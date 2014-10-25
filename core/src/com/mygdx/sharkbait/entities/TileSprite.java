package com.mygdx.sharkbait.entities;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.sharkbait.SharkBait;
import com.mygdx.sharkbait.utils.Position;

/** A sprite which gets drawn on a tile. */
public abstract class TileSprite implements Serializable {
    private static final long serialVersionUID = -3736952604040938017L;
    
    protected transient TextureRegion keyFrame;
    protected Position position;
    protected Vector2 draw;
    protected int zIndex;
    protected float stateTime;

    /** Constructs a sprite with the position (0, 0). */
    public TileSprite() {
        this(new Position(0, 0));
    }

    /** Constructs a sprite with the specified position. */
    public TileSprite(Position position) {
        this.position = position;
        draw = new Vector2(position.x, position.y);
    }

    /**
     * Draws the sprite.
     * 
     * @param batch the sprite batcher
     * @param tileWidth the width of a tile (screen width divided by dimension)
     */
    public void draw(Batch batch, float tileWidth) {
        batch.draw(keyFrame, tileWidth * draw.x,
                SharkBait.HEIGHT - (SharkBait.HEIGHT - SharkBait.WIDTH) / 2
                - tileWidth * (draw.y + 1), tileWidth, tileWidth);
    }

    /**
     * Updates the sprite.
     * 
     * @param delta elapsed time in seconds
     */
    public void update(float delta) {
        stateTime += delta;
    }

    /** Sets the z-index of the sprite. */
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    /** Returns the z-index of the sprite. */
    public int getZIndex() {
        return zIndex;
    }

    /** Returns the accumulated state time of the sprite in seconds. */
    public float getStateTime() {
        return stateTime;
    }

    /** Sets the board position of the sprite. */
    public void setPosition(Position position) {
        this.position = position;
        draw.set(position.x, position.y);
    }

    /** Returns the current board position of the sprite. */
    public Position getPosition() {
        return position;
    }
}
