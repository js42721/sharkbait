package com.mygdx.sharkbait;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.mygdx.sharkbait.entities.Player;
import com.mygdx.sharkbait.entities.Shark;
import com.mygdx.sharkbait.moves.AStarMove;
import com.mygdx.sharkbait.moves.Mover;
import com.mygdx.sharkbait.moves.SimpleMove;
import com.mygdx.sharkbait.powerups.Caffeine;
import com.mygdx.sharkbait.powerups.Life;
import com.mygdx.sharkbait.powerups.Powerup;
import com.mygdx.sharkbait.powerups.Slow;
import com.mygdx.sharkbait.utils.Assets;
import com.mygdx.sharkbait.utils.Board;
import com.mygdx.sharkbait.utils.Position;
import com.mygdx.sharkbait.world.World;
import com.mygdx.sharkbait.world.WorldListener;

public class GameScreen extends ScreenAdapter implements Screen {	
    public static final int RUNNING = 0;
    public static final int PAUSED = 1;
    public static final int LEVEL_END = 2;
    public static final int GAME_OVER = 3;

    /* 
     * Base values for game parameters.
     * 
     * P is probability out of 100 and T is time in seconds. 
     */
    private static final int DIMENSION = 11;
    private static final int STARTING_LIVES = 3;
    private static final int START_SURFACED_P = 50;
    private static final int SURFACE_P = 50;
    private static final int SUBMERGE_P = 50;
    private static final float ROCK_CHANGE_T = 2.0f;
    private static final float SHARK_MOVE_T = 1.0f;
    
    private static final int START_SURFACED_P_MIN = 20;
    private static final int SURFACE_P_MIN = 25;
    private static final int SUBMERGE_P_MAX = 75;
    private static final float ROCK_CHANGE_T_MAX = 4.0f;
    private static final float SHARK_MOVE_T_MAX = 0.5f;

    private final SharkBait game;
    private final SaveState save;
    private final Camera camera;
    private final Vector3 touch;
    private final Rectangle pauseBounds;
    private final Rectangle resumeBounds;
    private final Rectangle menuBounds;

    private World world;
    private WorldRenderer worldRenderer;
    private Mover mover;
    private int lives;
    private int state;
    private int level;
    private int startSurfacedProbability;
    private int surfaceProbability;
    private int submergeProbability;
    private float rockChangeInterval;
    private float rockChangeTick;
    private float sharkMoveDelay;
    private float generateMoveTick;

