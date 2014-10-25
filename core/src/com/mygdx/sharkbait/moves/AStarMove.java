package com.mygdx.sharkbait.moves;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mygdx.sharkbait.utils.Board;
import com.mygdx.sharkbait.utils.Position;
import com.mygdx.sharkbait.world.World;

import datastructures.DHeapPriorityMap;

/** Generates moves for the shark using the A* algorithm. */
public class AStarMove implements Mover {
    private final World world;
    private final List<Position> path;

    /** Constructs a move generator for the specified world. */
    public AStarMove(World world) {
        this.world = world;
        path = new ArrayList<Position>();
    }

    @Override
    public Position getNext() {
        return path.isEmpty() ? null : path.remove(path.size() - 1);
    }

    @Override
    public void compute() {
        path.clear();
        computePath(world.getShark().getPosition(), world.getPlayer().getPosition());
    }

    /**
     * Computes the shortest path from the source position to the destination
     * position or, if no path is available, the path which gets the closest to
     * the destination.
     */
    private void computePath(Position src, Position dst) {
        /* Configured as a min-heap by default, which is what we want. */
        DHeapPriorityMap<Position, AStarNode> open = new DHeapPriorityMap<Position, AStarNode>();
        Set<Position> closed = new HashSet<Position>();

        AStarNode start = new AStarNode(src, null, 0, 0, Board.distanceL1(src, dst));
        AStarNode best = start;

        open.put(start.position, start);

        while (!open.isEmpty()) {
            AStarNode current = open.peekValue();
            open.remove();
            if (current.position.equals(dst)) { // Destination reached.
                tracePath(current);
                return;
            }
            if (Board.isObstacle(current.position, dst, world.getGoal())) {
                /* 
                 * The goal is always untraversable for the shark so we correct
                 * the distance between positions that are on opposite sides of
                 * the goal.
                 */
                current.h += 2;
            }
            if (current.h < best.h) {
                best = current;
            }
            closed.add(current.position);
            List<Position> adjacent = Board.getAdjacent(current.position, world.getDimension());
            for (Position p : adjacent) {
                if (closed.contains(p) || !world.isSharkTraversable(p)) {
                    continue;
                }
                /* 
                 * The second addend of gTentative is meant to be the distance
                 * between the current node and its neighbor. In our case, that
                 * value is always 1.
                 */
                int gTentative = current.g + 1;
                AStarNode neighbor = open.get(p);
                if (neighbor == null || gTentative < neighbor.g) {
                    int h = Board.distanceL1(p, dst);
                    int f = gTentative + h;
                    open.put(p, new AStarNode(p, current, f, gTentative, h));
                }
            }
        }

        tracePath(best);
    }

    /**
     * Backtracks from a node to trace a computed path. The results are stored
     * in "path." The path follows the same order as the trace so the first
     * position is the destination.
     */
    private void tracePath(AStarNode node) {
        while (node.prev != null) {
            path.add(node.position);
            node = node.prev;
        }
    }

    private static class AStarNode implements Comparable<AStarNode> {
        AStarNode prev;
        Position position;
        int f, g, h;

        /**
         * Constructs a node with the specified values.
         *
         * @param position the location of the node
         * @param f f(x), the cost of the node (g(x) + h(x))
         * @param g g(x), the distance traveled from the starting node
         * @param h h(x), the heuristic estimate of the distance from the
         *        destination
         */
        AStarNode(Position position, AStarNode prev, int f, int g, int h) {
            this.position = position;
            this.prev = prev;
            this.f = f;
            this.g = g;
            this.h = h;
        }

        @Override
        public int compareTo(AStarNode other) {
            if (f < other.f) {
                return -1;
            }
            if (f > other.f) {
                return 1;
            }
            return 0;
        }
    }
}
