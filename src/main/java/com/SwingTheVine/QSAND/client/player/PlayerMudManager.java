package com.SwingTheVine.QSAND.client.player;

import com.SwingTheVine.QSAND.handler.ConfigHandler;

import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerMudManager implements IExtendedEntityProperties {
	
	public static final String propertyTagName = "PlayerMudControl"; // The name of this extended entity property class
	public static final String propertyMudLevelName = "MudLevel"; // The name of this SPECIFIC extended entity property
	public static final int mudControlDatawatcher = ConfigHandler.mudLevel; // The datawatcher to keep track of the mud properties
	private final EntityPlayer player; // The current player the code is executing for
	
	// Constructor
	public PlayerMudManager(final EntityPlayer player) {
		this.player = player; // Assigns the current player to the player variable on this instance
		
		// Tries to initialize a new datawatcher on the current player to track the mud level of the player
		try {
			
			// Initializes a new datawatcher on the current player to track the mud properties of the player
			this.player.getDataWatcher().addObject(PlayerMudManager.mudControlDatawatcher, (Object)0);
		} catch (Exception e){
			// If it crashes, crash with a custom error message instead
			
			// Generates the crash report
			final CrashReport report = CrashReport.makeCrashReport((Throwable)e, "Error initializing skin overlay. There appears to be a conflict between IDs of the local and global mud levels. Currently \"" + ConfigHandler.mudLevel + "\"");
			
			throw new ReportedException(report); // Crashes with a custom error message
		}
	}
	
	// Registers the player as one that has extended properties
	public static final void register(final EntityPlayer player) {
		player.registerExtendedProperties(propertyTagName, (IExtendedEntityProperties)new PlayerMudManager(player));
	}
	
	// Obtains and returns the extended properties of the player
	public static final PlayerMudManager get(final EntityPlayer player) {
		return (PlayerMudManager)player.getExtendedProperties(propertyTagName);
	}

	// Saves the property data to an NBT tag on the player
	@Override
	public void saveNBTData(NBTTagCompound nbtCompound) {
		final NBTTagCompound nbtTag = new NBTTagCompound(); // Constructs a new NBT tag
		nbtTag.setInteger(propertyMudLevelName, this.player.getDataWatcher().getWatchableObjectInt(PlayerMudManager.mudControlDatawatcher));
		nbtCompound.setTag(propertyTagName, (NBTBase)nbtTag);
	}

	// Loads the property data from an NBT tag on the player
	@Override
	public void loadNBTData(NBTTagCompound nbtCompound) {
		final NBTTagCompound nbtTag = (NBTTagCompound)nbtCompound.getTag(propertyTagName);
		this.player.getDataWatcher().updateObject(PlayerMudManager.mudControlDatawatcher, (Object)nbtTag.getInteger(propertyMudLevelName));
	}

	@Override
	public void init(Entity entity, World world) {
		
	}
	
	// Returns the mud control datawatcher of the current player
	public int getMudControlDatawatcher() {
		return this.player.getDataWatcher().getWatchableObjectInt(PlayerMudManager.mudControlDatawatcher);
	}
	
	// Converts and returns the mud control datawatcher into the mud level as an int
	public int getMudLevel() {
		return this.getMudControlDatawatcher() / 256 & 0xFF;
	}
	
	// Converts and returns the mud control datawatcher into the mud time as an int
	public int getMudTime() {
		return this.getMudControlDatawatcher() / 65536;
	}
	
	// Converts and returns the mud control datawatcher into the mud type as an int
	public int getMudType() {
		return this.getMudControlDatawatcher() & 0xFF;
	}
	
	// Sets the mud level of the player
	public void setMudLevel(final int mudLevel) {
		final int datawatcher = this.getMudControlDatawatcher();
		this.player.getDataWatcher().updateObject(PlayerMudManager.mudControlDatawatcher, (Object)(datawatcher / 65536 * 65536 | mudLevel * 256 | (datawatcher & 0xFF)));
	}
	
	// Sets the mud time of the player
	public void setMudTime(final int mudTime) {
		this.player.getDataWatcher().updateObject(PlayerMudManager.mudControlDatawatcher, (Object)((this.getMudControlDatawatcher() & 0xFFFF) | mudTime * 65536));
	}
	
	// Sets the mud type of the player
	public void setMudType(final int mudType) {
		this.player.getDataWatcher().updateObject(PlayerMudManager.mudControlDatawatcher, (Object)(this.getMudControlDatawatcher() / 256 * 256 | mudType));
	}
	
	// Adds mud time to the mud time of the player
	public void addMudTime(final int mudTime) {
		final int datawatcher = this.getMudControlDatawatcher();
		this.player.getDataWatcher().updateObject(PlayerMudManager.mudControlDatawatcher, (Object)((datawatcher & 0xFFFF) | (mudTime + datawatcher / 65536) * 65536));
	}
}
