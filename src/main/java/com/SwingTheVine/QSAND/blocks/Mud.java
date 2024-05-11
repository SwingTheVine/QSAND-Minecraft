package com.SwingTheVine.QSAND.blocks;

import java.util.List;

import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.manager.QuicksandManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class Mud extends Block implements IMetaBlockName {
	private static final String[] types = {"0", "1", "2", "3"}; // Values of the different metadata levels
	private static final boolean useOneTexture = true; // Should all metadata variants use the same texture?
	private static final float[] sinkTypes = {0.35F, 0.50F, 0.75F, 1.00F}; // The maximum sink level for each metadata variant
	public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, Integer.valueOf(types.length-1)); // Creates a metadata value for every "types" metadata value
	
	// Constructor
	public Mud(Material material) {
		super(material);
		this.setHardness(0.6f); // Sets the hardness of the block
		this.setHarvestLevel("shovel", 0); // Sets the hardness of the block when mined with a shovel
		this.setStepSound(Block.soundTypeSand); // Sets the sound that plays when the block is stepped on
		this.setDefaultState(this.blockState.getBaseState().withProperty(SINK, Integer.valueOf(0))); // Default metadata values for the block
		
		// Makes the block opaque if the user desires
		int opacity = (QSAND.makeBlocksOpaque)
				? 255 : 3;
		this.setLightOpacity(opacity);
	}
	
	// Changes the collision box. This is not the texture bounding box. This is not the hitbox.
	// This function changes the height of the block to make the entity "sink" into the block by the "sinkType" value corresponding to the metadata value of the block. (i.e. meta = 0 means reduce height by 0.35)
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(SINK).intValue() == 3) {return null;} // Removes collision box for bottomless mud
		return new AxisAlignedBB((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)pos.getY() + this.maxY - sinkTypes[state.getValue(SINK).intValue()], (double)pos.getZ() + this.maxZ);
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
		final int blockMetadata = state.getValue(SINK).intValue(); // Obtains this block's variant/metadata
		
		// If the triggering entity is living (as opposed to a death animation)...
		if (triggeringEntity instanceof EntityLivingBase) {
			boolean triggEntityAffected = true; // Should the triggering entity be affected by this block?
			boolean triggEntityJumping = false; // Is the triggering entity jumping?
			boolean triggEntityMoving = false; // Is the triggering entity moving?
			boolean triggEntitySplashing = false; // Is the triggering entity splashing?
			boolean triggEntityRotating = false; // Is the triggering entity rotating?
			final float blockMetadataBumped = (float)(blockMetadata + 1); // Adds 1 to the block's metadata value
			double triggEntityMovingDistance_movDis = 1.0;
			double forceBubbleSpawn_movCof = 16.0; // Forces the block to attempt to spawn bubbles. This value is used when the entity is not moving
			double movementPunish_mofKofDiv = 1.0; // Punishes the entity for moving. Makes them sink faster.
			
			// EQUATION BEACON. 1st complex
			// Increases the punishment value the LESS the entity is sunk in the block
			// Starts at a specified number not exceeding 145 and hits 0 when player sunk 1.2 blocks.
			// Then, mr_blackgoo goes back up towards 145.
			// It is parabolic in nature.
			// https://www.desmos.com/calculator/wgkirt9ra1
			final int mr_blackgoo = (int)Math.min(5.0 + Math.floor(Math.max(0.0, Math.pow(blockMetadataBumped * 2.0f * (1.5 - triggEntitySunkMod_kof1m), 2.0))), 145.0);
			
			// TODO: Add entity is Muddy Blob
			
			// TODO: Add check entity under
			if (triggEntityAffected) {
				this.checkEntityUnder(triggeringEntity);
			}
			
			// TODO: Add boot calculations
			
			// If the entity moves downwards with a high velocity, they are marked as splashing.
			//    metadata 1 = -0.100 or faster (Mud)
			//    metadata 2 = -0.100 or faster (Thinnish Mud)
			//    metadata 3 = -0.067 or faster (Deep Mud)
			//    metadata 4 = -0.050 or faster (Bottomless Mud)
			triggEntitySplashing =
					(triggeringEntity.motionY < -0.1 / Math.max(1.0f, blockMetadataBumped / 2.0f))
					? true : false;
			
			// If the entity moves upwards with a high velocity, they are marked as jumping.
			triggEntityJumping = 
					(triggEntityPosY - triggEntityPrevPosY > 0.2)
					? true : false;
			
			// TODO: Complete the second equation. Requires Air block event
			// If the entity is NOT a player, AND the entity has moved their camera along the Yaw axis...
			// OR if the entity is a player, and this is multiplayer, AND the server instance has detected Yaw axis movement...
			triggEntityRotating = 
					((!(triggeringEntity instanceof EntityPlayer) && ((EntityLivingBase)triggeringEntity).prevRotationYaw != ((EntityLivingBase)triggeringEntity).rotationYaw)
							|| (false))
					? true : false;
			
			// If the entity is marked as rotating, OR the entity has moved farther than 0.001 blocks along the X/Z axis...
			if (triggEntityRotating || Math.abs(triggeringEntity.prevPosX - triggeringEntity.posX) > 0.001 || Math.abs(triggeringEntity.prevPosZ - triggeringEntity.posZ) > 0.001) {
				
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
					movementPunish_mofKofDiv += mr_blackgoo * 0.005; // Amplify the punishment value by 0.005
				}
			}
			
			// TODO: not wearing boots function
			
			// If the block variant is 0 (Mud)...
			if (blockMetadata == 0) {
				
				// ... AND if the entity is NOT a player, AND the entity has sunk less than 1.25 blocks...
				if (!(triggeringEntity instanceof EntityPlayer) && triggEntitySunk_kof1 < 1.25) {
					
					System.out.println("MotionY Beacon 1");
					triggeringEntity.motionY = 0.0; // Set the entity's velocity to 0
					triggeringEntity.motionY += 0.08 + Math.min((1.25 - triggEntitySunk_kof1) / 100.0, 0.005); // Add Y velocity to the entity
	                // The upwards motion of the entity reduces the further into the block the entity is sunk:
	                // 125% sunk is 0.08 velocity upwards
	                // 100% sunk is 0.0825 velocity upwards
	                // 75% and less sunk is 0.085 velocity upwards
					
					triggeringEntity.onGround = true; // The entity is flagged as on the ground
					triggeringEntity.fallDistance = 0.0f; // The entity does not take fall damage
				}
			}
			
			// TODO: entity instanceof muddy blob
			
			// TODO: not player; not affected
			
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
						world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "game.player.swim", 0.15f, world.rand.nextFloat() * 0.25f + 0.1f); // Play sound
					}
					
					// If the entity is a player, AND the number in the world's random number generator sequence is 0 (1/3 chance)...
					if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(3) == 0) {
						// TODO: Add body bubbles
					}
				}
				
				// If the entity is marked as NOT splashing, AND the entity is marked as moving...
				if (!triggEntitySplashing && triggEntityMoving) {
					
					// ...AND the number in the world's random number generator sequence equals 0 (1/2 chance)...
                    if (world.rand.nextInt(2) == 0) {
                        world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f); // Play sound
                    }
                    
                    // ...AND the number in the world's random number generator sequence equals 0 (1/7 chance)...
                    if (world.rand.nextInt(7) == 0) {
                    	// TODO: Add body bubble
                    }
				}
				
				// If the entity is marked as jumping...
				if (triggEntityJumping) {
					
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.slime.attack", 0.25f, world.rand.nextFloat() * 0.25f + 0.25f); // Play sound
					
					// If the entity is a player, AND the number in the world's random number generator sequence equals 0 (1/5 chance)...
                    if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(5) == 0) {
                    	// TODO: Add body bubbles
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
                    
                    // If the variant of the block is greater than 2 (Bottomless Mud),
                    //    AND the remainder of the total world time divided by 32 is 0,
                    //    AND the number in the world's random number generator sequence equals 0 (1/20 chance)...
                    if (blockMetadata > 2 && world.getTotalWorldTime() % 32L == 0L && world.rand.nextInt(20) == 0) {
                        // TODO: Add body bubble
                    }
                }
			}
                
            triggeringEntity.motionX = 0.0; // Make the entity stop moving
            triggeringEntity.motionZ = 0.0; // Make the entity stop moving
            
            // TODO: add boots dont float
            
            // If the entity's velocity is greater than -0.1...
            if (triggeringEntity.motionY > -0.1) {
                // This only happens when the entity is NOT moving downwards very fast
            	System.out.println("MotionY Beacon 2.1: " + triggeringEntity.motionY);
            	triggeringEntity.motionY = 0.0; // Make the entity stop moving
            } else {
            	System.out.println("MotionY Beacon 2.2: " + triggeringEntity.motionY);
            	triggeringEntity.motionY /= 1.5; // Slow the downwards motion of the entity by 50%
            }
            
            // STATEMENT BEACON 2
            // If the block above this one is NOT the same as this block,
            //    OR the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
            if (world.getBlockState(pos.up(1)) != this || (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this)) {

                // If the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
                if (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this) {
                	triggEntitySunk_kof1 = 0.001; // Mark the distance the entity has sunk as 0.001 blocks
                }
                
                // If the if statement above runs, this one will not run
                // In other words, this will only run if:
                // The block above this one is NOT the same as this block,
                //    AND the entity has sunk is greater than 0.9 blocks...
                if (triggEntitySunk_kof1 > 0.9) {
                	
                	// ...AND if the entity is marked as moving, AND the punishment value is greater than 2.75...
                	if (triggEntityMoving && movementPunish_mofKofDiv > 2.75) {
                		triggEntityMoving = false; // Mark the entity as not moving
                		movementPunish_mofKofDiv = 0.0; // Reset the punishment value to 0
                		
                		// If the entity is NOT in water, AND the entity has sunk less than 1.3 blocks...
                		if (!triggeringEntity.isInWater() && triggEntitySunk_kof1 < 1.3) {
                			System.out.println("MotionY Beacon 5");
                			// Dramatically increases the rate the entity sinks right before it is submerged.
                			// Adds an upwards velocity to combat the effect of gravity.
                			// 0 to 1 blocks sunk has a velocity of 0.025 added.
                			// 1 to 1.3 blocks sunk has an added velocity that decreases at a constant rate.
                			// 1.3 blocks onwards has a velocity of 0 added.
                			// Note that the "constant rate" compounds on itself, making it seem exponential
                			triggeringEntity.motionY += 0.025 * Math.max(Math.min((1.3 - triggEntitySunk_kof1) / 0.3, 1.0), 0.0);
                		}
                	}
                	movementPunish_mofKofDiv *= 1.125; // Amplifies the punishment value by 1.125 (this does nothing if the if statement above is run)
                } else {
                	movementPunish_mofKofDiv *= 1.5; // Amplifies the punishment value by 1.5
                }
                
                double sinkRateMod_mys = 0.0; // Modifies how fast the entity sinks into the block. Units are blocks. Negative is down.
                
                // If the entity is NOT a player...
                if (!(triggeringEntity instanceof EntityPlayer)) {
                    sinkRateMod_mys = 0.0; // Makes the non-player entity sink faster when negative
                }
                
                // TODO: Fix this; suction check
                /*
                if (triggEntityJumping && false) {
                	triggeringEntity.motionY -= 0.05 * blockMetadataBumped * (Math.min(0.75, triggEntitySunkMod_kof1m) + 1.0); // Subtract some unknown modifier to the entity's Y velocity
                    world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.magmacube.jump", 0.25f, 0.25f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f, false); // Play sound
                }*/
                
                // If the entity has sunk greater than or equal to 1.2 blocks...
                if (triggEntitySunk_kof1 >= 1.2) {
                	
                	// ...AND the entity is NOT marked as splashing...
                	if (!triggEntitySplashing) {

	                	// TODO: More boot code
	                	if (false) {
	                		
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
	                		System.out.println("MotionY Beacon 6");
	                		triggeringEntity.motionY += thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
	                		System.out.println("Equation Beacon 2: " + thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
	                		
	                		triggeringEntity.onGround = false; // The entity is marked as NOT on the ground
	                		triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
	                		
	                		// If the number in the world's random number generator sequence equals 0...
                            if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 20.0, 1.0))) == 0) {
                            	// Happens more frequently the less the entity is sunk in the block (1/24 to 1/56 chance)
                                triggeringEntity.setInWeb(); // The entity is marked as in a web
                            }
                            
                            // If the next integer in the world's random number generator sequence equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(2.0f + blockMetadataBumped - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                            	// Happens more frequently the more the entity is sunk in the block.
                            	// However, this only triggers when the entity is sunk more than 1.2 blocks
                            	// That means this only happens on metadata 4 (Bottomless Mud).
                            	// 1.40-1.58 blocks has a 1/4 chance
                            	// 1.59-2.08 blocks has a 1/3 chance
                            	// 2.09-2.51 blocks has a 1/2 chance
                            	// 2.52-2.80 blocks has a 1/1 chance
                            	
                                triggeringEntity.onGround = true; // The entity is marked as on the ground
                            }
	                	}
                	}
                	
                } else {
                	
                	// TODO: Suction check function
                	
                	// If the entity has sunk greater than 0.9 blocks...
                	if (triggEntitySunk_kof1 >= 0.9) {
                		// Note: will never be greater than or equal to 1.2
                		
                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
                		// ...then the value is the first one. Otherwise, it is the second one
                		// This triggers more frequently the higher the metadata value is. (1/7 to 1/4 chance)
                		double thicknessLower = 
                				(world.getTotalWorldTime() % Math.max(8.0f - blockMetadataBumped, 1.0f) == 0.0f)
                				? 0.07485 : 0.075 + sinkRateMod_mys;
                		
                		// EQUATION BEACON 3. 2nd complex
                		triggeringEntity.motionY += thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
                		System.out.println("Equation Beacon 3: " + thicknessLower / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
                		
                		
                		// TODO: set stuck effect
                		triggeringEntity.fallDistance = 0.0f; // Resets the accumulated fall distance to 0
                		
                		// If the entity is a player...
                		if (triggeringEntity instanceof EntityPlayer) {
                			
                			// ...AND the number in the world's random number generator sequence equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(4.0f + blockMetadataBumped - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                            	// Happens more frequently the more the entity is sunk in the block.
                            	// However, this only triggers when the entity is sunk more than 1.2 blocks
                            	// That means this only happens on metadata 4 (Bottomless Mud).
                            	// 0.90-1.00 blocks is 1/7 chance
                            	// 1.01-1.20 blocks is 1/6 chance
                            	
                            	triggeringEntity.onGround = true; // Marks the entity as on the ground
                            }
                		} else {
                			
                			// Sucks the entity back towards where they came from on the X/Z axis when moving.
                			// More intense the further they sink.
                			final double mrs_blackgoo = 1 + mr_blackgoo / 10;
                			
                			System.out.println("Set Position Beacon 1: " + triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo + ", " + triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
                			// Sucks the entity back towards where they came from. Only partially though
                			triggeringEntity.setPosition(triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo, triggEntityPosY, triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
                		}
                		
                		// If the number in the world's random number generator sequence equals 0...
                        if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 10.0 / blockMetadataBumped, 1.0))) == 0) {
                        	// Happens more frequently the less the entity is sunk in the block (1/2 to 1/3 chance)
                        	
                            triggeringEntity.setInWeb(); // The entity is marked as in a web
                        }
                	} else {
                		
                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
                		// ...then the value is the first one. Otherwise, it is the second one
                		// This triggers more frequently the higher the metadata value is. (1/16 to 1/5 chance)
                		// This stops the entity from sinking for a tick
                		// https://www.desmos.com/calculator/0g4mgwzpmh
                		double thicknessHigher =
                				(world.getTotalWorldTime() % (int)Math.floor(Math.min(16.0, Math.max(16.0 - triggEntitySunk_kof1 * blockMetadataBumped * 3.0, 1.0))) == 0L)
                				? 1.01 : 1.05;
                		
                		// If the entity is a player...
                        if (triggeringEntity instanceof EntityPlayer) {

                            // ...AND the number in the world's random number generator sequence equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(7.0f + blockMetadataBumped * 5.0f - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
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
                        } else {
                        	
                        	// Sucks the entity back towards where they came from on the X/Z axis when moving.
                        	// More intense the further they sink
                        	final double mrs_blackgoo = 1 + mr_blackgoo / 10;
                        	
                            System.out.println("Set Position Beacon 2: " + triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo + ", " + triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
                            // Sucks the entity along the X-Z grid back towards where they came from. Only partially though
                            triggeringEntity.setPosition(triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo, triggEntityPosY, triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo); // Unknown

                            // If the next integer in the world's random number generator sequence at some unknown index equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(7.0f + blockMetadataBumped * 5.0f - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                                triggeringEntity.onGround = true; // The entity is marked as on the ground
                            }
                        }
                        
                        // TODO: is truly sink
                        
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
                                    System.out.println("MotionY Beacon 8.1");
                                    // Bumps the entity downwards slightly.
                                    // 0.0725 (plus a modifer) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                    triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher;
                                }
                                else {
                                    System.out.println("MotionY Beacon 8.2");
                                    // Bumps the entity downwards slightly.
                                    // 0.0725 (plus a modifier) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                    // Then, it is divided by the punishment value plus 0.025
                                    // If the punishment value is below 1.87, the entity goes upwards
                                    triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.025);
                                }
                            } // ...if the entity is NOT marked as moving...
                            else if (!triggEntityMoving) {
                            	System.out.println("MotionY Beacon 9");
                            	// Moves the entity downwards slightly.
                                // 0.075 (plus a modifer) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity goes upwards for a tick
                                triggeringEntity.motionY += (0.075 + sinkRateMod_mys) * thicknessHigher; // Unknown modifer to entity's Y velocity
                            }
                            else {
                            	System.out.println("MotionY Beacon 10");
                            	// Bumps the entity downwards slightly.
                                // 0.075 (plus a modifier) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity goes upwards for a tick
                                // Then, it is divided by the punishment value plus 0.025
                            	// If the punishment value is below 1.87, the entity goes upwards
                                triggeringEntity.motionY += (0.075 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.025); // Unknown modifer to entity's Y velocity
                            }
                        } else {
                        	triggeringEntity.setInWeb();
                        	
                        	// If the entity is marked as in water, OR the block above this one is water...
                            if (triggeringEntity.isInWater() || world.getBlockState(pos.up()).getBlock().getMaterial() == Material.water) {
                            	
                            	thicknessHigher = 1.01; // Changes the amplifier to 1.01
                            } // ...if the entity has sunk less than 0.475 blocks...
                            else if (triggEntitySunk_kof1 < 0.475) {
                            	
                            	thicknessHigher = 1.05; // Changes the amplifier to 1.05
                            }
                            
                            // If the entity is marked as not moving...
                            if (!triggEntityMoving) {
                            	System.out.println("MotionY Beacon 11.1: " + (0.0725 + sinkRateMod_mys) * thicknessHigher);
                            	// Moves the entity downwards slightly.
                                // 0.0725 (plus a modifer) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher;
                            }
                            else {
                            	System.out.println("MotionY Beacon 11.2: " + (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.025));
                            	// Bumps the entity downwards slightly.
                                // 0.0725 (plus a modifier) amplified by "thicknessHigher". If thicknessHigher is 1.05, the entity does not sink
                                // Then, it is divided by the punishment value plus 0.025
                            	// If the punishment value is below 1.87, the entity goes upwards
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.025); // Unknown modifier to entity's Y velocity
                            }

                            // TODO: truly sink function
                        }
                	}
                }
                
                // If the entity is marked as in water...
                if (triggeringEntity.isInWater()) {

                    // ...AND the entity is moving upward...
                    if (triggeringEntity.motionY > 0.0) {
                    	triggeringEntity.motionY = 0.0; // Set the entity's velocity to 0
                    }

                    triggeringEntity.motionY -= 0.01; // Modify the entity's velocity by -0.01
                }
                
                // TODO: anti hold jump script
            } else {
            	// TODO: set stuck effect
            	
            	triggeringEntity.setInWeb();
            }
            /*
            System.out.println("triggEntityMovingDistance_movDis: " + triggEntityMovingDistance_movDis);
            System.out.println("forceBubbleSpawn_movCof: " + forceBubbleSpawn_movCof);
            System.out.println("movementPunish_mofKofDiv: " + movementPunish_mofKofDiv);
            System.out.println("mr_blackgoo: " + mr_blackgoo);*/
		} else {
			
			if (triggEntitySunk_kof1 < 1.45) {
				triggeringEntity.setInWeb();
			}
			
			// TODO: handle mud tentacles
		}
		/*System.out.println("triggEntitySunk_kof1: " + triggEntitySunk_kof1);
        System.out.println("triggEntityPrevSunk_kof2: " + triggEntityPrevSunk_kof2);
        System.out.println("triggEntitySunkMod_kof1m: " + triggEntitySunkMod_kof1m);*/
	}
	
	// Declares that this block ID has metadata values.
	// Declares the metadata values for this block.
	// The metadata value corrosponds with its index in "types".
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item block, CreativeTabs creativeTabs, List<ItemStack> list) {
		
		// For every index in "types"...
		for (int indexType = 0; indexType < types.length; indexType++) {
			list.add(new ItemStack(block, 1, indexType)); // Declare a metadata variant with that index value
		}
    }
	
	// Obtains the block's metadata from the block's blockstate.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue(SINK).intValue();
    }
	
	// Obtains the block's blockstate from the block's metadata.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(SINK, Integer.valueOf(meta));
    }
	
	// Obtains the block (with metadata) when the player picks it (using Middle Mouse Button)
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	// Creates a new block state for this block ID.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {SINK});
    }
	
	// Obtains the metadata this block should drop.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	// Obtains if the specific side of the block is solid.
	// The default (0) and Thinnish (1) variants of this block are solid.
	// The Deep (2) and Bottomless (3) vaariants of this block are NOT solid
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		final int metadata = this.getMetaFromState(world.getBlockState(pos));
		return metadata <= 1;
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
		return Mud.types[stack.getItemDamage()];
	}
	
	// Sets the tooltips that should be added to the block
	// Leave blank for no tooltip
	@Override
	public void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
		list.add(StatCollector.translateToLocal("mfqm.tooltip_1"));
	}
	
	// Checks to see if the entity is fully submerged in the block
	public void checkEntityUnder(final Entity entity) {

        // If the entity is inside of this block, AND the entity is marked as drowning...
        if (QuicksandManager.isEntityInsideOfBlock(entity, this) && QuicksandManager.isDrowning(entity)) {
            QuicksandManager.spawnDrowningBubble(entity.worldObj, entity, this, true); // Spawn drowning bubbles

            // ...AND the world is NOT on a server, AND the entity is marked as alive...
            if (!entity.worldObj.isRemote && entity.isEntityAlive()) {
                entity.attackEntityFrom(DamageSource.drown, Math.max(((EntityLivingBase)entity).getMaxHealth() * 0.1f, 2.0f)); // Deals 10% of max health or 2hp in damage. Whichever is greater
            }
        }
    }
	
	// Returns types of metadata for the block
	public String[] getTypes() {
		return types;
	}
	
	// Returns if only one texture should be used for all metadata types
	public boolean getUseOneTexture() {
		return useOneTexture;
	}
}
