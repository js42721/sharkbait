package com.mygdx.sharkbait;

/**
 * Generates moves.
 */
public interface Mover {
    /**
     * Returns the next move.
     * 
     * @return the next move
     */
    public abstract Position getNext();

    /**
     * Computes move(s) based on the current position.
     */
    public abstract void compute();
}
