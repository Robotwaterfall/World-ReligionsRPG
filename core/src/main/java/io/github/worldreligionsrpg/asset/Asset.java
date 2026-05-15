package io.github.worldreligionsrpg.asset;

import com.badlogic.gdx.assets.AssetDescriptor;

public interface Asset<T> {
    AssetDescriptor<T> getDiscriptor();
}
