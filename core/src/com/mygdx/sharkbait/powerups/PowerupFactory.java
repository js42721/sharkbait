package com.mygdx.sharkbait.powerups;

import java.util.Comparator;

import datastructures.WeightedSamplingTree;

/**
 * Factory for powerups which uses weighted sampling to determine which powerup
 * to create.
 */
public class PowerupFactory {
    private final WeightedSamplingTree<Powerup> powerups;

    /** Creates a powerup factory. */
    public PowerupFactory() {
        powerups = new WeightedSamplingTree<Powerup>(new PowerupComparator());
    }

    /**
     * Registers a powerup, enabling the factory to create it. The weight
     * determines the likelihood of the powerup being created. If the powerup
     * has already been registered, no change will occur.
     * 
     * @param  p the powerup to be registered
     * @param  weight the sampling weight of the powerup
     * @throws NullPointerException if the powerup is null
     * @throws IllegalArgumentException if the specified weight is negative
     */
    public void register(Powerup p, int weight) {
        if (p == null) {
            throw new NullPointerException();
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Negative weight");
        }
        powerups.add(p, weight);
    }

    /**
     * Unregisters a powerup.
     * 
     * @param  p the powerup to be unregistered
     * @throws NullPointerException if the powerup is null
     */
    public void unregister(Powerup p) {
        if (p == null) {
            throw new NullPointerException();
        }
        powerups.remove(p);
    }

    /**
     * Selects and returns a powerup based on probability.
     * 
     * @return a powerup or null if one could not be created
     */
    public Powerup createPowerup() {
        Powerup p = powerups.sample();
        return p == null ? null : p.create();
    }

    private static class PowerupComparator implements Comparator<Powerup> {
        @Override
        public int compare(Powerup p1, Powerup p2) {
            return p1.getClass().getName().compareTo(p2.getClass().getName());
        }
    }
}
