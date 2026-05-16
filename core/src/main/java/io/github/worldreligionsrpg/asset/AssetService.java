package io.github.worldreligionsrpg.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class AssetService implements Disposable {
    private final AssetManager assetManager;

    public AssetService(FileHandleResolver fileHandleResolver){
        this.assetManager = new AssetManager(fileHandleResolver);
        this.assetManager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    public <T> T load(Asset<T> asset){
        this.assetManager.load(asset.getDiscriptor());
        this.assetManager.finishLoading();
        return this.assetManager.get(asset.getDiscriptor());
    }

    public <T> void queue(Asset<T> asset){
        this.assetManager.load(asset.getDiscriptor());
    }

    public <T> T get(Asset<T> asset){
        return this.assetManager.get(asset.getDiscriptor());
    }

    public <T> void unload(Asset<T> asset){
        this.assetManager.unload(asset.getDiscriptor().fileName);
    }

    public boolean update(){
        return this.assetManager.update();
    }

    public void debugDiagnostics(){
        Gdx.app.debug("AssetService", this.assetManager.getDiagnostics());
    }

    @Override
    public void dispose(){
        this.assetManager.dispose();
    }

}
