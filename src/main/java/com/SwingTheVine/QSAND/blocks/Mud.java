package com.SwingTheVine.QSAND.blocks;

import java.util.List;

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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Mud extends Block implements IMetaBlockName {
	private static final String[] types = {"0", "1", "2", "3"}; // Values of the different metadata levels
	private static final Boolean useOneTexture = true; // Should all metadata variants use the same texture?
	private static final float[] sinkTypes = {0.35F, 0.50F, 0.75F, 1.00F}; // The maximum sink level for each metadata variant
	public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, Integer.valueOf(types.length-1)); // Creates a metadata value for every "types" metadata value

	// Constructor
	public Mud(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SINK, Integer.valueOf(0))); // Default metadata values for the block
	}
	
	// Changes the collision box. This is not the texture bounding box. This is not the hitbox.
	// This function changes the height of the block to make the entity "sink" into the block by the "sinkType" value corresponding to the metadata value of the block. (i.e. meta=0 means reduce height by 0.35)
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
		double triggEntitySunk, triggEntityPrevSunk;
		
		// If the entity is a player...
		if (triggeringEntity instanceof EntityPlayer) {
			triggEntitySunk = (pos.getY() - triggEntityPosY + 1.0) * -1.0; // How far into the block the entity has sunk (in percent) relative to the top of the block (i.e. sunk 15% from the top of the block is -0.15)
			triggEntityPrevSunk = Math.max(((pos.getY() - triggEntityPrevPosY + 1.0) * -1.0), 0.0); // How far into the block the entity previously sunk (in percent) to the top of the block
			
		} else {
			triggEntitySunk = (pos.getY() - triggEntityPosY - 0.5) * -1.0; // How far into the block the entity has sunk (in percent) relative to the top of the block (i.e. sunk 15% from the top of the block is -0.15)
			triggEntityPrevSunk = Math.max(((pos.getY() - triggEntityPrevPosY - 0.5) * -1.0), 0.0); // How far into the block the entity previously sunk (in percent) to the top of the block
		}
		
		double triggEntitySunk_kof1 = triggEntitySunk; // Percentage of entity NOT sunk into block???
		double triggEntityPrevSunk_kof2 = triggEntityPrevSunk;
		double triggEntitySunkMod_kof1m = Math.max(triggEntitySunk_kof1, 0.0); // A modified version of trigger entity sunk
		final int blockMetadata = state.getValue(SINK).intValue(); // Obtains this block's variant/metadata
		
		// Player data
		// kof1 = how far into the block the player has sunk
		// kof2 = 0
		// kof1m = 0
		
		// Item data
		// kof1 & kof1m start at 1.5 and approaches 0
		// kof2 starts at 1.8 and approaches 0
		// kof1; kof2; kof1m = 1.15 at rest at bottom of metadata 0
		// kof1; kof2; kof1m = 1.0 at rest at bottom of metadata 1
		// kof1; kof2; kof1m = 0.75 at rest at bottom of metadata 2
		// kof1; kof2; kof1m = 0.5 at rest at bottom of metadata 3
		
		if (triggeringEntity instanceof EntityLivingBase) {
			Boolean triggEntityAffected = true; // Should the triggering entity be affected by this block?
			Boolean triggEntityJumping = false; // Is the triggering entity jumping?
			Boolean triggEntityMoving = false; // Is the triggering entity moving?
			Boolean triggEntitySplashing = false; // Is the triggering entity splashing?
			Boolean triggEntityRotating = false; // Is the triggering entity rotating?
			final float blockMetadataBumped = (float)(blockMetadata + 1); // Adds 1 to the block's metadata value
			
			// These variables are unknown
			double triggEntityMovingDistance_movDis = 1.0;
			double triggEntityMovingCoefficient_movCof = 16.0;
			double triggEntityMovingKoefficientDivider_mofKofDiv = 1.0;
			
			// EQUATION BEACON. 1st complex
			// https://www.desmos.com/calculator/wgkirt9ra1
			final int mr_blackgoo = (int)Math.min(5.0 + Math.floor(Math.max(0.0, Math.pow(blockMetadataBumped * 2.0f * (1.5 - triggEntitySunkMod_kof1m), 2.0))), 145.0);
			
			// Player data
			// movDis = 1.0 when entity at rest
			// movDis = 0.0049 when walking
			// movDis = 0.0063 when sprinting
			
			// movCof = 16.0 when at rest
			// movCof = 31.64 when walking
			// movCof = 31.14 when sprinting
			
			// mofKofDiv = 1.5 when at rest
			// mofKofDiv = 1.68 when walking
			// mofKofDiv = 1.73 when sprinting
			
			// mr_blackgoo = 14 when metadata = 0
			// mr_blackgoo = 41 when metadata = 1
			// mr_blackgoo = 86 when metadata = 2
			// mr_blackgoo = 145 when metadata = 3
			
			// TODO: Add entity is Muddy Blob
			
			// TODO: Add check entity under
			
			// TODO: Add boot calculations
			
			// If the entity moves downwards with a velocity higher than the equation,
			//    the entity is marked as splashing
			triggEntitySplashing =
					(triggeringEntity.motionY < -0.1 / Math.max(1.0f, blockMetadataBumped / 2.0f))
					? true : false;
			
			// If the entity moves upwards with a velocity higher than the equation,
			//    the entity is marked as jumping
			triggEntityJumping = 
					(triggEntityPosY - triggEntityPrevPosY > 0.2)
					? true : false;
			
			// TODO: Complete the second equation.
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
				
				// Unknown. However, it outputs a parabola
				triggEntityMovingCoefficient_movCof = Math.max(Math.min(32.0 / (1.0 + (triggEntityMovingDistance_movDis * 10.0)), 32.0), 16.0);
				
				// Unknown.
				triggEntityMovingKoefficientDivider_mofKofDiv = 1.0 + triggEntityMovingDistance_movDis * 25.0;
				
				// If the distance the entity has sunk into the block (relative to the top of the block) is less than 0.9,
				//    AND the distance the entity has sunk is not 0.0,
				//    AND the entity is marked as rotating...
				if (triggEntitySunkMod_kof1m < 0.9 && triggEntitySunkMod_kof1m != 0.0 && triggEntityRotating) {
					triggEntityMovingKoefficientDivider_mofKofDiv += mr_blackgoo * 0.005;
				}
			}
			
			// TODO: not wearing boots function
			
			if (blockMetadata == 0) {
				if (!(triggeringEntity instanceof EntityPlayer) && triggEntitySunk_kof1 < 1.25) {
					System.out.println("MotionY Beacon 1");
					triggeringEntity.motionY = 0.0; // The entity stops moving on the Y axis
					
					triggeringEntity.motionY += 0.08 + Math.min((1.25 - triggEntitySunk_kof1) / 100.0, 0.005); // Add Y velocity to the entity based on a modifier that goes between 0.005 and 0.000...
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
			// This is the part that makes the entity sink.
            // This is done either at set intervals (based on world time), or faster set intervals when the player is moving
            // If the remainder of the current world time divided by 128 is zero,
            //     OR the entity is marked as moving, AND the remainder of the current world time divided by a number that decreases the more the player moves is zero,
            //     OR the entity is marked as jumping, AND the remainder of the current world time divided by 8 is zero,
            //     OR the entity is splashing...
			if (world.getTotalWorldTime() % 128L == 0L
					|| (triggEntityMoving && world.getTotalWorldTime() % Math.max((int)Math.floor(triggEntityMovingCoefficient_movCof), 1) == 0L)
					|| (triggEntityJumping && world.getTotalWorldTime() % 8L == 0L)
					|| triggEntitySplashing) {
				
				// If the triggering entity is splashing...
				if (triggEntitySplashing) {
					
					// ...AND the previous sunk percentage is greater than 1.5 blocks
					if (triggEntityPrevSunk_kof2 > 1.5) {
						world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "game.player.swim", 0.15f, world.rand.nextFloat() * 0.25f + 0.1f); // Play sound
					}
					
					// ...AND the entity is a player, AND the number in the world's random number generator sequence is 0 (1/3 chance)...
					if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(3) == 0) {
						// TODO: Add body bubbles
					}
				}
				
				// If the entity is NOT splashing, AND the entity is moving...
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
				
				// If the entity is jumping...
				if (triggEntityJumping) {
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.slime.attack", 0.25f, world.rand.nextFloat() * 0.25f + 0.25f); // Play sound
					
					// ...AND the entity is a player, AND the number in the world's random number generator sequence equals 0 (1/5 chance)...
                    if (triggeringEntity instanceof EntityPlayer && world.rand.nextInt(5) == 0) {
                    	// TODO: Add body bubbles
                    }
				}
				
				// If the entity is NOT jumping, AND the entity is NOT moving, AND the entity is NOT splashing, AND the number in the world's random number generator sequence equals 0 (1/5 chance)...
                if (!triggEntityJumping && !triggEntityMoving && !triggEntitySplashing && world.rand.nextInt(5) == 0) {
                	
                	// ...AND the number in the world's random number generator equals 0 (1/5 chance)...
                    if (world.rand.nextInt(5) == 0) {
                        world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "liquid.water", 0.5f, world.rand.nextFloat() * 0.15f + 0.1f); // Play sound
                    }
                    else {
                        world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f); // Plaay sound
                    }
                    
                    // If the variant of the block is greater than 2, AND the remainder of the total world time divided by 32 is 0, AND the number in the world's random number generator sequence equals 0 (1/20 chance)...
                    if (blockMetadata > 2 && world.getTotalWorldTime() % 32L == 0L && world.rand.nextInt(20) == 0) {
                        // TODO: Add body bubble
                    }
                }
			}
                
            triggeringEntity.motionX = 0.0; // Make the entity stop moving
            triggeringEntity.motionZ = 0.0; // Make the entity stop moving
            
            // TODO: add boots dont float
            
            // If the entity's velocity is greater than -0.1
            // This only happens when the entity is NOT moving downwards very fast
            if (triggeringEntity.motionY > -0.1) {
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
                	triggEntitySunk_kof1 = 0.001;
                }
                
                // If the if statment above runs, this one will not run
                // In other words, this will only run if:
                // The block above this one is NOT the same as this block AND...
                // ...something unknown...
                if (triggEntitySunk_kof1 > 0.9) {
                	
                	// If the entity is marked as moving, AND something unknown...
                	if (triggEntityMoving && triggEntityMovingKoefficientDivider_mofKofDiv > 2.75) {
                		triggEntityMoving = false;
                		triggEntityMovingKoefficientDivider_mofKofDiv = 0.0;
                		
                		// If the entity is NOT in water, AND something unknown...
                		if (!triggeringEntity.isInWater() && triggEntitySunk_kof1 < 1.3) {
                			System.out.println("MotionY Beacon 5");
                			triggeringEntity.motionY += 0.025 * Math.max(Math.min((1.3 - triggEntitySunk_kof1) / 0.3, 1.0), 0.0);
                		}
                	}
                	triggEntityMovingKoefficientDivider_mofKofDiv *= 1.125;
                } else {
                	triggEntityMovingKoefficientDivider_mofKofDiv *= 1.5;
                }
                
                double sinkRateMod_mys = 0.0; // Modifies how fast the entity sinks into the block. Units are blocks. Negative is down.
                
                // If the entity is NOT a player...
                if (!(triggeringEntity instanceof EntityPlayer)) {
                    sinkRateMod_mys = 0.0; // Makes the entity sink faster than players
                }
                
                // TODO: Fix this; suction check
                /*
                if (triggEntityJumping && false) {
                	triggeringEntity.motionY -= 0.05 * blockMetadataBumped * (Math.min(0.75, triggEntitySunkMod_kof1m) + 1.0); // Subtract some unknown modifier to the entity's Y velocity
                    world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.magmacube.jump", 0.25f, 0.25f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f, false); // Play sound
                }*/
                
                if (triggEntitySunk_kof1 >= 1.2) {
                	
                	if (!triggEntitySplashing) {

	                	// TODO: More boot code
	                	if (false) {
	                		
	                	} else {
	                		
	                		// If the remainder of the total world time divided by the bumped metadata value equals 0...
	                		// ...then the value is the first one. Otherwise, it is the second one
	                		double a1 = 
	                				(world.getTotalWorldTime() % Math.max(8.0f - blockMetadataBumped, 1.0f) == 0.0f)
	                				? 0.07485 : 0.075;
	                		
	                		// EQUATION BEACON. 2nd complex
	                		// https://www.desmos.com/calculator/da7mbys8ov
	                		System.out.println("MotionY Beacon 6");
	                		triggeringEntity.motionY += a1 / Math.max((triggEntityMovingKoefficientDivider_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
	                		System.out.println("Equation Beacon 2: " + a1 / Math.max((triggEntityMovingKoefficientDivider_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
	                		
	                		triggeringEntity.onGround = false; // The entity is marked as NOT on the ground
	                		triggeringEntity.fallDistance = 0.0f; // The entity takes no fall damage
	                		
	                		// If the number in the world's random number generator sequence modified by some unknown modifer equals 0...
                            if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 20.0, 1.0))) == 0) {
                                triggeringEntity.setInWeb(); // The entity is marked as in a web
                            }
                            
                            // If the next integer in the world's random number generator sequence modified by some unknown modifer equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(2.0f + blockMetadataBumped - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                                triggeringEntity.onGround = true; // The entity is marked as on the ground
                            }
	                	}
                	}
                	
                } else {
                	
                	// TODO: Suction check function
                	
                	if (triggEntitySunk_kof1 >= 0.9) {
                		
                		// If the remainder of the total world time divided by the block's metadata value plus 1 equals 0...
                		// ...it is set to the first value. Otherwise, it is set to the second value
                		double a1 = 
                				(world.getTotalWorldTime() % Math.max(8.0f - blockMetadataBumped, 1.0f) == 0.0f)
                				? 0.07485 : 0.075 + sinkRateMod_mys;
                		
                		// EQUATION BEACON 3. 2nd complex
                		triggeringEntity.motionY += a1 / Math.max((triggEntityMovingKoefficientDivider_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
                		System.out.println("Equation Beacon 3: " + a1 / Math.max((triggEntityMovingKoefficientDivider_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6), 1.0) / Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0));
                		
                		
                		// TODO: set stuck effect
                		triggeringEntity.fallDistance = 0.0f;
                		
                		// If the entity is a player...
                		if (triggeringEntity instanceof EntityPlayer) {
                			
                			// ...AND the number in the world's random number generator sequence at some unknown index equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(4.0f + blockMetadataBumped - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                            	triggeringEntity.onGround = true;
                            }
                		} else {
                			final double mrs_blackgoo = 1 + mr_blackgoo / 10;
                			
                			System.out.println("Set Position Beacon 1: " + triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo + ", " + triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
                			triggeringEntity.setPosition(triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo, triggEntityPosY, triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
                		}
                		
                		// If the number in the world's random number generator sequence at some unknown index equals 0...
                        if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 10.0 / blockMetadataBumped, 1.0))) == 0) {
                            triggeringEntity.setInWeb(); // The entity is marked as in a web
                        }
                	} else {
                		double a2 = 
                				(world.getTotalWorldTime() % (int)Math.floor(Math.min(16.0, Math.max(16.0 - triggEntitySunk_kof1 * blockMetadataBumped * 3.0, 1.0))) == 0L)
                				? 1.01 : 1.05;
                		
                		// If the entity is a player...
                        if (triggeringEntity instanceof EntityPlayer) {

                            // ...AND the number in the world's random number generator sequence at some unknown index equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(7.0f + blockMetadataBumped * 5.0f - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                                triggeringEntity.onGround = true; // The entity is marked as on the ground
                            }
                        } else {
                        	final double mrs_blackgoo = 1 + mr_blackgoo / 10;
                        	
                        	// I suspect this is the line of code that sucks the player along the X-Z grid towards the center of the block
                            System.out.println("Set Position Beacon 2: " + triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo + ", " + triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo);
                        	triggeringEntity.setPosition(triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mrs_blackgoo, triggEntityPosY, triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mrs_blackgoo); // Unknown

                            // If the next integer in the world's random number generator sequence at some unknown index equals 0...
                            if (world.rand.nextInt(Math.max((int)Math.floor(7.0f + blockMetadataBumped * 5.0f - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
                                triggeringEntity.onGround = true; // The entity is marked as on the ground
                            }
                        }
                        
                        // TODO: is truly sink
                        
                        triggeringEntity.fallDistance = 0.0f;
                        
                        if (triggEntitySunk_kof1 >= 0.5) {
                        	
                        	// ...AND the number in the world's random number generation sequence equals 0...
                            if (world.rand.nextInt((int)Math.floor(Math.max(triggEntitySunkMod_kof1m * 5.0 / blockMetadataBumped, 1.0))) == 0) {
                            	// EQUATION BEACON 4. 3rd complex
                            	System.out.println("Equation Beacon 4");
                            	
                                triggeringEntity.setInWeb(); // The entity is marked as in a web

                                System.out.println("MotionY Beacon 8");
                                // If the entity is NOT marked as moving...
                                if (!triggEntityMoving) {
                                    triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * a2; // Unknown modifier to entity's Y velocity
                                }
                                else {
                                    triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * a2 / (triggEntityMovingKoefficientDivider_mofKofDiv + 0.025); // Unknown modifier to entity's Y velocity
                                }
                            } // ...if the entity is NOT marked as moving...
                            else if (!triggEntityMoving) {
                            	System.out.println("MotionY Beacon 9");
                                triggeringEntity.motionY += (0.075 + sinkRateMod_mys) * a2; // Unknown modifer to entity's Y velocity
                            }
                            else {
                            	System.out.println("MotionY Beacon 10");
                                triggeringEntity.motionY += (0.075 + sinkRateMod_mys) * a2 / (triggEntityMovingKoefficientDivider_mofKofDiv + 0.025); // Unknown modifer to entity's Y velocity
                            }
                        } else {
                        	triggeringEntity.setInWeb();
                        	
                        	// If the entity is marked as in water, OR the block above this one is water...
                            if (triggeringEntity.isInWater() || world.getBlockState(pos.up()).getBlock().getMaterial() == Material.water) {
                                a2 = 1.01; // Something unkown
                            } // ...if something unknown...
                            else if (triggEntitySunk_kof1 < 0.475) {
                                a2 = 1.05;
                            }

                            // CORE BEACON. Core code fragment. This makes the player "sink"
                            // If the entity is marked as not moving...
                            if (!triggEntityMoving) {
                            	System.out.println("MotionY Beacon 11.1: " + (0.0725 + sinkRateMod_mys) * a2);
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * a2; // Unknown modifier to entity's Y velocity
                                // Entities fall at a rate of 0.076125 blocks. Whatever the modifier is, it decreases the rate that the entity falls at
                            }
                            else {
                            	System.out.println("MotionY Beacon 11.2: " + (0.0725 + sinkRateMod_mys) * a2 / (triggEntityMovingKoefficientDivider_mofKofDiv + 0.025));
                                triggeringEntity.motionY += (0.0725 + sinkRateMod_mys) * a2 / (triggEntityMovingKoefficientDivider_mofKofDiv + 0.025); // Unknown modifier to entity's Y velocity
                            }

                            // TODO: truly sink function
                        }
                	}
                }
                
                // If the entity is marked as in water...
                if (triggeringEntity.isInWater()) {

                	
                    // ...AND the entity is moving upward...
                    if (triggeringEntity.motionY > 0.0) {
                    	System.out.println("MotionY Beacon 3");
                    	triggeringEntity.motionY = 0.0; // Set the entity's velocity to 0
                    }

                    System.out.println("MotionY Beacon 4");
                    triggeringEntity.motionY -= 0.01; // Modify the entity's velocity by -0.01
                }
                
                // TODO: anti hold jump script
            } else {
            	// TODO: set stuck effect
            	
            	triggeringEntity.setInWeb();
            }
            /*
            System.out.println("triggEntityMovingDistance_movDis: " + triggEntityMovingDistance_movDis);
            System.out.println("triggEntityMovingCoefficient_movCof: " + triggEntityMovingCoefficient_movCof);
            System.out.println("triggEntityMovingKoefficientDivider_mofKofDiv: " + triggEntityMovingKoefficientDivider_mofKofDiv);
            System.out.println("mr_blackgoo: " + mr_blackgoo);*/
		} else {
			
			if (triggEntitySunk_kof1 < 1.45) {
				triggeringEntity.setInWeb();
			}
			
			// TODO: handle mud tentacles
		}/*
		System.out.println("triggEntitySunk_kof1: " + triggEntitySunk_kof1);
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
	
	// Obtains the special name of the block variant.
	// This is used to add a custom name to a block variant in the language file
	@Override
	public String getSpecialName(ItemStack stack) {
		return Mud.types[stack.getItemDamage()];
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
	
	// Returns types of metadata for the block
	public String[] getTypes() {
		return types;
	}
	
	// Returns if only one texture should be used for all metadata types
	public Boolean getUseOneTexture() {
		return useOneTexture;
	}
}
