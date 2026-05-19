package io.github.worldreligionsrpg;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerTool {

    public static void main(String[] args){
        String inputDir = "assets/objects";
        String outputDir = "assets_finished/graphics";
        String packFileName = "objects";

        TexturePacker.process(inputDir,outputDir,packFileName);
    }
}
