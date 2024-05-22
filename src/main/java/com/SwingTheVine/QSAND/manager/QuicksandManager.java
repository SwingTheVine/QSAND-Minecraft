package com.SwingTheVine.QSAND.manager;

import java.util.UUID;

import com.SwingTheVine.QSAND.client.player.CustomPlayerGUIRenderer;
import com.SwingTheVine.QSAND.entity.Bubble;
import com.SwingTheVine.QSAND.entity.EntityLongStick;
import com.SwingTheVine.QSAND.entity.TentaclesMud;
import com.SwingTheVine.QSAND.handler.ConfigHandler;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.BlockFluidClassic;

public class QuicksandManager {
	
	// Spawns in a new item in a semi-random position
	public static void dropItem(final World world, final int x, final int y, final int z, final ItemStack item) {
        
		// If the code is NOT executing server-side...
		if (!world.isRemote) {
			
			// Calculates a semirandom position to spawn the item in at
            final float var6 = 0.7f;
            final double d0 = world.rand.nextFloat() * var6 + (1.0f - var6) * 0.5;
            final double d2 = world.rand.nextFloat() * var6 + (1.0f - var6) * 0.5;
            final double d3 = world.rand.nextFloat() * var6 + (1.0f - var6) * 0.5;
            
            // Constructs the item
            final EntityItem entityitem = new EntityItem(world, x + d0, y + d2, z + d3, item);
            
            entityitem.setPickupDelay(10); // Adds a delay to picking the item up
            
            world.spawnEntityInWorld((Entity)entityitem); // Spawns the item into the world
        }
    }
	
	// Spawns in a new item in an exact position
	public static void dropItem(final World world, final double x, final double y, final double z, final ItemStack item) {
        
		// If the code is NOT executing server-side...
		if (!world.isRemote) {
			
			// Constructs the item
            final EntityItem entityitem = new EntityItem(world, x, y, z, item);
            
            entityitem.setPickupDelay(10); // Adds a delay to picking the item up
            
            world.spawnEntityInWorld((Entity)entityitem); // Spawns the item into the world
        }
    }
	
	// Finds out if the entity is inside of the type of block specified
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
	
	// Finds out if the entity is inside of the type of block specified
	public static boolean isEntityInsideOfBlockS(final Entity triggeringEntity, final Block block) {
        double overCor = 0.0; // If the fluid is a full block
        
        if (block == null) {
        	System.out.println("The block type is null. Skipping...");
        	return false;
        }
        
        // If the block is a liquid, AND the block is a classic fluid...
        if (block.getMaterial().isLiquid() && block instanceof BlockFluidClassic) {
            overCor = ((((BlockFluidClassic)block).getMaxRenderHeightMeta() == -1) ? 0.0 : 0.15);
        }
        
        final double triggEntityHeight = triggeringEntity.posY + triggeringEntity.getEyeHeight() - 0.2 + overCor; // How tall the entity is. Measured from camera POV to bottom of entity
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
	
	// Finds out if the entity is inside of the type of block specified
	public static boolean isEntityInsideOfBlockM(final Entity triggeringEntity, final Block block) {

		final double triggEntityHeight = triggeringEntity.posY + triggeringEntity.getEyeHeight() - 0.2; // How tall the entity is. Measured from camera POV to bottom of entity
        final int triggEntityPosX = MathHelper.floor_double(triggeringEntity.posX); // Triggering entity's X position
        final int triggEntityMaxPosY = MathHelper.floor_float((float)MathHelper.floor_double(triggEntityHeight)); // Triggering entity's maximum Y position floored
        final int triggEntityPosZ = MathHelper.floor_double(triggeringEntity.posZ); // Triggering entity's Z position
        
        // What block the entity is inside of
        Block triggEntityInsideBlockLower = triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY + 1, triggEntityPosZ)).getBlock();
        
