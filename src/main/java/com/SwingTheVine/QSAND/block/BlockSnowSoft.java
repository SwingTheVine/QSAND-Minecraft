package com.SwingTheVine.QSAND.block;

import java.util.List;
import java.util.Random;

import com.SwingTheVine.QSAND.util.BeaconHandler;
import com.SwingTheVine.QSAND.util.ConfigHandler;
import com.SwingTheVine.QSAND.util.QuicksandManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Implements Soft Snow block calculation and physics.
 * 
 * @since <b>0.43.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class BlockSnowSoft extends SinkingBlock implements IMetaBlockName {
	
	private static final String[] types = {"0", "1"}; // Values of the different metadata levels
	private static final boolean useOneTexture = true; // Should all metadata variants use the same texture?
	
	// Creates a metadata value for every "types" metadata value
	public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, Integer.valueOf(types.length - 1));
	
	private static final float[] sinkTypes = {1.00F, 1.00F}; // The maximum sink level for each metadata variant
	private final BeaconHandler beacon = new BeaconHandler(false); // Constructs a beacon handler. Enabled if "true" passed in
	
	// Constructor
	public BlockSnowSoft(final Material material) {
		super(material);
		this.setHardness(0.75f);
		this.setStepSound(Block.soundTypeSnow);
		this.setResistance(1.0f);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SINK, Integer.valueOf(0))); // Default metadata values
																										// for the block
		
		// Makes the block opaque if the user desires
		final int opacity = (ConfigHandler.makeBlocksOpaque) ? 255 : 3;
		this.setLightOpacity(opacity);
	}
	
	// Changes the collision box. This is not the texture bounding box. This is not the hitbox.
	// This function changes the height of the block to make the entity "sink" into the block by the "sinkType" value
	// corresponding to the metadata value of the block. (i.e. meta=0 means reduce height by 0.35)
	@Override
	public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
		
		return null;
	}
	
	// What to do when an entity is INSIDE the block
	// This is the core of the Soft Snow calculations
	@Override
	public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state,
		final Entity triggeringEntity) {
		
		final double triggEntityPosY = triggeringEntity.posY; // Triggering entity's Y position
		final double triggEntityPrevPosY = triggeringEntity.prevPosY; // Triggering entity's previous Y position
		final double triggEntityVelY = triggeringEntity.motionY; // Triggering entity's Y velocity
		
		// Triggering entity's distance sunk into the block.
		// First statement is when the entity is a player.
		// Second statement is when the entity is NOT a player
		final double triggEntitySunk;
		double triggEntitySunk_kof1 = (triggeringEntity instanceof EntityPlayer)
			? (pos.getY() - triggEntityPosY + 1.0)
			: (pos.getY() - triggEntityPosY - 0.5);
			
		// Triggering entity's distance previously sunk into the block
		// First statement is when the entity is a player.
		// Second statement is when the entity is NOT a player
		final double triggEntityPrevSunk, triggEntityPrevSunk_kof2 = (triggeringEntity instanceof EntityPlayer)
			? Math.max((pos.getY() - triggEntityPrevPosY + 1.0), 0.0)
			: Math.max((pos.getY() - triggEntityPrevPosY - 0.5), 0.0);
			
		final double triggEntitySunkMod_kof1m = Math.max(triggEntitySunk_kof1, 0.0); // A modified version of trigger entity sunk
		final int blockMetadata = state.getValue(SINK).intValue(); // Obtains this block's variant/metadata. TODO: Changed. Same
																	// as mud
		
		// If the triggering entity is living (as opposed to a death animation)...
		if (triggeringEntity instanceof EntityLivingBase) {
			final boolean triggEntityAffected = true; // Should the triggering entity be affected by this block?
			boolean triggEntityJumping = false; // Is the triggering entity jumping?
			boolean triggEntityMoving = false; // Is the triggering entity moving?
			boolean triggEntitySplashing = false; // Is the triggering entity splashing?
			boolean triggEntityRotating = false; // Is the triggering entity rotating?
			final float blockMetadataBumped = 1.0f; // TODO: Quicksand Changed 10 -> 1
			double triggEntityMovingDistance_movDis = 1.0;
			double forceFlavorEvent_movCof = 16.0; // Forces the block to attempt to spawn a flavor event (either bubbles, sound,
													// or both)
			double movementPunish_mofKofDiv = 1.0; // Punishes the entity for moving. Makes them sink faster.
			
			// EQUATION BEACON. 1st complex
			// Increases the punishment value the LESS the entity is sunk in the block
			// Starts at a specified number not exceeding 145 and hits 0 when player sunk 1.2 blocks.
			// Then, mr_blackgoo goes back up towards 145.
			// It is parabolic in nature.
			// https://www.desmos.com/calculator/wgkirt9ra1
			final int mr_blackgoo = (int) Math.min(
				5.0 + Math.floor(Math.max(0.0, blockMetadataBumped * 10.0f * (1.5 - triggEntitySunkMod_kof1m))), 60.0); // TODO:
																														// Changed
																														// Quicksand
																														// 145 ->
																														// 60
				
			// If the entity has been marked as NOT affected by this block...
			if (!triggEntityAffected) {
				this.runSubmergedChecks(triggeringEntity);
			}
			
			// If the entity moves downwards with a high velocity, they are marked as splashing.
			// metadata 1 = -0.100 or faster (Mud)
			// metadata 2 = -0.100 or faster (Thinnish Mud)
			// metadata 3 = -0.067 or faster (Deep Mud)
			// metadata 4 = -0.050 or faster (Bottomless Mud)
			triggEntitySplashing = (triggeringEntity.motionY < -0.1) ? true : false;
			
			// If the entity moves upwards with a high velocity, they are marked as jumping.
			triggEntityJumping = (triggEntityPosY - triggEntityPrevPosY > 0.1) // Changed. Quicksand 0.2 -> 0.1
				? true
				: false;
				
			// If the entity is NOT a player, AND the entity has moved their camera along the Yaw axis...
			// OR if the entity is a player, and this is multiplayer, AND the server instance has detected Yaw axis movement...
			triggEntityRotating = ((!(triggeringEntity instanceof EntityPlayer)
				&& ((EntityLivingBase) triggeringEntity).prevRotationYaw != ((EntityLivingBase) triggeringEntity).rotationYaw)
				|| ((triggeringEntity instanceof EntityPlayer) && world.isRemote
					&& Math
						.abs(ConfigHandler.serverInstancePreRenderYaw - ConfigHandler.serverInstanceRenderYaw) > 10.0))
							? true
							: false;
							
			// If the entity is marked as rotating, OR the entity has moved farther than 0.0015 blocks along the X/Z axis...
			// TODO: Quicksand changed 0.001 -> 0.0015 -> 0.001
			if (triggEntityRotating || Math.abs(triggeringEntity.prevPosX - triggeringEntity.posX) > 0.001
				|| Math.abs(triggeringEntity.prevPosZ - triggeringEntity.posZ) > 0.001) {
				
				triggEntityMoving = true; // The entity is moving
				
				// Finds the hypotenuse of the distance traveled.
				// This is the actual distance traveled on a radical plane
				triggEntityMovingDistance_movDis = Math
					.pow(Math.pow(triggeringEntity.prevPosX - triggeringEntity.posX, 2.0)
						+ Math.pow(triggeringEntity.prevPosZ - triggeringEntity.posZ, 2.0), 0.5);
						
				// Exponentially increases the chance of a bubble spawn event the more the entity moves forwards.
				// Outputs as a number between 16 and 32. Lower number equals higher chance
				// https://www.desmos.com/calculator/rxdyif2tis
				forceFlavorEvent_movCof = Math
					.max(Math.min(32.0 / (1.0 + (triggEntityMovingDistance_movDis * 10.0)), 32.0), 16.0);
					
				// Base punishment for punishing the player for moving.
				// This value will be modified later
				movementPunish_mofKofDiv = 1.0 + triggEntityMovingDistance_movDis / 2.0; // TODO: Quicksand Changed * 25.0 -> /
																							// 2.0
				
				// If the entity has sunk less than 0.9 blocks,
				// AND the entity HAS sunk into the block,
				// AND the entity is marked as rotating...
				if (triggEntitySunkMod_kof1m < 0.9 && triggEntitySunkMod_kof1m != 0.0 && triggEntityRotating) {
					
					// TODO: Quicksand Changed 0.005 -> 0.003 -> 0.005
					movementPunish_mofKofDiv += mr_blackgoo * 0.005; // Amplify the punishment value by 0.005
				}
			}
			
			// TODO: Quicksand Changed. Removed instanceof slime
			
			// TODO: Quicksand Changed. Removed not player, not affected
			
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
			
			// TODO: Quicksand Changed. Removed STATEMENT BEACON 1
			// In its place, this function below is added
			
			if ((triggEntityMoving
				&& world.getTotalWorldTime() % Math.max((int) Math.floor(forceFlavorEvent_movCof / 1.5), 1) == 0L)
				|| (triggEntityJumping && world.getTotalWorldTime() % 8L == 0L) || triggEntitySplashing) {
				
				if (triggEntitySplashing && triggEntityPrevSunk_kof2 > 1.5) {
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "step.snow",
						1.0f, world.rand.nextFloat() * 0.5f + 0.5f);
				}
				
				if (!triggEntitySplashing && triggEntityMoving && world.rand.nextInt(2) == 0) {
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "dig.snow",
						1.0f, world.rand.nextFloat() * 0.5f + 0.5f);
				}
				
				if (triggEntityJumping) {
					world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "dig.cloth",
						0.75f, world.rand.nextFloat() * 0.5f + 0.5f);
				}
			}
			
			// TODO: Quicksand Changed. Removed body bubble
			
			triggeringEntity.motionX = 0.0; // Make the entity stop moving
			triggeringEntity.motionZ = 0.0; // Make the entity stop moving
			
			// If the entity's velocity is greater than -0.1...
			if (triggeringEntity.motionY > -0.1) {
				// This only happens when the entity is NOT moving downwards very fast
				beacon.logBeacon("MotionY", "2.1", triggeringEntity.motionY);
				triggeringEntity.motionY = 0.0; // Make the entity stop moving
			} else {
				beacon.logBeacon("MotionY", "2.2", triggeringEntity.motionY);
				triggeringEntity.motionY /= 2.0; // Slow the downwards motion of the entity by 100%
			}
			
			// STATEMENT BEACON 2
			// If the block above this one is NOT the same as this block,
			// OR the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this
			// block...
			if (world.getBlockState(pos.up(1)) != this
				|| (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this)) {
				
				// If the block above this one is the same as this block, AND the block 2 blocks above this one is the same as
				// this block...
				if (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this) {
					triggEntitySunk_kof1 = 0.001; // Mark the distance the entity has sunk as 0.001 blocks
				}
				
				boolean underSnow = false; // TODO: Quicksand Changed. Added under snow
				
				// TODO: Quicksand Changed. Added with variable above
				if (triggEntitySunk_kof1 > 0.65 || world.getBlockState(pos.down()) == this) {
					underSnow = true;
				}
				
				double sinkRateMod_mys = 0.0; // Modifies how fast the entity sinks into the block. Units are blocks. Negative is
												// down.
				
				// If the entity is NOT a player...
				if (!(triggeringEntity instanceof EntityPlayer)) {
					sinkRateMod_mys = 0.0; // Makes the non-player entity sink faster when negative
				}
				
				// TODO: Quicksand Changed. Removed suction check
				
				// If the entity has sunk greater than or equal to 1.2 blocks...
				if (triggEntitySunk_kof1 >= 1.2) {
					
					// TODO: Quicksand changed. Removed not splashing
					// All below is new
					
					final double thicknessLower = 0.07485;
					
					triggeringEntity.motionY += thicknessLower
						/ Math.max(
							(movementPunish_mofKofDiv - 1.0)
								* (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6 * blockMetadataBumped) * 17.5,
							1.0)
						/ Math.pow(Math.max(triggEntitySunkMod_kof1m / 1.25, 1.0), 2.0);
					triggeringEntity.onGround = true;
					triggeringEntity.fallDistance = 0.0f;
					
					// TODO: else -> else if
				} else if (triggEntitySunk_kof1 >= 0.9) {
					
					// TODO: Quicksand Changed. Removed suction check
					
					// Note: will never be greater than or equal to 1.2
					
					// TODO: Changed. Removed sink rate modifer from output when false. 0.07485 -> 0.0748
					// TODO: Quicksand Changed. Added sink rate modifer back and 0.085 mod straight
					final double thicknessLower = 0.085 + sinkRateMod_mys;
					
					// TODO: Quicksand Changed. Removed EQUATION BEACON 3. 2nd complex
					QuicksandManager.setStuckEffect((EntityLivingBase) triggeringEntity, mr_blackgoo); // Makes the entity stuck
					
					// TODO: Quicksand Changed. Removed old code. All new...
					
					if (!triggEntitySplashing) {
						if (triggEntityMoving || world.rand
							.nextInt(Math.max((int) Math.floor(75.0 - triggEntitySunk_kof1 * 5.0), 1)) == 0) {
							triggeringEntity.motionY += thicknessLower / Math.max(1.0, 5.0 - triggEntitySunk_kof1);
							if (!triggEntityMoving && underSnow) {
								world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ,
									"dig.snow", 1.0f, world.rand.nextFloat() * 0.5f + 0.5f, false);
							}
						} else {
							triggeringEntity.motionY += thicknessLower;
						}
						triggeringEntity.onGround = true;
						triggeringEntity.fallDistance = 0.0f;
					}
					if (triggeringEntity instanceof EntityPlayer) {
						if (world.getTotalWorldTime()
							% Math.max(1.0 + Math.floor(Math.max(triggEntitySunk_kof1 * 5.0 - blockMetadata / 10, 0.0)),
								1.0) == 0.0) {
							triggeringEntity.setInWeb();
							triggeringEntity.onGround = false;
						}
					} else {
						final double mw_kof = 1 + mr_blackgoo / 10;
						triggeringEntity.setPosition(
							triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mw_kof,
							triggEntityPosY,
							triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mw_kof);
					}
					
				} else { // STOPPED HERE
					
					// TODO: Changed. 16.0 -> 4.0 16.0 -> 8.0 BumpedMetadata removed. 3.0 -> 8.0
					// TODO: Quicksand Changed. Added sink rate modifer back and 0.085 mod straight
					final double thicknessHigher = 0.085 + sinkRateMod_mys;
					
					// If the number in the world's random number generator sequence equals 0...
					// TODO: Changed. 7.0f + bumpedmetadata removed. 5.0f -> 50.0 Quicksand 50.0 -> 15.0
					if (world.rand.nextInt(Math.max(
						(int) Math.floor(50.0 - Math.pow(Math.max(triggEntitySunkMod_kof1m, 0.0), 1.5)), 1)) == 0) {
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
						QuicksandManager.setStuckEffect((EntityLivingBase) triggeringEntity, mr_blackgoo);
					}
					
					triggeringEntity.fallDistance = 0.0f; // Resets the accumulated fall distance to 0
					
					// If the entity has sunk greater than or equal to 0.5 blocks...
					if (triggEntitySunk_kof1 >= 0.6) { // TODO: Quicksand changed. 0.5 -> 0.6
						
						// ...AND the number in the world's random number generation sequence equals 0...
						// TODO: Quicksand changed. Added entity moving, changed equation
						if (world.rand.nextInt(
							Math.min(Math.max((int) Math.floor(75.0 - triggEntitySunk_kof1 * 25.0), 1), 75)) == 0) {
							// Happens more frequently the less the entity is sunk in the block
							// 1/1 to 1/2 chance (Bottomless Mud)
							// 1/1 to 1/3 chance (Deep Mud) TODO: Update
							// 1/2 to 1/4 chance (Thinnish Mud)
							// 1/5 to 1/9 chance (Mud)
							
							// TODO: Quicksand changed. Removed setInWeb
							
							// If the entity is NOT marked as moving...
							if (!triggEntityMoving) {
								beacon.logBeacon("MotionY", "8.1");
								// Bumps the entity downwards slightly.
								
								// TODO: Quicksand Changed. Equation changed
								triggeringEntity.motionY += thicknessHigher / Math.max(2.0, 5.0 - triggEntitySunk_kof1);
								
								
								// TODO: Quicksand added
								if (underSnow) {
									world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ,
										"dig.snow", 1.0f, world.rand.nextFloat() * 0.5f + 0.5f, false);
								}
							} else if (world.rand.nextInt(
								Math.min(Math.max((int) Math.floor(75.0 - triggEntitySunk_kof1 * 25.0), 1), 10)) == 0) { // TODO:
																															// Quicksand
																															// added
								
								// TODO: Quicksand Changed. Equation changed
								triggeringEntity.motionY += thicknessHigher / Math.max(2.0, 5.0 - triggEntitySunk_kof1);
								
								
								// TODO: Quicksand added
								if (underSnow) {
									world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ,
										"dig.snow", 1.0f, world.rand.nextFloat() * 0.5f + 0.5f, false);
								}
								
							} else {
								beacon.logBeacon("MotionY", "8.2");
								// TODO: Quicksand changed equation
								triggeringEntity.motionY += thicknessHigher;
							}
						} else { // TODO: Quciksand changed. Removed else if
							beacon.logBeacon("MotionY", "10");
							// TODO: Quicksand changed equation
							triggeringEntity.motionY += thicknessHigher;
						}
					} else {
						triggeringEntity.setInWeb();
						
						// TODO: Quicksand changed. Removed thicknessHigher amplification change
						
						// TODO: Quicksand Changed. Added parent if statement
						if (triggEntityMoving || world.rand.nextInt(
							Math.min(Math.max((int) Math.floor(75.0 - triggEntitySunk_kof1 * 25.0), 1), 75)) == 0) {
							
							// If the entity is marked as not moving...
							if (!triggEntityMoving) {
								
								beacon.logBeacon("MotionY", "11.1", (0.0725 + sinkRateMod_mys) * thicknessHigher);
								// Moves the entity downwards slightly.
								triggeringEntity.motionY--; // TODO: Quicksand changed. Added in place of other motion Y
								
								if (underSnow) { // TODO: Quicksand changed. Added
									world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ,
										"dig.snow", 1.0f, world.rand.nextFloat() * 0.5f + 0.5f, false);
								}
							} else if (world.rand.nextInt(
								Math.min(Math.max((int) Math.floor(75.0 - triggEntitySunk_kof1 * 25.0), 1), 35)) == 0) {
								// TODO: Quicksand changed. Added else if statement
								
								// Moves the entity downwards slightly.
								triggeringEntity.motionY--; // TODO: Quicksand changed. Added in place of other motion Y
								
								if (underSnow) { // TODO: Quicksand changed. Added
									world.playSound(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ,
										"dig.snow", 1.0f, world.rand.nextFloat() * 0.5f + 0.5f, false);
								}
							} else {
								beacon.logBeacon("MotionY", "11.2",
									(0.0725 + sinkRateMod_mys) * thicknessHigher / (movementPunish_mofKofDiv + 0.025));
								// Bumps the entity downwards slightly.
								triggeringEntity.motionY--; // TODO: Quicksand changed. Added in place of other motion Y
							}
							
							// TODO: Quicksand changed. 0.5 -> 0.45
							// If the entity has sunk less than 0.5 blocks into this one, AND the entity is truly sinking...
							if (triggEntitySunk_kof1 < 0.45
								&& QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunk_kof1)) {
								QuicksandManager.setStuckEffect((EntityLivingBase) triggeringEntity, 145); // TODO: Quicksand
																											// changed. 145 -> 60
							}
						}
					}
				}
				
				// TODO: Quicksand changed. Added
				if (world.getTotalWorldTime()
					% Math.max(1.0 + Math.floor(Math.max(triggEntitySunk_kof1 * 10.0, 0.0)), 1.0) == 0.0) {
					triggeringEntity.onGround = false;
				}
				
				// If the entity is marked as in water...
				if (triggeringEntity.isInWater()) {
					
					triggeringEntity.motionY /= 4.0;
				}
			} else {
				// If the entity has sunk less than 0.5 blocks into this one, AND the entity is truly sinking...
				if (triggEntitySunk_kof1 < 0.5
					&& QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunkMod_kof1m)) {
					QuicksandManager.setStuckEffect((EntityLivingBase) triggeringEntity, 145); // TODO: Quicksand changed. 145 ->
																								// 60
				}
				
				triggeringEntity.setInWeb();
			}
			
			// Prevents the entity from continuously jumping
			QuicksandManager.antiHoldJumpScript(triggeringEntity, triggEntitySunk_kof1, true);
			
		} else if (triggEntitySunk_kof1 > 1.25) {
			// TODO: Quicksand Changed. Added
			triggeringEntity.motionX *= 0.5880000114440918;
			triggeringEntity.motionZ *= 0.5880000114440918;
			triggeringEntity.motionY = 0.0;
			if (triggEntitySunk_kof1 < 1.45) {
				triggeringEntity.motionY += 0.1;
			} else {
				triggeringEntity.motionY += 0.03999999910593033;
			}
		} else {
			// TODO: Quicksand Changed. Added
			triggeringEntity.motionX = 0.0;
			triggeringEntity.motionZ = 0.0;
			triggeringEntity.motionY = 0.0;
		}
	}
	
	// Declares that this block ID has metadata values.
	// Declares the metadata values for this block.
	// The metadata value corrosponds with its index in "types".
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(final Item block, final CreativeTabs creativeTabs, final List<ItemStack> list) {
		
		// For every index in "types"...
		for (int indexType = 0; indexType < types.length; indexType++) {
			list.add(new ItemStack(block, 1, indexType)); // Declare a metadata variant with that index value
		}
	}
	
	// Obtains the block (with metadata) when the player picks it (using Middle Mouse Button)
	@Override
	public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final BlockPos pos,
		final EntityPlayer player) {
		
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	// Obtains the metadata this block should drop.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public int damageDropped(final IBlockState state) {
		
		return this.getMetaFromState(state);
	}
	
	// Obtains if the specific side of the block is solid.
	@Override
	public boolean isSideSolid(final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
		
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
	public String getSpecialName(final ItemStack stack) {
		
		return BlockSnowSoft.types[stack.getItemDamage()];
	}
	
	// Returns the block's metadata through damage value
	public int getDamageValue(final World world, final int blockPosX, final int blockPosY, final int blockPosZ) {
		
		return world.getBlockState(new BlockPos(blockPosX, blockPosY, blockPosZ)).getBlock().getDamageValue(world,
			new BlockPos(blockPosX, blockPosY, blockPosZ));
	}
	
	// Sets the tooltips that should be added to the block
	// Leave blank for no tooltip
	@Override
	public void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
		
		list.add(StatCollector.translateToLocal("mfqm.tooltip_1"));
	}
	
	// TODO: Quicksand changed. Added
	// Makes the block "fall" when air is below it
	@Override
	public void updateTick(final World world, final BlockPos pos, final IBlockState state, final Random rand) {
		
		if (!world.getBlockState(pos.down()).getBlock().getMaterial().isSolid()
			&& world.getBlockState(pos.down()).getBlock().getMaterial().isReplaceable()) {
			world.setBlockState(pos.down(), this.getDefaultState(), 3);
			world.setBlockToAir(pos);
			return;
		}
		super.updateTick(world, pos, state, rand);
	}
	
	// TODO: Quicksand changed. Added
	@Override
	protected boolean canSilkHarvest() {
		
		return true;
	}
	
	// TODO: Quicksand changed. Added
	@Override
	public boolean canDropFromExplosion(final Explosion par1Explosion) {
		
		return false;
	}
	
	// TODO: Quicksand changed. Added
	@Override
	public void harvestBlock(final World world, final EntityPlayer player, final BlockPos pos, final IBlockState state,
		final TileEntity te) {
		
		if (EnchantmentHelper.getSilkTouchModifier(player)) {
			QuicksandManager.dropItem(world, pos.getX(), pos.getY(), pos.getZ(),
				new ItemStack(this, 1, state.getBlock().damageDropped(state)));
			world.markBlockForUpdate(pos);
			return;
		}
		super.harvestBlock(world, player, pos, state, te);
	}
	
	// TODO: Quicksand changed. Added
	@Override
	public float getPlayerRelativeBlockHardness(final EntityPlayer player, final World world, final BlockPos pos) {
		
		if (!((Entity) player).onGround) {
			return 7.5E-5f;
		}
		final float f = this.getBlockHardness(world, pos);
		return ForgeHooks.blockStrength(this.getDefaultState(), player, world, pos);
	}
	
	// TODO: Quicksand changed. Added
	@Override
	public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
		
		return true;
	}
	
	// TODO: Quicksand changed. Added
	@Override
	public void onBlockAdded(final World world, final BlockPos pos, final IBlockState state) {
		
		world.scheduleBlockUpdate(pos, this, this.tickRate(world), 1);
	}
	
	// TODO: Quicksand changed. Added
	@Override
	public void onNeighborBlockChange(final World world, final BlockPos pos, final IBlockState state,
		final Block neighborBlock) {
		
		if (world.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
			world.setBlockToAir(pos.up());
		}
		world.scheduleBlockUpdate(pos, this, this.tickRate(world), 1);
	}
	
	// TODO: Quicksand changed. Added
	@Override
	public Item getItemDropped(final IBlockState state, final Random random, final int fortune) {
		
		if (state.getValue(SINK) == 1) {
			return null;
		}
		return super.getItemDropped(state, random, fortune);
	}
	
	// Checks to see if the entity is fully submerged in the block
	public void runSubmergedChecks(final Entity triggeringEntity) {
		
		// If the entity is inside of this block, AND the entity is marked as drowning...
		if (QuicksandManager.isEntityInsideOfBlock(triggeringEntity, this)
			&& QuicksandManager.isDrowning(triggeringEntity)) {
			QuicksandManager.spawnDrowningBubble(triggeringEntity.worldObj, triggeringEntity, this, false); // Spawn drowning
																											// bubbles
			
			// ...AND the world is NOT on a server, AND the entity is marked as alive...
			if (!triggeringEntity.worldObj.isRemote && triggeringEntity.isEntityAlive()) {
				triggeringEntity.attackEntityFrom(DamageSource.drown,
					Math.max(((EntityLivingBase) triggeringEntity).getMaxHealth() * 0.1f, 2.0f)); // Deals 10% of max health or
																									// 2hp in damage. Whichever is
																									// greater
			}
		}
	}
	
	// Creates a new block state for this block ID.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	protected BlockState createBlockState() {
		
		return new BlockState(this, new IProperty[] {SINK});
	}
	
	// Obtains the block's metadata from the block's blockstate.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public int getMetaFromState(final IBlockState state) {
		
		return state.getValue(SINK).intValue();
	}
	
	// Obtains the block's blockstate from the block's metadata.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public IBlockState getStateFromMeta(final int meta) {
		
		return getDefaultState().withProperty(SINK, Integer.valueOf(meta));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getQuicksandBlockColor() {
		
		return 16777215;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getQuicksandRenderColor(final IBlockState state) {
		
		final int metadata = state.getValue(SINK);
		if (metadata == 1) {
			return 15658734;
		}
		return 16777215;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getQuicksandColorMultiplier(final IBlockAccess world, final BlockPos pos, final int renderPass) {
		
		final int metadata = world.getBlockState(pos).getValue(SINK);
		if (metadata == 1) {
			return 15658734;
		}
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
