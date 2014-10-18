package com.mygdx.sharkbait;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The game world.
 */
public class World implements Serializable {
    private static final long serialVersionUID = 1;

    private final List<Water> water;
    private final List<Rock> rocks;
    private final Map<Position, Powerup> powerups;
    private final Shark shark;
    private final Island island;
    private final Player player;

    private transient WorldListener listener;
    private final Set<Position> surfaced;
    private final Position goal;
    private final Random rand;
    private final int dimension;
    
    /**
     * Creates a game world based on an n x n board.
     * 
     * @param  dimension the value of n
     * @param  startSurfacedProbability probability out of 100 of a rock
     *         starting out surfaced
     * @throws NullPointerException if listener is null
     * @throws IllegalArgumentException if dimension is less than 3
     */
    public World(WorldListener listener, int dimension, int startSurfacedProbability) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (dimension < 3) {
            throw new IllegalArgumentException("Dimension must be 3 or greater");
        }
        this.listener = listener;
        this.dimension = dimension;

        rand = new Random();
        goal = new Position(dimension / 2, dimension / 2);
        powerups = new HashMap<Position, Powerup>();
        shark = new Shark(new Position(dimension - 1, dimension - 1), 1);
        island = new Island(goal);
        player = new Player(new Position(0, dimension - 1));

        int tiles = dimension * dimension;
        surfaced = new HashSet<Position>(tiles);
        water = new ArrayList<Water>(tiles);
        rocks = new ArrayList<Rock>(tiles);

