package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.entity.Bubble;
import com.SwingTheVine.QSAND.entity.SlimeSand;

import net.minecraft.entity.Entity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class QSAND_Entities {
	
	private static int entityID = 0; // Starting ID of the entity
	
	public static void registerEntities() {
		registerEntity(Bubble.class, "bubble", 120, 2, true); // Registers the bubble entity
		registerEntity(SlimeSand.class, "slime_sand", 80, 3, true); // Registers the sand slime entity
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
		//EntityRegistry.addSpawn((Class)Bubble.class, 1, 1, 1, EnumCreatureType.AMBIENT); // Makes the entity spawn naturally
	}
	
	// Generates a spawn egg for the entity
	public static void generateSpawnEgg() {
		EntityRegistry.registerEgg(SlimeSand.class, 16049320, 14858107);
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