        return triggEntityInsideBlockLower == block;
    }
	
	// Finds out if the entity is inside of the type of block specified
	public static boolean isEntityInsideOfBlockL(final Entity triggeringEntity, final Block block) {

		final double triggEntityHeight = triggeringEntity.posY;
        final int triggEntityPosX = MathHelper.floor_double(triggeringEntity.posX); // Triggering entity's X position
        final int triggEntityMaxPosY = MathHelper.floor_float((float)MathHelper.floor_double(triggEntityHeight)); // Triggering entity's maximum Y position floored
        final int triggEntityPosZ = MathHelper.floor_double(triggeringEntity.posZ); // Triggering entity's Z position
        
        // What block the entity is inside of
        Block triggEntityInsideBlockLower = triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY, triggEntityPosZ)).getBlock();
        
        if (triggEntityInsideBlockLower == block) {
        	return triggEntityPosZ + triggEntityInsideBlockLower.getBlockBoundsMaxY() > triggEntityHeight || isEntityInsideOfBlockS(triggeringEntity, block);
        }
        
        return isEntityInsideOfBlockS(triggeringEntity, block);
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
	
	// Gets the mud type of the block
	public static int getMudType(final Block block) {
		
		// For every block in the block list...
		for (int i = 0; i < CustomPlayerGUIRenderer.blockList.length; i++) {
			
			// If the block in a certain block index equals this block...
            if (CustomPlayerGUIRenderer.blockList[i] == block) {
                return i; // Return the index
            }
        }
		
		return -1; // No mud type found
	}
	
	public static int getLastMudType(final int type) {
        
		// If type is less than 0...
		if (type < 0) {
            return 0; // Return 0
        }
		
		// If type is greater than the length of the block list...
        if (type > QSAND_Blocks.blockObjectList.length / 5) {
            return 0; // Return 0
        }
        
        try {
	        // Otherwise, return the light opacity of the block at the index "type"
	        return (int)(Integer)QSAND_Blocks.blockObjectList[(type*5) + 3];
        } catch (Exception ignored) {
        	return 0;
        }
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
	
	// Keeps players from holding the jump button to escape
	public static void antiHoldJumpScript(final Entity entity, final double triggEntitySunk, final boolean isStuck) {
		
		// If the entity is not a player...
		if (!(entity instanceof EntityPlayer)) {
			return; // The entity can't hold the jump button. Don't run this script
		}
		
		// If the entity is on a server...
		if (entity.worldObj.isRemote) {
			double weight = 0.0; // Weight of the player
			double weightArmor = 0.0; // Weight of the armor on the player
			double weightInv = 0.0; // Weight of the player's inventory
			
			// If the player entity is the current player...
			if((EntityPlayer)entity == Minecraft.getMinecraft().thePlayer) {
				
				// ...AND we are weighing armor...
				if (ConfigHandler.useCustomArmorCalc) {
					double damageReduction = 0.0; // How much damage is reduced by this piece of armor
					
					// For every armor slot...
					for (int armorSlot = 0; armorSlot <= 3; armorSlot++) {
						
						// If the player's boot slot is NOT null, AND the player's boot slot contains an ItemArmor item...
						if (((EntityPlayer)entity).getCurrentArmor(armorSlot) != null && ((EntityPlayer)entity).getCurrentArmor(armorSlot).getItem() instanceof ItemArmor) {
							
							// Finds how much damage the boots reduce
							damageReduction = ((ItemArmor)((EntityPlayer)entity).getCurrentArmor(armorSlot).getItem()).getArmorMaterial().getDamageReductionAmount(3 - armorSlot);
							
							// Adds the calculated weight of this piece of armor into the total armor weight variable
							weightArmor += Math.signum(Math.max(((-0.4 * armorSlot) + 1.3) - triggEntitySunk, 0.0)) * (damageReduction + 10.0 / Math.max(damageReduction, 1.0)) / 2.0 + Math.pow(damageReduction, 2.0);
						}
					}
				}
				
				// If we are weighing the inventory...
				if (ConfigHandler.useCustomInvCalc) {
					
					weightInv = ConfigHandler.serverInstanceWeightInv; // Retrieves the weight of the player from the server
					
					// If the remainder of the total world time divided by 32 equals 0...
					if (entity.worldObj.getTotalWorldTime() % 32L == 0L) {
						weightInv = 0.0; // Reset the weight of the inventory
						
						// For every inventory slot...
						for (int invSlot = 0; invSlot <= 35; invSlot++) {
							
							// If the inventory slot is NOT null...
							if (((EntityPlayer)entity).inventory.mainInventory[invSlot] != null) {
								final ItemStack slotItem = ((EntityPlayer)entity).inventory.mainInventory[invSlot]; // The item in the slot
								
								// If the item in the slot is a block...
								if (slotItem.getItem() instanceof ItemBlock) {
									final Block slotBlock = Block.getBlockFromItem(slotItem.getItem()); // The block in the slot
									
									// If the block is NOT null...
									if (slotBlock != null) {
										
										// ...AND the block has a tile entity...
										if (slotBlock.hasTileEntity(slotBlock.getDefaultState())) {
											weightInv += 10 * slotItem.stackSize; // Add the number of items in that slot times 10 as the weight
										} else {
											final Material slotBlockMaterial = slotBlock.getMaterial(); // Get the material of the block in the slot
											weightInv += (2.5 + (slotBlockMaterial.getCanBurn() ? 0 : 1) * 2.5 + (slotBlock.canDropFromExplosion((Explosion)null) ? 1 : 0) * slotBlock.getExplosionResistance((Entity)null) / 2.0) / ((slotBlock.isOpaqueCube() ? 0 : 1) + 1.0) * (((slotBlockMaterial.getMaterialMobility() == 2) ? 1 : 0) + 1.0) / 2.0 * slotItem.stackSize;
										}
									} else {
										weightInv += 0.5 * slotItem.stackSize; // Adds the number of items in that slot times 0.5 as the weight
									}
								} else if (slotItem.getItem() instanceof ItemArmor) {
									// Else if the item in the slot is a piece of armor...
									
									// Adds the weight of the damage the armor to the 2nd power
									weightInv += Math.pow(((ItemArmor)slotItem.getItem()).getArmorMaterial().getDamageReductionAmount(((ItemArmor)slotItem.getItem()).armorType), 2.0) * slotItem.stackSize;
								} else if (slotItem.getItem().isDamageable()) {
									// Else if the item can be damaged (i.e. sword)
									
									weightInv += 5 * slotItem.stackSize; // Adds the weight of the number of items in the slot times 5
								} else if (slotItem.getItem().getItemStackLimit(slotItem) == 1) {
									// Else if the item can NOT be stacked (i.e. bow)
									
									weightInv += 2.5 * slotItem.stackSize; // Adds the weight of the number of items in the slot times 2.5
								} else {
									// Else...
									
									weightInv += 0.5 * slotItem.stackSize; // Adds the weight of the number of items in the slot times 0.5
								}
							}
						}
						
						ConfigHandler.serverInstanceWeightInv = weightInv; // Stores the inventory weight of the player in the server
					}
				}
				
				// Calculates the total weight from the inventoy and armor weight
				weight = Math.max(-250.0 + weightInv / 2.5 * (1.0 + Math.signum(Math.max(0.75 - triggEntitySunk, 0.0)) * 1.5), 0.0) + weightArmor;
				
				// Makes the entity fall/sink faster
				entity.motionY -= Math.min(weight, 2000.0 * Math.max((triggEntitySunk - 0.25) * 5.0, 1.0)) / 300000.0;
			}
		}
		
		// If the entity is marked as stuck, AND the user wants to use the custom boot calculations...
		if (isStuck && ConfigHandler.useCustomBootCalc) {
			boolean hasBoots = false; // Is the entity wearing boots?
			boolean bootsFloat = false; // Do the boots (the entity is wearing) float?
			
			// If the entity's boot slot is NOT null...
			if (((EntityPlayer)entity).getCurrentArmor(0) != null) {
				
				// ...AND the player entity's boot slot has Wading Boots...
				if (((EntityPlayer)entity).getCurrentArmor(0).getItem() == QSAND_Items.bootsWading) {
					bootsFloat = true; // Then the boots can float
				}
				
				hasBoots = true; // The entity is wearing boots
			}
			
			// If the entity has sunk less than 1.3 blocks into this one...
			if (triggEntitySunk < 1.3) {
				bootsFloat = false; // Then the boots don't float
			}
			
			// If the entity is wearing boots, AND the entity has sunk less than 1.475 blocks into this one, AND the boot don't float...
			if (hasBoots && triggEntitySunk < 1.475 && !bootsFloat) {
				final boolean wasOnGround = entity.onGround; // Was the entity on the ground previously?
				
				entity.onGround = (false | entity.isCollidedVertically); // If the entity is colliding with something on the Y axis, then the entity is marked as on the ground
				
				// If the remainder of the total world time divided by an equation equals 0...
				if (entity.worldObj.getTotalWorldTime() % Math.max(1.0 + Math.floor(20.0 / Math.max(triggEntitySunk * 5.0, 0.01)), 1.0) == 0.0) {
					entity.onGround = wasOnGround; // Reverts any changes made
				}
				
				// If the world is hosted on a server...
				if (entity.worldObj.isRemote) {
					
					// Retrieves and stores the custom player properties for managing the stuck effect of quicksand
					final PlayerStuckEffectManager playerProperties = PlayerStuckEffectManager.get((EntityLivingBase)entity);
					
					int stuckLevel = -1; // Creates a new variable to hold the new stuck level to assign
					
					// If the player has custom player properties for managing the stuck effect...
					if (playerProperties != null) {
						stuckLevel = playerProperties.getLevel(); // Gets the stored stuck level of the stuck effect
					}
					
					// Sets a new stuck level into the stuck effect of the player
					setStuckEffect((EntityLivingBase)entity, Math.max(Math.max(Math.min((int)Math.floor(Math.pow(10.0 * Math.max(1.5 - triggEntitySunk, 0.0), 2.0)) + stuckLevel, 255), 5), stuckLevel));
				}
			}
		}
		
		// If the player is holding down the jump key...
		if (isJumpKeyDown((EntityPlayer)entity)) {
			entity.onGround = false; // Mark the player as not on the ground
		}
	}
	
	// Is the player pressing down the jump key?
	public static boolean isJumpKeyDown(final EntityPlayer player) {
		
		// If the code is executing server-side, AND the current player is the player...
		if (player.worldObj.isRemote && Minecraft.getMinecraft().thePlayer == player) {
			final GameSettings gameSettings = Minecraft.getMinecraft().gameSettings; // The game settings of the player
			return GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump); // Returns true if the player is pressing down the key binded to their jump key
		}
		
		return false; // In all other cases, return false
	}
	
	// Is the player pressing down the crouch key?
	public static boolean isCrouchKeyDown(final EntityPlayer player) {
		
		// If the code is executing server-side, AND the current player is the player...
		if (player.worldObj.isRemote && Minecraft.getMinecraft().thePlayer == player) {
			final GameSettings gameSettings = Minecraft.getMinecraft().gameSettings; // The game settings of the player
			return GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak); // Returns true if the player is pressing down the key binded to their sneak key
		}
		
		return false; // In all other cases, return false
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
	
	// Obtains the entity from their UUID
	public static Entity getEntityByUUID(final World world, final Entity entity, final UUID uuid, final double radius) {
        final AxisAlignedBB boundingBox = AxisAlignedBB.fromBounds(entity.posX - radius, entity.posY - radius, entity.posZ - radius, entity.posX + radius, entity.posY + radius, entity.posZ + radius);
        
        for (final Object certainEntity : world.getEntitiesWithinAABBExcludingEntity(entity, boundingBox)) {
            
        	if (uuid.equals(((Entity)certainEntity).getUniqueID())) {
                return (Entity)certainEntity;
            }
        }
        return null;
    }
	
	// Spawns in a mud tentacle
	public static void spawnMudTentacles(final World world, final Entity entity, final int blockPosX, final int blockPosY, final int blockPosZ, final Block block, final int metadata, final int rate, final int chance) {
		
		// If the user has enabled mud/quicksand tentacles,
		//    AND the code is NOT executing server-side,
		//    AND the entity is NOT null,
		//    AND the entity is a living base,
		//    AND the width of the entity is less than 1.75 blocks,
		//    AND the entity is not colliding with anything along the Y axis,
		//    AND the remainder of the total world time divided by the spawn rate equals 0,
		//    AND the next integer in the world's random number generator sequence equals 0 (1/chance chance),
		//    AND the tentacles can be spawned in this block...
		if (ConfigHandler.useMudTentacles
				&& !world.isRemote
				&& entity != null
				&& entity instanceof EntityLivingBase
				&& entity.width < 1.75
				&& entity.isCollidedVertically
				&& world.getTotalWorldTime() % rate == 0L
				&& world.rand.nextInt(chance) == 0
				&& canBeMudTentacles(world, blockPosX, blockPosY, blockPosZ, block, metadata)) {
			
			// Obtain the weak & slow potion effects on the player
			// If there are none, it is set to null
			final PotionEffect effectWeak = ((EntityLivingBase)entity).getActivePotionEffect(Potion.weakness);
			final PotionEffect effectSlow = ((EntityLivingBase)entity).getActivePotionEffect(Potion.digSlowdown);
			
			int effectWeakLevel = -1; // The amplification level of the potion effect
			int effectSlowLevel = -1; // The amplification level of the potion effect
			
			// If the weak effect is NOT null...
			if (effectWeak != null) {
				effectWeakLevel = effectWeak.getAmplifier(); // Obtain the amplification level on the player for this potion effect
			}
			
			// If the slow effect is NOT null...
			if (effectSlow != null) {
				effectSlowLevel = effectSlow.getAmplifier(); // Obtain the amplification level on the player for this potion effect
			}
			
			// If the weak effect is less than or equal to -1, OR the slow effect is less than or equal to 2...
			if (effectWeakLevel <= -1 || effectSlowLevel <= 2) {
				
				// Spawn a mud/quicksand tentacle
				world.spawnEntityInWorld((Entity)new TentaclesMud(world, entity.posX, Math.min(entity.posY + 0.5, blockPosY + 1), entity.posZ, (Entity)entity, blockPosY + 1, 0));
			}
		}
	}
	
	// Handler for mud tentacles
	public static void handleMudTentacles(final World world, final Entity entity, final int blockPosX, final int blockPosY, final int blockPosZ, final Block block, final int metadata) {
		
		// If the user has enabled mud tentacles,
		//    AND the code is NOT executing server-side,
		//    AND the entity is NOT null,
		//    AND the entity is an item,
		//    AND the item is food,
		//    AND the food item has NOT collided on the y axis,
		//    AND wolves like this food item,
		//    AND the remainder of the total world time divided by 64 equals 0,
		//    AND the next integer in the world's random number generator sequence equals 0 (1/5 chance),
		//    AND the block can spawn mud tentacles...
		if(ConfigHandler.useMudTentacles
				&& !world.isRemote
				&& entity != null
				&& entity instanceof EntityItem
				&& ((EntityItem)entity).getEntityItem().getItem() instanceof ItemFood
				&& !entity.isCollidedVertically
				&& ((ItemFood)((EntityItem)entity).getEntityItem().getItem()).isWolfsFavoriteMeat()
				&& world.getTotalWorldTime() % 64L == 0L
				&& world.rand.nextInt(5) == 0
				&& canBeMudTentacles(world, blockPosX, blockPosY, blockPosZ, block, metadata)) {
			// Needless to say, this is a very rare occurrence
			
			entity.attackEntityFrom(DamageSource.generic, 1000.0f); // Deals 1000 HP of damage to the entity
			
			world.spawnEntityInWorld((Entity)new EntityLongStick(world, entity.posX, (double)(blockPosY + 1), entity.posZ, (Entity)null, blockPosX, blockPosY, blockPosZ));
		}
	}
	
	public static boolean canBeMudTentacles(final World world, final int blockPosX, final int blockPosY, final int blockPosZ, final Block block, final int metadata) {
		
		// If the user has enabled mud tentacles, AND the code is NOT executing server-side...
		if (ConfigHandler.useMudTentacles && !world.isRemote) {
			
			// ...AND the world's dimension ID does NOT equal 0...
			if (world.provider.getDimensionId() != 0) {
				return false; // Only spawn mud tentacles in the Overworld
			}
			
			// Creates a block position object that holds the position of this block
			BlockPos blockPos = new BlockPos(blockPosX, blockPosY, blockPosZ);
			
			// If the block above this block is NOT the same as this block, AND all other adjacent blocks are the same as this block
			if (world.getBlockState(blockPos.up(1)) != block
					&& world.getBlockState(blockPos.down(1)) == block
					&& world.getBlockState(blockPos.add(-1, 0, 0)) == block
					&& world.getBlockState(blockPos.add(1, 0, 0)) == block
					&& world.getBlockState(blockPos.add(0, 0, -1)) == block
					&& world.getBlockState(blockPos.add(0, 0, 1)) == block) {
				
				// Obtains the current chunk
				final Chunk currentChunk = world.getChunkFromBlockCoords(blockPos);
				
				int spawnChance = 10; // Base spawn chance of mud tentacles being able to spawn. Smaller equals greater spawn chance
				
				if (block == QSAND_Blocks.quicksandJungle) {
					spawnChance = 10; // 1 in 10 spawn chance
				} else if (block == QSAND_Blocks.mud) {
					spawnChance = 40; // 1 in 40 spawn chance
				} else {
					spawnChance = 20; // 1 in 20 spawn chance
				}
				
				// TODO: Custom World Generation equation
				if (currentChunk.getRandomWithSeed(987654321L).nextInt(spawnChance) == 0) {
					return true; // Mud tentacles can spawn in this block
				}
			}
		}
		
		return false; // Otherwise, mud tentacles can NOT spawn in this block
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
            for (int i = 0; i < 4 + world.rand.nextInt(6); i++) {
            	
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
	
	// Spawns Bubbles for Long Stick
	public static void spawnStickBubble(final World world, final double x, final double y, final double z, final Block block, boolean useMetadata, final boolean deep) {
        
		// If the code is NOT executing server-side...
		if (!world.isRemote) {
            return; // Return early
        }
		
        int random = 4 + world.rand.nextInt(6); // A random number between 4 and 9
        
        // If the entity is NOT deep in the block...
        if (!deep) {
        	random = 2 + world.rand.nextInt(3); // Replaces the previous random number with a random number between 2 and 4
        }
        
        // For every random number, spawn a bubble...
        for (int i = 0; i < random; ++i) {
        	int blockMetadata = 0;
        	
        	// Finds the bubble coordinates to spawn the bubble at
            final double bubblePosX = x + world.rand.nextFloat() * 1.0f - 0.5;
            final double bubblePosZ = z + world.rand.nextFloat() * 1.0f - 0.5;
            final double bubblePosY = y - 1.0;
            
            // Finds the block coordinates the bubble will spawn in
            final int blockPosX = (int)Math.floor(bubblePosX);
            final int blockPosY = (int)Math.floor(bubblePosY);
            final int blockPosZ = (int)Math.floor(bubblePosZ);
            
            // If the bubble will spawn in a different type of block than the block that triggered the bubble to spawn...
            if (world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)).getBlock() != block) {
                return; // Don't spawn the bubble in a different type of block
            }
            
            // If the metadata from the block should be used...
            if (useMetadata) {
            	blockMetadata = world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)));
            }
            
            final float size = 1.25f - world.rand.nextFloat() * 1.0f; // The size of the bubble
            final int time = (int)Math.floor((1000 + world.rand.nextInt(500)) * size); // How long the bubble should live for
            
            // Spawns the bubble
            spawnBubbleDelay(world, bubblePosX, blockPosY + surfaceY(block), bubblePosZ, block, blockMetadata, size, time, i * 200 + world.rand.nextInt(10) * 100);
        }
    }
	
	// Obtains the value of the custom drown air datawatcher
	public static int getCustomDrownAir(final Entity entity) {
		
		// Tries to run this code
        try {
        	
        	System.out.println(entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirPlayersDW));
        	
        	// If the entity is a player...
            if (entity instanceof EntityPlayer) {
            	
            	// Returns the datawatcher for custom drowning for players
                return entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirPlayersDW);
            }
            
            // If the entity is NOT a player, it returns the datawatcher for custom drowning for entities
            return entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirEntitiesDW);
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
            	
            	// Changes the value of the datawatcher for custom drowning for players
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirPlayersDW, (Object)value);
            } else {
            	
            	// If the entity is NOT a player, it changes the value of the datawatcher for custom drowning for entities
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirEntitiesDW, (Object)value);
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
            	
            	// Adds the modifier to the datawatcher for custom drowning for players
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirPlayersDW, (Object)(entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirPlayersDW) + modifier));
            } else {
            	
            	// If the entity is NOT a player, it adds the modifier to the datawatcher for custom drowning for entities
                entity.getDataWatcher().updateObject(ConfigHandler.customDrownAirEntitiesDW, (Object)(entity.getDataWatcher().getWatchableObjectInt(ConfigHandler.customDrownAirEntitiesDW) + modifier));
            }
        } catch (Exception ignored) {}
        // If it crashes, don't crash and ignore the error
    }
	
    // Checks if the entity should be marked as drowning
	public static boolean isDrowning(final Entity entity) {
		
		// If the entity is a player...
        if (entity instanceof EntityPlayer) {
        	
        	// ...AND the user has disabled realistic drowning for players...
            if (!ConfigHandler.useCustomDrownPlayers) {
                return true; // The user disabled this. Return early
            }
            
            // If the entity is NOT a player...
            //    ...AND the user has disabled realistic drowning for entities...
        } else if (!ConfigHandler.useCustomDrownEntities) {
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
	
	// If the child is the child of the parent in question
	public static boolean isChildOf(final Class parent, final Class child) {
        return child.getSuperclass() == parent || (child.getSuperclass() != Entity.class && isChildOf(parent, child.getSuperclass()));
    }
	
	// Sets the stuck effect of the player
	public static void setStuckEffect(final EntityLivingBase entity, final int level) {
		
		// If the entity has a stuck level...
		if (PlayerStuckEffectManager.get(entity) != null) {
			PlayerStuckEffectManager.get(entity).setStuckLevel(level); // Set the new stuck level
		}
	}
}
