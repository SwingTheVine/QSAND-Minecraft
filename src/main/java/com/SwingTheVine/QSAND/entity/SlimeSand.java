package com.SwingTheVine.QSAND.entity;

import com.SwingTheVine.QSAND.init.QSAND_Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;

public class SlimeSand extends EntityLiving implements IMob {
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
	
	public SlimeSand(World world) {
		super(world);
		this.slimeInv = new ItemStack[5]; // Makes the slime inventory 5 slots
		this.antiSit = 0.5f;
		this.deepFactor = 0.0f;
		this.tDeepFactor  = 0.0f;
		this.pullTime = 0.0f;
		this.slimeJumpDelay = this.rand.nextInt(20) + 10;
		
	}
	
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
    
    public void writeEntityToNBT(final NBTTagCompound nbtt) {
        super.writeEntityToNBT(nbtt);
        nbtt.setInteger("Size", this.getSlimeSize() - 1);
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbtt) {
        super.readEntityFromNBT(nbtt);
        int var0 = nbtt.getInteger("Size");
        if (var0 < 0) {
            var0 = 0;
        }
        this.setSlimeSize(var0 + 1);
    }
    
    protected void syncronizeDepth() {
        if (!this.worldObj.isRemote) {
            this.dataWatcher.updateObject(datawatcherDepthID, (Object)this.tDeepFactor);
        }
        else {
            this.tDeepFactor = this.dataWatcher.getWatchableObjectFloat(datawatcherDepthID);
        }
    }
    
    protected void setSlimeSize(final int size) {
        this.dataWatcher.updateObject(datawatcherSizeID, (Object)new Byte((byte)size));
        this.dataWatcher.updateObject(datawatcherDepthID, (Object)this.tDeepFactor);
        this.setSize(0.5f * size, 0.5f * size);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((double)this.getSpawnHp());
        this.setHealth(this.getMaxHealth());
        this.experienceValue = size + 1;
    }
    
    public int getSlimeSize() {
        return 3;
    }
    
    protected String getJumpSound() {
        return "mob.slime.big";
    }
    
    protected SlimeSand createInstance() {
        return new SlimeSand(this.worldObj);
    }
    
    public void setDead() {
        super.setDead();
    }
    
    protected boolean canDamagePlayer() {
        return true;
    }
    
    protected float getSoundVolume() {
        return 0.25f;
    }
    
    public int getVerticalFaceSpeed() {
        return 0;
    }
    
    protected boolean makesSoundOnJump() {
        return true;
    }
    
    protected boolean makesSoundOnLand() {
        return true;
    }
    
    protected String getHurtSound() {
        return "mob.slime.big";
    }
    
    protected String getDeathSound() {
        return "mob.slime.big";
    }
    
    public void onUpdate() {
    	
        if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.isDead = true;
        }
        
        if (this.isWet()) {
            this.attackEntityFrom(DamageSource.drown, 10.0f);
        } else {
        	
            for (int var3 = 0; var3 < 2; ++var3) {
            	
                final float var4 = this.rand.nextFloat() * 3.1415927f * 2.0f;
                final float var5 = this.rand.nextFloat() * 0.5f + 0.5f;
                final float var6 = MathHelper.sin(var4) * 2.0f * 0.5f * var5;
                final float var7 = MathHelper.cos(var4) * 2.0f * 0.5f * var5;
                //this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + var6, this.getEntityBoundingBox().minY + 0.5, this.posZ + var7, 0.0, 0.0, 0.0);
            }
        }
        
        this.extinguish();
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5f;
        this.squishFactorPrev = this.squishFactor;
        final boolean var8 = this.onGround;
        super.onUpdate();
        
