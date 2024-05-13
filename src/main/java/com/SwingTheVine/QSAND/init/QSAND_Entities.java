package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.entity.Bubble;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class QSAND_Entities {
	
	private static int entityID = 0; // Starting ID of the entity
	
	public static void registerEntities() {
		registerEntity(Bubble.class, "bubble", 64, 20, true);
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
		EntityRegistry.addSpawn((Class)Bubble.class, 1, 1, 1, EnumCreatureType.AMBIENT);
	}
	
	public static void generateSpawnEgg() {
		EntityRegistry.registerEgg(Bubble.class, 255, 0);
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