        initialize(startSurfacedProbability);
    }

    /**
     * Attaches a listener.
     * 
     * @param  listener the listener to attach
     * @throws NullPointerException if listener is null
     */
    public void registerListener(WorldListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        this.listener = listener;
    }

    /**
     * Updates the game world.
     * 
     * @param delta elapsed time in seconds
     */
    public void update(float delta) {
        for (Water w : water) {
            w.update(delta);
        }

        for (Rock r : rocks) {
            r.update(delta);
        }

        for (Powerup p : powerups.values()) {
            p.update(delta);
        }

        player.update(delta);
        shark.update(delta);

        checkGoalReached();
        checkPlayerHit();
        checkPowerups();
    }

    /**
     * Changes rock states.
     * 
     * @param surfaceProbability probability out of 100 of a submerged rock
     *        surfacing
     * @param submergeProbability probability out of 100 of a surfaced rock
     *        submerging
     */
    public void changeRocks(int surfaceProbability, int submergeProbability) {
        for (int row = 0; row < dimension; ++row) {
            for (int column = 0; column < dimension; ++column) {
                Position p = new Position(column, row);
                if (surfaced.contains(p)) {
                    if (rand.nextInt(100) < submergeProbability) {
                        surfaced.remove(p);
                    }
                } else if (rand.nextInt(100) < surfaceProbability) {
                    surfaced.add(p);
                }
            }
        }

        correctSurfaced();
        
        for (Rock r : rocks) {
            if (surfaced.contains(r.getPosition())) {
                if (r.getState() != Rock.SURFACED) {
                    r.setState(Rock.SURFACING);
                }
            } else if (r.getState() != Rock.SUBMERGED) {
                r.setState(Rock.SUBMERGING);
            }
        }
    }

    /**
     * Places a powerup on a random open rock if possible.
     * 
     * @return {@code true} if a powerup was successfully placed
     * @throws NullPointerException if powerup is null
     */
    public boolean addPowerup(Powerup powerup) {
        if (powerup == null) {
            throw new NullPointerException();
        }

        /* 
         * Removes occupied rocks from the surfaced set so that they won't be
         * considered for powerup placement.
         */
        surfaced.remove(player.getPosition());
        for (Position p : powerups.keySet()) {
            surfaced.remove(p);
        }

        boolean created = false;
        if (!surfaced.isEmpty()) {
            int r = rand.nextInt(surfaced.size());
            int i = 0;
            for (Position p : surfaced) {
                if (i++ == r) {
                    powerup.setPosition(p);
                    break;
                }
            }
            powerups.put(powerup.getPosition(), powerup);
            created = true;
        }

        /* Adds occupied rocks back to the surfaced set. */
        surfaced.add(player.getPosition());
        for (Position p : powerups.keySet()) {
            surfaced.add(p);
        }

        return created;
    }
    
    /**
     * Checks if a position has a surfaced rock that the player can stand on.
     * 
     * @param  p a board position
     * @return {@code true} if the position has a surfaced rock 
     */
    public boolean isRock(Position p) {
        return surfaced.contains(p);
    }
    
    /**
     * Checks if a shark can traverse the specified position.
     * 
     * @param p a board position
     * @return {@code true} if the position is traversable
     */
    public boolean isSharkTraversable(Position p) {
        return !p.equals(goal)
                && (!surfaced.contains(p) || p.equals(player.getPosition()));
    }

    /**
     * Checks if the player can move to the specified position.
     * 
     * @param p a board position
     * @return {@code true} if the player can move to the position
     */
    public boolean validatePlayerMove(Position p) {
        return (surfaced.contains(p) || p.equals(goal))
                && Board.isAdjacent(player.getPosition(), p);
    }

    /**
     * Checks if the shark can attack the specified position.
     * 
     * @param p a board position
     * @return {@code true} if the shark can attack the position
     */
    public boolean validateSharkAttack(Position p) {
        return !p.equals(goal) && Board.isAdjacent(p, shark.getPosition());
    }

    public Collection<Water> getWater() {
        return Collections.unmodifiableCollection(water);
    }

    public Collection<Rock> getRocks() {
        return Collections.unmodifiableCollection(rocks);
    }

    public Collection<Powerup> getPowerups() {
        return Collections.unmodifiableCollection(powerups.values());
    }

    public int getDimension() {
        return dimension;
    }

    public Position getGoal() {
        return goal;
    }

    public Island getIsland() {
        return island;
    }

    public Player getPlayer() {
        return player;
    }

    public Shark getShark() {
        return shark;
    }

    private void checkGoalReached() {
        if (player.getPosition().equals(goal)
                && player.getState() == Player.IDLE) {
            player.restore(); // Done to remove transparency effect.
            listener.levelEnd();
        }
    }

    private void checkPlayerHit() {
        if (shark.getState() == Shark.ATTACKED
                && Board.isAdjacent(shark.getPosition(), player.getPosition())
                && player.getState() == Player.IDLE
                && !player.isInvincible()) {
            player.hit();
            listener.playerHit();
        }
    }

    private void checkPowerups() {
        Powerup p = powerups.get(player.getPosition());
        if (p != null && player.getState() == Player.IDLE) {
            listener.powerupFound(powerups.remove(p.getPosition()));
        }
    }

    private void correctSurfaced() {
        surfaced.remove(goal);
        surfaced.remove(shark.getPosition());
        if (shark.getState() == Shark.MOVING) {
            surfaced.remove(shark.getPreviousPosition());
        }
        surfaced.add(player.getPosition());
        for (Position p : powerups.keySet()) {
            surfaced.add(p);
        }
    }

    private void initialize(int startSurfacedProbability) {
        for (int row = 0; row < dimension; ++row) {
            for (int column = 0; column < dimension; ++column) {
                if (rand.nextInt(100) < startSurfacedProbability) {
                    Position p = new Position(column, row);
                    surfaced.add(p);
                }
            }
        }

        correctSurfaced();

        for (int row = 0; row < dimension; ++row) {
            for (int column = 0; column < dimension; ++column) {
                Position p = new Position(column, row);
                water.add(new Water(p));
                if (!p.equals(goal)) {
                    Rock r = new Rock(p);
                    if (surfaced.contains(p)) {
                        r.setState(Rock.SURFACED);
                    }
                    rocks.add(r);
                }
            }
        }
    }
}
