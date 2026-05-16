package io.github.worldreligionsrpg.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.worldreligionsrpg.asset.Asset;
import io.github.worldreligionsrpg.asset.AssetService;
import io.github.worldreligionsrpg.asset.MapAsset;

import java.util.Map;
import java.util.function.Consumer;

public class TiledService {
    private final AssetService assetService;

    private TiledMap currentMap;

    private Consumer<TiledMap> mapChangerConsumer;
    private Consumer<TiledMapTileMapObject> loadObjectConsumer;

    public TiledService(AssetService assetService){
        this.assetService = assetService;
        this.mapChangerConsumer = null;
        this.loadObjectConsumer = null;
        this.currentMap = null;
    }

    public TiledMap loadMap(MapAsset mapAsset){
        TiledMap tiledMap = this.assetService.load(MapAsset.MAIN);
        tiledMap.getProperties().put("mapAsset", mapAsset);
        return tiledMap;
    }

    public void setMap(TiledMap map){
        if(this.currentMap != null){
            this.assetService.unload(this.currentMap.getProperties().get("mapAsset", MapAsset.class));
        }

        this.currentMap = map;
        loadMapObjects(map);
        if(this.mapChangerConsumer != null){
            this.mapChangerConsumer.accept(map);
        }
    }

    public void loadMapObjects(TiledMap tiledMap){
        for(MapLayer layer : tiledMap.getLayers()){
            if("objects".equals(layer.getName())){
                loadObjectLayer(layer);
            }
        }

    }


    public void loadObjectLayer(MapLayer objectLayer){
        if(loadObjectConsumer == null){
            return;
        }

        for(MapObject mapObject : objectLayer.getObjects()){
            if(mapObject instanceof TiledMapTileMapObject tileMapObject){
                loadObjectConsumer.accept(tileMapObject);
            }else{
                throw new GdxRuntimeException("Unsupported Object" + mapObject.getClass().getSimpleName());
            }
        }

    }

    public void setMapChangerConsumer(Consumer<TiledMap> mapChangerConsumer) {
        this.mapChangerConsumer = mapChangerConsumer;
    }

    public void setLoadObjectConsumer(Consumer<TiledMapTileMapObject> loadObjectConsumer) {
        this.loadObjectConsumer = loadObjectConsumer;
    }
}