        if (this.onGround && !var8) {
        	
            for (int var9 = this.getSlimeSize(), var10 = 0; var10 < var9 * 16; ++var10) {
            	
                final float var11 = this.rand.nextFloat() * 3.1415927f * 2.0f;
                final float var12 = this.rand.nextFloat() * 0.5f + 0.5f;
                final float var13 = MathHelper.sin(var11) * var9 * 0.5f * var12;
                final float var14 = MathHelper.cos(var11) * var9 * 0.5f * var12;
                //this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + var13, this.getEntityBoundingBox().minY, this.posZ + var14, 0.0, 0.0, 0.0);
            }
            
            if (this.makesSoundOnLand()) {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) / 0.8f);
                this.playSound("step.grass", 0.5f, this.rand.nextFloat() * 0.2f + 0.1f);
                this.playSound("step.grass", 0.5f, this.rand.nextFloat() * 0.2f + 0.1f);
            }
            
            this.squishAmount = -0.5f;
        } else if (!this.onGround && var8) {
            this.squishAmount = 1.0f;
        }
        
        if (this.onGround && this.worldObj.getTotalWorldTime() % 16L == 0L && this.worldObj.rand.nextInt(10) == 0) {
            this.squishAmount = 2.0f;
            this.playSound("step.grass", 0.5f, this.rand.nextFloat() * 0.2f + 0.1f);
        }
        
        this.alterSquishAmount();
        
        if (this.riddenByEntity == null) {
        	
            if (this.deepFactor > 0.0f) {
                this.tDeepFactor -= 0.5f;
            }
        } else {
        	
            if (!this.worldObj.isRemote) {
            	
                if (!(this.riddenByEntity instanceof EntityPlayer)) {
                	
                    this.riddenByEntity.mountEntity((Entity)null);
                    return;
                }
                
                if (((EntityPlayer)this.riddenByEntity).capabilities.disableDamage) {
                    ((EntityPlayer)this.riddenByEntity).mountEntity((Entity)null);
                    return;
                }
            }
            
            if (this.worldObj.getTotalWorldTime() % 2L == 0L && this.rand.nextInt(5) == 0) {
                this.playSound("step.grass", 0.25f, this.rand.nextFloat() * 0.5f + 0.5f);
            }
            
            if (this.deepFactor < 12.5f) {
                this.tDeepFactor += 0.02f;
                final float pullTime = this.pullTime;
                this.pullTime = pullTime - 1.0f;
                
                if (pullTime <= 0.0f) {
                    this.pullTime = (float)(20 + this.rand.nextInt(10));
                    this.squishAmount = 1.5f;
                    this.tDeepFactor += 0.75f;
                    this.playSound("step.sand", 0.25f, this.rand.nextFloat() * 0.2f + 0.2f);
                    this.playSound("mob.magmacube.jump", 0.25f, 0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    this.playSound("mob.magmacube.jump", 0.25f, 0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    this.playSound("mob.magmacube.jump", 0.25f, 0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    this.playSound("step.grass", 0.25f, this.rand.nextFloat() * 0.5f + 0.5f);
                }
            } else if (!this.worldObj.isRemote && this.worldObj.getTotalWorldTime() % 4L == 0L && this.rand.nextInt(5) == 0) {
                this.riddenByEntity.attackEntityFrom(DamageSource.inWall, ((EntityLivingBase)this.riddenByEntity).getMaxHealth() * 0.1f);
            }
        }
        
        if (Math.abs(this.deepFactor - this.tDeepFactor) > 0.1) {
            this.deepFactor += (this.tDeepFactor - this.deepFactor) / 10.0f;
        }
        
        if (this.worldObj.isRemote) {
            final int var9 = this.getSlimeSize();
            this.setSize(0.6f * var9, 0.6f * var9);
        }
    }
    
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.antiSit + this.riddenByEntity.getYOffset() - Math.min(1.25, Math.max(0.0f, this.deepFactor / 10.0f)) * 1.25, this.posZ);
        }
    }
    /*
    protected void updateEntityActionState() {
    	super.updateEntityActionState();
    	
        this.despawnEntity();
        EntityPlayer var1 = null;
        
        if (this.riddenByEntity == null) {
            var1 = this.worldObj.getClosestPlayerToEntity((Entity)this, 16.0);
        }
        
        if (var1 != null) {
            this.faceEntity((Entity)var1, 10.0f, 20.0f);
        }
        
        if (this.onGround && this.slimeJumpDelay-- <= 0) {
        	
            this.slimeJumpDelay = this.getJumpDelay();
            
            if (var1 != null) {
                this.slimeJumpDelay /= 10;
            }
            
            if (this.riddenByEntity == null) {
            	
                this.isJumping = true;
                
                if (this.makesSoundOnJump()) {
                    this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                    this.playSound("dig.sand", 0.5f, this.rand.nextFloat() * 0.5f + 0.5f);
                }
                
                this.moveStrafing = 1.0f - this.rand.nextFloat() * 2.0f;
                this.moveForward = (float)(1 * this.getSlimeSize());
            } else {
                this.slimeJumpDelay = 10;
            }
        } else {
        	
            this.isJumping = false;
            
            if (this.onGround) {
                final float n = 0.0f;
                this.moveForward = n;
                this.moveStrafing = n;
            }
        }
    }*/
    
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
                            
            				final float hp = player.getHealth() / opMul / 3.0f;
                            
            				if (hp < 1.0f || this.rand.nextInt((int)Math.floor(Math.max(1.0f, hp))) == 0) {
                                
            					((EntityPlayer)(this.riddenByEntity = (Entity)player)).mountEntity((Entity)this);
                                
            					if (this.rand.nextInt(5) == 0) {
                                    this.playSound("mob.slime.attack", 0.25f, this.rand.nextFloat() * 0.25f + 0.25f);
                                    this.tDeepFactor += 2.0f;
                                }
                                
            					this.syncronizeDepth();
                            }
                        }
                        
            			this.playSound("mob.attack", 0.25f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                        this.playSound("step.sand", 0.25f, this.rand.nextFloat() * 0.5f + 0.5f);
                    }
                }
            }
        }
    }
    
    public boolean attackEntityFrom(final DamageSource dmgs, float power) {
        
    	final Entity ply = dmgs.getEntity();
        power = Math.max(power * 0.2f, 1.0f);
        
        if (dmgs == DamageSource.inFire || dmgs == DamageSource.onFire || dmgs == DamageSource.lava) {
            power *= 0.5f;
        }
        
        if (this.riddenByEntity == null && ply != null && ply instanceof EntityPlayer) {
            
        	final Entity ent = dmgs.getSourceOfDamage();
            
        	if (ent == ply && ply.isEntityAlive() && ply.distanceWalkedModified > 0.0f && this.getHealth() > power && !this.worldObj.isRemote && this.rand.nextInt(5) == 0) {
                
        		final SlimeSand sandBlob = new SlimeSand(this.worldObj);
                
        		sandBlob.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0f, 0.0f);
                
        		final double xv = this.worldObj.rand.nextFloat() * 0.5 - 0.25;
                final double zv = this.worldObj.rand.nextFloat() * 0.5 - 0.25;
                final double yv = this.worldObj.rand.nextFloat() * 0.25 + 0.25;
                this.motionX = xv;
                this.motionY = yv;
                this.motionZ = zv;
                
                power = 0.0f;
                this.setHealth(this.getMaxHealth());
                this.squishAmount = 2.0f;
                
                this.playSound("mob.slime.attack", 0.25f, this.rand.nextFloat() * 0.25f + 0.25f);
                this.playSound("step.sand", 1.0f, this.rand.nextFloat() * 0.25f + 0.25f);
                this.playSound("step.sand", 1.0f, this.rand.nextFloat() * 0.25f + 0.25f);
                this.playSound("step.grass", 0.25f, this.rand.nextFloat() * 0.5f + 0.5f);
                
                sandBlob.motionX = -xv;
                sandBlob.motionY = yv;
                sandBlob.motionZ = -zv;
                sandBlob.squishAmount = 2.0f;
                
                this.worldObj.spawnEntityInWorld((Entity)sandBlob);
                return true;
            }
        }
        
        final boolean Result = super.attackEntityFrom(dmgs, power);
        
        if (Result) {
            this.playSound("dig.sand", 0.5f, this.rand.nextFloat() * 0.5f + 0.5f);
        }
        
        return Result;
    }
    
    protected void dropFewItems(final boolean par1, final int par2) {
        
    	for (int var3 = this.rand.nextInt(2) + this.rand.nextInt(1 + par2), var4 = 2; var4 < var3 * 2; ++var4) {
            this.entityDropItem(new ItemStack(Items.slime_ball, 1, 0), 0.0f);
        }
        
    	for (int var3 = this.rand.nextInt(2) + this.rand.nextInt(1 + par2), var4 = 1; var4 < var3; ++var4) {
            this.entityDropItem(new ItemStack(QSAND_Blocks.quicksandSoft, 1, 0), 0.0f);
        }
        
    	for (int var3 = this.rand.nextInt(2) + this.rand.nextInt(1 + par2), var4 = 1; var4 < var3 * 3; ++var4) {
            this.entityDropItem(new ItemStack(Blocks.sand, 1, 0), 0.0f);
        }
    }
    
    protected Item getDropItem() {
        return Items.slime_ball;
    }
    
    protected boolean canDespawn() {
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
        return 1;
    }
    
    protected int getSpawnHp() {
        return 15;
    }
    
    protected void alterSquishAmount() {
        this.squishAmount *= 0.85f;
    }
    
    protected String getSlimeParticle() {
        return "splash";
    }
    
    protected int getJumpDelay() {
        return this.rand.nextInt(40) + 100;
    }
    
    public boolean shouldRiderSit() {
        return false;
    }
    
    @Override
    public boolean isAIDisabled() {
    	return false;
    }
}
