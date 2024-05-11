package com.SwingTheVine.QSAND.manager;

import com.SwingTheVine.QSAND.handler.ConfigHandler;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
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
	public static void spawnBubbleDelay(final World world, final double x, final double y, final double z, final Block bl, final int mt, final float sz, final int tim, final int dly) {
        world.spawnEntityInWorld((Entity)new EntityBubble(world, x, y, z, bl, mt, sz, tim, dly));
    }
	
	// Spawns bubbles for when an entity is drowning
	public static void spawnDrowningBubble(final World world, final Entity entity, final Block block, int blockMetadata) {
		
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
                final int xxx = (int)Math.floor(bubblePosX);
                final int yyy = (int)Math.floor(bubblePosY);
                final int zzz = (int)Math.floor(bubblePosZ);
                
                // If the block that made the bubble spawn is NOT of the same type the bubble will spawn in...
                if (world.getBlockState(new BlockPos(xxx, yyy, zzz)).getBlock() != block) {
                	// In other words, if the bubble is going to spawn in a different type of block...
                	
                    return; // Don't spawn the bubble
                }
                
                final float bubbleSize = 1.25f - world.rand.nextFloat() * 1.0f;
                final int bubbleTime = (int)Math.floor((1000 + world.rand.nextInt(500)) * bubbleSize);
                spawnBubbleDelay(world, bubblePosX, yyy + surfaceY(block), bubblePosZ, block, blockMetadata, bubbleSize, bubbleTime, i * 100 + world.rand.nextInt(40) * 100);
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
}
