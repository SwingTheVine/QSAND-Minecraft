package com.SwingTheVine.QSAND.entity.effect;

import java.util.UUID;

import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.util.QuicksandManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Controls calculations for tentacles that appear in swallowing flesh.
 * 
 * @since <b>0.32.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class EntityTentacles extends Entity implements IEntityAdditionalSpawnData {
	
	public Entity target;
	public boolean targetUpdate;
	public UUID targetUUID;
	public int animationLifeTime;
	public int maxHurtResistantTime;
	public float health;
	public int high;
	
	public EntityTentacles(final World world) {
		super(world);
		this.targetUpdate = true;
		this.animationLifeTime = 0;
		this.health = 10.0f;
		this.setSize(1.0f, 1.0f);
		// TODO: this.yOffset = this.height / 2.0f;
		this.ignoreFrustumCheck = true;
		this.animationLifeTime = 0;
		this.noClip = true;
	}
	
	public EntityTentacles(final World world, final double entityPosX, final double entityPosY, final double entityPosZ,
		final Entity target, final int high, final int animationLifeTime) {
		this(world);
		this.setPosition(entityPosX, entityPosY, entityPosZ);
		this.high = high;
		this.animationLifeTime = animationLifeTime;
		this.target = target;
	}
	
	@Override
	protected void entityInit() {
		
		this.dataWatcher.addObject(25, (Object) (-1));
		this.dataWatcher.addObject(26, (Object) 0);
		this.dataWatcher.addObject(27, (Object) 10.0f);
	}
	
	protected void checkTarget() {
		
		if (!this.worldObj.isRemote) {
			
			this.dataWatcher.updateObject(26, (Object) this.animationLifeTime);
			this.dataWatcher.updateObject(27, (Object) this.health);
			
			if (this.target != null) {
				this.dataWatcher.updateObject(25, (Object) this.target.getEntityId());
			} else {
				this.dataWatcher.updateObject(25, (Object) (-1));
				
				if (this.targetUUID != null) {
					this.target = QuicksandManager.getEntityByUUID(this.worldObj, this, this.targetUUID, 32.0);
				}
			}
		} else {
			this.health = this.dataWatcher.getWatchableObjectFloat(27);
			this.target = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(25));
			
			if (this.animationLifeTime < 10000) {
				
				if (this.target != null) {
					this.animationLifeTime = this.dataWatcher.getWatchableObjectInt(26);
					this.setSize(this.target.width + 0.1f, 1.0f);
				} else {
					this.animationLifeTime = 10000 + Math.max(100 - this.animationLifeTime, 0);
				}
			}
		}
	}
	
	// Kills the tentacle if it is outside the swallowing flesh block
	public void CheckInQuicksandBlock() {
		
		if (this.worldObj.getBlockState(new BlockPos((int) Math.floor(this.posX), (int) Math.floor(this.posY - 0.5),
			(int) Math.floor(this.posZ))) != QSAND_Blocks.swallowingFlesh
			&& this.worldObj.getBlockState(new BlockPos((int) Math.floor(this.posX), (int) Math.floor(this.posY + 0.5),
				(int) Math.floor(this.posZ))) != QSAND_Blocks.swallowingFlesh) {
			this.setDead();
		}
	}
	
	public void ManipulateWithTerrain() {
	
	}
	
	@Override
	public boolean canBeCollidedWith() {
		
		return true;
	}
	
	@Override
	public void writeSpawnData(final ByteBuf data) {
		
		data.writeInt(this.high);
		data.writeInt(this.animationLifeTime);
		data.writeInt((this.target != null) ? this.target.getEntityId() : 0);
		data.writeFloat(this.health);
	}
	
	@Override
	public void readSpawnData(final ByteBuf data) {
		
		this.high = data.readInt();
		this.animationLifeTime = data.readInt();
		this.target = this.worldObj.getEntityByID(data.readInt());
		this.health = data.readFloat();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(final double range) {
		
		double distance = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
		distance *= 64.0;
		return range < distance * distance;
	}
	
	public void CheckDead() {
		
		if (this.animationLifeTime < 10000 && this.worldObj.getTotalWorldTime() % 32L == 0L) {
			this.target = null;
			this.animationLifeTime = 10000 + Math.max(100 - this.animationLifeTime, 0);
		}
	}
	
	public void DeadEffect() {
		
		for (int var1 = 0; var1 < 5; ++var1) {
			final double var2 = this.rand.nextGaussian() * 0.02;
			final double var3 = this.rand.nextGaussian() * 0.02;
			final double var4 = this.rand.nextGaussian() * 0.02;
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
				this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width,
				this.high + 0.5f + (double) (this.rand.nextFloat() * this.height),
				this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, var2, var3, var4);
		}
		this.setDead();
	}
	
	@Override
	@Deprecated
	public void onUpdate() {
		
		// TODO: Finish this
	}
	
	@Override
	public float getEyeHeight() {
		
		return 1.5f;
	}
	
	@Override
	public boolean attackEntityFrom(final DamageSource dmgs, final float amount) {
		
		if (this.worldObj.isRemote) {
			return false;
		}
		
		if (this.health > 0.0f && this.maxHurtResistantTime < 1) {
			this.health -= amount;
			
			if (this.health <= 0.01f) {
				this.playSound("game.neutral.die", 1.0f,
					(this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f + 1.0f);
				this.playSound("mob.silverfish.kill", 1.0f,
					(this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f + 1.75f);
				this.playSound("mob.silverfish.step", 0.5f, this.worldObj.rand.nextFloat() * 0.25f + 0.5f);
				this.health = 0.0f;
				this.checkTarget();
			} else {
				this.playSound("game.neutral.hurt", 1.0f,
					(this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f + 1.0f);
			}
			
			this.maxHurtResistantTime = 20;
			return true;
		}
		
		return false;
	}
	
	@Override
	public void moveEntity(final double p_70091_1_, final double p_70091_3_, final double p_70091_5_) {
	
	}
	
	@Override
	public void writeEntityToNBT(final NBTTagCompound compound) {
		
		compound.setInteger("high", this.high);
		compound.setInteger("TickHere", this.animationLifeTime);
		if (this.target != null) {
			compound.setLong("TargetMSB", this.target.getUniqueID().getMostSignificantBits());
			compound.setLong("TargetLSB", this.target.getUniqueID().getLeastSignificantBits());
		} else {
			compound.setLong("TargetMSB", 0L);
			compound.setLong("TargetLSB", 0L);
		}
		compound.setFloat("FKHealth", this.health);
	}
	
	@Override
	public void readEntityFromNBT(final NBTTagCompound nbtt) {
		
		this.high = nbtt.getInteger("high");
		this.animationLifeTime = nbtt.getInteger("TickHere");
		this.targetUUID = new UUID(nbtt.getLong("TargetMSB"), nbtt.getLong("TargetLSB"));
		this.health = nbtt.getFloat("FKHealth");
	}
	
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		
		return 0.0f;
	}
}
