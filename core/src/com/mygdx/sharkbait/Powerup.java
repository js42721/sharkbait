package com.mygdx.sharkbait;

import java.io.Serializable;

/**
 * Base class for powerups.
 */
public abstract class Powerup extends TileSprite implements Serializable {
    private static final long serialVersionUID = 1;
    
    public static final int Z_INDEX = 4;
    
    /**
     * Creates a sprite with the powerup z-index (4). The sprite's starting
     * position will be (0, 0). 
     */
    public Powerup() {
        super();
        zIndex = Z_INDEX;
    }

    /**
     * Creates a sprite with the powerup z-index (4) and the specified
     * position.
     * 
     * @param position a board position
     */
    public Powerup(Position position) {
        super(position);
        zIndex = Z_INDEX;
    }

    /**
     * Returns an instance of a subclass. Used for factory production.
     * 
     * @return a subclass instance
     */
    public abstract Powerup create();
}
