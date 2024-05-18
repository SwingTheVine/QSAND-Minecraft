package com.SwingTheVine.QSAND.entity;

import org.lwjgl.Sys;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.handler.ConfigHandler;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Bubble extends Entity {
	public long entitySpawnTime; // When the bubble spawned
	public int entityLiveTime; // How long the bubble should live for
	public float size; // The size of the bubble
	public Block block; // The block that spawned the bubble
	public int entityMetadata; // The metadata of the bubble
	public float randomRotation; // A randomized rotation to spawn at
	public static final String textureLocation = ModInfo.id + ":textures/blocks/mud_0.png"; // The location of the texture used for the bubble
	
	// Constructor
	public Bubble(final World world) {
		super(world);
		this.setSize(1.0f, 1.0f); // Sets the width and height of the bubble
		this.entitySpawnTime = Sys.getTime(); // Gets the time at construction
		
		// If the world is NOT a server instance, AND the user does NOT want to spawn singleplayer bubbles...
		if(!world.isRemote && !ConfigHandler.spawnUnseenBubbles) {
			// The user will never see these bubbles normally in singleplayer.
        	// There is no reason to spawn them UNLESS they are recording using a mod
			
			this.setDead(); // Kills the bubble
		}
	}
	
	// Constructor
	public Bubble(final World world, final double bubblePosX, final double bubblePosY, final double bubblePosZ, final Block block, final int blockMetadata, final float size, final int time) {
        
		this(world); // What world the bubble is in
        this.block = block; // What block spawned the bubble
        this.entityMetadata = blockMetadata; // The metadata of the block that spawned the bubble
        this.size = size; // The size of the bubble
        this.randomRotation = world.rand.nextFloat() * 360.0f; // Picks a random orientation to spawn at
        this.entityLiveTime = time; // How long the bubble should live for
        double maximumSpawningHeight = bubblePosY; // The maximum Y coordinate the entity can occupy
        final int blockPosX = (int)Math.floor(bubblePosX); // X position of the block that spawned the bubble
        final int blockPosZ = (int)Math.floor(bubblePosZ); // Z position of the block that spawned the bubble
        
        // For 6 blocks...
        for (int i = 0; i <= 5; ++i) {
        	
        	// If the block above the previous block is NOT opaque, AND not a liquid...
            if (!world.getBlockState(new BlockPos(blockPosX, (int)Math.floor(bubblePosY + 0.5 + i), blockPosZ)).getBlock().getMaterial().isOpaque() && !world.getBlockState(new BlockPos(blockPosX, (int)Math.floor(bubblePosY + 0.5 + i), blockPosZ)).getBlock().getMaterial().isLiquid()) {
            	
            	maximumSpawningHeight = bubblePosY + i; // The bubble is marked to teleport to the top block
                this.entitySpawnTime += i * 400; // Increases time until death
                break; // Skips the if statement below
            }
            
            // If the block above the previous block is NOT the same as the block that spawned the bubble...
            if (world.getBlockState(new BlockPos(blockPosX, (int)Math.floor(bubblePosY + 0.5 + i), blockPosZ)).getBlock() != block) {
                this.setDead(); // Kills the entity
                break;
            }
        }
        
        final int riseToBlockPosY = (int)Math.floor(maximumSpawningHeight - 0.5); // The Y position of the highest block the bubble can rise to
        
        // If the block the bubble rises to is NOT a liquid, AND the block is not in its default state (0 metadata)...
        if (world.getBlockState(new BlockPos(blockPosX, riseToBlockPosY, blockPosZ)).getBlock().getMaterial().isLiquid() && world.getBlockState(new BlockPos(blockPosX, riseToBlockPosY, blockPosZ)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(blockPosX, riseToBlockPosY, blockPosZ))) != 0) {
            this.setDead(); // Kills the bubble
        }
        
        this.setPosition(bubblePosX, maximumSpawningHeight, bubblePosZ); // Teleports to the top
    }
	
	// Constructor
	public Bubble(final World world, final double blockPosX, final double blockPosY, final double blockPosZ, final Block block, final int metadata, final float size, final int time, final int delay) {
        this(world, blockPosX, blockPosY, blockPosZ, block, metadata, size, time);
        this.entitySpawnTime += delay; // Increases the time until death
    }
	
	// Bubble initialization method
	protected void entityInit() {
		
    }
	
	// Checks if the bubble is within range to render
	@SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(final double distanceFromEntity) {
        double maximumRenderDistance = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
        maximumRenderDistance *= 64.0;
        return distanceFromEntity < maximumRenderDistance * maximumRenderDistance;
    }
	
	// Called to update entity
	public void onUpdate() {
		
		// If the world is NOT a server instance, AND the user does NOT want to spawn singleplayer bubbles...
		if(!this.worldObj.isRemote && !ConfigHandler.spawnUnseenBubbles) {
			// The user will never see these bubbles normally in singleplayer.
        	// There is no reason to spawn them UNLESS they are recording using a mod
			
			this.setDead(); // Kills the bubble
		}
		
        super.onUpdate();
        
        // If the remainder of the total world time divided by 32 equals 0, AND the block half a block below this bubble is NOT the same as the block that spawned the bubble...
        if (this.worldObj.getTotalWorldTime() % 32L == 0L && this.worldObj.getBlockState(new BlockPos((int)Math.floor(this.posX), (int)Math.floor(this.posY - 0.5), (int)Math.floor(this.posZ))) != this.block) {
            
        	this.setDead(); // Kills the bubble
            return; // Stops updating the bubble
        }
        
        // If the bubble has been alive for longer than it should be...
        if (Sys.getTime() - this.entitySpawnTime > this.entityLiveTime) {
        	
        	// 
            for (int i = 0; i < 3.0f * this.size; ++i) {
            	
            	// The greater the size, the further out the bubble can spawn from the triggering entity
                final double bubblePosX = this.worldObj.rand.nextFloat() * 0.2 * this.size - 0.1 * this.size;
                final double bubblePosZ = this.worldObj.rand.nextFloat() * 0.2 * this.size - 0.1 * this.size;
                
                // Spawns a particle
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, true, this.posX + bubblePosX, this.posY, this.posZ + bubblePosZ, 0.0, 0.0, 0.0, Block.getIdFromBlock(this.block));
            }
            
            // Plays a popping sound
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "liquid.lavapop", 0.5f + this.worldObj.rand.nextFloat() * 0.25f, (0.5f + this.worldObj.rand.nextFloat() * 0.5f) / this.size, false);
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.slime.attack", 0.15f, (1.25f + this.worldObj.rand.nextFloat() * 0.5f) / this.size, false);
            
            this.setDead(); // Kills the bubble
        }
    }
	
	public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
		
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
    	
    }
    
    // What size the shadow under the entity be
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0f; // Removes the shadow
    }
}
