package com.SwingTheVine.QSAND.entity;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.SwingTheVine.QSAND.client.player.PlayerMudManager;
import com.SwingTheVine.QSAND.entity.ai.SlimeMudAI;
import com.SwingTheVine.QSAND.handler.ConfigHandler;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.manager.QuicksandManager;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;

public class SlimeMud extends EntityLiving implements IMob {
	private ItemStack[] slimeInv; // The slime's inventory
	public float squishAmount;
	public float squishFactor;
	public float squishFactorPrev;
	public float deepFactor;
	public float tDeepFactor;
	public float pullTime;
	public float antiSit;
	private int slimeJumpDelay;
	public static final String textureLocation = ":textures/entities/SlimeSand.png"; // The location of the texture used for the bubble
	public int datawatcherDepthID = 13; // Default datawatcher ID 
	public int datawatcherSizeID = 14; // Default datawatcher ID

	// Constructor
	public SlimeMud(World world) {
		super(world);
		this.slimeInv = new ItemStack[5]; // Makes the slime inventory 5 slots
		this.antiSit = 0.15f;
		this.deepFactor = 0.0f;
		this.tDeepFactor = 0.0f;
		this.pullTime = 0.0f;
		this.slimeJumpDelay = this.rand.nextInt(20) + 10;
		
		this.moveHelper = new SlimeMudAI.SlimeMoveHelper(this);
        this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
        this.tasks.addTask(2, new SlimeMudAI.AISlimeAttack(this));
        this.tasks.addTask(3, new SlimeMudAI.AISlimeFaceRandom(this));
        this.tasks.addTask(4, new SlimeMudAI.AISlimeHop(this));
	}
	
	// Initializes the slime
	protected void entityInit() {
        super.entityInit();
        
     // Creates an array to hold the IDs of all datawatcher slots
        boolean[] datawatcherIDExists = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
        
        // For every slot, it checks if a datawatcher already  exists in that slot
        for (int i = 0; i < this.dataWatcher.getAllWatched().size(); i++) {
        	// If it does, it updates that slot in the ID array with "true"
        	
        	datawatcherIDExists[this.dataWatcher.getAllWatched().get(i).getDataValueId()] = true;
        }
        
        // Finds the next available datawatcher slot and creates a new datawatcher there
        for (int i = 15; i >= 0; i--) {
        	
        	// If no datawatcher exists in that slot...
        	if (!datawatcherIDExists[i]) {
        		
        		datawatcherSizeID = i; // Change the Size ID to that slot ID
        		datawatcherIDExists[i] = true; // Change the slot ID to "true"
        		
        		// Add a new datawatcher in that slot
        		this.dataWatcher.addObject(datawatcherSizeID, (Object)new Byte((byte)1));
        		break; // Stop looking for available datawatcher slots
        	} else if (i == 0) {
        		// Else if it has run out of slot options...
        		
        		datawatcherSizeID = i; // Change the Size ID to that slot ID
        		datawatcherIDExists[i] = true; // Change the slot ID to "true"
        		
        		// Print out an error message
        		System.out.println("Entity has run out of available positions to create a new datawatcher! All 16 are full.");
        		
        		// Crash
        		this.dataWatcher.addObject(datawatcherSizeID, (Object)new Byte((byte)1));
        		break;
        	}
        }
        
        // Finds the next available datawatcher slot and creates a new datawatcher there
        for (int i = 15; i >= 0; i--) {
        	
        	// If no datawatcher exists in that slot...
        	if (!datawatcherIDExists[i]) {
        		
        		datawatcherDepthID = i; // Change the Size ID to that slot ID
        		datawatcherIDExists[i] = true; // Change the slot ID to "true"
        		
        		// Add a new datawatcher in that slot
        		this.dataWatcher.addObject(datawatcherDepthID, (Object)0.0f);
        		break;// Stop looking for available datawatcher slots
        	} else if (i == 0) {
        		// Else if it has run out of slot options...
        		
        		datawatcherDepthID = i; // Change the Size ID to that slot ID
        		datawatcherIDExists[i] = true; // Change the slot ID to "true"
        		
        		// Print out an error message
        		System.out.println("Entity has run out of available positions to create a new datawatcher! All 16 are full.");
        		
        		// Crash
        		this.dataWatcher.addObject(datawatcherDepthID, (Object)0.0f);
        		break;
        	}
        }
    }
	
