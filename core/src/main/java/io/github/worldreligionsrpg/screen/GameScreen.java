package io.github.worldreligionsrpg.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.worldreligionsrpg.Main;
import io.github.worldreligionsrpg.asset.AssetService;
import io.github.worldreligionsrpg.asset.MapAsset;
import io.github.worldreligionsrpg.input.GameControllerState;
import io.github.worldreligionsrpg.input.KeyboardController;
import io.github.worldreligionsrpg.system.ControllerSystem;
import io.github.worldreligionsrpg.system.MoveSystem;
import io.github.worldreligionsrpg.system.RenderSystem;
import io.github.worldreligionsrpg.tiled.TiledAshleyConfigurator;
import io.github.worldreligionsrpg.tiled.TiledService;

import java.util.function.Consumer;

public class GameScreen extends ScreenAdapter {
    private final Engine engine;
    private final TiledService tiledService;
    private final TiledAshleyConfigurator ashleyConfigurator;
    private final KeyboardController keyboardController;
    private final Main game;

    public GameScreen(Main game) {
        this.game = game;
        this.tiledService = new TiledService(game.getAssetService());
        this.engine = new Engine();
        this.ashleyConfigurator = new TiledAshleyConfigurator(this.engine, game.getAssetService());
        this.keyboardController = new KeyboardController(GameControllerState.class, engine);

        this.engine.addSystem(new ControllerSystem());
        this.engine.addSystem(new MoveSystem());
        this.engine
                .addSystem(new RenderSystem(game.getBatch(), game.getViewport(), game.getCamera()));
    }

    @Override
    public void show() {
        game.setInputProcessor(keyboardController);
        keyboardController.setActiveState(GameControllerState.class);

        Consumer<TiledMap> renderConsumer = this.engine.getSystem(RenderSystem.class)::setMap;
        this.tiledService.setMapChangerConsumer(renderConsumer);
        this.tiledService.setLoadObjectConsumer(this.ashleyConfigurator::onLoadObject);

        TiledMap tiledMap = this.tiledService.loadMap(MapAsset.MAIN);
        this.tiledService.setMap(tiledMap);
    }

    @Override
    public void hide() {
        this.engine.removeAllEntities();
    }

    /**
     * this delta time is the time that has been passed between two frames without this the player
     * may go through a wall since the collision checker is behind that's why the delta time is
     * clamped to a very low number
     */
    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1f / 30f);
        this.engine.update(delta);

    }

    @Override
    public void dispose() {
        for (EntitySystem system : this.engine.getSystems()) {
            if (system instanceof Disposable disposableSystem) {
                disposableSystem.dispose();
            }
        }


    }
}
