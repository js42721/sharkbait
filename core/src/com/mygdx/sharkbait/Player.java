package com.mygdx.sharkbait;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Player sprite.
 */
public class Player extends TileSprite implements Serializable {
    private static final long serialVersionUID = 1;

    public static final int Z_INDEX = 5;
    
    public static final int IDLE = 0;
    public static final int DODGING = 1;
    public static final int MOVING = 2;

    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    /* The length in seconds of the invincibility period after death. */
    private static final float INVINCIBILITY_DURATION = 7;

    private Position prev;
    private int state;
    private int direction;
    private int flickerCounter;
    private float speed;
    private float invincibilityTimer;
    private boolean mirror;
    private boolean invincible;
    private boolean transparent;

    /**
     * Creates the player.
     * 
     * @param position the player's starting position
     */
    public Player(Position position) {
        super(position);
        prev = position;
        speed = 1;
        zIndex = Z_INDEX;
    }

    /**
     * Moves the player to the specified position. The caller is responsible for
     * ensuring that the new position is adjacent to the old one.
     * 
     * @param next the new position
     */
    public void move(Position next) {
        adjustDirection(next);
        prev = position;
        position = next;
        stateTime = 0;
        state = MOVING;
    }

    /**
     * Makes the player dodge an attack. Does not change the player's position.
     */
    public void dodge() {
        stateTime = 0;
        state = DODGING;
    }

    /**
     * Called when the player gets attacked. Temporarily makes the player
     * invincible.
     */
    public void hit() {
        if (!invincible) {
            invincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;
        }
    }

    /**
     * Sets the multiplier for the player's movement speed across rocks.
     * Does not affect dodging.
     * 
     * @param speed the new movement speed multiplier
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Returns the player's movement speed multiplier. 
     * 
     * @return movement speed multiplier
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Checks if invincibility is in effect.
     * 
     * @return {@code true} if the player is invincible
     */
    public boolean isInvincible() {
        return invincible;
    }

    /**
     * Returns the player's state.
     * 
     * @return one of the following states: IDLE, DODGING, MOVING
     */
    public int getState() {
        return state;
    }
    
    /**
     * Resets stats and removes abnormal statuses.
     */
    public void restore() {
        speed = 1;
        invincible = false;
        transparent = false;
    }

    @Override
    public void draw(Batch batch, float tileWidth) {		
        switch (state) {
        case IDLE:
            keyFrame = Assets.player;
            break;
        case DODGING:
            keyFrame = Assets.playerDodge.getKeyFrame(stateTime);
            break;
        case MOVING:
            keyFrame = Assets.playerMove.getKeyFrame(stateTime * speed);
            break;
        }

        if (mirror) {
            keyFrame = new TextureRegion(keyFrame);
            keyFrame.flip(true, false);
        }

        if (transparent) {
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, 0.2f);
            super.draw(batch, tileWidth);
            batch.setColor(c);
        } else {
            super.draw(batch, tileWidth);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (invincible) {
            invincibilityTimer -= delta;
            transparent = (++flickerCounter & 2) == 0;
            if (invincibilityTimer <= 0) {
                invincible = false;
                transparent = false;
            }
        }

        switch (state) {
        case DODGING:
            if (Assets.playerDodge.isAnimationFinished(stateTime)) {
                state = IDLE;
            }
            break;
        case MOVING:
            if (Assets.playerMove.isAnimationFinished(stateTime * speed)) {
                state = IDLE;
                draw.set(position.x, position.y);
                break;
            }
            float distance = stateTime * speed / Assets.playerMove.animationDuration;
            switch (direction) {
            case UP:
                draw.y = prev.y - distance;
                break;
            case DOWN:
                draw.y = prev.y + distance;
                break;
            case LEFT:
                draw.x = prev.x - distance;
                break;
            case RIGHT:
                draw.x = prev.x + distance;
                break;
            }
            break;
        }
    }

    /* Sets the player's direction and orientation based on his next move. */
    private void adjustDirection(Position next) {
        if (next.y < position.y) {
            direction = UP;
        } else if (next.y > position.y) {
            direction = DOWN;
        } else if (next.x < position.x) {
            direction = LEFT;
            if (!mirror) {
                mirror = true;
            }
        } else if (next.x > position.x) {
            direction = RIGHT;
            if (mirror) {
                mirror = false;
            }
        }
    }
}
