package com.mygdx.sharkbait;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * A sprite which occupies a board position.
 */
public abstract class TileSprite implements Serializable {
    private static final long serialVersionUID = 1;

    protected Position position;
    protected transient TextureRegion keyFrame;
    protected Vector2 draw;
    protected int zIndex;
    protected float stateTime;

    /**
     * No-arg constructor. The position will be initialized to (0, 0).
     */
    public TileSprite() {
        this(new Position(0, 0));
    }

    /**
     * Constructs a sprite.
     * 
     * @param position the starting position for the sprite
     */
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

    /**
     * Sets the z-index of the sprite.
     * 
     * @param zIndex the z-index of the sprite
     */
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    /**
     * Returns the z-index of the sprite.
     * 
     * @return the z-index of the sprite
     */
    public int getZIndex() {
        return zIndex;
    }

    /**
     * Returns the accumulated state time of the sprite.
     * 
     * @return accumulated time in seconds
     */
    public float getStateTime() {
        return stateTime;
    }

    /**
     * Sets the board position of the sprite.
     * 
     * @param position a board position
     */
    public void setPosition(Position position) {
        this.position = position;
        draw.set(position.x, position.y);
    }

    /**
     * Returns the current board position of the sprite.
     * 
     * @return the current position of the sprite
     */
    public Position getPosition() {
        return position;
    }
}
