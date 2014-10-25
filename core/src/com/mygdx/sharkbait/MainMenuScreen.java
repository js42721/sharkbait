package com.mygdx.sharkbait;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.sharkbait.utils.Assets;

public class MainMenuScreen implements Screen {    
    private interface ButtonListener {
        void clicked(MainMenuScreen screen);
    }

    private enum Buttons implements ButtonListener {
        CONTINUE("CONTINUE") {
            @Override
            public void clicked(MainMenuScreen screen) {
                /* Loads a saved game. */
                screen.startGame(screen.save);
            }
        },

        NEW_GAME("NEW GAME") {
            @Override
            public void clicked(MainMenuScreen screen) {
                /* Erases the save file and starts a new game. */
                screen.save.clear();
                screen.save.erase();
                screen.startGame(null);
            }
        };

        final String text;

        Buttons(String text) {
            this.text = text;
        }
    }

    /* Must dispose manually. */
    private final Stage stage;
    private final Skin skin;

    private final SharkBait game;
    private final SaveState save;
    private final Camera camera;
    private final Vector3 touch;
    private final Rectangle muteBounds;
    private final Table menu;

    public MainMenuScreen(SharkBait game) {
        this.game = game;

        save = new SaveState(SharkBait.SAVE);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        menu = new Table(skin);
        touch = new Vector3();

        camera = new OrthographicCamera(SharkBait.WIDTH, SharkBait.HEIGHT);
        camera.position.set(SharkBait.WIDTH / 2, SharkBait.HEIGHT / 2, 0);
        
        stage = new Stage(new StretchViewport(SharkBait.WIDTH, SharkBait.HEIGHT), game.batch);
        Gdx.input.setInputProcessor(stage);
        
        int muteWidth = Assets.muteTexture.getWidth();
        int muteHeight = Assets.muteTexture.getHeight();
        muteBounds = new Rectangle(SharkBait.WIDTH / 2 - muteWidth / 2,
                SharkBait.HEIGHT / 4, muteWidth, muteHeight);

        createMenu();
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    /**
     * Loads a saved game or, if save is null, starts a new one. Loading from
     * a bad save can cause a crash to occur so make sure the load method for
     * the save returns true.
     */
    private void startGame(SaveState saveState) {
        dispose();
        game.setScreen(new GameScreen(game, saveState));
    }

    /**
     * Creates the menu. The "continue" option will appear only if the save
     * file is successfully loaded.
     */
    private void createMenu() {
        boolean loadGame = save.load();
        for (final Buttons b : Buttons.values()) {
            if (!loadGame && b.equals(Buttons.CONTINUE)) {
                continue;
            }
            TextButton textButton = new TextButton(b.text, skin);
            textButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.playSound(Assets.selectSound);
                    b.clicked(MainMenuScreen.this);
                }
            });
            menu.add(textButton).size(100, 50);
            menu.row();
        }

        menu.setFillParent(true);
        stage.addActor(menu);
    }

    private void update() {
        stage.act();

        if (Gdx.input.justTouched()) {
            camera.unproject(touch.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (muteBounds.contains(touch.x, touch.y)) {
                Settings.setSound(!Settings.sound());
                Settings.save();
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1); 
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
        
        camera.update();
        
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.batch.draw(Assets.titleTexture, 0, SharkBait.HEIGHT * 0.75f, 
                SharkBait.WIDTH, SharkBait.HEIGHT * 0.13f);
        game.batch.draw(Settings.sound() ? Assets.soundTexture : Assets.muteTexture,
                muteBounds.x, muteBounds.y);

        int highestLevel = Settings.getHighestLevel();
        if (highestLevel > 0) {
            String highestLevelStr = "HIGHEST COMPLETED LEVEL: " + highestLevel;
            TextBounds bounds = game.font.getBounds(highestLevelStr);
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, highestLevelStr, SharkBait.WIDTH / 2 - bounds.width / 2, 50);
        }

        game.batch.end();
        
        stage.draw();
    }
}
