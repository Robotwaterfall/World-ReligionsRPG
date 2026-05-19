package io.github.worldreligionsrpg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import io.github.worldreligionsrpg.Main;
import io.github.worldreligionsrpg.asset.AssetService;
import io.github.worldreligionsrpg.asset.AtlasAsset;

public class LoadingScreen extends ScreenAdapter {

    private final Main main;
    private final AssetService assetService;

    public LoadingScreen(Main main, AssetService assetService){
        this.main = main;
        this.assetService = assetService;
    }

    @Override
    public void show(){
        for(AtlasAsset atlas : AtlasAsset.values()){
            assetService.queue(atlas);
        }
    }

    @Override
    public void render(float delta){
        if(this.assetService.update()){
            Gdx.app.debug("Loading Screen", "Finished Asset Loading");
            createScreen();
            this.main.removeScreen(this);
            this.dispose();
            this.main.setScreen(GameScreen.class);
        }
    }

    private void createScreen() {
        this.main.addScreen(new GameScreen(this.main));
    }

}