    public GameScreen(SharkBait game, SaveState saveState) {
        this.game = game;

        camera = new OrthographicCamera(SharkBait.WIDTH, SharkBait.HEIGHT);
        camera.position.set(SharkBait.WIDTH / 2, SharkBait.HEIGHT / 2, 0);
        
        int pauseWidth = Assets.pauseTexture.getWidth();
        int pauseHeight = Assets.pauseTexture.getHeight();
        pauseBounds = new Rectangle(SharkBait.WIDTH - 64, 
                (SharkBait.HEIGHT - SharkBait.WIDTH) / 2 - 50,
                pauseWidth, pauseHeight);
        
        int resumeWidth = Assets.resumeTexture.getWidth();
        int resumeHeight = Assets.resumeTexture.getHeight();
        resumeBounds = new Rectangle(SharkBait.WIDTH / 2 - resumeWidth / 2, 
                SharkBait.HEIGHT / 2 + 100, resumeWidth, resumeHeight);
        
        int menuWidth = Assets.menuTexture.getWidth();
        int menuHeight = Assets.menuTexture.getHeight();
        menuBounds = new Rectangle(SharkBait.WIDTH / 2 - menuWidth / 2, 
                SharkBait.HEIGHT / 2 + 25, menuWidth, menuHeight);
        
        touch = new Vector3();

        if (saveState != null) {
            /* Loads saved game. */
            save = saveState;
            load();
            if (state == LEVEL_END) {
                ++level;
                constructLevel();
                save.clear(); // Allows the previous level to be GCed.
            }
        } else {
            /* Starts new game. */
            save = new SaveState(SharkBait.SAVE);
            lives = STARTING_LIVES;
            level = 1;
            constructLevel();
        }

        state = RUNNING;
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void pause() {
        if (state != GAME_OVER) {
            save();
        }
        if (state == RUNNING) {
            state = PAUSED;
        }
    }

    private void update(float delta) {		
        switch (state) {
        case RUNNING:
            updateRunning(delta);
            break;
        case PAUSED:
            updatePaused();
            break;
        }
    }

    private void updateRunning(float delta) {
        if (Gdx.input.justTouched()) {
            camera.unproject(touch.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (pauseBounds.contains(touch.x, touch.y)) {
                state = PAUSED;
                return;
            }
            /* Controls the player. */
            if (world.getPlayer().getState() == Player.IDLE) {
                float tileWidth = (float)SharkBait.WIDTH / DIMENSION;
                float offsetY = (SharkBait.HEIGHT - SharkBait.WIDTH) / 2.0f;
                int x = (int)(touch.x / tileWidth);
                int y = (int)((SharkBait.HEIGHT - touch.y - offsetY) / tileWidth);
                playerMove(new Position(x, y));
            }
        }

        /* Changes rocks periodically. */
        rockChangeTick += delta;
        if (rockChangeTick > rockChangeInterval) {
            rockChangeTick -= rockChangeInterval;
            world.changeRocks(surfaceProbability, submergeProbability);
            if (mover instanceof AStarMove) {
                mover.compute();
            }
        }

        /* Controls the shark. */
        generateMoveTick += delta;
        if (generateMoveTick > sharkMoveDelay) {
            generateMoveTick -= sharkMoveDelay;
            sharkMove();
        }
        
        world.update(delta);
    }

    private void playerMove(Position p) {
        Player player = world.getPlayer();
        Position playerPos = player.getPosition();
        if (p.equals(playerPos)) {
            player.dodge();
            Assets.playSound(Assets.jumpSound);
        } else if (world.validatePlayerMove(p)) {
            player.move(p);
            Assets.playSound(Assets.jumpSound);
            /*
             * If the shark uses the A* algorithm, his path must be
             * computed whenever the player moves.
             */
            if (mover instanceof AStarMove) {
                mover.compute();
            }
            /*
             * Ensures that the player appears above the shark when
             * jumping down from a horizontal attack.
             */
            Shark shark = world.getShark();
            Position sharkPos = shark.getPosition();
            if (Board.isAdjacent(playerPos, sharkPos)
                    && playerPos.x != sharkPos.x && p.y > playerPos.y) {
                shark.setZIndex(Shark.MISSED_Z_INDEX);
                worldRenderer.zIndexSort();
            }
        }
    }
    
    private void sharkMove() {
        Shark shark = world.getShark();
        Position sharkPos = shark.getPosition();
        Position playerPos = world.getPlayer().getPosition();
        if (shark.getState() == Shark.IDLE
                && world.validateSharkAttack(playerPos)) {
            shark.attack(playerPos);
            Assets.playSound(Assets.splashSound);               
            /*
             * Ensures that the shark appears behind the player and the rock
             * when attacking from above.
             */
            if (sharkPos.y < playerPos.y) {
                shark.setZIndex(Shark.ABOVE_Z_INDEX);
            } else {
                shark.setZIndex(Shark.Z_INDEX);
            }
            worldRenderer.zIndexSort();
        } else if (shark.getState() == Shark.IDLE 
                || shark.getState() == Shark.MOVING){
            if (mover instanceof SimpleMove) {
                mover.compute();
            }
            Position next = mover.getNext();
            if (next != null && !world.isRock(next)) {
                shark.move(next);
            }
        }
    }

    private void updatePaused() {
        if (Gdx.input.justTouched()) {
            camera.unproject(touch.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (resumeBounds.contains(touch.x, touch.y)) {
                state = RUNNING;
            } else if (menuBounds.contains(touch.x, touch.y)) {
                save();
                dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1); 
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        
        drawPanel();
        
        worldRenderer.render();
        
        switch (state) {
        case RUNNING:
            drawRunning();
            break;
        case PAUSED:
            drawPaused();
            break;
        case LEVEL_END:
            drawLevelEnd();
            break;
        case GAME_OVER:
            drawGameOver();
            break;
        }
        
        game.batch.end();
    }

    private void drawLevelEnd() {
        drawMessage("LEVEL " + level + " COMPLETED");
    }

    private void drawRunning() {
        game.batch.draw(Assets.pauseTexture, pauseBounds.x, pauseBounds.y);
    }

    private void drawPaused() {
        game.batch.draw(Assets.resumeTexture, resumeBounds.x, resumeBounds.y);
        game.batch.draw(Assets.menuTexture, menuBounds.x, menuBounds.y);
    }

    private void drawGameOver() {
        drawMessage("YOU BECAME SHARK FOOD...");
    }

    /** Displays the specified message at the center of the map area. */
    private void drawMessage(String msg) {
        TextBounds bounds = game.font.getBounds(msg);
        float x = (float)SharkBait.WIDTH / 2 - bounds.width / 2;
        float y = (float)SharkBait.HEIGHT / 2;

        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, msg, x, y);
    }

    /** Draws the lower panel. */
    private void drawPanel() {
        float centerX = (float)SharkBait.WIDTH / 2;

        String levelDisplay = "LEVEL " + level;
        TextBounds levelDisplayBounds = game.font.getBounds(levelDisplay);
        float levelDisplayX = centerX - levelDisplayBounds.width / 2;
        float levelDisplayY = (float)(SharkBait.HEIGHT - SharkBait.WIDTH) / 4 + 25;

        String livesDisplay = "LIVES: " + lives;
        TextBounds livesDisplayBounds = game.font.getBounds(livesDisplay);
        float livesDisplayX = centerX - livesDisplayBounds.width / 2;
        float livesDisplayY = levelDisplayY - 25;

        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, levelDisplay, levelDisplayX, levelDisplayY);
        game.font.draw(game.batch, livesDisplay, livesDisplayX, livesDisplayY);
    }

    /** Creates the next level. */
    private void constructLevel() {
        resetTimers();
        adjustDifficulty();

        world = new World(new GameWorldListener(), DIMENSION, startSurfacedProbability);
        world.getShark().setMoveDelay(sharkMoveDelay);
        
        /* 
         * The probability of a life appearing is inversely proportional to
         * the number of lives the player has.
         */
        Random rand = new Random();
        if (rand.nextInt(lives * 2) == 0) {
            world.addPowerup(new Life());
        }

        mover = constructMover(world, level);
        if (mover instanceof AStarMove) {
            mover.compute();
        }
        
        worldRenderer = new WorldRenderer(game.batch, world);
    }

    /**
     * Resets in-game timers. 
     * Should be called at the start of level construction.
     */
    private void resetTimers() {
        rockChangeTick = 0;
        generateMoveTick = 0;
    }

    /**
     * Sets the difficulty of the game by adjusting some parameters.
     * Should be called at the start of level construction.
     */ 
    private void adjustDifficulty() {
        int difficulty = level - 1;
        startSurfacedProbability = Math.max(START_SURFACED_P_MIN, START_SURFACED_P - difficulty);
        surfaceProbability = Math.max(SURFACE_P_MIN, SURFACE_P - difficulty);
        submergeProbability = Math.min(SUBMERGE_P_MAX, SUBMERGE_P + difficulty);
        rockChangeInterval = Math.min(ROCK_CHANGE_T_MAX, ROCK_CHANGE_T + difficulty * 0.05f);
        sharkMoveDelay = Math.max(SHARK_MOVE_T_MAX, SHARK_MOVE_T - difficulty * 0.025f);
    }

    /** Returns a move generator for the shark. */
    private Mover constructMover(World world, int level) {
        return level < 10 ? new SimpleMove(world) : new AStarMove(world);
    }

    /** Saves game state to the save file. */
    private void save() {
        save.setWorld(world);
        save.setState(state);
        save.setLevel(level);
        save.setLives(lives);
        save.setRockChangeTick(rockChangeTick);
        save.setGenerateMoveTick(generateMoveTick);
        save.save();
    }

    /** Reconstructs game from saved data. */
    private void load() {
        world = save.getWorld();
        state = save.getState();
        level = save.getLevel();
        lives = save.getLives();
        rockChangeTick = save.getRockChangeTick();
        generateMoveTick = save.getGenerateMoveTick();

        adjustDifficulty();

        /* This value might have been changed by a powerup. */
        sharkMoveDelay = world.getShark().getMoveDelay();
        
        mover = constructMover(world, level);
        if (mover instanceof AStarMove) {
            mover.compute();
        }
        
        /* We need to reattach the listener since it is not serializable. */
        world.registerListener(new GameWorldListener());
        
        worldRenderer = new WorldRenderer(game.batch, world);
    }

    private class GameWorldListener implements WorldListener {
        @Override
        public void powerupFound(Powerup p) {
            if (p instanceof Life) {
                ++lives;
                Assets.playSound(Assets.lifeSound);
            } else if (p instanceof Caffeine) {
                /* Increases the player's maximum jump rate. */
                float speed = world.getPlayer().getSpeed();
                world.getPlayer().setSpeed(speed + 1);
                Assets.playSound(Assets.caffeineSound);
            } else if (p instanceof Slow) {
                /* Slows the shark to half of his current speed. */
                sharkMoveDelay *= 2;
                world.getShark().setMoveDelay(sharkMoveDelay);
                Assets.playSound(Assets.slowSound);
            }

            /* Ensures that the powerup disappears once collected. */
            worldRenderer.refreshSprites();
            worldRenderer.zIndexSort();
        }

        @Override
        public void playerHit() {
            Assets.playSound(Assets.chompSound);
            if (--lives == 0) {
                state = GAME_OVER;
                Assets.playSound(Assets.gameOverSound);
                save.clear();
                save.erase();
                
                /* Shows the main menu after 3 seconds. */
                Timer.schedule(new Task() {
                    @Override
                    public void run() {
                        dispose();
                        game.setScreen(new MainMenuScreen(game));
                    }
                }, 3);
            }
        }

        @Override
        public void levelEnd() {
            state = LEVEL_END;
            Assets.playSound(Assets.winSound);
            if (level > Settings.getHighestLevel()) {
                /* Sets the new highest level record. */
                Settings.setHighestLevel(level);
                Settings.save();
            }
            
            /* Starts the next level after 3 seconds. */
            Timer.schedule(new Task() {
                @Override
                public void run() {
                    ++level;
                    constructLevel();
                    state = RUNNING;
                }
            }, 3);
        }
    }
}

