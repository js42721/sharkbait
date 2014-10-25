package com.mygdx.sharkbait.utils;

import java.util.ArrayList;
import java.util.List;

/** Board-related utilities. */
public final class Board {
    private Board() {}

    /** Returns the Manhattan distance between a and b. */
    public static int distanceL1(Position a, Position b) {
        return Math.abs(b.x - a.x) + Math.abs(b.y - a.y);
    }

    /** Returns the Euclidean distance between a and b. */
    public static double distanceL2(Position a, Position b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    /** Returns a list of the positions that are adjacent to a given position. */
    public static List<Position> getAdjacent(Position position, int dimension) {
        List<Position> adjacent = new ArrayList<Position>(4);
        if (position.x > 0) {
            adjacent.add(new Position(position.x - 1, position.y)); // Left.
        }
        if (position.x < dimension - 1) {
            adjacent.add(new Position(position.x + 1, position.y)); // Right.
        }
        if (position.y > 0) { 
            adjacent.add(new Position(position.x, position.y - 1)); // Up.
        }
        if (position.y < dimension - 1) { 
            adjacent.add(new Position(position.x, position.y + 1)); // Down.
        }
        return adjacent;
    }

    /** Returns true if a is horizontally or vertically adjacent to b. */
    public static boolean isAdjacent(Position a, Position b) {
        return ((Math.abs(a.x - b.x) == 1 && a.y == b.y)
                || (Math.abs(a.y - b.y) == 1 && a.x == b.x));
    }

    /** Returns true if a and b are lined up on opposite sides of c. */
    public static boolean isObstacle(Position a, Position b, Position c) {
        return (a.x == c.x && b.x == c.x && a.y < c.y ^ b.y < c.y) 
                || (a.y == c.y && b.y == c.y && a.x < c.x ^ b.x < c.x);
    }
}
