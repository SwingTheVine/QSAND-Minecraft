package com.SwingTheVine.QSAND.init;

import java.util.ArrayList;

import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.entity.Bubble;
import com.SwingTheVine.QSAND.entity.SlimeMud;
import com.SwingTheVine.QSAND.entity.SlimeSand;
import com.SwingTheVine.QSAND.entity.SlimeTar;
import com.SwingTheVine.QSAND.entity.SlimeVoid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class QSAND_Entities {
	
	private static int entityID = 0; // Starting ID of the entity
	
	public static void registerEntities() {
		registerEntity(Bubble.class, "bubble", 120, 2, true); // Registers the bubble entity
		registerEntity(SlimeVoid.class, "slime_void", 80, 3, true); // Registers the void slime entity
		registerEntity(SlimeMud.class, "slime_mud", 80, 3, true); // Registers the mud slime entity
		registerEntity(SlimeSand.class, "slime_sand", 80, 3, true); // Registers the sand slime entity
		registerEntity(SlimeTar.class, "slime_tar", 80, 3, true); // Registers the tar slime entity
	}
	
	/**
     * Add a spawn entry for the supplied entity in the supplied {@link BiomeGenBase} list
     * @param entityClass Entity class added
     * @param weightedProb Probability
     * @param min Min spawn count
     * @param max Max spawn count
     * @param typeOfCreature Type of spawn
     * @param biomes List of biomes
     */
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
				//    AND the biome is NOT hills,
				//    AND the biome is NOT magical,
				//    AND the biome is NOT mushroom,
				//    AND the biome is NOT plains,
				//    AND the biome is NOT forest,
				//    AND the biome is NOT wet...
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
		
		EntityRegistry.addSpawn(SlimeVoid.class, 2, 1, 1, EnumCreatureType.MONSTER, allBiomes);
		EntityRegistry.addSpawn(SlimeMud.class, 2, 1, 1, EnumCreatureType.MONSTER, swampBiomes);
		EntityRegistry.addSpawn(SlimeSand.class, 2, 1, 1, EnumCreatureType.MONSTER, desertBiomes);
		EntityRegistry.addSpawn(SlimeTar.class, 2, 1, 1, EnumCreatureType.MONSTER, allBiomes);
	}
	
	// Generates a spawn egg for the entity
	public static void generateSpawnEgg() {
		EntityRegistry.registerEgg(SlimeVoid.class, 10205416, 10993884);
		EntityRegistry.registerEgg(SlimeMud.class, 7428915, 5787429);
		EntityRegistry.registerEgg(SlimeSand.class, 16049320, 14858107);
		EntityRegistry.registerEgg(SlimeTar.class, 1973277, 2696228);
	}

	/**
	 * Register an entity with the specified tracking values.
	 *
	 * @param entityClass          The entity's class
	 * @param entityName           The entity's unique name
	 * @param trackingRange        The range at which MC will send tracking updates
	 * @param updateFrequency      The frequency of tracking updates
	 * @param sendsVelocityUpdates Whether to send velocity information packets as well
	 */
	private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(entityClass, entityName, entityID++, QSAND.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}
}
