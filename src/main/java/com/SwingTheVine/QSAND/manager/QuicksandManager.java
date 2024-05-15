package com.SwingTheVine.QSAND.manager;

import com.SwingTheVine.QSAND.entity.Bubble;
import com.SwingTheVine.QSAND.handler.ConfigHandler;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;

public class QuicksandManager {
	
	public static boolean isEntityInsideOfBlock(final Entity triggeringEntity, final Block block) {
        double overCor = 0.0; // If the fluid is a full block
        
        // If the block is a liquid, AND the block is a classic fluid...
        if (block.getMaterial().isLiquid() && block instanceof BlockFluidClassic) {
            overCor = ((((BlockFluidClassic)block).getMaxRenderHeightMeta() == -1) ? 0.0 : 0.15);
        }
        
        final double triggEntityHeight = triggeringEntity.posY + triggeringEntity.getEyeHeight() - 0.075 + overCor; // How tall the entity is. Measured from camera POV to bottom of entity
        final int triggEntityPosX = MathHelper.floor_double(triggeringEntity.posX); // Triggering entity's X position
        final int triggEntityMaxPosY = MathHelper.floor_float((float)MathHelper.floor_double(triggEntityHeight)); // Triggering entity's maximum Y position floored
        final int triggEntityPosZ = MathHelper.floor_double(triggeringEntity.posZ); // Triggering entity's Z position
        
        // Checks what the block above the entity's head is (if true) or what block the entity's head is in.
        // In other words, it returns what block the entity is inside of
        Block triggEntityInsideBlockLower = (block == QSAND_Blocks.moss)
        		? triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY + 1, triggEntityPosZ)).getBlock()
				: triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY, triggEntityPosZ)).getBlock();
        
		// Same as before except it is checking one block higher
        Block triggEntityInsideBlockHigher = (block == QSAND_Blocks.moss)
        		? triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY + 2, triggEntityPosZ)).getBlock()
				: triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY + 1, triggEntityPosZ)).getBlock();
		
		// Returns true if the top half of the entity is inside this block,
        //    AND this block is a moss block,
        //       OR the block above the entity is a solid block, AND the top of the solid block is higher than the entity's height,
        //       OR the top of this block is higher than the top of the entity
        return triggEntityInsideBlockLower == block && (block == QSAND_Blocks.moss || (triggEntityInsideBlockHigher.getMaterial().isSolid() && triggEntityMaxPosY + 1 > triggEntityHeight) || triggEntityMaxPosY + triggEntityInsideBlockLower.getBlockBoundsMaxY() > triggEntityHeight);
    }
	
	// Gets the mud level from how far the player has sunk into the block
	public static int getMudLevel(final EntityPlayer player, final double blockPosY, final World world) {
		final double eyeHeight = player.posY + player.getEyeHeight(); // The eye height of the player
		final double deltaY = eyeHeight - blockPosY; // Difference in Y position between the eye height of the player and the block
		
		// If the player has sunk all but 0.7 blocks above the BOTTOM of the block...
		if (deltaY < 0.7) {
			// This means the eye height of the player has sunk 0.3 blocks into the block
			
			return 10; // The mud level is 10
		}
		
		// If the player has sunk all but 1.04 blocks above the BOTTOM of the block...
		if (deltaY < 1.04) {
			return 9; // The mud level is 9
		}
		
		// If the player has sunk all but 1.1 blocks above the BOTTOM of the block...
		if (deltaY < 1.1) {
			return 8; // The mud level is 8
		}
		
		// If the player has sunk all but 1.21 blocks above the BOTTOM of the block...
		if (deltaY < 1.21) {
			return 7; // The mud level is 7
		}
		
		// If the player has sunk all but 1.32 blocks above the BOTTOM of the block...
		if (deltaY < 1.32) {
			return 6; // The mud level is 6
		}
		
		// If the player has sunk all but 1.53 blocks above the BOTTOM of the block...
		if (deltaY < 1.53) {
			return 5; // The mud level is 5
		}
		
		// If the player has sunk all but 1.67 blocks above the BOTTOM of the block...
		if (deltaY < 1.67) {
			return 4; // The mud level is 4
		}
		
		// If the player has sunk all but 1.98 blocks above the BOTTOM of the block...
		if (deltaY < 1.98) {
			return 3; // The mud level is 3
		}
		
		// If the player has sunk all but 2.14 blocks above the BOTTOM of the block...
		if (deltaY < 2.14) {
			return 2; // The mud level is 2
		}
		
		// If the player has sunk all but 2.4 blocks above the BOTTOM of the block...
		if (deltaY < 2.4) {
			return 1; // The mud level is 1
		}
		
		return 0; // The mud level is 0. The player is not inside the block
	}
	
	// Is the entity truly sinking?
	public static boolean isTrulySinking(final Entity entity, final double triggEntitySunk_kof1) {
		return !(entity instanceof EntityPlayer) || entity.worldObj.isRemote;
	}
	
	// Checks if the entity should be marked with suction
	public static boolean suctionWorldCheck(final Entity entity, final World world, final double velocity) {
		double velocityY = Math.max(velocity * 10.0, 0.25);
		
		if (entity instanceof EntityPlayer && ConfigHandler.useCustomBootCalc && world.isRemote) {
			boolean boots = false;
			
			if (((EntityPlayer)entity).getCurrentArmor(0) != null) {
				boots = true;
			}
			
			if (boots) {
				velocityY = Math.max(velocityY / 2.0, 0.0);
			}
		}
		
		return world.getTotalWorldTime() % Math.floor(Math.max(10.0 * Math.pow(velocityY, 2.0), 1.0)) == 0.0 && world.rand.nextInt(5) == 0;
	}
	
	// Returns the height of the block
	public static double surfaceY(final Block block) {
		
		// If the block is NOT a classic fluid...
        if (!(block instanceof BlockFluidClassic)) {
            return block.getBlockBoundsMaxY(); // Return the maximum Y boundary
        }
        
        // If the classic fluid block's height does NOT equal -1...
        if (((BlockFluidClassic)block).getMaxRenderHeightMeta() != -1) {
            return 0.85;
        }
        
        return 1.0;
    }
	
	// Spawns a bubble on a delay
	public static void spawnBubble(final World world, final double blockPosX, final double blockPosY, final double blockPosZ, final Block block, final int metadata, final float size, final int time) {
        world.spawnEntityInWorld((Entity)new Bubble(world, blockPosX, blockPosY, blockPosZ, block, metadata, size, time));
    }
	
	// Spawns a bubble on a delay
	public static void spawnBubbleDelay(final World world, final double blockPosX, final double blockPosY, final double blockPosZ, final Block block, final int metadata, final float size, final int time, final int delay) {
        world.spawnEntityInWorld((Entity)new Bubble(world, blockPosX, blockPosY, blockPosZ, block, metadata, size, time, delay));
    }
	
	// Spawns a random body bubble
	public static void spawnBodyBubble(final World world, final Entity entity, final int blockPosX, final int blockPosY, final int blockPosZ, final Block block, boolean useMetadata) {
		int blockMetadata = 0;
		
		// If the world is NOT a server instance, AND the user does NOT want to spawn singleplayer bubbles...
        if (!world.isRemote && !ConfigHandler.spawnUnseenBubbles) {
        	// The user will never see these bubbles normally in singleplayer.
        	// There is no reason to spawn them UNLESS they are recording using a mod
        	
            return; // Don't spawn drowning bubbles
        }
        
        // Spawns in a random area within 0.5 blocks of the entity
        final double bubblePosX = entity.posX + world.rand.nextFloat() * 1.0f - 0.5;
        final double bubblePosZ = entity.posZ + world.rand.nextFloat() * 1.0f - 0.5;
        
        // If the block is not this block...
        if (world.getBlockState(new BlockPos((int)Math.floor(blockPosX), blockPosY, (int)Math.floor(blockPosZ))).getBlock() != block) {
        	// In other words, if the bubble is going to spawn in a different type of block...
        	
        	return; // The block the entity is in is not the block that triggered this. Don't spawn
        }
        
        // If the metadata from the block should be used...
        if (useMetadata) {
        	blockMetadata = world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)));
        }
        
        final float bubbleSize = 1.25f - world.rand.nextFloat() * 0.5f;
        final int bubbleTime = (int)Math.floor((1000 + world.rand.nextInt(500)) * bubbleSize);
        spawnBubble(world, bubblePosX, blockPosY + surfaceY(block), bubblePosZ, block, blockMetadata, bubbleSize, bubbleTime);
	}
	
	// Spawns a random body bubble
	public static void spawnBodyBubbleRandom(final World world, final Entity entity, final int blockPosX, final int blockPosY, final int blockPosZ, final Block block, boolean useMetadata) {
		int blockMetadata = 0;
		
		// If the world is NOT a server instance, AND the user does NOT want to spawn singleplayer bubbles...
        if (!world.isRemote && !ConfigHandler.spawnUnseenBubbles) {
        	// The user will never see these bubbles normally in singleplayer.
        	// There is no reason to spawn them UNLESS they are recording using a mod
        	
            return; // Don't spawn drowning bubbles
        }
        
        // Spawns in a random area within 0.5 blocks of the entity
        final double bubblePosX = entity.posX + world.rand.nextFloat() * 1.0f - 0.5;
        final double bubblePosZ = entity.posZ + world.rand.nextFloat() * 1.0f - 0.5;
        
        // If the block is not this block...
        if (world.getBlockState(new BlockPos((int)Math.floor(blockPosX), blockPosY, (int)Math.floor(blockPosZ))).getBlock() != block) {
        	// In other words, if the bubble is going to spawn in a different type of block...
        	
        	return; // The block the entity is in is not the block that triggered this. Don't spawn
        }
        
        // If the metadata from the block should be used...
        if (useMetadata) {
        	blockMetadata = world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)));
        }
        
        final float bubbleSize = 1.25f - world.rand.nextFloat() * 0.5f;
        final int bubbleTime = (int)Math.floor((1000 + world.rand.nextInt(500)) * bubbleSize);
        spawnBubbleDelay(world, bubblePosX, blockPosY + surfaceY(block), bubblePosZ, block, blockMetadata, bubbleSize, bubbleTime, world.rand.nextInt(20) * 100);
	}
	
	// Spawns bubbles for when an entity is drowning
	public static void spawnDrowningBubble(final World world, final Entity entity, final Block block, boolean useMetadata) {
		int blockMetadata = 0;
		
		// If the world is NOT a server instance, AND the user does NOT want to spawn singleplayer bubbles...
        if (!world.isRemote && !ConfigHandler.spawnUnseenBubbles) {
        	// The user will never see these bubbles normally in singleplayer.
        	// There is no reason to spawn them UNLESS they are recording using a mod
        	
            return; // Don't spawn drowning bubbles
        }
        
        // If the entity is not alive (as opposed to a death animation)...
        if (!entity.isEntityAlive()) {
            return; // The entity can not drown. Don't spawn drowning bubbles
        }
        
        // If the entity is a player, AND the entity has disabled damage, AND the user does NOT want to spawn singleplayer bubbles...
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage && !ConfigHandler.spawnUnseenBubbles) {
        	// The user will never see these bubbles normally in singleplayer.
        	// There is no reason to spawn them UNLESS they are recording using a mod
        	
            return; // The player is in Creative Mode and can not drown. Don't spawn drowning bubbles
        }
        
        // If the remainder of the total world time divided by 48 equals 
        if (world.getTotalWorldTime() % 48L == 0L && world.rand.nextInt(2) == 0) {
        	
        	// Spawns between 4 and 9 bubbles (4 + 0-5)
            for (int i = 0; i < 4 + world.rand.nextInt(6); ++i) {
            	
            	// Spawns in a random area within 0.5 blocks of the entity
                final double bubblePosX = entity.posX + world.rand.nextFloat() * 1.0f - 0.5;
                final double bubblePosZ = entity.posZ + world.rand.nextFloat() * 1.0f - 0.5;
                
                // Spawns at the eye level of the entity
                double bubblePosY = entity.posY + entity.getEyeHeight();
                
                // If the entity is a player...
                if (entity instanceof EntityPlayer) {
                	bubblePosY -= 0.15; // The bubble will spawn 0.15 blocks lower (since the eye level is 0.15 blocks lower for players)
                }
                
                // The block the bubble spawned in
                final int blockPosX = (int)Math.floor(bubblePosX);
                final int blockPosY = (int)Math.floor(bubblePosY);
                final int blockPosZ = (int)Math.floor(bubblePosZ);
                
                // If the block that made the bubble spawn is NOT of the same type the bubble will spawn in...
                if (world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)).getBlock() != block) {
                	// In other words, if the bubble is going to spawn in a different type of block...
                	
                    return; // Don't spawn the bubble
                }
                
                // If the metadata from the block should be used...
                if (useMetadata) {
                	blockMetadata = world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)));
                }
                
                final float bubbleSize = 1.25f - world.rand.nextFloat() * 1.0f;
                final int bubbleTime = (int)Math.floor((1000 + world.rand.nextInt(500)) * bubbleSize);
                spawnBubbleDelay(world, bubblePosX, blockPosY + surfaceY(block), bubblePosZ, block, blockMetadata, bubbleSize, bubbleTime, i * 100 + world.rand.nextInt(40) * 100);
            }
        }
    }
	
	// Obtains the value of the custom drown air datawatcher
	public static int getCustomDrownAir(final Entity entity) {
		
		// Tries to run this code
        try {
        	
        	// If the entity is a player...
            if (entity instanceof EntityPlayer) {
            	
            	// Returns the datawatcher for custom drowning in blocks
                return entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirBlockDW);
            }
            
            // If the entity is NOT a player, it returns the datawatcher for custom drowning in entities
            return entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirEntityDW);
        } catch (Exception ignored) {
        	// If it crashes, don't crash and ignore the error
        	
            return 0; // Returns 0
        }
    }
    
	// Sets the value for the custom drown air datawatcher
    public static void setCustomDrownAir(final Entity entity, final int value) {
    	
    	// Tries to run this code
        try {
        	
        	// If the entity is a player...
            if (entity instanceof EntityPlayer) {
            	
            	// Changes the value of the datawatcher for custom drowning in blocks
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirBlockDW, (Object)value);
            } else {
            	
            	// If the entity is NOT a player, it changes the value of the datawatcher for custom drowning in entities
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirEntityDW, (Object)value);
            }
        } catch (Exception ignored) {}
        // If it crashes, don't crash and ignore the error
    }
    
    // Adds to the value of the custom drown air datawatcher
    public static void addCustomDrownAir(final Entity entity, final int modifier) {
    	
    	// Tries to run this code
        try {
        	
        	// If the entity is a player...
            if (entity instanceof EntityPlayer) {
            	
            	// Adds the modifier to the datawatcher for custom drowning in blocks
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirBlockDW, (Object)(entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirBlockDW) + modifier));
            } else {
            	
            	// If the entity is NOT a player, it adds the modifier to the datawatcher for custom drowning in entities
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirEntityDW, (Object)(entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirEntityDW) + modifier));
            }
        } catch (Exception ignored) {}
        // If it crashes, don't crash and ignore the error
    }
	
    // Checks if the entity should be marked as drowning
	public static boolean isDrowning(final Entity entity) {
		
		// If the entity is a player...
        if (entity instanceof EntityPlayer) {
        	
        	// ...AND the user has disabled realistic drowning in blocks...
            if (!ConfigHandler.useCustomDrownBlock) {
                return true; // The user disabled this. Return early
            }
            
            // If the entity is NOT a player...
            //    ...AND the user has disabled realistic drowning in entities...
        } else if (!ConfigHandler.useCustomDrownEntity) {
            return true; // The user disabled this. Return early
        }
        
        // If the entity is a player, AND the player has disabled damage...
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage) {
            return false; // The user is in Creative Mode. They can not drown
        }
        
        final int customDrownAir = getCustomDrownAir(entity); // Obtains the datawatcher value
        
        // If the entity is NOT on a server instance...
        if (!entity.worldObj.isRemote) {
        	
        	// ...AND if the datawatcher value is less than 0, AND the remainder of the total world time divided by 16 equals 0...
            if (customDrownAir < 0 && entity.worldObj.getTotalWorldTime() % 16L == 0L) {
                setCustomDrownAir(entity, 0); // Sets the datawatcher value to 0
                return true; // The entity is marked as drowning
            }
        } else if (customDrownAir < 1) {
        	// If the entity is on a server instance, AND the datawatcher value is less than 1...
        	
            return true; // The entity is marked as drowning
        }
        
        return false; // The entity is marked as NOT drowning
    }
	
	// Gets the mud type of the block
	public static int getMudType(final Block block) {
		return -1; // This is not needed
	}
	
	public static void setStuckEffect(final EntityLivingBase entity, final int level) {
		
		if (PlayerStuckEffectManager.get(entity) != null) {
			PlayerStuckEffectManager.get(entity).setStuckLevel(level);
		}
	}
}