	// Writes the NBT data for the slime
    public void writeEntityToNBT(final NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Size", this.getSlimeSize() - 1); // Sets a new NBT tag called "Size"
    }
    
    // Reads the NBT data for the slime
    public void readEntityFromNBT(final NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        int size = compound.getInteger("Size"); // Reads the NBT tag called "Size"
        
        // If the size of the slime is less than 0...
        if (size < 0) {
        	size = 0; // Set the size to 0
        }
        
        this.setSlimeSize(size + 1); // Set the size of the slime to the size + 1
    }
    
    // Depth of slime
    protected void syncronizeDepth() {
    	
    	// If the code is NOT executing server-side...
        if (!this.worldObj.isRemote) {
        	// Update the depth datawatcher to match the current depth
            this.dataWatcher.updateObject(datawatcherDepthID, (Object)this.tDeepFactor);
        } else {
        	
        	// Set the deep factor to the depth
            this.tDeepFactor = this.dataWatcher.getWatchableObjectFloat(datawatcherDepthID);
        }
    }
    
    // Sets the slime's size
    protected void setSlimeSize(final int size) {
    	
    	// Updates the datawatchers
        this.dataWatcher.updateObject(datawatcherSizeID, (Object)new Byte((byte)size));
        this.dataWatcher.updateObject(datawatcherDepthID, (Object)this.tDeepFactor);
        
        // Sets the slime's parameters
        this.setSize(0.6f * size, 0.6f * size);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((double)this.getSpawnHp());
        this.setHealth(this.getMaxHealth());
        this.experienceValue = size + 2;
    }
    
    // Obtains and returns the slime's size
    public int getSlimeSize() {
        return 3;
    }
    
    // Obtains and returns the slime's jump sound
    public String getJumpSound() {
        return "mob.slime.big";
    }
    
    // Creates an instance of the slime
    protected SlimeMud createInstance() {
        return new SlimeMud(this.worldObj);
    }
    
    // Kills the slime
    public void setDead() {
        super.setDead();
    }
    
    // Returns if the slime can damage players
    public boolean canDamagePlayer() {
        return true;
    }
    
    // Returns the sound volume for sounds played
    public float getSoundVolume() {
        return 1.2f;
    }
    
    // The speed it takes to move the entityliving's rotationPitch through the faceEntity method.
    public int getVerticalFaceSpeed() {
        return 0;
    }
    
    // Returns if the slime makes sounds when the slime jumps
    public boolean makesSoundOnJump() {
        return true;
    }
    
    // Returns if the slime makes sounds when the slime lands
    protected boolean makesSoundOnLand() {
        return true;
    }
    
    // Returns the sound to play when the slime is hurt
    protected String getHurtSound() {
        return "mob.slime.big";
    }
    
    // Returns the sound to play when the slime dies
    protected String getDeathSound() {
        return "mob.slime.big";
    }
	
	public boolean handlerWaterMovement() {
		return false;
	}
	
	public int getSizeInventory() {
		return 3;
	}
	
	// Triggers to updaate the slime
    public void onUpdate() {
    	
    	// If the code is NOT executing server-side, AND the difficulty is peaceful...
        if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.isDead = true; // Kill the slime
        }
        
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5f;
        this.squishFactorPrev = this.squishFactor;
        final boolean isOnGround = this.onGround; // Is the slime on the ground?
        
        super.onUpdate(); // UPDATES THE SLIME
        
        // If the slime is on the ground, AND the slime was on the ground before the update...
        if (this.onGround && !isOnGround) {
        	
        	// Spawns as many particles as the slime is big (bigger slime = more particles)
            for (int slimeSize = this.getSlimeSize(), particle = 0; particle < slimeSize * 16; particle++) {
            	
            	// Calculates the position to spawn the particle, then spawns it
                final float yaw = this.rand.nextFloat() * 3.1415927f * 2.0f;
                final float height = this.rand.nextFloat() * 0.5f + 0.5f;
                final float offsetX = MathHelper.sin(yaw) * slimeSize * 0.5f * height;
                final float offsetZ = MathHelper.cos(yaw) * slimeSize * 0.5f * height;
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, true, this.posX + offsetX, this.getEntityBoundingBox().minY + 0.5, this.posZ + offsetZ, 0.0, 0.0, 0.0, Block.getIdFromBlock(QSAND_Blocks.mud));
            }
            
