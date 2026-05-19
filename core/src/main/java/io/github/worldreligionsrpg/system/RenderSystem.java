package io.github.worldreligionsrpg.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.worldreligionsrpg.asset.AssetService;
import io.github.worldreligionsrpg.asset.MapAsset;
import io.github.worldreligionsrpg.component.Graphic;
import io.github.worldreligionsrpg.component.Transform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.github.worldreligionsrpg.Constants.WorldConstants.UNIT_SCALE;

public class RenderSystem extends SortedIteratingSystem implements Disposable {

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Batch batch;
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final List<MapLayer> bgLayers;
    private final List<MapLayer> frgLayers;

    public RenderSystem(Batch batch, Viewport viewport, OrthographicCamera camera) {
        super(
            Family.all(Transform.class, Graphic.class).get(),
            Comparator.comparing(Transform.MAPPER::get)
        );
        this.batch = batch;
        this.viewport = viewport;
        this.camera = camera;
        this.mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, this.batch);
        this.bgLayers = new ArrayList<>();
        this.frgLayers = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime){
        AnimatedTiledMapTile.updateAnimationBaseTime();
        this.viewport.apply();

        batch.begin();
        this.batch.setColor(Color.WHITE);
        this.mapRenderer.setView(this.camera);
        bgLayers.forEach(mapRenderer::renderMapLayer);

        forceSort();
        super.update(deltaTime);

        this.batch.setColor(Color.WHITE);
        frgLayers.forEach(mapRenderer::renderMapLayer);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime){
        Transform transform = Transform.MAPPER.get(entity);
        Graphic graphic = Graphic.MAPPER.get(entity);
        if(graphic.getRegion() == null){
            return;
        }

        Vector2 position = transform.getPosition();
        Vector2 scaling = transform.getScaling();
        Vector2 size = transform.getSize();
        this.batch.setColor(graphic.getColor());
        this.batch.draw(
            graphic.getRegion(),
            position.x - (1f -scaling.x) * size.x * 0.5f,
            position.y - (1f -scaling.y) * size.y * 0.5f,
            size.x * 0.5f, size.y * 0.5f,
            size.x, size.y,
            scaling.x, scaling.y,
            transform.getRotationDeg()
        );
        this.batch.setColor(Color.WHITE);
    }

    public void setMap(TiledMap tiledMap){
        this.mapRenderer.setMap(tiledMap);

        this.frgLayers.clear();
        this.bgLayers.clear();
        List<MapLayer> currentLayers = bgLayers;
        for(MapLayer layer : tiledMap.getLayers()){
            if("objects".equals(layer.getName())){
            currentLayers = bgLayers;
            continue;

        }
        if(layer.getClass().equals(MapLayer.class)){
            continue;
        }

        currentLayers.add(layer);
    }
        }

    @Override
    public void dispose(){
        this.mapRenderer.dispose();
    }
}
