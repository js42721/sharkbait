package com.mygdx.sharkbait.moves;

import com.mygdx.sharkbait.utils.Position;

/** Generates moves. */
public interface Mover {
    /** Returns the next move. */
    public abstract Position getNext();

    /** Computes a move or a series of moves. */
    public abstract void compute();
}
