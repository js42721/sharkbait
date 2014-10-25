package com.mygdx.sharkbait.utils;

import java.io.Serializable;

/** Game board coordinates. */
public final class Position implements Serializable {
    private static final long serialVersionUID = 1;

    public final int x;
    public final int y;

    /** Creates a position with the specified coordinates. */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Creates a position with the same coordinates as another position. */
    public Position(Position another) {
        this.x = another.x;
        this.y = another.y;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position p = (Position)obj;
            return x == p.x && y == p.y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
