package com.mygdx.sharkbait.moves;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mygdx.sharkbait.utils.Board;
import com.mygdx.sharkbait.utils.Position;
import com.mygdx.sharkbait.world.World;

/** Generates moves for the shark based on distance from the player. */
public class SimpleMove implements Mover {
    private final World world;
    private final Random rand;
    private Position next;

    /** Constructs a move generator for the specified world. */
    public SimpleMove(World world) {
        this.world = world;
        rand = new Random();
    }

    @Override
    public Position getNext() {
        return next;
    }

    @Override
    public void compute() {
        Position current = world.getShark().getPosition();
        Position human = world.getPlayer().getPosition();
        Position goal = world.getGoal();

        int currentDistanceFromHuman = Board.distanceL1(current, human);
        if (Board.isObstacle(current, human, goal)) {
            currentDistanceFromHuman += 2;
        }

        List<Position> adjacent = Board.getAdjacent(current, world.getDimension());
        Iterator<Position> itr = adjacent.iterator();
        while (itr.hasNext()) {
            Position candidate = itr.next();
            int distanceFromHuman = Board.distanceL1(candidate, human);
            if (Board.isObstacle(candidate, human, goal)) {
                distanceFromHuman += 2;
            }
            if (!world.isSharkTraversable(candidate)
                    || distanceFromHuman >= currentDistanceFromHuman) {
                itr.remove();
            }
        }

        next = adjacent.isEmpty() ? null : adjacent.get(rand.nextInt(adjacent.size()));
    }
}
