package io.github.worldreligionsrpg.tiled;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import io.github.worldreligionsrpg.asset.AssetService;
import io.github.worldreligionsrpg.asset.AtlasAsset;
import io.github.worldreligionsrpg.component.Animation2D;
import io.github.worldreligionsrpg.component.Facing;
import io.github.worldreligionsrpg.component.Fsm;
import io.github.worldreligionsrpg.component.Graphic;
import io.github.worldreligionsrpg.component.Transform;
import io.github.worldreligionsrpg.component.Animation2D.AnimationType;
import io.github.worldreligionsrpg.component.Facing.FacingDirection;
import io.github.worldreligionsrpg.input.Controller;
import static io.github.worldreligionsrpg.Constants.WorldConstants.UNIT_SCALE;

public class TiledAshleyConfigurator {
    private final Engine engine;
    private final AssetService assetService;

    public TiledAshleyConfigurator(Engine engine, AssetService assetService) {
        this.engine = engine;
        this.assetService = assetService;

    }

    public void onLoadObject(TiledMapTileMapObject tileMapObject) {
        Entity entity = this.engine.createEntity();
        TiledMapTile tile = tileMapObject.getTile();
        TextureRegion textureRegion = getTextureRegion(tile);
        int z = tile.getProperties().get("z", 1, Integer.class);

        entity.add(new Graphic(Color.WHITE.cpy(), textureRegion));
        addEntityTransform(tileMapObject.getX(), tileMapObject.getY(), z,
                textureRegion.getRegionWidth(), textureRegion.getRegionHeight(),
                tileMapObject.getScaleX(), tileMapObject.getScaleY(), entity);
        addEntityController(tileMapObject, tile, entity);

        addEntityMove(tileMapObject, tile, entity);

        addEntityAnimation(tile, entity);
        entity.add(new Facing(Facing.FacingDirection.DOWN));
        entity.add(new Fsm(entity));

        this.engine.addEntity(entity);
    }

    private void addEntityAnimation(TiledMapTile tile, Entity entity) {
        String animationStr = tile.getProperties().get("animation", "", String.class);
        String atlasAssetStr =
                tile.getProperties().get("atlasAsset", AtlasAsset.OBJECTS.name(), String.class);
        AtlasAsset atlasAsset = AtlasAsset.valueOf(atlasAssetStr);
        FileTextureData textureData =
                (FileTextureData) tile.getTextureRegion().getTexture().getTextureData();
        // Use the parent folder name (e.g. 'player') as the atlas key so lookups like
        // 'player/walk_down' succeed. Fall back to the file name without extension.
        String atlasKey;
        try {
            String parentName = textureData.getFileHandle().parent().name();
            atlasKey = (parentName == null || parentName.isEmpty())
                    ? textureData.getFileHandle().nameWithoutExtension()
                    : parentName;
        } catch (Exception e) {
            atlasKey = textureData.getFileHandle().nameWithoutExtension();
        }

        AnimationType animationType = null;
        if (!animationStr.isBlank()) {
            animationType = AnimationType.valueOf(animationStr);
        } else {
            // Try to detect animation by checking the atlas for common animation sets
            TextureAtlas textureAtlas = this.assetService.get(atlasAsset);
            for (AnimationType candidate : AnimationType.values()) {
                String combinedKey = atlasKey + "/" + candidate.getAtlasKey() + "_"
                        + FacingDirection.DOWN.getAtlasKey();
                if (!textureAtlas.findRegions(combinedKey).isEmpty()) {
                    animationType = candidate;
                    break;
                }
            }
        }

        if (animationType == null) {
            return;
        }

        float speed = tile.getProperties().get("animationSpeed", 0f, Float.class);
        // If animationSpeed is not set or zero, use a sensible default so animations progress
        if (speed <= 0f) {
            speed = 1f;
        }
        entity.add(new Animation2D(atlasAsset, atlasKey, animationType, Animation.PlayMode.LOOP,
                speed));
    }

    private void addEntityMove(
            com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject tileMapObject,
            TiledMapTile tile, Entity entity) {
        // Prefer object-level property, fall back to tile-level
        float speed = 0f;
        if (tileMapObject != null) {
            speed = tileMapObject.getProperties().get("speed", 0f, Float.class);
        }
        if (speed == 0f && tile != null) {
            speed = tile.getProperties().get("speed", 0f, Float.class);
        }

        if (speed == 0f) {
            return;
        }

        entity.add(new io.github.worldreligionsrpg.component.Move(speed));
    }

    public void addEntityController(TiledMapTileMapObject tileMapObject, TiledMapTile tile,
            Entity entity) {
        // Check object-level property first, then tile-level
        boolean controller = false;
        if (tileMapObject != null) {
            controller = tileMapObject.getProperties().get("controller", false, Boolean.class);
        }
        if (!controller && tile != null) {
            controller = tile.getProperties().get("controller", false, Boolean.class);
        }

        if (!controller) {
            return;
        }

        entity.add(new Controller());
    }

    private static void addEntityTransform(float x, float y, int z, float w, float h, float scaleX,
            float scaleY, Entity entity) {
        Vector2 position = new Vector2(x, y);
        Vector2 size = new Vector2(w, h);
        Vector2 scaling = new Vector2(scaleX, scaleY);

        position.scl(UNIT_SCALE);
        size.scl(UNIT_SCALE);

        entity.add(new Transform(position, z, size, scaling, 0f));
    }


    private TextureRegion getTextureRegion(TiledMapTile tile) {
        String atlasAssetStr =
                tile.getProperties().get("atlasAsset", AtlasAsset.OBJECTS.name(), String.class);
        AtlasAsset atlasAsset = AtlasAsset.valueOf(atlasAssetStr);
        TextureAtlas textureAtlas = this.assetService.get(atlasAsset);
        FileTextureData textureData =
                (FileTextureData) tile.getTextureRegion().getTexture().getTextureData();
        // Use parent folder name (e.g. 'player') for atlas key; fallback to filename
        String atlasKey;
        try {
            String parentName = textureData.getFileHandle().parent().name();
            atlasKey = (parentName == null || parentName.isEmpty())
                    ? textureData.getFileHandle().nameWithoutExtension()
                    : parentName;
        } catch (Exception e) {
            atlasKey = textureData.getFileHandle().nameWithoutExtension();
        }
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(atlasKey + "/" + atlasKey);
        if (region != null) {
            return region;
        }

        // Otherwise region wasn't found so render at least something
        return tile.getTextureRegion();
    }
}
