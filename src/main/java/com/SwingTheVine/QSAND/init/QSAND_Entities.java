package com.SwingTheVine.QSAND.init;

import java.util.ArrayList;

import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.entity.effect.EntityBubble;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeMud;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeSand;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeTar;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeVoid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/** Registers blocks for this mod.
 * 
 * @since <b>0.28.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated CJMinecraft's code
 * @author <b>CJMinecraft</b> - 1.8.9 source code written here: <a href=
 * "https://github.com/CJMinecraft01/Minecraft-Modding-Tutorials/blob/master/src/main/java/cjminecraft/bitofeverything/init/ModEntities.java">
 * https://github.com/CJMinecraft01/Minecraft-Modding-Tutorials/blob/master/src/main/java/cjminecraft/bitofeverything/init/
 * ModEntities.java
 * </a>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class QSAND_Entities {
	
	private static int entityID = 0; // Starting ID of the entity
	
	public static void registerEntities() {
		
		registerEntity(EntityBubble.class, "bubble", 120, 2, true); // Registers the bubble entity
		registerEntity(EntitySlimeVoid.class, "slime_void", 80, 3, true); // Registers the void slime entity
		registerEntity(EntitySlimeMud.class, "slime_mud", 80, 3, true); // Registers the mud slime entity
		registerEntity(EntitySlimeSand.class, "slime_sand", 80, 3, true); // Registers the sand slime entity
		registerEntity(EntitySlimeTar.class, "slime_tar", 80, 3, true); // Registers the tar slime entity
	}
	
	/** Add a spawn entry for the supplied entity in the supplied {@link BiomeGenBase} list
	 * 
	 * @param entityClass Entity class added
	 * @param weightedProb Probability
	 * @param min Min spawn count
	 * @param max Max spawn count
	 * @param typeOfCreature Type of spawn
	 * @param biomes List of biomes */
	public static void setEntityToSpawn() {
		
		// Generates lists of different biome types
		final ArrayList<BiomeGenBase> allBiomesList = new ArrayList<BiomeGenBase>();
		final ArrayList<BiomeGenBase> swampBiomesList = new ArrayList<BiomeGenBase>();
		final ArrayList<BiomeGenBase> desertBiomesList = new ArrayList<BiomeGenBase>();
		
		// For every biome...
		for (final BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
			
			// If the biome is NOT null...
			if (biome != null) {
				
				// Add the biome to the "all biomes" list
				allBiomesList.add(biome);
				
				// If the biome is a swamp,
				// AND the biome is NOT hills,
				// AND the biome is NOT magical,
				// AND the biome is NOT mushroom,
				// AND the biome is NOT plains,
				// AND the biome is NOT forest,
				// AND the biome is NOT wet...
				if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SWAMP)
					&& !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.HILLS)
					&& !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL)
					&& !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MUSHROOM)
					&& !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.PLAINS)
					&& !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST)
					&& !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WET)) {
					
					// Add the biome to the "swamp biomes" list
					swampBiomesList.add(biome);
				}
				
				// If the biome is sandy (previously desert)...
				if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY)) {
					desertBiomesList.add(biome); // Add the biome to the "desert biomes" list
				}
			}
			
			swampBiomesList.add(BiomeGenBase.swampland); // Adds swampland to the "swamp biomes" list
			desertBiomesList.add(BiomeGenBase.desert); // Adds desert to the "desert biomes" list
			desertBiomesList.add(BiomeGenBase.desertHills); // Adds desert hills to the "desert biomes" list
		}
		
		final BiomeGenBase[] allBiomesArray = new BiomeGenBase[allBiomesList.size()];
		final BiomeGenBase[] swampBiomesArray = new BiomeGenBase[swampBiomesList.size()];
		final BiomeGenBase[] desertBiomesArray = new BiomeGenBase[desertBiomesList.size()];
		
		final BiomeGenBase[] allBiomes = allBiomesList.toArray(allBiomesArray);
		final BiomeGenBase[] swampBiomes = swampBiomesList.toArray(swampBiomesArray);
		final BiomeGenBase[] desertBiomes = desertBiomesList.toArray(desertBiomesArray);
		
		EntityRegistry.addSpawn(EntitySlimeVoid.class, 2, 1, 1, EnumCreatureType.MONSTER, allBiomes);
		EntityRegistry.addSpawn(EntitySlimeMud.class, 2, 1, 1, EnumCreatureType.MONSTER, swampBiomes);
		EntityRegistry.addSpawn(EntitySlimeSand.class, 2, 1, 1, EnumCreatureType.MONSTER, desertBiomes);
		EntityRegistry.addSpawn(EntitySlimeTar.class, 2, 1, 1, EnumCreatureType.MONSTER, allBiomes);
	}
	
	// Generates a spawn egg for the entity
	public static void generateSpawnEgg() {
		
		EntityRegistry.registerEgg(EntitySlimeVoid.class, 10205416, 10993884);
		EntityRegistry.registerEgg(EntitySlimeMud.class, 7428915, 5787429);
		EntityRegistry.registerEgg(EntitySlimeSand.class, 16049320, 14858107);
		EntityRegistry.registerEgg(EntitySlimeTar.class, 1973277, 2696228);
	}
	
	/** Register an entity with the specified tracking values.
	 *
	 * @param entityClass The entity's class
	 * @param entityName The entity's unique name
	 * @param trackingRange The range at which MC will send tracking updates
	 * @param updateFrequency The frequency of tracking updates
	 * @param sendsVelocityUpdates Whether to send velocity information packets as well */
	private static void registerEntity(final Class<? extends Entity> entityClass, final String entityName,
		final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates) {
		
		EntityRegistry.registerModEntity(entityClass, entityName, entityID++, QSAND.instance, trackingRange,
			updateFrequency, sendsVelocityUpdates);
	}
}
