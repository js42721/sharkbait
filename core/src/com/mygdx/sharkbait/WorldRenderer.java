package com.mygdx.sharkbait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.sharkbait.entities.Rock;
import com.mygdx.sharkbait.entities.TileSprite;
import com.mygdx.sharkbait.entities.Water;
import com.mygdx.sharkbait.powerups.Powerup;
import com.mygdx.sharkbait.world.World;

/** Draws the game world. */
public class WorldRenderer {
    private final SpriteBatch batch;
    private final World world;
    private final List<TileSprite> zOrderedSprites;
    private final Comparator<TileSprite> zIndexCmp;

    /**
     * Constructs a renderer.
     * 
     * @param batch the sprite batcher
     * @param world the world to render
     */
    public WorldRenderer(SpriteBatch batch, World world) {
        this.batch = batch;
        this.world = world;
        
        zOrderedSprites = new ArrayList<TileSprite>();
        zIndexCmp = new ZIndexComparator();

        refreshSprites();
        zIndexSort();
    }

    /** Draws sprites. */
    public void render() {
        /* More accurate but causes distortion. */
        //float tileWidth = (float)SharkBait.WIDTH / world.getDimension();
        float tileWidth = SharkBait.WIDTH / world.getDimension();
        
        /* Water gets drawn first. */
        for (Water w : world.getWater()) {
            w.draw(batch, tileWidth);
        }
        
        /* The island gets drawn after the water but before everything else. */
        world.getIsland().draw(batch, tileWidth);

        /* All other sprites get drawn in the order of their z-indices. */
        for (TileSprite s : zOrderedSprites) {
            s.draw(batch, tileWidth);
        }
    }

    /** Gathers world sprites. Used when sprites are added or removed. */
    public void refreshSprites() {
        zOrderedSprites.clear();

        zOrderedSprites.add(world.getPlayer());
        zOrderedSprites.add(world.getShark());

        for (Rock r : world.getRocks()) {
            zOrderedSprites.add(r);
        }

        for (Powerup p : world.getPowerups()) {
            zOrderedSprites.add(p);
        }
    }

    /** Sorts all the sprites whose draw order depends on z-index. */
    public void zIndexSort() {
        Collections.sort(zOrderedSprites, zIndexCmp);
    }

    private static class ZIndexComparator implements Comparator<TileSprite> {
        @Override
        public int compare(TileSprite lhs, TileSprite rhs) {
            if (lhs.getZIndex() > rhs.getZIndex()) {
                return 1;
            }
            if (lhs.getZIndex() < rhs.getZIndex()) {
                return -1;
            }
            return 0;
        }
    }
}
