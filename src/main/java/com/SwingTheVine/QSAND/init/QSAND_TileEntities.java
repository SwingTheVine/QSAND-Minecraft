package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.tileentity.TileEntityLarvae;

import net.minecraftforge.fml.common.registry.GameRegistry;

/** Registers entities that are tied to specific blocks/tiles.
 * 
 * @since <b>0.87.7</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrCrayfish's code
 * @author <b>MrCrayfish</b> - 1.8.9 source code written here: <a href=
 * "https://github.com/MrCrayfish/ModdingTutorials/blob/master/src/main/java/com/mrcrayfish/teleportmod/init/TeleportTileEntities.java">
 * https://github.com/MrCrayfish/ModdingTutorials/blob/master/src/main/java/com/mrcrayfish/teleportmod/init/TeleportTileEntities.
 * java
 * </a>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class QSAND_TileEntities {
	
	public static void registerTileEntities() {
		
		// Registers the tile entities with the Game Registry
		GameRegistry.registerTileEntity(TileEntityLarvae.class, "qsandLarvae");
	}
}