            // If the slime makes a sound when it lands...
            if (this.makesSoundOnLand()) {
            	
            	// Play sounds
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            }
            
            this.squishAmount = -0.5f;
            
        } else if (!this.onGround && isOnGround) {
            this.squishAmount = 1.0f;
        }
        
        this.alterSquishAmount(); // Makes the slime retract downwards slightly
        
        // If the slime is not being ridden...
        if (this.riddenByEntity == null) {
        	
        	// If the deep factor is greater than 0...
            if (this.deepFactor > 0.0f) {
                this.tDeepFactor -= 0.5f; // Slowly reduce until it hits 0
            }
        } else {
        	
        	// If the code is NOT running server-side
            if (!this.worldObj.isRemote) {
            	
            	// If the slime is NOT being ridden by a player...
                if (!(this.riddenByEntity instanceof EntityPlayer)) {
                	
                    this.riddenByEntity.mountEntity((Entity)null); // Dismount the mounted entity
                    return; // End code execution for onUpdate()
                }
                
                // If the slime is being ridden by a player that has disabled damage...
                if (((EntityPlayer)this.riddenByEntity).capabilities.disableDamage) {
                    ((EntityPlayer)this.riddenByEntity).mountEntity((Entity)null); // Dismount the mounted entity
                    return; // End code execution for onUpdate()
                }
                
                // Adds mining fatigue for 100 seconds with an amplification of 10
                ((EntityLivingBase)this.riddenByEntity).addPotionEffect(new PotionEffect(4, 100, 10, false, false));
            
                // Adds weakness for 100 seconds with an amplification of 10
                ((EntityLivingBase)this.riddenByEntity).addPotionEffect(new PotionEffect(18, 100, 10, false, false));
                
                // Adds mining fatigue for 100 seconds with an amplification of 2
                ((EntityLivingBase)this.riddenByEntity).addPotionEffect(new PotionEffect(4, 100, 2, false, false));
                
                // Adds weakness for 100 seconds with an amplification of 2
                ((EntityLivingBase)this.riddenByEntity).addPotionEffect(new PotionEffect(18, 100, 2, false, false));
                
                // Adds slowness for 100 seconds with an amplification of 0
                ((EntityLivingBase)this.riddenByEntity).addPotionEffect(new PotionEffect(18, 100, 0, false, false));
            
                // If the mounted entity's position is greater than the position of the slime plus 0.25...
                if (this.riddenByEntity.posY > this.posY + 0.25) {
                	// TODO: Check player muddy
                }
            }
            
            // If the remainder of the total world time divided by 8 equals 0, AND the next random integer equals 0 (1/5 chance)...
            if (this.worldObj.getTotalWorldTime() % 8L == 0L && this.rand.nextInt(5) == 0) {
                this.playSound("mob.silverfish.step", 0.4f, this.rand.nextFloat() * 0.15f + 0.1f); // Play sound
            }
            
            // If the deep factor is less than 12.5
            if (this.deepFactor < 12.5f) {
            	
                this.tDeepFactor += 0.015f; // Increase tDeepFactor
                final float initialPullTime = this.pullTime; // Create a local pullTime variable
                this.pullTime = initialPullTime - 1.0f; // Subtracts 1 from pullTime
                
                // If the initial pull time is less that or equal to 0...
                if (initialPullTime <= 0.0f) {
                	
                    this.pullTime = (float)(35 + this.rand.nextInt(10)); // Set pullTime to a number between 35 and 44
                    this.squishAmount = 1.5f; // Stretch upwards slightly
                    this.tDeepFactor += 1.45f; // Add to tDeepFactor
                    
                    // Play sounds
                    this.playSound("mob.magmacube.jump", 0.25f, 0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    this.playSound("mob.magmacube.jump", 0.25f, 0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    this.playSound("mob.magmacube.jump", 0.25f, 0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    this.playSound("mob.magmacube.jump", 0.25f, 0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                }
            } else if (!this.worldObj.isRemote && this.worldObj.getTotalWorldTime() % 4L == 0L && this.rand.nextInt(5) == 0) {
            	// Else If the code is NOT executing server-side, AND the remainder of the total world time divided by 4 equals 0, AND the next random integer equals 0 (1/5 chance)...
            	
            	// Attack the entity riding the slime for 10% of the entities max health
                this.riddenByEntity.attackEntityFrom(DamageSource.inWall, ((EntityLivingBase)this.riddenByEntity).getMaxHealth() * 0.1f);
            }
        }
        
        // If the absolute value of the deep factor minus the dDeepFactor is greater than 0.1...
        if (Math.abs(this.deepFactor - this.tDeepFactor) > 0.1) {
            this.deepFactor += (this.tDeepFactor - this.deepFactor) / 10.0f; // Add 10% of that to deepFactor
        }
        
        // If the code is executing server-side...
        if (this.worldObj.isRemote) {
            final int size = this.getSlimeSize(); // Gets the slime size
            this.setSize(0.6f * size, 0.6f * size); // Sets the size
        }
    }
    
    // Checks if the player is muddy
 	// If the player is muddy, it adds a skin overlay
 	public void checkPlayerMuddy(final EntityPlayer triggeringPlayer, final double playerPosX, final double playerPosY, final double playerPosZ, final World world) {
 		
 		// If the skin overlay is disabled by the user...
 		if (!ConfigHandler.useSkinOverlay) {
 			return; // The user does not want the skin overlay. Don't run this function
 		}
 		
 		// If the world is NOT run on a server...
 		if (!world.isRemote) {
 			final PlayerMudManager playerMudControl = PlayerMudManager.get(triggeringPlayer);
 			final int preMudLevel = QuicksandManager.getMudLevel(triggeringPlayer, playerPosY, world);
 			
 			// If the pre-mud level is greater than the mud level of the player times the mud time of the player divided by 1000...
 			if (preMudLevel > playerMudControl.getMudLevel() * (playerMudControl.getMudTime() / 1000.0f)) {
 				playerMudControl.setMudLevel(preMudLevel); // Sets the players mud level to the pre-mud level
 				
 				final int mudType = 10; // TODO: Fix
 				playerMudControl.setMudType(mudType); // Sets the mud type to the mud type of this block
 				
 				// If the mud time is less than 5000 ticks...
 				if (playerMudControl.getMudTime() < 5000) {
 					playerMudControl.addMudTime(100); // Adds 100 ticks to mud time
 				}
 				
 				// Sets the mud time to the current mud time, or 1000. Whichever is smaller
 				playerMudControl.setMudTime(Math.min(playerMudControl.getMudTime(), 1000));
 			}
 		}
 	}
 	
 	// Updates the mounted entity's position
    public void updateRiderPosition() {
    	
    	// If the entity riding the slime is NOT null...
        if (this.riddenByEntity != null) {
        	
        	// Set the position to slightly below this position
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.antiSit + this.riddenByEntity.getYOffset() - Math.min(1.25, Math.max(0.0f, this.deepFactor / 10.0f)) * 1.25, this.posZ);
        }
    }
    
    public void onCollideWithPlayer(final EntityPlayer player) {
    	
        if (player.capabilities.disableDamage) {
            return;
        }
        
        if (this.canDamagePlayer()) {
        	
            final int var2 = this.getSlimeSize();
            final float opMul = Math.max(player.getMaxHealth() / 20.0f, 1.0f);
            
            if (!(player.ridingEntity instanceof SlimeVoid) && !(player.ridingEntity instanceof SlimeMud) && !(player.ridingEntity instanceof SlimeTar) && !(player.ridingEntity instanceof SlimeSand)) {
                
            	final float sizeCoof = 2.0f;
                
            	if (this.canEntityBeSeen((Entity)player) && this.getDistanceSqToEntity((Entity)player) < var2 * (double)sizeCoof) {
                    
            		boolean inv = false;
                    
            		if (this.riddenByEntity == null) {
                        
            			if (player.getHealth() < 10.0f * opMul) {
                            
            				inv = true;
                            
            				if (this.rand.nextInt(10) == 0) {
                            	player.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), (float)this.getAttackStrength());
                            }
                        } else {
                            inv = player.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), (float)this.getAttackStrength());
                        }
                    }
                    
            		if (inv) {
                        
            			if (!this.isInsideOfMaterial(Material.water)) {
                            
            				final float hp = player.getHealth() / opMul / 5.0f;
                            
            				if (hp < 1.0f || this.rand.nextInt((int)Math.floor(Math.max(1.0f, hp))) == 0) {
                                
            					((EntityPlayer)(this.riddenByEntity = (Entity)player)).mountEntity((Entity)this);
                                
            					if (this.rand.nextInt(5) == 0) {
                                    this.playSound("mob.slime.attack", 0.55f, this.rand.nextFloat() * 0.25f + 0.25f);
                                    this.tDeepFactor += 2.0f;
                                }
                                
            					this.syncronizeDepth();
                            }
                        }
                        
            			this.playSound("mob.attack", 0.25f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                        
            			if (!this.worldObj.isRemote) {
            				
            				// Adds slowness for 100 seconds with an amplification of 0
            				player.addPotionEffect(new PotionEffect(2, 100, 0, false, false));
            				
            				this.checkPlayerMuddy(player, 0.0, player.posY - 0.25, 0.0, this.worldObj);
            			}
                    }
                }
            }
        }
    }
    
    public boolean attackEntityFrom(final DamageSource dmgs, float power) {
        
    	final Entity ply = dmgs.getEntity();
    	boolean Stopper = false;
    	
        if (ply != null && ply instanceof EntityPlayer) {
        	
            final Entity ent = dmgs.getSourceOfDamage();
            
            if (ent == ply && ply.isEntityAlive() && ply.distanceWalkedModified > 0.0f) {
            	
                boolean Weapon = false;
                
                if (((EntityPlayer)ply).getCurrentEquippedItem() != null) {
                	
                    final ItemStack item = ((EntityPlayer)ply).getCurrentEquippedItem();
                    
                    final Object var1 = new Item().getAttributeModifiers(item); // TODO: Might not work
                    
                    if (var1 != null) {
                    	
                        final Iterator var2 = ((Multimap)var1).entries().iterator();
                        
                        if (var2 != null) {
                        	
                            while (var2.hasNext()) {
                            	
                                final Map.Entry var3 = (Entry)var2.next();
                                
                                if (var3 != null) {
                                	
                                    final AttributeModifier atr = (AttributeModifier)var3.getValue();
                                    
                                    if (atr == null) {
                                        continue;
                                    }
                                    
                                    if (atr.getName() == "Weapon modifier") {
                                        Weapon = true;
                                        break;
                                    }
                                    
                                    if (atr.getName() == "Tool modifier") {
                                        Weapon = true;
                                        break;
                                    }
                                    
                                    continue;
                                }
                            }
                        }
                    }
                }
                
                if (!Weapon) {
                    if (!this.worldObj.isRemote) {}
                } else if (!this.worldObj.isRemote) {
                	
                    boolean canStuck = true;
                    final float opMul = Math.max(this.getMaxHealth() / 25.0f, 1.0f);
                    
                    if (this.worldObj.getDifficulty() != EnumDifficulty.HARD) {
                        canStuck = false;
                    }
                    
                    if (EnchantmentHelper.getFireAspectModifier((EntityLivingBase)ply) > 0) {
                        canStuck = false;
                    }
                    
                    if (EnchantmentHelper.getKnockbackModifier((EntityLivingBase)ply) > 0) {
                        canStuck = false;
                    }
                    
                    if (this.getHealth() <= power) {
                        canStuck = false;
                    }
                    
                    final PotionEffect PEWeak = ((EntityLivingBase)ply).getActivePotionEffect(Potion.weakness);
                    final PotionEffect PESlow = ((EntityLivingBase)ply).getActivePotionEffect(Potion.digSlowdown);
                    int weakLevel = -1;
                    int slowLevel = -1;
                    
                    if (PEWeak != null) {
                        weakLevel = PEWeak.getAmplifier();
                    }
                    
                    if (PESlow != null) {
                        slowLevel = PESlow.getAmplifier();
                    }
                    
                    if (canStuck) {
                    	
                        if (power < 3.0f * opMul) {
                        	
                            for (int i = 0; i < this.slimeInv.length; ++i) {
                            	
                                if (this.slimeInv[i] == null) {
                                	
                                    this.slimeInv[i] = ((EntityPlayer)ply).getCurrentEquippedItem();
                                    ((EntityPlayer)ply).inventory.setInventorySlotContents(((EntityPlayer)ply).inventory.currentItem, (ItemStack)null);
                                    this.playSound("mob.slime.attack", 0.35f, this.rand.nextFloat() * 0.5f + 0.5f);
                                    this.enablePersistence(); // Shout out to this Gist for providing a list of updated names for the searge functions: https://gist.github.com/leonardosnt/f908dcdc910a7801c05b57edf21b34fb
                                    ((EntityLivingBase)ply).addPotionEffect(new PotionEffect(2, 100, 0, false, false));
                                    this.checkPlayerMuddy((EntityPlayer)ply, 0.0, ply.posY - 0.25 + this.rand.nextFloat() * 0.5f, 0.0, this.worldObj);
                                    Stopper = true;
                                    ((EntityLivingBase)ply).setInWeb();
                                    break;
                                }
                            }
                        }
                        
                        ((EntityLivingBase)ply).addPotionEffect(new PotionEffect(4, 100 + Math.max(slowLevel * 20, 0), Math.min(slowLevel + 1, 5), false, false));
                        ((EntityLivingBase)ply).addPotionEffect(new PotionEffect(18, 100 + Math.max(weakLevel * 20, 0), Math.min(weakLevel + 1, 5), false, false));
                    }
                }
            }
        }
        
        final boolean Result = super.attackEntityFrom(dmgs, power);
        
        if (Stopper) {
            this.setInWeb();
        }
        
        return Result;
    }
    
    protected void dropFewItems(final boolean par1, final int par2) {
        
    	for (int var3 = this.rand.nextInt(8) + this.rand.nextInt(1 + par2), var4 = 0; var4 < var3 * 2; ++var4) {
            this.entityDropItem(new ItemStack(Items.slime_ball, 1, 0), 0.0f);
        }
    	
    	this.dropItem(Items.slime_ball, 4);
        
    	// TODO: Drop mudball
    	
    	if (!this.worldObj.isRemote) {
    		
    		for (int i = 0; i < this.slimeInv.length; ++i) {
    			if (this.slimeInv[i] != null) {
    				this.entityDropItem(this.slimeInv[i], 0.0f);
    			}
    		}
    	}
        
    	for (int var3 = this.rand.nextInt(2) + this.rand.nextInt(1 + par2), var4 = 0; var4 < var3; ++var4) {
            //this.entityDropItem(new ItemStack(Blocks.sand, 1, 0), 0.0f); TODO: Drop peat item
        }
    }
    
    protected Item getDropItem() {
        return Items.slime_ball;
    }
    
    protected boolean canDespawn() {
        for (int i = 0; i < this.slimeInv.length; ++i) {
        	if (this.slimeInv[i] != null) {
        		return false;
        	}
        }
        return true;
    }
    
    public boolean getCanSpawnHere() {
    	
        if (this.worldObj.provider.getDimensionId() != 0) {
            return false;
        }
        
        final BlockPos blockForChunk = new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        final Chunk var1 = this.worldObj.getChunkFromBlockCoords(blockForChunk);
        return this.worldObj.getWorldInfo().getTerrainType() != WorldType.FLAT && (this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.posY > 50.0 && this.posY < 70.0 && this.rand.nextInt(2) == 0) && super.getCanSpawnHere();
    }
    
    protected int getAttackStrength() {
        return 2;
    }
    
    protected int getSpawnHp() {
        return 25;
    }
    
    protected void alterSquishAmount() {
        this.squishAmount *= 0.8f;
    }
    
    protected String getSlimeParticle() {
        return "splash";
    }
    
    public int getJumpDelay() {
        return this.rand.nextInt(20) + 20;
    }
    
    public boolean shouldRiderSit() {
        return false;
    }
    
    @Override
    public boolean isAIDisabled() {
    	return false;
    }
}
