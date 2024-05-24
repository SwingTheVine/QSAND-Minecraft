package com.SwingTheVine.QSAND.block;

import java.util.List;

import com.SwingTheVine.QSAND.entity.monster.EntitySlimeSand;
import com.SwingTheVine.QSAND.util.BeaconHandler;
import com.SwingTheVine.QSAND.util.ConfigHandler;
import com.SwingTheVine.QSAND.util.QuicksandManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockQuicksand extends SinkingBlock implements IMetaBlockName {
	private static final String[] types = {"0"}; // Values of the different metadata levels
	private static final boolean useOneTexture = true; // Should all metadata variants use the same texture?
	private static final float[] sinkTypes = {1.00F}; // The maximum sink level for each metadata variant
	private BeaconHandler beacon = new BeaconHandler(false); // Constructs a beacon handler. Enabled if "true" passed in
	
	// Constructor
	public BlockQuicksand(Material material) {
		super(material);
		this.setHardness(2.5f);
		this.setStepSound(Block.soundTypeSand);
		this.setResistance(1000.0f);
		this.setTickRandomly(true);
		
		// Makes the block opaque if the user desires
		int opacity = (ConfigHandler.makeBlocksOpaque)
				? 255 : 3;
		this.setLightOpacity(opacity);
	}
	
	// Changes the collision box. This is not the texture bounding box. This is not the hitbox.
	// This function changes the height of the block to make the entity "sink" into the block by the "sinkType" value corresponding to the metadata value of the block. (i.e. meta=0 means reduce height by 0.35)
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
	}
	
	// What to do when an entity is INSIDE the block
	// This is the core of the quicksand calculations
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity triggeringEntity) {
		final double triggEntityPosY = triggeringEntity.posY; // Triggering entity's Y position
		final double triggEntityPrevPosY = triggeringEntity.prevPosY; // Triggering entity's previous Y position
		final double triggEntityVelY = triggeringEntity.motionY; // Triggering entity's Y velocity
		
		// Triggering entity's distance sunk into the block.
		// First statement is when the entity is a player.
		// Second statement is when the entity is NOT a player
		double triggEntitySunk, triggEntitySunk_kof1 = 
				(triggeringEntity instanceof EntityPlayer)
				? (pos.getY() - triggEntityPosY + 1.0)
				: (pos.getY() - triggEntityPosY - 0.5); 

		// Triggering entity's distance previously sunk into the block
		// First statement is when the entity is a player.
		// Second statement is when the entity is NOT a player
		double triggEntityPrevSunk, triggEntityPrevSunk_kof2 = 
				(triggeringEntity instanceof EntityPlayer)
				? Math.max((pos.getY() - triggEntityPrevPosY + 1.0), 0.0)
				: Math.max((pos.getY() - triggEntityPrevPosY - 0.5), 0.0); 
					
		double triggEntitySunkMod_kof1m = Math.max(triggEntitySunk_kof1, 0.0); // A modified version of trigger entity sunk
		final int blockMetadata = 0; // Obtains this block's variant/metadata. TODO: Changed. Since there is only one type, this is 0
		
		// If the triggering entity is living (as opposed to a death animation)...
		if (triggeringEntity instanceof EntityLivingBase) {
			boolean triggEntityAffected = true; // Should the triggering entity be affected by this block?
			boolean triggEntityJumping = false; // Is the triggering entity jumping?
			boolean triggEntityMoving = false; // Is the triggering entity moving?
			boolean triggEntitySplashing = false; // Is the triggering entity splashing?
			boolean triggEntityRotating = false; // Is the triggering entity rotating?
			final float blockMetadataBumped = 10.0f; // TODO: Changed
			double triggEntityMovingDistance_movDis = 1.0;
			double forceBubbleSpawn_movCof = 16.0; // Forces the block to attempt to spawn bubbles. This value is used when the entity is not moving
			double movementPunish_mofKofDiv = 1.0; // Punishes the entity for moving. Makes them sink faster.
			
			// EQUATION BEACON. 1st complex
			// Increases the punishment value the LESS the entity is sunk in the block
			// Starts at a specified number not exceeding 145 and hits 0 when player sunk 1.2 blocks.
			// Then, mr_blackgoo goes back up towards 145.
			// It is parabolic in nature.
			// https://www.desmos.com/calculator/wgkirt9ra1
			final int mr_blackgoo = (int)Math.min(5.0 + Math.floor(Math.max(0.0, blockMetadataBumped * 10.0f * (1.5 - triggEntitySunkMod_kof1m))), 145.0); // TODO: Changed
			
			// TODO: Changed. Removed entity is Muddy Blob
			
			// If the entity has been marked as NOT affected by this block...
			if (!triggEntityAffected) {
				this.runSubmergedChecks(triggeringEntity);
			}
			
			// TODO: Changed. Removed boot calculations
			
			// If the entity moves downwards with a high velocity, they are marked as splashing.
			//    metadata 1 = -0.100 or faster (Mud)
			//    metadata 2 = -0.100 or faster (Thinnish Mud)
			//    metadata 3 = -0.067 or faster (Deep Mud)
			//    metadata 4 = -0.050 or faster (Bottomless Mud)
			triggEntitySplashing =
					(triggeringEntity.motionY < -0.1) // TODO: Changed. Lack of metadata has simplified this statement
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
			
			// If the entity is marked as rotating, OR the entity has moved farther than 0.0015 blocks along the X/Z axis...
			// TODO: Changed. 0.001 -> 0.0015
			if (triggEntityRotating || Math.abs(triggeringEntity.prevPosX - triggeringEntity.posX) > 0.0015 || Math.abs(triggeringEntity.prevPosZ - triggeringEntity.posZ) > 0.0015) {
				
				triggEntityMoving = true; // The entity is moving
				
				// Finds the hypotenuse of the distance traveled.
				// This is the actual distance traveled on a radical plane
				triggEntityMovingDistance_movDis = Math.pow(Math.pow(triggeringEntity.prevPosX - triggeringEntity.posX, 2.0) + Math.pow(triggeringEntity.prevPosZ - triggeringEntity.posZ,  2.0), 0.5);
				
				// Exponentially increases the chance of a bubble spawn event the more the entity moves forwards.
				// Outputs as a number between 16 and 32. Lower number equals higher chance
				// https://www.desmos.com/calculator/rxdyif2tis
				forceBubbleSpawn_movCof = Math.max(Math.min(32.0 / (1.0 + (triggEntityMovingDistance_movDis * 10.0)), 32.0), 16.0);
				
				// Base punishment for punishing the player for moving.
				// This value will be modified later
				movementPunish_mofKofDiv = 1.0 + triggEntityMovingDistance_movDis * 25.0;
				
				// If the entity has sunk less than 0.9 blocks,
				//    AND the entity HAS sunk into the block,
				//    AND the entity is marked as rotating...
				if (triggEntitySunkMod_kof1m < 0.9 && triggEntitySunkMod_kof1m != 0.0 && triggEntityRotating) {
					
					// TODO: Changed. 0.005 -> 0.003
					movementPunish_mofKofDiv += mr_blackgoo * 0.003; // Amplify the punishment value by 0.005
				}
			}
			
			// TODO: Changed. Not wearing boots function removed
			
			// TODO: Changed. Metadata == 0 statement removed
			
			// If the entity is a Sand Slime
			if (triggeringEntity instanceof EntitySlimeSand) {
				
				// Adds a potion effect of Regeneration for 5 seconds, 1.0 amplifier
				((EntityLivingBase)triggeringEntity).addPotionEffect(new PotionEffect(10, 5, 1, false, false));
				
				// If the block above this one does NOT equal this type of block...
				if (world.getBlockState(pos.up(1)).getBlock() != this) {
					
					// ...AND if the entity is sunk more than 1.35 blocks into this block...
					if (triggEntitySunk_kof1 >= 1.35) {
						
						// ...AND the entity is NOT marked as splashing...
						if (!triggEntitySplashing) {
							triggeringEntity.motionY = 0.0; // Stop all Y movement for the entity
							triggeringEntity.motionY += 0.08; // Add 0.08 Y movement to the entity
							triggeringEntity.onGround = true; // The entity is marked as on the ground
							triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
						}
					} else {
						triggeringEntity.motionY = 0.0; // Stop all Y movement for the entity
						triggeringEntity.motionY += 0.1; // Add 0.08 Y movement to the entity
						triggeringEntity.onGround = true; // The entity is marked as on the ground
						triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
					}
				} else {
					triggeringEntity.motionY = 0.0; // Stop all Y movement for the entity
					triggeringEntity.motionY += 0.1; // Add 0.08 Y movement to the entity
					triggeringEntity.onGround = true; // The entity is marked as on the ground
					triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
				}
				return; // Stop entity collision calculations
			}
			
			// If the entity is NOT a player, AND the entity was marked as NOT affected by the block...
			if (!(triggeringEntity instanceof EntityPlayer) && !triggEntityAffected) {
				
				// If the block above this one does NOT equal this type of block...
				if (world.getBlockState(pos.up(1)).getBlock() != this) {
					
					// ...AND if the entity is sunk more than 1.35 blocks into this block...
					if (triggEntitySunk_kof1 >= 1.35) {
						
						// ...AND the entity is NOT marked as splashing...
						if (!triggEntitySplashing) {
							triggeringEntity.motionY = 0.0; // Stop all Y movement for the entity
							triggeringEntity.motionY += 0.08; // Add 0.08 Y movement to the entity
							triggeringEntity.onGround = true; // The entity is marked as on the ground
							triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
						}
					} else {
						triggeringEntity.motionY = 0.0; // Stop all Y movement for the entity
						triggeringEntity.motionY += 0.1; // Add 0.08 Y movement to the entity
						triggeringEntity.onGround = true; // The entity is marked as on the ground
						triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
					}
				} else {
					triggeringEntity.motionY = 0.0; // Stop all Y movement for the entity
					triggeringEntity.motionY += 0.1; // Add 0.08 Y movement to the entity
					triggeringEntity.onGround = true; // The entity is marked as on the ground
					triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
				}
				return; // Stop entity collision calculations
			}
			
			// STATEMENT BEACON 1
            // If the remainder of the current world time divided by 128 is zero,
            //     OR the entity is marked as moving, AND the remainder of the current world time divided by a number that decreases the more the entity moves is zero,
            //     OR the entity is marked as jumping, AND the remainder of the current world time divided by 8 is zero,
            //     OR the entity is marked as splashing...
			if (world.getTotalWorldTime() % 128L == 0L
					|| (triggEntityMoving && world.getTotalWorldTime() % Math.max((int)Math.floor(forceBubbleSpawn_movCof), 1) == 0L)
					|| (triggEntityJumping && world.getTotalWorldTime() % 8L == 0L)
					|| triggEntitySplashing) {
				// This is the part that makes most of the bubbles
	            // This is done either at set intervals (based on world time), or faster set intervals when the player is moving
				
				// ...AND if the triggering entity is splashing...
				if (triggEntitySplashing) {
					
					// ...AND if the previous sunk distance is greater than 1.5 blocks...
					if (triggEntityPrevSunk_kof2 > 1.5) {
						
						// TODO: Changed. 0.15f -> 0.25f
						world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "game.player.swim", 0.25f, world.rand.nextFloat() * 0.25f + 0.1f); // Play sound
					}
					
					// If the entity is a player, AND the number in the world's random number generator sequence is 0 (1/2 chance)...
					// TODO: Changed. nextInt(3) -> nextInt(2)
					if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(2) == 0) {
						QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
						QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
						QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
						// TODO: Changed. -1 -> 0
					}
				}
				
				// If the entity is marked as NOT splashing, AND the entity is marked as moving...
				if (!triggEntitySplashing && triggEntityMoving) {
					
					// ...AND the number in the world's random number generator sequence equals 0 (1/2 chance)...
                    if (world.rand.nextInt(2) == 0) {
                    	
                    	// TODO: Changed. The sound and float value changed
                    	world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "game.player.swim", 0.25f, world.rand.nextFloat() * 0.25f + 0.1f); // Play sound
                    }
                    
                    // ...AND the number in the world's random number generator sequence equals 0 (1/5 chance)...
                    // TODO: Changed. nextInt(7) -> nextInt(5)
                    if (world.rand.nextInt(5) == 0) {
                    	QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
                    	// TODO: Changed. -1 -> 0
                    }
				}
				
				// If the entity is marked as jumping...
				if (triggEntityJumping) {
					
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.slime.attack", 0.25f, world.rand.nextFloat() * 0.25f + 0.25f); // Play sound
					
					// If the entity is a player, AND the number in the world's random number generator sequence equals 0 (1/2 chance)...
                    // TODO: Changed. 5 -> 2
					if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(2) == 0) {
						QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
						QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
						QuicksandManager.spawnBodyBubbleRandom(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
						// TODO: Changed. -1 -> 0
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
			
			// If the remainder of the total world time divided by 16 is 0,
            //    AND the number in the world's random number generator sequence equals 0 (1/10 chance)...
            // TODO: Changed. Removed metadata statement. 32L -> 16L. nextInt(20) -> nextInt(10)
            if (world.getTotalWorldTime() % 16L == 0L && world.rand.nextInt(10) == 0) {
            	QuicksandManager.spawnBodyBubble(world, triggeringEntity, pos.getX(), pos.getY(), pos.getZ(), this, false);
            	// TODO: Changed. -1 -> 0
            }
                
            triggeringEntity.motionX = 0.0; // Make the entity stop moving
            triggeringEntity.motionZ = 0.0; // Make the entity stop moving
            
            // TODO: Changed. boots dont float removed
            
            // If the entity's velocity is greater than -0.1...
            if (triggeringEntity.motionY > -0.1) {
                // This only happens when the entity is NOT moving downwards very fast
            	beacon.logBeacon("MotionY", "2.1", triggeringEntity.motionY);
            	triggeringEntity.motionY = 0.0; // Make the entity stop moving
            } else {
            	beacon.logBeacon("MotionY", "2.2", triggeringEntity.motionY);
            	triggeringEntity.motionY /= 2.0; // Slow the downwards motion of the entity by 100%
            	// TODO: Changed. 1.5 -> 2.0
            }
            
            // STATEMENT BEACON 2
	        // If the block above this one is NOT the same as this block,
	        //    OR the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
	        if (world.getBlockState(pos.up(1)) != this || (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this)) {
	
	            // If the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
	            if (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this) {
	            	triggEntitySunk_kof1 = 0.001; // Mark the distance the entity has sunk as 0.001 blocks
	            }
	            
	            // TODO: Changed. kof1 > 0.9 removed
	            
	            double sinkRateMod_mys = 0.0; // Modifies how fast the entity sinks into the block. Units are blocks. Negative is down.
                
	            // If the entity is NOT a player...
                if (!(triggeringEntity instanceof EntityPlayer)) {
                    sinkRateMod_mys = 0.0; // Makes the non-player entity sink faster when negative
                }
                
                // If the triggering entity is marked as jumping, AND the entity has been marked for suction...
                if (triggEntityJumping && QuicksandManager.suctionWorldCheck(triggeringEntity, world, triggEntityVelY)) {
                	triggeringEntity.motionY -= 0.05 * (Math.min(0.75, triggEntitySunkMod_kof1m) + 1.0); // Subtract some unknown modifier to the entity's Y velocity
                    world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.magmacube.jump", 0.25f, 0.25f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f, false); // Play sound
                }
                
                // If the entity has sunk greater than or equal to 1.2 blocks...
                if (triggEntitySunk_kof1 >= 1.2) {
                	
                	// ...AND the entity is NOT marked as splashing...
                	if (!triggEntitySplashing) {
                		
                		// TODO: Changed. Boot statement removed.

	                	// If the remainder of the total world time divided by 4 equals 0...
                		// TODO: Changed. Equation replaced
                		double thicknessLower = 
                				(world.getTotalWorldTime() % 4L == 0L)
                				? 0.07485 : 0.075;
                		
                		// EQUATION BEACON. 2nd complex
                		// Makes the entity sink extremely fast
                		// This equation is really complicated.
                		// Basically, "thicknessLower" is the maximum value, and the further the player has sunk, the closer it approaches 0.
                		// After a while (happens sooner the higher the punishment value is), the rate it approaches 0 changes and becomes faster
                		// https://www.desmos.com/calculator/klpmbgz7je
                		beacon.logBeacon("MotionY", "6", thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
                		triggeringEntity.motionY += thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
                		beacon.logBeacon("Equation", "2", thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
                		
                		triggeringEntity.onGround = false; // The entity is marked as NOT on the ground
                		triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
                		
                		// If the number in the world's random number generator sequence equals 0...
                		// TODO: Changed. 20.0 -> 5.0
                        if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 5.0, 1.0))) == 0) {
                        	// Happens more frequently the less the entity is sunk in the block (1/24 to 1/56 chance) TODO: Update
                            triggeringEntity.setInWeb(); // The entity is marked as in a web
                        }
                        
                        // If the next integer in the world's random number generator sequence equals 0...
                        // TODO: Changed. Removed bumped metadata from equation. 2.0f -> 50.0
                        if (world.rand.nextInt(Math.max((int)Math.floor(50.0 - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                        	// Happens more frequently the more the entity is sunk in the block.
                        	// However, this only triggers when the entity is sunk more than 1.2 blocks
                        	// That means this only happens on metadata 4 (Bottomless Mud).
                        	// 1.40-1.58 blocks has a 1/4 chance
                        	// 1.59-2.08 blocks has a 1/3 chance TODO: Update
                        	// 2.09-2.51 blocks has a 1/2 chance
                        	// 2.52-2.80 blocks has a 1/1 chance
                        	
                            triggeringEntity.onGround = true; // The entity is marked as on the ground
                        }
                	}
                } else {
                	
                	// If the entity is moving upwards, AND the number in the world's random number generator sequence modified by the modified distance sunk by the triggering entity equals 0, AND the entity has been marked for suction...
                	if (triggEntityPosY - triggEntityPrevPosY > 0.001 && world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunkMod_kof1m * 3.0), 10)) == 0 && QuicksandManager.suctionWorldCheck(triggeringEntity, world, triggEntityVelY)) {
                		triggeringEntity.motionY -= 0.035 * blockMetadataBumped * (Math.min(0.75, triggEntitySunkMod_kof1m) + 1.0); // The entity's Y velocity is modified by the modified distance sunk by the triggering entity
                		world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.magmacube.jump", 0.25f, 0.25f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f, false); // Play sound
                	}
                	
                	// If the entity has sunk greater than 0.9 blocks...
                	if (triggEntitySunk_kof1 >= 0.9) {
                		// Note: will never be greater than or equal to 1.2
                		
                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
                		// ...then the value is the first one. Otherwise, it is the second one
                		// This triggers more frequently the higher the metadata value is. (1/7 to 1/4 chance)
                		// TODO: Changed. Removed sink rate modifer from output when false. 0.07485 -> 0.0748
                		double thicknessLower = 
                				(world.getTotalWorldTime() % 2L == 0L)
                				? 0.0748 : 0.075;
                		
                		// EQUATION BEACON 3. 2nd complex
                		triggeringEntity.motionY += thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
                		beacon.logBeacon("Equation", "3", thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
                		
                		
                		QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, mr_blackgoo); // Makes the entity stuck
                		
                		triggeringEntity.onGround = false; // TODO: Changed. Added
                		triggeringEntity.fallDistance = 0.0f; // Resets the accumulated fall distance to 0
                		
                		// TODO: Changed. Removed parent if statement "if is player"
                		// If the number in the world's random number generator sequence equals 0...
                        if (world.rand.nextInt(Math.max((int)Math.floor(50.0 - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                        	// Happens more frequently the more the entity is sunk in the block.
                        	// However, this only triggers when the entity is sunk more than 1.2 blocks
                        	// That means this only happens on metadata 4 (Bottomless Mud).
                        	// 0.90-1.00 blocks is 1/7 chance TODO: Update
                        	// 1.01-1.20 blocks is 1/6 chance
                        	
                        	triggeringEntity.onGround = true; // Marks the entity as on the ground
                        }
                		
                		// If the number in the world's random number generator sequence equals 0...
                        // TODO: Changed. 10.0 -> 5.0   BumpedMetadata -> 2.0
                        if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 5.0 / 2.0, 1.0))) == 0) {
                        	// Happens more frequently the less the entity is sunk in the block (1/2 to 1/3 chance)
                        	
                            triggeringEntity.setInWeb(); // The entity is marked as in a web
                        }
                	} else {
                		
                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
                		// ...then the value is the first one. Otherwise, it is the second one TODO: Update
                		// This triggers more frequently the higher the metadata value is. (1/16 to 1/5 chance)
                		// This stops the entity from sinking for a tick
                		// https://www.desmos.com/calculator/0g4mgwzpmh
                		// TODO: Changed. 16.0 -> 4.0   16.0 -> 8.0   BumpedMetadata removed.   3.0 -> 8.0
                		double thicknessHigher =
                				(world.getTotalWorldTime() % (int)Math.floor(Math.min(4.0, Math.max(8.0 - triggEntitySunk_kof1 * 8.0, 1.0))) == 0L)
                				? 1.01 : 1.05;
                		
                		// If the number in the world's random number generator sequence equals 0...
                		// TODO: Changed. 7.0f + bumpedmetadata removed.   5.0f -> 50.0
                        if (world.rand.nextInt(Math.max((int)Math.floor(50.0 - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                        	// Happens more frequently the more the entity is sunk in the block.
                        	// However, this only triggers when the entity is sunk more than 1.2 blocks
                        	// Metadata 0... (Mud)
                        	// 0 has a 1/12 chance
                        	// 0.10-0.35 has a 1/11 chance
                        	// Metadata 1... (Thinnish Mud) TODO: Update
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
                            	// 1/1 to 1/3 chance (Deep Mud) TODO: Update
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
                                    // TODO: Changed. 0.025 -> 0.075
                                    triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.075);
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
                            	// TODO: Changed. 0.025 -> 0.075
                                triggeringEntity.motionY += (0.075 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.075); // Unknown modifer to entity's Y velocity
                            }
                        } else {
                        	triggeringEntity.setInWeb();
                        	
                        	thicknessHigher = 1.05; // Changes the amplifier to 1.05
                            
                        	// TODO: Changed. Removed is in water statement
                        	
                            // If the entity is marked as not moving...
                            if (!triggEntityMoving) {
                            	beacon.logBeacon("MotionY", "11.1", (0.0725 + sinkRateMod_mys) * thicknessHigher);
                            	// Moves the entity downwards slightly.
                                // 0.0725 (plus a modifer) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher;
                            }
                            else {
                            	beacon.logBeacon("MotionY", "11.2", (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.025));
                            	// Bumps the entity downwards slightly.
                                // 0.0725 (plus a modifier) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                // Then, it is divided by the punishment value plus 0.025
                            	// If the punishment value is below 1.87, the entity goes upwards
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.025); // Unknown modifier to entity's Y velocity
                            }

                            // If the entity has sunk less than 0.5 blocks into this one, AND the entity is truly sinking...
                            if (triggEntitySunk_kof1 < 0.5 && QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunk_kof1)) {
                            	QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, 145);
                            }
                        }
                	}
                }
                
                // If the entity is marked as in water...
                if (triggeringEntity.isInWater()) {

                    triggeringEntity.motionY /= 4.0; // TODO: Changed.
                }
            } else {
            	// If the entity has sunk less than 0.5 blocks into this one, AND the entity is truly sinking...
                if (triggEntitySunk_kof1 < 0.5 && QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunkMod_kof1m)) {
                	QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, 145);
                }
            	
            	triggeringEntity.setInWeb();
            }
	        
	        // Prevents the entity from continuously jumping
            QuicksandManager.antiHoldJumpScript(triggeringEntity, triggEntitySunk_kof1, true);
	        
		} else {
			
			if (triggEntitySunk_kof1 < 1.45) {
				triggeringEntity.setInWeb();
			}
			
			// TODO: Changed. Removed mud tentacles
		}
	}
	
	// TODO: Changed. Removal of subblock
	
	// Obtains the block (with metadata) when the player picks it (using Middle Mouse Button)
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	// Obtains the metadata this block should drop.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	// Obtains if the specific side of the block is solid.
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	// Used to determine ambient occlusion and culling when rebuilding chunks for render
	@Override
	public boolean isOpaqueCube() {
        return true;
    }

	// When false, the block will not push entities outside of itself
	@Override
	public boolean isFullCube() {
		return false;
    }
	
	// Obtains the special name of the block variant.
	// This is used to add a custom name to a block variant in the language file
	@Override
	public String getSpecialName(ItemStack stack) {
		return BlockQuicksand.types[stack.getItemDamage()];
	}
	
	// Sets the tooltips that should be added to the block
	// Leave blank for no tooltip
	@Override
	public void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
		list.add(StatCollector.translateToLocal("mfqm.tooltip_1"));
	}
	
	// Checks to see if the entity is fully submerged in the block
	public void runSubmergedChecks(final Entity triggeringEntity) {

        // If the entity is inside of this block, AND the entity is marked as drowning...
        if (QuicksandManager.isEntityInsideOfBlock(triggeringEntity, this) && QuicksandManager.isDrowning(triggeringEntity)) {
            QuicksandManager.spawnDrowningBubble(triggeringEntity.worldObj, triggeringEntity, this, false); // Spawn drowning bubbles

            // ...AND the world is NOT on a server, AND the entity is marked as alive...
            if (!triggeringEntity.worldObj.isRemote && triggeringEntity.isEntityAlive()) {
            	triggeringEntity.attackEntityFrom(DamageSource.drown, Math.max(((EntityLivingBase)triggeringEntity).getMaxHealth() * 0.1f, 2.0f)); // Deals 10% of max health or 2hp in damage. Whichever is greater
            }
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getQuicksandBlockColor() {
        return 16777215;
    }
    
	@Override
    @SideOnly(Side.CLIENT)
    public int getQuicksandRenderColor(IBlockState state) {
        return 16777215;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getQuicksandColorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return 16777215;
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
