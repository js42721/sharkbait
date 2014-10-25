package com.mygdx.sharkbait.entities;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.sharkbait.utils.Assets;
import com.mygdx.sharkbait.utils.Position;

/** Shark sprite. */
public class Shark extends TileSprite implements Serializable {
    private static final long serialVersionUID = 1;

    public static final int Z_INDEX = 6;
    public static final int ABOVE_Z_INDEX = 1;
    public static final int MISSED_Z_INDEX = 3;
    
    public static final int IDLE = 0;
    public static final int MOVING = 1;
    public static final int SURFACING = 2;
    public static final int ATTACKED= 3;
    public static final int SUBMERGING = 4;
    
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    /* How far the shark moves towards the player when attacking. */
    private static final float ATTACK_DISTANCE = 0.8f;

    private Position prev;
    private Position target;
    private int state;
    private int direction;
    private float moveDelay;
    private boolean mirror;

    /**
     * Creates a shark.
     * 
     * @param position  the shark's starting position
     * @param moveDelay the time in seconds between moves
     */
    public Shark(Position position, float moveDelay) {
        super(position);
        this.moveDelay = moveDelay;
        prev = position;
        zIndex = Z_INDEX;
    }

    /**
     * Sets the movement delay value (measured in seconds) of the shark.
     * This value is used to synchronize the shark's movement with the move
     * generator and should always be equal to the time between moves.
     * 
     * @param moveDelay the time in seconds between moves
     */
    public void setMoveDelay(float moveDelay) {
        if (state == MOVING) {
            stateTime = (stateTime / this.moveDelay) * moveDelay;
        }
        this.moveDelay = moveDelay;
    }

    /** Returns the shark's movement delay. */
    public float getMoveDelay() {
        return moveDelay;
    }

    /**
     * Returns the shark's state.
     * 
     * @return one of the following states: IDLE, MOVING, SURFACING,
     *                                      ATTACKING, SUBMERGING
     */
    public int getState() {
        return state;
    }

    /**
     * Returns the previous position occupied by the shark. If the shark has not
     * yet moved, this returns his current position.
     */
    public Position getPreviousPosition() {
        return prev;
    }

    /** Moves the shark to the specified position. */
    public void move(Position next) {
        adjustDirection(next);
        prev = position;
        position = next;
        stateTime = 0;
        state = MOVING;
    }

    /** Makes the shark attack the specified position. */
    public void attack(Position position) {
        adjustDirection(position);
        target = position;
        stateTime = 0;
        state = SURFACING; // Starts attack animation.
    }

    @Override
    public void draw(Batch batch, float tileWidth) {
        switch (state) {
        case IDLE:
        case MOVING:
            keyFrame = Assets.shark.getKeyFrame(stateTime);
            break;
        case SURFACING:
            keyFrame = Assets.sharkSurface.getKeyFrame(stateTime);
            break;
        case ATTACKED:
        case SUBMERGING:
            keyFrame = Assets.sharkSubmerge.getKeyFrame(stateTime);
            break;
        }

        if (mirror) {
            keyFrame = new TextureRegion(keyFrame);
            keyFrame.flip(true, false);
        }

        super.draw(batch, tileWidth);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        float distance = 0;
        Position origin = null;
        switch (state) {
        case IDLE:
            return;
        case MOVING:
            if (stateTime > moveDelay) {
                stateTime = 0;
                state = IDLE;
                draw.set(position.x, position.y);
                return;
            }
            distance = stateTime / moveDelay;
            origin = prev;
            break;
        case SURFACING:
            if (Assets.sharkSurface.isAnimationFinished(stateTime)) {
                stateTime = 0;
                state = ATTACKED;
                return;
            }
            distance = stateTime / Assets.sharkSurface.animationDuration 
                    * ATTACK_DISTANCE;
            origin = position;
            break;
        case ATTACKED:
            stateTime = 0;
            state = SUBMERGING;
        case SUBMERGING:
            if (Assets.sharkSubmerge.isAnimationFinished(stateTime)) {
                stateTime = 0;
                state = IDLE;
                draw.set(position.x, position.y);
                return;
            }
            distance = -(stateTime / Assets.sharkSubmerge.animationDuration
                    * ATTACK_DISTANCE + (1 - ATTACK_DISTANCE));
            origin = target;
            break;
        }

        switch (direction) {
        case UP:
            draw.y = origin.y - distance;
            break;
        case DOWN:
            draw.y = origin.y + distance;
            break;
        case LEFT:
            draw.x = origin.x - distance;
            break;
        case RIGHT:
            draw.x = origin.x + distance;
            break;
        }
    }

    /** Sets the shark's direction and orientation based on his next move. */
    private void adjustDirection(Position next) {
        if (next.y < position.y) {
            direction = UP;
        } else if (next.y > position.y) {
            direction = DOWN;
        } else if (next.x < position.x) {
            direction = LEFT;
            if (mirror) {
                mirror = false;
            }
        } else if (next.x > position.x) {
            direction = RIGHT;
            if (!mirror) {
                mirror = true;
            }
        }
    }
}
