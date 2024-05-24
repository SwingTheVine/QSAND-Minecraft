package com.SwingTheVine.QSAND.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerStuckEffectManager {
	public static final String propertyTagName = "PlayerStuckEffect"; // The name of this extended entity property class
	public static final String propertyMudLevelName = "MudLevel"; // The name of this SPECIFIC extended entity property
	public static final int mudControlDatawatcher = ConfigHandler.mudLevel; // The datawatcher to keep track of the mud properties
	private final EntityLivingBase entity; // The current entity the code is executing for
	private int level; // Level of stuckness
	private int tarTreads;
	
	// Constructor
	public PlayerStuckEffectManager(final EntityLivingBase entity) {
		this.entity = entity; // Assigns the current entity to the player variable on this instance
		this.level = -1; // Assigns a temporary value to this instance of the level variable
		this.tarTreads = 0; // Assigns a value of 0 to this instance of the number of tar treads
	}
	
	// Registers the entity as one that has extended properties
	public static final void register(final EntityLivingBase entity) {
		entity.registerExtendedProperties(propertyTagName, (IExtendedEntityProperties)new PlayerStuckEffectManager(entity));
	}
	
	// Obtains and returns the extended properties of the player
	public static final PlayerStuckEffectManager get(final EntityLivingBase entity) {
		return (PlayerStuckEffectManager)entity.getExtendedProperties(propertyTagName);
	}
	
	// Saves the property data to an NBT tag on the player
	public void saveNBTData(NBTTagCompound nbtCompound) {
		
	}

	// Loads the property data from an NBT tag on the player
	public void loadNBTData(NBTTagCompound nbtCompound) {
		
	}

	public void init(Entity entity, World world) {
		
	}
	
	public void doInit() {
		this.level = -1;
		this.tarTreads = 0;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public int getTarTreads() {
		return this.tarTreads;
	}
	
	public void setTarTreads(final int tarTreads) {
		this.tarTreads = tarTreads;
	}
	
	public void setStuckLevel(final int level) {
		this.level = level;
	}
}
