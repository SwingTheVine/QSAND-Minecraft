package com.SwingTheVine.QSAND.fluid;

import java.util.List;
import java.util.Random;

import com.SwingTheVine.QSAND.block.IMetaBlockName;
import com.SwingTheVine.QSAND.block.SinkingBlockFluidClassic;
import com.SwingTheVine.QSAND.client.player.PlayerMudManager;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeMud;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;
import com.SwingTheVine.QSAND.util.BeaconHandler;
import com.SwingTheVine.QSAND.util.ConfigHandler;
import com.SwingTheVine.QSAND.util.QuicksandManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FluidBog extends SinkingBlockFluidClassic implements IMetaBlockName {
	
	private static final String[] types = {"0"}; // Names of all metadata variants
	private static final float[] sinkTypes = {1.00F}; // The maximum sink level for each metadata variant
	private static final boolean useOneTexture = true; // Should all metadata variants use the same texture?
	//public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, Integer.valueOf(types.length-1)); // Creates a metadata value for every "types" metadata value
	public static final Material materialBog = new MaterialLiquid(MapColor.grassColor);
	public static final int typeColor = 4538917;
	public static final int maxOpacity = 2000;
	public static final int lastOpacity = 500;
	public static final int incOpacity = 50;
	private BeaconHandler beacon = new BeaconHandler(false); // Constructs a beacon handler. Enabled if "true" passed in

	public FluidBog(Fluid fluid, Material material) {
		super(fluid, material);
		this.setResistance(1000.0f);
		this.setQuantaPerBlock(2);
		this.quantaPerBlockFloat = 1.75f;
		this.setTickRandomly(true);
		
		
		// Makes the block opaque if the user desires
		int opacity = (ConfigHandler.makeBlocksOpaque)
				? 32 : 3;
		this.setLightOpacity(opacity); // Sets the opacity level of the block
	}
	
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		
        if (state.getBlock().getMetaFromState(state) == 0 && world.getBlockState(pos).getBlock() != this) {
            
        	final int chckRes = this.checkFusion(world, pos, world.rand);
            
        	if (chckRes != 0) {
                return;
            }
        	
            if (world.getBlockState(pos).getBlock().getMaterial().isLiquid()) {
                
            	final int denToRep = getDensity((IBlockAccess)world, pos);
                
            	if (denToRep >= this.density) {
                    return;
                }
            }
            
            if (!world.isAirBlock(pos)) {
                world.setBlockState(pos, state, 1);
            }
        }
    }
	
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return state.getBlock().getMetaFromState(state) == 0;
    }
    
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos) {
        return -1.0f;
    }
    
    public int getMaxRenderHeightMeta() {
        return -1;
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity triggeringEntity) {
    	final double triggEntityPosY = triggeringEntity.posY; // Triggering entity's Y position
		final double triggEntityPrevPosY = triggeringEntity.prevPosY; // Triggering entity's previous Y position
		final double triggEntityVelY = triggeringEntity.motionY; // Triggering entity's Y velocity
		
		// Triggering entity's distance sunk into the block.
		// First statement is when the entity is a player.
		// Second statement is when the entity is NOT a player
		double triggEntitySunk_kof1 = (triggeringEntity instanceof EntityPlayer)
				? (pos.getY() - triggEntityPosY + 1.0)
				: (pos.getY() - triggEntityPosY - 0.5); 

		// Triggering entity's distance previously sunk into the block
		// First statement is when the entity is a player.
		// Second statement is when the entity is NOT a player
		double triggEntityPrevSunk_kof2 = (triggeringEntity instanceof EntityPlayer)
				? Math.max((pos.getY() - triggEntityPrevPosY + 1.0), 0.0)
				: Math.max((pos.getY() - triggEntityPrevPosY - 0.5), 0.0); 
					
		double triggEntitySunkMod_kof1m = Math.max(triggEntitySunk_kof1, 0.0); // A modified version of trigger entity sunk
		final int blockMetadata = state.getBlock().getMetaFromState(state); // Obtains this block's variant/metadata
		
		// If the triggering entity is living (as opposed to a death animation)...
		if (triggeringEntity instanceof EntityLivingBase) {
			boolean triggEntityAffected = true; // Should the triggering entity be affected by this block?
			boolean triggEntityJumping = false; // Is the triggering entity jumping?
			boolean triggEntityMoving = false; // Is the triggering entity moving?
			boolean triggEntitySplashing = false; // Is the triggering entity splashing?
			boolean triggEntityRotating = false; // Is the triggering entity rotating?
			boolean triggEntityHasBoots = false; // Is the triggering entity wearing boots?
			boolean triggEntityBootsFloat = false; // Can the boots the triggering entity is wearing float in quicksand?
			final float blockMetadataBumped = 1.0f; // TODO: Mud Changed
			double triggEntityMovingDistance_movDis = 1.0; // The distance the entity moves along the X/Z plane
			double forceFlavorEvent_movCof = 16.0; // Forces the block to attempt to spawn a flavor event (either bubbles, sound, or both)
			double movementPunish_mofKofDiv = 1.0; // Punishes the entity for moving. Makes them sink faster.
			
			// EQUATION BEACON. 1st complex
			// Increases the punishment value the LESS the entity is sunk in the block
			// Starts at a specified number not exceeding 145 and hits 0 when player sunk 1.2 blocks.
			// Then, mr_blackgoo goes back up towards 145.
			// It is parabolic in nature.
			// https://www.desmos.com/calculator/wgkirt9ra1
			final int mr_blackgoo = (int)Math.min(5.0 + Math.floor(Math.max(0.0, Math.pow(blockMetadataBumped * 2.0f * (1.5 - triggEntitySunkMod_kof1m), 2.0))), 145.0);
			
			// If the triggering entity is a Muddy Slime...
			if (triggeringEntity instanceof EntitySlimeMud) {
				triggEntityAffected = false; // The triggering entity is marked as NOT affected by this block
			}
			
			// Run submerged code if the triggering entity is submerged
			this.runSubmergedChecks(triggeringEntity);
			
			// If the triggering entity is a player...
			if (triggeringEntity instanceof EntityPlayer) {
				
				// ...AND the player's boot slot is not null, AND the player's boot slot contains Wading Boots...
				if (((EntityPlayer)triggeringEntity).getCurrentArmor(0) != null && ((EntityPlayer)triggeringEntity).getCurrentArmor(0).getItem() == QSAND_Items.bootsWading) {
					triggEntityHasBoots = true;
					triggEntityBootsFloat = true;
				}
				
				if (triggeringEntity instanceof EntityPlayer) {
					this.checkPlayerMuddy((EntityPlayer)triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), world);
				}
			}
			
			// If the entity moves downwards with a high velocity, they are marked as splashing.
			triggEntitySplashing =
					(triggeringEntity.motionY < -0.1) // TODO: Mud Changed. Simplified equation
					? true : false;
			
			// If the entity moves upwards with a high velocity, they are marked as jumping.
			triggEntityJumping = 
					(triggEntityPosY - triggEntityPrevPosY > 0.2)
					? true : false;
			
			// If the entity is NOT a player, AND the entity has moved their camera along the Yaw axis...
			// OR if the entity is a player, and this is multiplayer, AND the server instance has detected Yaw axis movement...
			triggEntityRotating = 
					((!(triggeringEntity instanceof EntityPlayer) && ((EntityLivingBase)triggeringEntity).prevRotationYaw != ((EntityLivingBase)triggeringEntity).rotationYaw)
							|| ((triggeringEntity instanceof EntityPlayer) && world.isRemote && Math.abs(ConfigHandler.serverInstancePreRenderYaw - ConfigHandler.serverInstanceRenderYaw) > 10.0))
					? true : false;
			
			// If the entity is marked as rotating, OR the entity has moved farther than 0.001 blocks along the X/Z axis...
			if (triggEntityRotating || Math.abs(triggeringEntity.prevPosX - triggeringEntity.posX) > 0.0015 || Math.abs(triggeringEntity.prevPosZ - triggeringEntity.posZ) > 0.0015) { // TODO: Mud Changed. 0.001 -> 0.0015
				
				triggEntityMoving = true; // The entity is moving
				
				// Finds the hypotenuse of the distance traveled.
				// This is the actual distance traveled on a radical plane
				triggEntityMovingDistance_movDis = Math.pow(Math.pow(triggeringEntity.prevPosX - triggeringEntity.posX, 2.0) + Math.pow(triggeringEntity.prevPosZ - triggeringEntity.posZ,  2.0), 0.5);
				
				// Exponentially increases the chance of a bubble spawn event the more the entity moves forwards.
				// Outputs as a number between 16 and 32. Lower number equals higher chance
				// https://www.desmos.com/calculator/rxdyif2tis
				forceFlavorEvent_movCof = Math.max(Math.min(32.0 / (1.0 + (triggEntityMovingDistance_movDis * 10.0)), 32.0), 16.0);
				
				// Base punishment for punishing the player for moving.
				// This value will be modified later
				movementPunish_mofKofDiv = 1.0 + triggEntityMovingDistance_movDis / 2.0; // TODO: Mud Changed. * 25.0 -> / 2.0
				
				// If the entity has sunk less than 0.9 blocks,
				//    AND the entity HAS sunk into the block,
				//    AND the entity is marked as rotating...
				if (triggEntitySunkMod_kof1m < 0.9 && triggEntitySunkMod_kof1m != 0.0 && triggEntityRotating) {
					movementPunish_mofKofDiv += mr_blackgoo * 0.005; // Amplify the punishment value by 0.005
				}
			}
			
			// TODO: Mud Changed. Removed not wearing boots slowing player
			
			// TODO: Mud Changed. Metadata 0 statement removed
			
			// TODO: Mud Changed. Removed Mud Slime statement
			
			// TODO: Mud Changed. Removed not player, not affected
			
			// STATEMENT BEACON 1
            // If the remainder of the current world time divided by 128 is zero,
            //     OR the entity is marked as moving, AND the remainder of the current world time divided by a number that decreases the more the entity moves is zero,
            //     OR the entity is marked as jumping, AND the remainder of the current world time divided by 8 is zero,
            //     OR the entity is marked as splashing...
			if (world.getTotalWorldTime() % 128L == 0L
					|| (triggEntityMoving && world.getTotalWorldTime() % Math.max((int)Math.floor(forceFlavorEvent_movCof), 1) == 0L)
					|| (triggEntityJumping && world.getTotalWorldTime() % 8L == 0L)
					|| triggEntitySplashing) {
				// This is the part that makes most of the bubbles
	            // This is done either at set intervals (based on world time), or faster set intervals when the player is moving
				
				// ...AND if the triggering entity is splashing...
				if (triggEntitySplashing && triggEntityPrevSunk_kof2 > 1.5) { // TODO: Mud Changed. logic changed
					
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "game.player.swim", 0.15f, world.rand.nextFloat() * 0.25f + 0.1f); // Play sound
					
					// If the entity is a player, AND the number in the world's random number generator sequence is 0 (1/3 chance)...
					if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(3) == 0) {
						//QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), (SinkingBlock)(Block)this, false);
						//QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), (SinkingBlock)(Block)this, false);
					}
				}
				
				// If the entity is marked as NOT splashing, AND the entity is marked as moving...
				if (!triggEntitySplashing && triggEntityMoving) {
					
					// ...AND the number in the world's random number generator sequence equals 0 (1/2 chance)...
                    if (world.rand.nextInt(2) == 0) {
                        world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "game.player.swim", 0.15f, world.rand.nextFloat() * 0.15f + 0.1f); // TODO: Mud Changed Play sound
                    }
                    
                    // ...AND the number in the world's random number generator sequence equals 0 (1/5 chance)...
                    if (world.rand.nextInt(5) == 0) { // TODO: Mud Changed. 7 -> 5
                    	//QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), (SinkingBlock)(Block)this, false);
                    }
				}
				
				// If the entity is marked as jumping...
				if (triggEntityJumping) {
					
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.slime.attack", 0.25f, world.rand.nextFloat() * 0.25f + 0.25f); // Play sound
					
					// If the entity is a player, AND the number in the world's random number generator sequence equals 0 (1/5 chance)...
                    if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(2) == 0) { // TODO: Mud Changed. 5 -> 2
                    	//QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), (SinkingBlock)(Block)this, false);
                    	//QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), (SinkingBlock)(Block)this, false);
                    	//QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), (SinkingBlock)(Block)this, false);
                    }
				}
				
				// If the entity is marked as NOT jumping,
				//    AND the entity is marked as NOT moving,
				//    AND the entity is marked as NOT splashing,
				//    AND the number in the world's random number generator sequence equals 0 (1/5 chance)...
                if (!triggEntityJumping && !triggEntityMoving && !triggEntitySplashing && world.rand.nextInt(5) == 0) {
                	
                	// ...AND the number in the world's random number generator equals 0 (1/5 chance)...
                    if (world.rand.nextInt(5) == 0) {
                        world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "liquid.water", 0.5f, world.rand.nextFloat() * 0.15f + 0.1f); // Play sound
                    }
                    else {
                        world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f); // Plaay sound
                    }
                }
			}
			
			// TODO: Mud Changed. Moved down from above
			// If the variant of the block is greater than 2 (Bottomless Mud),
            //    AND the remainder of the total world time divided by 16 is 0,
            //    AND the number in the world's random number generator sequence equals 0 (1/10 chance)...
            if (!triggEntityBootsFloat && world.getTotalWorldTime() % 16L == 0L && world.rand.nextInt(10) == 0) {
            	//QuicksandManager.spawnBodyBubble(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), (SinkingBlock)(Block)this, false);
            }
                
            triggeringEntity.motionX = 0.0; // Make the entity stop moving
            triggeringEntity.motionZ = 0.0; // Make the entity stop moving
            
            // If the triggering entity has sunk less than 1.2 blocks into this block...
            if (triggEntitySunk_kof1 < 1.2) { // TODO: Mud Changed. 1.3 -> 1.2
            	triggEntityBootsFloat = false; // The boots don't float
            }
            
            // If the entity's velocity is greater than -0.1...
            if (triggeringEntity.motionY > -0.1) {
                // This only happens when the entity is NOT moving downwards very fast
            	beacon.logBeacon("MotionY", "2.1", triggeringEntity.motionY);
            	triggeringEntity.motionY = 0.0; // Make the entity stop moving
            } else {
            	beacon.logBeacon("MotionY", "2.2", triggeringEntity.motionY);
            	triggeringEntity.motionY /= 2.0; // Slow the downwards motion of the entity by 50% TODO: Mud Changed. 1.5 -> 2.0
            }
            
            // STATEMENT BEACON 2
            // If the block above this one is NOT the same as this block,
            //    OR the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
            if ((world.getBlockState(pos.up(1)) != this && world.getBlockState(pos.up(1)) != QSAND_Blocks.moss)
            		|| ((world.getBlockState(pos.up(1)) == this || world.getBlockState(pos.up(1)) == this)
            				&& (world.getBlockState(pos.up(2)) == this || world.getBlockState(pos.up(2)) == QSAND_Blocks.moss))) { // TODO: Mud Changed. Added moss checks

                // If the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
                if ((world.getBlockState(pos.up(1)) == this || world.getBlockState(pos.up(1)) == QSAND_Blocks.moss)
                		&& (world.getBlockState(pos.up(2)) == this) || world.getBlockState(pos.up(2)) == QSAND_Blocks.moss) { // TODO: Mud Changed. Added moss checks
                	triggEntitySunk_kof1 = 0.001; // Mark the distance the entity has sunk as 0.001 blocks
                }
                
                // TODO: Mud Changed. Removed sunk more than 0.9 blocks statement
                
                double sinkRateMod_mys = 0.0; // Modifies how fast the entity sinks into the block. Units are blocks. Negative is down.
                
                // If the entity is NOT a player...
                if (!(triggeringEntity instanceof EntityPlayer)) {
                    sinkRateMod_mys = 0.0; // Makes the non-player entity sink faster when negative
                }
                
                // TODO: Mud Change. Removed jumping and suction statement
                
                // If the entity has sunk greater than or equal to 1.2 blocks...
                if (triggEntitySunk_kof1 >= 1.2) {
                	
                	if (triggEntitySunk_kof1 < 1.35) { // TODO: Mud Changed. Added statement
                		
                		// ...AND the entity is NOT marked as splashing...
                    	if (!triggEntitySplashing) {

    	                	// ...AND the entity's boots float in this block...
    	                	if (triggEntityBootsFloat) {
    	                		// TODO: Mud Changed. Removed on ground
    	                		
    	                		// If the entity has sunk more than 1.25 blocks into this block...
    	                		if (triggEntitySunk_kof1 > 1.25) {
    	                			triggeringEntity.motionY += 0.085; // Adds a modifier to the entity's Y velocity
    	                		} else {
    	                			triggeringEntity.motionY += 0.1; // Adds a modifier to the entity's Y velocity
    	                		}
    	                	} else {
    	                		
    	                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
    	                		// ...then the value is the first one. Otherwise, it is the second one
    	                		// This triggers more frequently the higher the metadata value is. (1/7 to 1/4 chance)
    	                		double thicknessLower = 
    	                				(world.getTotalWorldTime() % Math.max(8.0f - blockMetadataBumped, 1.0f) == 0.0f)
    	                				? 0.07485 : 0.075;
    	                		
    	                		// EQUATION BEACON. 2nd complex
    	                		// Makes the entity sink extremely fast
    	                		// This equation is really complicated.
    	                		// Basically, "thicknessLower" is the maximum value, and the further the player has sunk, the closer it approaches 0.
    	                		// After a while (happens sooner the higher the punishment value is), the rate it approaches 0 changes and becomes faster
    	                		// https://www.desmos.com/calculator/klpmbgz7je
    	                		beacon.logBeacon("MotionY", "6"); // TODO: Mud Changed. Major change. Changed equation.
    	                		triggeringEntity.motionY += thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6 * blockMetadataBumped) * 17.5, 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
    	                		beacon.logBeacon("Equation", "2", thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6 * blockMetadataBumped) * 17.5, 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
    	                	}
    	                	
	                		// TODO: Mud Changed. Moved from else statement above
	                		triggeringEntity.onGround = false; // The entity is marked as NOT on the ground
	                		
	                		// TODO: Mud Changed. Added player statement
	                		if (triggeringEntity instanceof EntityPlayer) {

		                		// If the number in the world's random number generator sequence equals 0...
	                            if (world.getTotalWorldTime() % Math.max(1.0 + Math.floor(Math.max(triggEntitySunk_kof1 * 20.0 - blockMetadata / 10, 0.0)), 1.0) == 0.0) { // TODO: Mud Changed. Major equation change
	                            	// Happens more frequently the less the entity is sunk in the block (1/24 to 1/56 chance)
	                                triggeringEntity.setInWeb(); // The entity is marked as in a web
	                            }
	                		} else {
	                			final double mw_kof = 1 + mr_blackgoo / 10;
                                triggeringEntity.setPosition(triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mw_kof, triggEntityPosY, triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mw_kof);
	                		}
                            
                            // If the next integer in the world's random number generator sequence equals 0...
                            // TODO: Mud Changed. 2.0 -> 5.0    removed blockMetadataBumped
                            if (world.rand.nextInt(Math.max((int)Math.floor(5.0f - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                            	// Happens more frequently the more the entity is sunk in the block.
                            	// However, this only triggers when the entity is sunk more than 1.2 blocks
                            	// That means this only happens on metadata 4 (Bottomless Mud).
                            	// 1.40-1.58 blocks has a 1/4 chance
                            	// 1.59-2.08 blocks has a 1/3 chance
                            	// 2.09-2.51 blocks has a 1/2 chance
                            	// 2.52-2.80 blocks has a 1/1 chance
                            	
                                triggeringEntity.onGround = true; // The entity is marked as on the ground
                            }

	                		triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
                    	}
                	} else if (!triggEntitySplashing) { // TODO: Mud Changed. Added else if statement
                		triggeringEntity.motionY = 0.0;
                	}
                } else {
                	
                	// If the entity is moving upwards, AND the number in the world's random number generator sequence modified by the modified distance sunk by the triggering entity equals 0, AND the entity has been marked for suction...
                	if (triggEntityPosY - triggEntityPrevPosY > 0.001 && world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunkMod_kof1m * 7.5), 15)) == 0 && QuicksandManager.suctionWorldCheck(triggeringEntity, world, triggEntityVelY)) { // TODO: Mud Changed. 3.0 -> 7.5     10 -> 15
                		triggeringEntity.motionY -= 0.025 * blockMetadataBumped * (Math.min(0.75, triggEntitySunkMod_kof1m) + 1.0); // The entity's Y velocity is modified by the modified distance sunk by the triggering entity TODO: Mud Changed. 0.035 -> 0.025
                		world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.magmacube.jump", 0.25f, 0.25f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f, false); // Play sound
                	}
                	
                	movementPunish_mofKofDiv = 1.0; // TODO: Mud Changed. Added
                	
                	// If the entity has sunk greater than 0.9 blocks...
                	if (triggEntitySunk_kof1 >= 0.9) {
                		// Note: will never be greater than or equal to 1.2
                		
                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
                		// ...then the value is the first one. Otherwise, it is the second one
                		// This triggers more frequently the higher the metadata value is. (1/7 to 1/4 chance)
                		double thicknessLower = 
                				(world.getTotalWorldTime() % Math.max(8.0f - blockMetadataBumped * 2, 1.0f) == 0.0f) // TODO: Mud Changed. Multiplied blockMetadataBumped by 2
                				? 0.07485 : 0.075 + sinkRateMod_mys;
                		
                		// EQUATION BEACON 3. 2nd complex
                		triggeringEntity.motionY += thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6 * blockMetadataBumped) * 17.5, 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0); // TODO: Mud Changed. Same major change as before
                		beacon.logBeacon("Equation", "3", thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6 * blockMetadataBumped) * 17.5, 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
                		
                		QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, mr_blackgoo); // Makes the entity stuck
                		
                		if (!triggEntitySplashing) { // TODO: Mud Changed. Added statement
	                		
	                		// TODO: Mud Changed. Brought statement out of player statement
	                		// ...AND the number in the world's random number generator sequence equals 0...
	                        if (world.rand.nextInt(Math.max((int)Math.floor(5.0f - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) { // TODO: Mud Changed. Removed bumpedMetadata, 4.0 -> 5.0
	                        	// Happens more frequently the more the entity is sunk in the block.
	                        	// However, this only triggers when the entity is sunk more than 1.2 blocks
	                        	// That means this only happens on metadata 4 (Bottomless Mud).
	                        	// 0.90-1.00 blocks is 1/7 chance
	                        	// 1.01-1.20 blocks is 1/6 chance
	                        	
	                        	triggeringEntity.onGround = true; // Marks the entity as on the ground
	                        }
	                		
	                		// If the entity is a player...
	                		if (triggeringEntity instanceof EntityPlayer) {
	                			
	                            // TODO: Mud Changed. Brought statement into player statement
	                            // If the number in the world's random number generator sequence equals 0...
	                			if (world.getTotalWorldTime() % Math.max(1.0 + Math.floor(Math.max(triggEntitySunk_kof1 * 20.0 - blockMetadata / 10, 0.0)), 1.0) == 0.0) { // TODO: Mud Changed. Major equation change
	                            	// Happens more frequently the less the entity is sunk in the block (1/2 to 1/3 chance)
	                            	
	                                triggeringEntity.setInWeb(); // The entity is marked as in a web
	                            }
	                		} else {
	                			
	                			// Sucks the entity back towards where they came from on the X/Z axis when moving.
	                			// More intense the further they sink.
	                			final double mrs_blackgoo = 1 + mr_blackgoo / 10;
	                			
	                			beacon.logBeacon("Set Position", "1", triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo + ", " + triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
	                			// Sucks the entity back towards where they came from. Only partially though
	                			triggeringEntity.setPosition(triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo, triggEntityPosY, triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
	                		}
                		}
                	} else {
                		
                		// TODO: Mud Changed. Added statement
                		// If the entity is moving upwards, AND the number in the world's random number generator sequence modified by the modified distance sunk by the triggering entity equals 0, AND the entity has been marked for suction...
                    	if (triggEntityPosY - triggEntityPrevPosY > 0.001 && world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunkMod_kof1m * 7.5), 15)) == 0 && QuicksandManager.suctionWorldCheck(triggeringEntity, world, triggEntityVelY)) { // TODO: Mud Changed. 3.0 -> 7.5     10 -> 15
                    		triggeringEntity.motionY -= 0.5; //TODO: Mud Changed. 0.035 -> 0.5
                    		world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.magmacube.jump", 0.25f, 0.25f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f, false); // Play sound
                    	}
                		
                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
                		// ...then the value is the first one. Otherwise, it is the second one
                		// This triggers more frequently the higher the metadata value is. (1/16 to 1/5 chance)
                		// This stops the entity from sinking for a tick
                		// https://www.desmos.com/calculator/0g4mgwzpmh
                		double thicknessHigher =
                				(world.getTotalWorldTime() % (int)Math.floor(Math.min(16.0, Math.max(16.0 - triggEntitySunk_kof1 * blockMetadataBumped * 3.0, 1.0))) == 0L)
                				? 1.01 : 1.05;
                		
                		// TODO: Mud Changed. Deleted parent statement instanceof player
                		if (world.rand.nextInt(Math.max((int)Math.floor(25.0f * blockMetadataBumped - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) { // TODO: Mud Changed. Major equation change
                        	// Happens more frequently the more the entity is sunk in the block.
                        	// However, this only triggers when the entity is sunk more than 1.2 blocks
                        	// Metadata 0... (Mud)
                        	// 0 has a 1/12 chance
                        	// 0.10-0.35 has a 1/11 chance
                        	// Metadata 1... (Thinnish Mud)
                        	// 0 has a 1/17 chance
                        	// 0.10-0.55 has a 1/16 chance
                        	// Metadata 2... (Deep Mud)
                        	// 0 has a 1/22 chance
                        	// 0.10-0.75 has a 1/21 chance
                        	// Metadata 3... (Bottomless Mud)
                        	// 0 has a 1/27 chance
                        	// 0.10-0.90 has a 1/26 chance
                        	
                        	triggeringEntity.onGround = true; // The entity is marked as on the ground
                        }
                        
                        // If the entity is truly sinking...
                        if (QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunk_kof1)) {
                        	QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, mr_blackgoo);
                        }
                        
                        triggeringEntity.fallDistance = 0.0f; // Resets the accumulated fall distance to 0
                        
                        // If the entity has sunk greater than or equal to 0.5 blocks...
                        if (triggEntitySunk_kof1 >= 0.5) {
                        	
                        	// ...AND the number in the world's random number generation sequence equals 0...
                            if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 5.0 / blockMetadataBumped, 1.0))) == 0) {
                            	// Happens more frequently the less the entity is sunk in the block
                            	// 1/1 to 1/2 chance (Bottomless Mud)
                            	// 1/1 to 1/3 chance (Deep Mud)
                            	// 1/2 to 1/4 chance (Thinnish Mud)
                            	// 1/5 to 1/9 chance (Mud)
                            	
                                triggeringEntity.setInWeb(); // The entity is marked as in a web

                                // If the entity is NOT marked as moving...
                                if (!triggEntityMoving) {
                                    beacon.logBeacon("MotionY", "8.1");
                                    // Bumps the entity downwards slightly.
                                    // 0.0725 (plus a modifer) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                    triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher;
                                }
                                else {
                                    beacon.logBeacon("MotionY", "8.2");
                                    // Bumps the entity downwards slightly.
                                    // 0.0725 (plus a modifier) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                    // Then, it is divided by the punishment value plus 0.025
                                    // If the punishment value is below 1.87, the entity goes upwards
                                    triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.01); // TODO: Mud Changed. 0.025 -> 0.01
                                }
                            } // ...if the entity is NOT marked as moving...
                            else if (!triggEntityMoving) {
                            	beacon.logBeacon("MotionY", "9");
                            	// Moves the entity downwards slightly.
                                // 0.075 (plus a modifer) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity goes upwards for a tick
                                triggeringEntity.motionY += (0.075 + sinkRateMod_mys) * thicknessHigher; // Unknown modifer to entity's Y velocity
                            }
                            else {
                            	beacon.logBeacon("MotionY", "10");
                            	// Bumps the entity downwards slightly.
                                // 0.075 (plus a modifier) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity goes upwards for a tick
                                // Then, it is divided by the punishment value plus 0.025
                            	// If the punishment value is below 1.87, the entity goes upwards
                                triggeringEntity.motionY += (0.075 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.01); // TODO: Mud Changed. 0.025 -> 0.01
                            }
                        } else {
                        	triggeringEntity.setInWeb();
                        	
                        	// TODO: Mud Changed. Removed is in water statement
                            
                            // If the entity is marked as not moving...
                            if (!triggEntityMoving) {
                            	beacon.logBeacon("MotionY", "11.1", (0.0725 + sinkRateMod_mys) * thicknessHigher);
                            	// Moves the entity downwards slightly.
                                // 0.0725 (plus a modifer) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher;
                            }
                            else {
                            	beacon.logBeacon("MotionY", "11.2", (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.01));
                            	// Bumps the entity downwards slightly.
                                // 0.0725 (plus a modifier) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                // Then, it is divided by the punishment value plus 0.025
                            	// If the punishment value is below 1.87, the entity goes upwards
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.01); // TODO: Mud Changed. 0.025 -> 0.01
                            }

                            // If the entity has sunk less than 0.45 blocks into this one, AND the entity is truly sinking...
                            if (triggEntitySunk_kof1 < 0.45 && QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunk_kof1)) {
                            	QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, 100); // TODO: Mud Changed. 145 -> 100
                            }
                        }
                	}
                }
                
                // If the entity is marked as in water...
                if (triggeringEntity.isInWater()) {
                    triggeringEntity.motionY /= 4.0; // TODO: Mud Changed. Replaced statement body
                }
            } else {
            	
            	// If the entity has sunk less than 0.45 blocks into this one, AND the entity is truly sinking...
                if (triggEntitySunk_kof1 < 0.5 && QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunkMod_kof1m)) {
                	QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, 100); // TODO: Mud Changed. 145 -> 100
                }
            	
            	triggeringEntity.setInWeb();
            }
            
            // Prevents the entity from continuously jumping
            QuicksandManager.antiHoldJumpScript(triggeringEntity, triggEntitySunk_kof1, true); // TODO: Mud Changed. Moved from above
            /*
            System.out.println("triggEntityMovingDistance_movDis: " + triggEntityMovingDistance_movDis);
            System.out.println("forceFlavorEvent_movCof: " + forceFlavorEvent_movCof);
            System.out.println("movementPunish_mofKofDiv: " + movementPunish_mofKofDiv);
            System.out.println("mr_blackgoo: " + mr_blackgoo);*/
		} else {
			
			if (triggEntitySunk_kof1 < 1.45) {
				triggeringEntity.setInWeb();
			}
		}
		/*System.out.println("triggEntitySunk_kof1: " + triggEntitySunk_kof1);
        System.out.println("triggEntityPrevSunk_kof2: " + triggEntityPrevSunk_kof2);
        System.out.println("triggEntitySunkMod_kof1m: " + triggEntitySunkMod_kof1m);*/
    }
    
    public int checkFusion(final World world, BlockPos pos, final Random rand) {
        int xx = pos.getX();
        int yy = pos.getY();
        int zz = pos.getZ();
        int met = 0;
        boolean isWaterNear = false;
        boolean isLavaNear = false;
        for (int re = 0; re <= 6; ++re) {
            switch (re) {
                case 0: {
                    xx = pos.down().getX();
                    yy = pos.getY();
                    zz = pos.getZ();
                    break;
                }
                case 1: {
                    xx = pos.up().getX();
                    yy = pos.getY();
                    zz = pos.getZ();
                    break;
                }
                case 2: {
                    xx = pos.getX();
                    yy = pos.getY();
                    zz = pos.down().getZ();
                    break;
                }
                case 3: {
                    xx = pos.getX();
                    yy = pos.getY();
                    zz = pos.up().getZ();
                    break;
                }
                case 4: {
                    xx = pos.getX();
                    yy = pos.down().getY();
                    zz = pos.getZ();
                    break;
                }
                case 5: {
                    xx = pos.getX();
                    yy = pos.up().getY();
                    zz = pos.getZ();
                    break;
                }
            }
            if (world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().getMaterial() == Material.lava) {
                met = world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(xx, yy, zz)));
                isLavaNear = true;
                break;
            }
            if (world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().getMaterial() == Material.water) {
                isWaterNear = true;
                break;
            }
        }
        if (isLavaNear) {
            world.setBlockState(pos, Blocks.dirt.getDefaultState(), 3);
            world.playSoundEffect((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f), "random.fizz", 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
            for (int l = 0; l < 8; ++l) {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + 1.2, pos.getZ() + Math.random(), 0.0, 0.0, 0.0);
            }
            return 2;
        }
        return 1;
    }
    
    public void updateTick(final World world, BlockPos pos, IBlockState state, final Random rand) {
        final int chckRes = this.checkFusion(world, pos, rand);
        if (chckRes == 2) {
            return;
        }
        if (state.getBlock().getMetaFromState(state) == 0) {
            final Block block = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + this.densityDir, pos.getZ())).getBlock();
            final int bMeta = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + this.densityDir, pos.getZ())).getBlock().getMetaFromState(world.getBlockState(new BlockPos(pos.getX(), pos.getY() + this.densityDir, pos.getZ())));
            if (block == this && bMeta != 0) {
            	BlockPos tempPos = new BlockPos(pos.getX(), pos.getY() + this.densityDir, pos.getZ());
                world.setBlockState(tempPos, state, 1);
                //world.setBlockMetadataWithNotify(new BlockPos(pos.getX(), pos.getY() + this.densityDir, pos.getZ()), 0, 2);
                // TODO: Make sure this works properly
                world.setBlockState(tempPos, world.getBlockState(tempPos).getBlock().getStateFromMeta(0));
                world.setBlockToAir(pos);
                return;
            }
        }
        else {
            int inThisBlocks = 0;
            int inMetaBlocks = 0;
            if (world.rand.nextInt(5) == 0) {
                if (world.getBlockState(pos.add(1, 0, 0)).getBlock() == this) {
                    ++inThisBlocks;
                    if (world.getBlockState(pos.add(1, 0, 0)).getBlock().getMetaFromState(world.getBlockState(pos.add(1, 0, 0))) == 0) {
                        ++inMetaBlocks;
                    }
                }
                if (world.getBlockState(pos.add(-1, 0, 0)).getBlock() == this) {
                    ++inThisBlocks;
                    if (world.getBlockState(pos.add(-1, 0, 0)).getBlock().getMetaFromState(world.getBlockState(pos.add(-1, 0, 0))) == 0) {
                        ++inMetaBlocks;
                    }
                }
                if (world.getBlockState(pos.add(0, 0, -1)).getBlock() == this) {
                    ++inThisBlocks;
                    if (world.getBlockState(pos.add(0, 0, -1)).getBlock().getMetaFromState(world.getBlockState(pos.add(0, 0, -1))) == 0) {
                        ++inMetaBlocks;
                    }
                }
                if (world.getBlockState(pos.add(0, 0, 1)).getBlock() == this) {
                    ++inThisBlocks;
                    if (world.getBlockState(pos.add(0, 0, 1)).getBlock().getMetaFromState(world.getBlockState(pos.add(0, 0, 1))) == 0) {
                        ++inMetaBlocks;
                    }
                }
                if (inThisBlocks > 2 && inMetaBlocks > 1) {
                    //world.setBlockMetadataWithNotify(pos, world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) - 1, 2);
                    // TODO: Make sure this works properly. It is so janky...
                	world.setBlockState(pos, world.getBlockState(pos).getBlock().getStateFromMeta(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) - 1), 2);
                }
            }
        }
        this.quantaPerBlockFloat = 1.75f;
        super.updateTick(world, pos, state, rand);
    }
    
    public void velocityToAddToEntity(final World par1World, final int par2, final int par3, final int par4, final Entity par5Entity, final Vec3 par6Vec3) {
    	
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (world.getBlockState(pos.up()).getBlock().getMaterial() == Material.air) {
            final int md = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
            if (world.rand.nextInt(3000) == 0) {
                final double xx = pos.getX() + (double)random.nextFloat();
                final double zz = pos.getZ() + (double)random.nextFloat();
                //QuicksandManager.spawnQSBubble(world, xx, pos.getY() + 1, zz, (Block)this, 0, 0.5f);
            }
        }
    }
    
    public boolean canDisplace(IBlockAccess world, BlockPos pos) {
        return !world.getBlockState(pos).getBlock().getMaterial().isLiquid() && super.canDisplace(world, pos);
    }
    
    public boolean displaceIfPossible(World world, BlockPos pos) {
        return !world.getBlockState(pos).getBlock().getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
    }
    
    public boolean isNormalCube() {
        return false;
    }
    
    public void runSubmergedChecks(final Entity ent) {
        if (QuicksandManager.isEntityInsideOfBlock(ent, (Block)this) && QuicksandManager.isDrowning(ent)) {
            //QuicksandManager.spawnDrowningBubble(ent.worldObj, ent, (SinkingBlock)(Block)this, false);
            if (!ent.worldObj.isRemote && ent.isEntityAlive()) {
                ent.attackEntityFrom(DamageSource.drown, Math.max(((EntityLivingBase)ent).getMaxHealth() * 0.1f, 2.0f));
            }
        }
    }
    
    public void checkPlayerMuddy(final EntityPlayer triggeringPlayer, final int blockPosX, final int blockPosY, final int blockPosZ, final World world) {
    	// If the skin overlay is disabled by the user...
 		if (!ConfigHandler.useSkinOverlay) {
 			return; // The user does not want the skin overlay. Don't run this function
 		}
        if (!world.isRemote) {
        	final PlayerMudManager playerMudControl = PlayerMudManager.get(triggeringPlayer);
 			final int preMudLevel = QuicksandManager.getMudLevel(triggeringPlayer, blockPosY, world);
            if (preMudLevel * (this.maxOpacity / 1000.0f) > playerMudControl.getMudLevel() * (playerMudControl.getMudTime() / 1000.0f)) {
            	playerMudControl.setMudLevel(preMudLevel);
                final int mdtp = QuicksandManager.getMudType((Block)this);
                playerMudControl.setMudType(mdtp);
                if (playerMudControl.getMudTime() < this.maxOpacity) {
                	playerMudControl.addMudTime(this.incOpacity);
                }
                playerMudControl.setMudTime(Math.min(playerMudControl.getMudTime(), this.maxOpacity));
            }
        }
    }

    // Obtains the special name of the block variant.
 	// This is used to add a custom name to a block variant in the language file
 	@Override
 	public String getSpecialName(ItemStack stack) {
 		return FluidBog.types[stack.getItemDamage()];
 	}

 	// Sets the tooltips that should be added to the block
 	// Leave blank for no tooltip
 	@Override
 	public void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
 		list.add(StatCollector.translateToLocal("mfqm.tooltip_1"));
 	}
 	
 	// Returns types of metadata for the block
 	@Override
 	public String[] getTypes() {
 		return types;
 	}
 	
 	// Returns if only one texture should be used for all metadata types
 	@Override
 	public boolean getUseOneTexture() {
 		return useOneTexture;
 	}
}
