<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.12.1" name="objects" tilewidth="80" tileheight="112" tilecount="5" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <image source="training_dummy/idle_down_00.png" width="32" height="32"/>
 </tile>
 <tile id="1">
  <image source="chest/chest.png" width="16" height="16"/>
 </tile>
 <tile id="2">
  <image source="house/house.png" width="80" height="112"/>
 </tile>
 <tile id="3">
  <image source="oak_tree/oak_tree.png" width="41" height="63"/>
 </tile>
 <tile id="4">
  <properties>
   <property name="life" type="float" value="10"/>
   <property name="speed" type="float" value="3"/>
  </properties>
  <image source="player/idle_down_00.png" width="32" height="32"/>
 </tile>
</tileset>
