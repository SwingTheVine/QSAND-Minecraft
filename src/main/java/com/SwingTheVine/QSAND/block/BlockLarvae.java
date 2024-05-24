package com.SwingTheVine.QSAND.block;

import java.util.List;
import java.util.Random;

import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;
import com.SwingTheVine.QSAND.tileentity.TileEntityLarvae;
import com.SwingTheVine.QSAND.util.BeaconHandler;
import com.SwingTheVine.QSAND.util.ConfigHandler;
import com.SwingTheVine.QSAND.util.QuicksandManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLarvae extends SinkingBlock implements IMetaBlockName {
	private static final String[] types = {"0"}; // Values of the different metadata levels
	private static final boolean useOneTexture = true; // Should all metadata variants use the same texture?
	private static final float[] sinkTypes = {1.00F}; // The maximum sink level for each metadata variant
	private BeaconHandler beacon = new BeaconHandler(false); // Constructs a beacon handler. Enabled if "true" passed in
	
	// Constructor
	public BlockLarvae(Material material) {
		super(material);
		this.setHardness(2.0f); // Sets the hardness of the block
		this.setResistance(1.0f);
		this.setStepSound(Block.soundTypeGrass); // Sets the sound that plays when the block is stepped on
	}
	
	public TileEntity createNewTileEntity(final World world, final int i) {
        if (world.isRemote) {
            final TileEntityLarvae TE = new TileEntityLarvae();
            TE.phase = world.rand.nextDouble() * 6.28318 * 2.0;
            return TE;
        }
        return null;
    }
	
	@SideOnly(Side.CLIENT)
	public void setBlockBoundsForItemRender() {
		final float f = 0.75f;
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, f, 1.0f);
	}
	
	@SideOnly(Side.CLIENT)
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		final float f = 0.75f;
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, f, 1.0f);
	}
	
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        //return side.getIndex() == 1;
		return true;
    }
	
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered2(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
        switch (par5) {
            case 0: {
                return (!par1IBlockAccess.getBlockState(new BlockPos(par2, par3, par4)).getBlock().isOpaqueCube() 
                		&& par1IBlockAccess.getBlockState(new BlockPos(par2, par3, par4)).getBlock() != QSAND_Blocks.larvae) 
                		|| (par1IBlockAccess.getBlockState(new BlockPos(par2, par3, par4)).getBlock() == QSAND_Blocks.larvae 
                		&& par1IBlockAccess.getBlockState(new BlockPos(par2, par3 + 1, par4)).getBlock() != QSAND_Blocks.larvae);
            }
            case 1: {
                return par1IBlockAccess.getBlockState(new BlockPos(par2, par3 + 1, par4)).getBlock() != QSAND_Blocks.larvae;
            }
            default: {
                return !par1IBlockAccess.getBlockState(new BlockPos(par2, par3 - 1, par4)).getBlock().isOpaqueCube() && par1IBlockAccess.getBlockState(new BlockPos(par2, par3 - 1, par4)).getBlock() != QSAND_Blocks.larvae;
            }
        }
    }
	
	// Changes the collision box. This is not the texture bounding box. This is not the hitbox.
	// This function changes the height of the block to make the entity "sink" into the block by the "sinkType" value corresponding to the metadata value of the block. (i.e. meta = 0 means reduce height by 0.35)
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }
    
	// What to do when an entity is INSIDE the block
	// This is the core of the quicksand calculations
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
		final int blockMetadata = 0; // Obtains this block's variant/metadata
		
		// If the triggering entity is living (as opposed to a death animation)...
		if (triggeringEntity instanceof EntityLivingBase) {
			boolean triggEntityAffected = true; // Should the triggering entity be affected by this block?
			boolean triggEntityJumping = false; // Is the triggering entity jumping?
			boolean triggEntityMoving = false; // Is the triggering entity moving?
			boolean triggEntitySplashing = false; // Is the triggering entity splashing?
			boolean triggEntityRotating = false; // Is the triggering entity rotating?
			final float blockMetadataBumped = 1.0f;
			double triggEntityMovingDistance_movDis = 1.0;
			double forceFlavorEvent_movCof = 16.0; // Forces the block to attempt to spawn a flavor event (either bubbles, sound, or both)
			double movementPunish_mofKofDiv = 1.0; // Punishes the entity for moving. Makes them sink faster.
			
			// EQUATION BEACON. 1st complex
			// Increases the punishment value the LESS the entity is sunk in the block
			// Starts at a specified number not exceeding 145 and hits 0 when player sunk 1.2 blocks.
			// Then, mr_blackgoo goes back up towards 145.
			// It is parabolic in nature.
			// https://www.desmos.com/calculator/wgkirt9ra1
			final int mr_blackgoo = (int)Math.min(5.0 + Math.floor(Math.max(0.0, blockMetadataBumped * 100.0f * (1.5 - triggEntitySunkMod_kof1m))), 128.0);
			
			// If the entity moves downwards with a high velocity, they are marked as splashing.
			//    metadata 1 = -0.100 or faster (Mud)
			//    metadata 2 = -0.100 or faster (Thinnish Mud)
			//    metadata 3 = -0.067 or faster (Deep Mud)
			//    metadata 4 = -0.050 or faster (Bottomless Mud)
			triggEntitySplashing =
					(triggeringEntity.motionY < -0.1)
					? true : false;
			
			// If the entity moves upwards with a high velocity, they are marked as jumping.
			triggEntityJumping = 
					(triggEntityPosY - triggEntityPrevPosY > 0.1)
					? true : false;
			
			// If the entity is NOT a player, AND the entity has moved their camera along the Yaw axis...
			// OR if the entity is a player, and this is multiplayer, AND the server instance has detected Yaw axis movement...
			triggEntityRotating = 
					((!(triggeringEntity instanceof EntityPlayer) && ((EntityLivingBase)triggeringEntity).prevRotationYaw != ((EntityLivingBase)triggeringEntity).rotationYaw)
							|| ((triggeringEntity instanceof EntityPlayer) && world.isRemote && Math.abs(ConfigHandler.serverInstancePreRenderYaw - ConfigHandler.serverInstanceRenderYaw) > 10.0))
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
				forceFlavorEvent_movCof = Math.max(Math.min(32.0 / (1.0 + (triggEntityMovingDistance_movDis * 10.0)), 32.0), 16.0);
				
				// Base punishment for punishing the player for moving.
				// This value will be modified later
				movementPunish_mofKofDiv = 1.0 + triggEntityMovingDistance_movDis / 2.0;
				
				// If the entity has sunk less than 0.9 blocks,
				//    AND the entity HAS sunk into the block,
				//    AND the entity is marked as rotating...
				if (triggEntitySunkMod_kof1m < 0.9 && triggEntitySunkMod_kof1m != 0.0 && triggEntityRotating) {
					movementPunish_mofKofDiv += mr_blackgoo * 0.005; // Amplify the punishment value by 0.005
				}
			}
                
            triggeringEntity.motionX = 0.0; // Make the entity stop moving
            triggeringEntity.motionZ = 0.0; // Make the entity stop moving
            
            // If the entity's velocity is greater than -0.1...
            if (triggeringEntity.motionY > -0.1) {
                // This only happens when the entity is NOT moving downwards very fast
            	beacon.logBeacon("MotionY", "2.1", triggeringEntity.motionY);
            	triggeringEntity.motionY = 0.0; // Make the entity stop moving
            } else {
            	beacon.logBeacon("MotionY", "2.2", triggeringEntity.motionY);
            	triggeringEntity.motionY /= 2.0; // Slow the downwards motion of the entity by 50%
            }
            
            if ((triggEntityMoving && world.getTotalWorldTime() % Math.max((int)Math.floor(forceFlavorEvent_movCof / 1.5), 1) == 0L) || (triggEntityJumping && world.getTotalWorldTime() % 8L == 0L) || triggEntitySplashing) {
                if (triggEntitySplashing && triggEntityPrevSunk_kof2 > 1.5) {
                    world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.5f, world.rand.nextFloat() * 0.25f + 0.25f);
                }
                if (!triggEntitySplashing && triggEntityMoving && world.rand.nextInt(2) == 0) {
                    world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f);
                }
                if (triggEntityJumping) {
                    world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.75f, world.rand.nextFloat() * 0.15f + 0.85f);
                }
            }
            
            // STATEMENT BEACON 2
            // If the block above this one is NOT the same as this block,
            //    OR the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
            if (world.getBlockState(pos.up(1)) != this || (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this)) {

                // If the block above this one is the same as this block, AND the block 2 blocks above this one is the same as this block...
                if (world.getBlockState(pos.up(1)) == this && world.getBlockState(pos.up(2)) == this) {
                	triggEntitySunk_kof1 = 0.001; // Mark the distance the entity has sunk as 0.001 blocks
                }
                
                double sinkRateMod_mys = 0.0; // Modifies how fast the entity sinks into the block. Units are blocks. Negative is down.
                
                // If the entity is NOT a player...
                if (!(triggeringEntity instanceof EntityPlayer)) {
                    sinkRateMod_mys = 0.0; // Makes the non-player entity sink faster when negative
                }
                
                
                if (triggEntitySunk_kof1 >= 1.2) {
                	final double thickness = 0.06 + sinkRateMod_mys;
                	triggeringEntity.motionY += thickness / Math.max((movementPunish_mofKofDiv - 1.0) * (Math.max(triggEntitySunkMod_kof1m - 0.5, 0.75) * 1.6 * blockMetadataBumped) * 17.5, 1.0) / Math.pow(Math.max(triggEntitySunk_kof1 / 1.25, 1.0), 2.0);
                	triggeringEntity.onGround = false;
                	triggeringEntity.fallDistance = 0.0f;
                	
                	if (world.rand.nextInt(Math.max((int)Math.floor(15.0 - Math.pow(Math.max(triggEntitySunk_kof1, 0.0), 1.5)), 1)) == 0) {
                        triggeringEntity.onGround = true;
                    }
                } else {
                	
                	// If the if statement above runs, this one will not run
                    // In other words, this will only run if:
                    // The block above this one is NOT the same as this block,
                    //    AND the entity has sunk is greater than 0.9 blocks...
                    if (triggEntitySunk_kof1 > 0.9) {
                    	
                    	QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, mr_blackgoo);
                    	
                    	final double thickness = 0.06 + sinkRateMod_mys;
                    	
                    	if (!triggEntitySplashing) {
                    		
                            if (triggEntityMoving || world.rand.nextInt(Math.max((int)Math.floor(75.0 - triggEntitySunk_kof1 * 5.0), 1)) == 0) {
                                triggeringEntity.motionY += thickness / Math.max(1.0, 5.0 - triggEntitySunk_kof1);
                            } else {
                                triggeringEntity.motionY += thickness;
                            }
                            
                            triggeringEntity.onGround = false;
                            triggeringEntity.fallDistance = 0.0f;
                        }
                    	
                        if (triggeringEntity instanceof EntityPlayer) {
                            
                        	if (world.getTotalWorldTime() % Math.max(1.0 + Math.floor(Math.max(triggEntitySunk_kof1 * 5.0 - blockMetadata / 10, 0.0)), 1.0) == 0.0) {
                                triggeringEntity.setInWeb();
                                triggeringEntity.onGround = false;
                            }
                        } else {
                            
                        	final double mw_kof = 1 + mr_blackgoo / 10;
                            triggeringEntity.setPosition(triggeringEntity.prevPosX + (triggeringEntity.posX - triggeringEntity.prevPosX) / mw_kof, triggEntityPosY, triggeringEntity.prevPosZ + (triggeringEntity.posZ - triggeringEntity.prevPosZ) / mw_kof);
                        }
                        
                        if (world.rand.nextInt(Math.max((int)Math.floor(15.0 - Math.pow(Math.max(triggEntitySunk_kof1, 0.0), 1.5)), 1)) == 0) {
                            triggeringEntity.onGround = true;
                        }
                    } else {
                    	
                    	final double thickness = 0.06 + sinkRateMod_mys;
                        
                    	if (world.rand.nextInt(Math.max((int)Math.floor(15.0 - Math.pow(Math.max(triggEntitySunk_kof1, 0.0), 1.5)), 1)) == 0) {
                            triggeringEntity.onGround = true;
                        }
                        
                    	if (QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunkMod_kof1m)) {
                            QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, mr_blackgoo);
                        }
                        
                    	triggeringEntity.fallDistance = 0.0f;
                        
                    	if (triggEntitySunk_kof1 >= 0.6) {
                            
                    		if (triggEntityMoving || world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunk_kof1 * 150.0), 25)) == 0) {
                                
                    			if (!triggEntityMoving) {
                                	triggeringEntity.motionY += thickness / Math.max(2.0, 5.0 - triggEntitySunk_kof1);
                                } else if (world.rand.nextInt(Math.min(Math.max((int)Math.floor(75.0 - triggEntitySunk_kof1 * 25.0), 1), 10)) == 0) {
                                	triggeringEntity.motionY += thickness / Math.max(2.0, 5.0 - triggEntitySunk_kof1);
                                } else {
                                	triggeringEntity.motionY += thickness;
                                }
                            } else {
                            	triggeringEntity.motionY += thickness;
                            }
                        } else {
                        	
                        	triggeringEntity.setInWeb();
                            
                        	if (triggEntityMoving || world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunk_kof1 * 150.0), 25)) == 0) {
                                
                        		if (!triggEntityMoving) {
                                    triggeringEntity.motionY--;
                                } else if (world.rand.nextInt(Math.min(Math.max((int)Math.floor(75.0 - triggEntitySunk_kof1 * 25.0), 1), 35)) == 0) {
                                	triggeringEntity.motionY -= 0.25;
                                } else {
                                	triggeringEntity.motionY -= 0.1;
                                }
                            } else {
                            	triggeringEntity.motionY += thickness;
                            }
                            
                        	if (triggEntitySunk_kof1 < 0.45 && QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunkMod_kof1m)) {
                        		QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, 128);
                            }
                        }
                    }
                    
                    if (!world.isRemote) {
                    	
                        if (triggeringEntity instanceof EntityPlayer) {
                        	triggEntitySunk_kof1 += 1.5;
                        	triggEntitySunk_kof1 = Math.max(triggEntitySunk_kof1, 0.0);
                        }
                        if (triggEntitySunk_kof1 < 0.75 && triggEntityPosY - triggEntityPrevPosY < -0.005) {
                        	triggeringEntity.attackEntityFrom(DamageSource.generic, Math.max(((EntityLivingBase)triggeringEntity).getMaxHealth() * 0.05f, 1.0f));
                            world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f);
                            world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.hit", 0.25f, world.rand.nextFloat() * 0.25f + 1.75f);
                        }
                        else if (triggEntitySunk_kof1 < 0.75) {
                            if (triggEntitySunk_kof1 < 0.01) {
                                if (world.rand.nextInt(10) == 0) {
                                	triggeringEntity.attackEntityFrom(DamageSource.generic, Math.max(((EntityLivingBase)triggeringEntity).getMaxHealth() * 0.05f, 1.0f));
                                    world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f);
                                    world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.hit", 0.25f, world.rand.nextFloat() * 0.25f + 1.75f);
                                }
                            }
                            else if (world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunk_kof1 * 100.0), 10)) == 0) {
                            	triggeringEntity.attackEntityFrom(DamageSource.generic, Math.max(((EntityLivingBase)triggeringEntity).getMaxHealth() * 0.05f, 1.0f));
                                world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f);
                                world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.hit", 0.25f, world.rand.nextFloat() * 0.25f + 1.75f);
                            }
                        }
                        if (world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunk_kof1 * 50.0), 10)) == 0) {
                            world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.say", 0.25f, world.rand.nextFloat() * 0.25f + 1.75f);
                        }
                    }
                }
                
                if (triggeringEntity.isInWater() && triggeringEntity.motionY > 0.0) {
                    triggeringEntity.motionY /= 4.0;
                }
                
            } else {
            	
            	triggeringEntity.setInWeb();
            	
                if (triggEntitySunk_kof1 < 0.5 && QuicksandManager.isTrulySinking(triggeringEntity, triggEntitySunkMod_kof1m)) {
                    QuicksandManager.setStuckEffect((EntityLivingBase)triggeringEntity, 128);
                }
                
                if (!world.isRemote) {
                	
                    if (triggeringEntity instanceof EntityPlayer) {
                    	triggEntitySunk_kof1 += 1.5;
                    	triggEntitySunk_kof1 = Math.max(triggEntitySunk_kof1, 0.0);
                    }
                    
                    if (world.getBlockState(pos.up(2)).getBlock() == this) {
                        
                    	if (world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunk_kof1 * 50.0), 10)) == 0) {
                            world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.say", 0.25f, world.rand.nextFloat() * 0.25f + 1.75f);
                        }
                        
                    	if (world.rand.nextInt(10) == 0) {
                        	triggeringEntity.attackEntityFrom(DamageSource.generic, ((EntityLivingBase)triggeringEntity).getMaxHealth() * 0.05f);
                            world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.step", 0.25f, world.rand.nextFloat() * 0.15f + 0.1f);
                            world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.hit", 0.25f, world.rand.nextFloat() * 0.25f + 1.75f);
                        }
                    }
                }
            }
        	
        	// Prevents the entity from continuously jumping
            QuicksandManager.antiHoldJumpScript(triggeringEntity, triggEntitySunk_kof1, true);
            /*
            System.out.println("triggEntityMovingDistance_movDis: " + triggEntityMovingDistance_movDis);
            System.out.println("forceFlavorEvent_movCof: " + forceFlavorEvent_movCof);
            System.out.println("movementPunish_mofKofDiv: " + movementPunish_mofKofDiv);
            System.out.println("mr_blackgoo: " + mr_blackgoo);*/
		} else {
			
			if (triggEntitySunk_kof1 < 1.25) {
				triggeringEntity.setInWeb();
				
				if (triggeringEntity instanceof EntityItem && ((EntityItem)triggeringEntity).getEntityItem().getItem() instanceof ItemFood && world.rand.nextInt(Math.max((int)Math.floor(triggEntitySunk_kof1 * 50.0), 10)) == 0) {
	                world.playSoundEffect(triggeringEntity.posX, triggEntityPosY, triggeringEntity.posZ, "mob.silverfish.say", 0.25f, world.rand.nextFloat() * 0.25f + 1.75f);
	                triggeringEntity.attackEntityFrom(DamageSource.generic, 0.1f);
	            }
			}
		}
		/*System.out.println("triggEntitySunk_kof1: " + triggEntitySunk_kof1);
        System.out.println("triggEntityPrevSunk_kof2: " + triggEntityPrevSunk_kof2);
        System.out.println("triggEntitySunkMod_kof1m: " + triggEntitySunkMod_kof1m);*/
	}
	
	// Declares that this block ID has metadata values.
	// Declares the metadata values for this block.
	// The metadata value corrosponds with its index in "types".
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION WITH METADATA
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item block, CreativeTabs creativeTabs, List<ItemStack> list) {
		
		// For every index in "types"...
		for (int indexType = 0; indexType < types.length; indexType++) {
			list.add(new ItemStack(block, 1, indexType)); // Declare a metadata variant with that index value
		}
    }
	
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
        return false;
    }

	// When false, the block will not push entities outside of itself
	@Override
	public boolean isFullCube() {
		return false;
    }
	
	public void updateTick(final World world, BlockPos pos, IBlockState state, final Random rand) {
        this.checkLavaNear(world, pos);
        if (!world.getBlockState(pos.down()).getBlock().getMaterial().isSolid() && world.getBlockState(pos.down()).getBlock().getMaterial().isReplaceable()) {
            world.setBlockState(pos.down(), state, 3);
            world.setBlockToAir(pos);
            return;
        }
        super.updateTick(world, pos, state, rand);
    }
	
	public void checkLavaNear(final World world, BlockPos pos) {
        int xx = pos.getX();
        int yy = pos.getY();
        int zz = pos.getZ();
        int met = 0;
        final boolean isWaterNear = false;
        boolean isLavaNear = false;
        boolean isFireNear = false;
        
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
                met = world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().getDamageValue(world, new BlockPos(xx, yy, zz));
                isLavaNear = true;
                break;
            }
            
            if (world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().getMaterial() == Material.fire) {
                isFireNear = true;
                break;
            }
        }
        
        if (isLavaNear || isFireNear) {
        	
            if (world.rand.nextInt(5) == 0) {
                world.playSoundEffect((double)(pos.getX() + world.rand.nextFloat()), (double)(pos.getY() + world.rand.nextFloat()), (double)(pos.getZ() + world.rand.nextFloat()), "mob.silverfish.hit", 0.5f, world.rand.nextFloat() * 0.25f + 1.75f);
            }
            
            if (!isFireNear && world.rand.nextInt(25) == 0) {
                world.setBlockToAir(pos);
                world.playSoundEffect((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f), "random.fizz", 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
                
                for (int l = 0; l < 8; ++l) {
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + 1.2, pos.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                return;
            }
            
            world.scheduleBlockUpdate(pos, (Block)this, 5, 1);
        }
    }
	
	protected boolean canSilkHarvest() {
		return true;
	}
	
	public boolean canDropFromExplosion(final Explosion par1Explosion) {
        return false;
    }
	
	public void harvestBlock(final World world, final EntityPlayer entityPlayer, BlockPos pos, IBlockState state, TileEntity tileEntity) {
        if (EnchantmentHelper.getSilkTouchModifier((EntityLivingBase)entityPlayer)) {
            QuicksandManager.dropItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack((Block)this));
            world.markBlockForUpdate(pos);
            return;
        }
        super.harvestBlock(world, entityPlayer, pos, state, tileEntity);
    }
    
    public boolean canHarvestBlock(final EntityPlayer player, final int meta) {
        return true;
    }
    
    public void breakBlock(final World world, BlockPos pos, IBlockState state) {
        if (this != world.getBlockState(pos)) {
            world.playSoundEffect((double)(pos.getX() + world.rand.nextFloat()), (double)(pos.getY() + world.rand.nextFloat()), (double)(pos.getZ() + world.rand.nextFloat()), "mob.spider.death", 0.75f, world.rand.nextFloat() * 0.25f + 1.75f);
        }
        super.breakBlock(world, pos, state);
    }
    
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleBlockUpdate(pos, (Block)this, this.tickRate(world), 1);
    }
    
    // TODO: The casts may not work
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        ((World)world).scheduleBlockUpdate(pos, (Block)this, this.tickRate((World)world), 1);
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return QSAND_Items.larvaeRaw;
    }
    
    public int quantityDropped(IBlockState state, int fortune, Random random) {
    	return random.nextInt(2) + 2;
    }
    
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos) {
    	if (!((Entity)player).onGround) {
            return 6.0E-5f;
        }
        final float f = this.getBlockHardness(world, pos);
        return ForgeHooks.blockStrength(world.getBlockState(pos), player, world, pos);
    }
	
	// Obtains the special name of the block variant.
	// This is used to add a custom name to a block variant in the language file
	@Override
	public String getSpecialName(ItemStack stack) {
		return BlockLarvae.types[stack.getItemDamage()];
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
            QuicksandManager.spawnDrowningBubble(triggeringEntity.worldObj, triggeringEntity, this, true); // Spawn drowning bubbles

            // ...AND the world is NOT on a server, AND the entity is marked as alive...
            if (!triggeringEntity.worldObj.isRemote && triggeringEntity.isEntityAlive()) {
            	triggeringEntity.attackEntityFrom(DamageSource.drown, Math.max(((EntityLivingBase)triggeringEntity).getMaxHealth() * 0.1f, 2.0f)); // Deals 10% of max health or 2hp in damage. Whichever is greater
            }
        }
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
