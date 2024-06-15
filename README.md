# Quicksand And Numerous Dungeons

## How versioning works
The versioning system for this mod follows the [Semantic Versioning rules](https://semver.org/). As such, it is formatted in an `X.Y.Z` format where:
* X is the major version. This is incremented when a non-backward compatible update is pushed.
* Y is the minor version. This is incremented whenever I push to GitHub.
* Z is the patch version. This is incremented whenever I launch Minecraft to test a patch.

## Gradle Issue
If you encounter gradle issues with cached libraries missing, take a look at the [Repair Gradle Instructions](https://github.com/SwingTheVine/QSAND-Minecraft/blob/master/RepairGradleInstructions.txt)

## Resources
An exhaustive list of references I used to make this mod. ⭐ = Exceptional resource.
* [More Fun Quicksand Mod](https://www.curseforge.com/minecraft/mc-mods/more-fun-quicksand-mod) decompiled and deobfuscated to be used as a base. Not as easy as it sounds...

* [CJMinecraft's YouTube playlist](https://www.youtube.com/watch?v=gS58vMJM_00&list=PLpKu3PfwdqHQc5F3YnUdBm3rOyfLke3sj&index=13) with an example of custom blocks with metadata
* [radman63's Minecraft Forum thread](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/modification-development/2451794-need-help-with-blockstates) on blockstates
* ⭐ [Kevin M's YouTube playlist](https://www.youtube.com/playlist?list=PLiFAb_ju1TajRzMXxLAk8P8LHe5JRNs_3) on how to create custom mob models
* [BlueHexalon's Minecraft Forum thread](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/modification-development/2610305-1-8-9-entities-with-custom-renderer-is-rendering) and [TheGoldCrayon's Minecraft Fourm thread](https://forums.minecraftforge.net/topic/37547-solved-189-custom-entity-help/?do=findComment&comment=199602) on troubleshooting custom model errors
* [Jacknoshima's Minecraft Forum thread](https://forums.minecraftforge.net/topic/9327-how-do-you-get-a-spawn-egg-into-a-custom-creative-tab/) on moving spawn eggs to a custom creative tab
* ⭐ [darkk7's Minecraft Forum post](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2523556-mapwriter-continued-an-open-source-mini-map?page=3) with a list of changes made to the World Renderer and Tessalator
* [Lothrazar's Minecraft Forum thread](https://forums.minecraftforge.net/topic/34975-188-111501591-looking-for-worldrenderer-functions-startdrawingquads/) with code about using World Renderer and OpenGL
* ⭐ [WillieWillus's GitHub gist](https://gist.github.com/williewillus/57d7093efa80163e96e0) with a general overview of how the rendering engine changed between 1.7 and 1.8
* [DragonessAtHeart's Minecraft Forum thread](https://forums.minecraftforge.net/topic/36185-189-solved-registering-a-new-mob-entity/) on IRenderFactory
* [Lothrazar's GitHub repo](https://github.com/Lothrazar/ERZ/blob/trunk/1.12/src/main/java/teamroots/emberroot/entity/spriteling/RenderSpriteling.java) with an example of a custom rendering factory for custom entities
* [BlueHexalon's GitHub repo](https://github.com/BlueHexalon/bluehex_housing_mod/blob/master/mod/src/main/java/com/bluehex/bh_housing/client/renderer/RenderNPC.java) with an example of an alternative rendering method for custom entities
* ⭐ [MyCrayfish's YouTube playlist](https://www.youtube.com/watch?v=3oqZ1MNCu2Y&list=PLy11IosblXIFDFAT3wz_5Nve05wIVKFSJ&index=7) with a good example on Tile Entities
* ⭐ [Choonster's GitHub repo](https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.8.9/src/main/java/com/choonster/testmod3/init/ModFluids.java) with an example of custom fluids
* ⭐ [Emasher's Minecraft Forum thread](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/1571414-how-to-use-the-forge-biomedictionary) with a guide on the Forge Biome Dictionary
* 🌟 [TehNut-Mods's GitHub repo](https://github.com/TehNut-Mods/ResourcefulCrops/blob/1.7.10/src/main/java/tehnut/resourceful/crops/ConfigHandler.java) with a functioning Config Handler
* ⭐ [MCPCFanC's Minecraft Forum thread](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/modification-development/2405990-mcmod-info-file-guide-and-help) with an explanation of every aspect of the `mcmod.info` file
