package io.github.worldreligionsrpg;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.worldreligionsrpg.asset.AssetService;
import io.github.worldreligionsrpg.screen.GameScreen;
import io.github.worldreligionsrpg.screen.LoadingScreen;

import java.util.HashMap;
import java.util.Map;

import static io.github.worldreligionsrpg.Constants.WorldConstants.WORLD_HEIGHT;
import static io.github.worldreligionsrpg.Constants.WorldConstants.WORLD_WIDTH;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private Batch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private AssetService assetService;
    private GLProfiler glProfiler;
    private FPSLogger fpsLogger;

    private final Map<Class<? extends Screen>, Screen> screenCache = new HashMap<>();

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.assetService = new AssetService(new InternalFileHandleResolver());
        this.glProfiler = new GLProfiler(Gdx.graphics);
        this.glProfiler.enable();
        this.fpsLogger = new FPSLogger();

        addScreen(new LoadingScreen(this, assetService));
        setScreen(LoadingScreen.class);
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
        super.resize(width, height);
    }

    public void addScreen(Screen screen){
        screenCache.put(screen.getClass(), screen);
    }

    public void removeScreen(Screen screen) {
        screenCache.remove(screen.getClass());
    }

    public void setScreen(Class<? extends Screen> screenClass){
        Screen screen = screenCache.get(screenClass);
        if(screen == null){
            throw new GdxRuntimeException("No Screen With Class" + screenClass + "Found In The Screen Cache");
        }
        super.setScreen(screen);
    }

    @Override
    public void render(){
        glProfiler.reset();

        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();

        Gdx.graphics.setTitle("WorldReligionsRPG - Draw Calls: " + glProfiler.getDrawCalls());
        fpsLogger.log();
    }

    @Override
    public void dispose(){
        screenCache.values().forEach(Screen::dispose);
        screenCache.clear();

        this.batch.dispose();
        this.assetService.debugDiagnostics();
        this.assetService.dispose();

    }

    public AssetService getAssetService() {
        return assetService;
    }

    public Batch getBatch(){
        return batch;
    }

    public Viewport getViewport(){
        return viewport;
    }

    public OrthographicCamera getCamera(){
        return camera;
    }
}
