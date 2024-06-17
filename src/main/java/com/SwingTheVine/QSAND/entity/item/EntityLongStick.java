package com.SwingTheVine.QSAND.entity.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** An entity that controls the Long Stick.
 * 
 * @since <b>0.31.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class EntityLongStick extends Entity implements IEntityAdditionalSpawnData {
	
	public int stickPosX;
	public int stickPosY;
	public int stickPosZ;
	public Entity player;
	
	public EntityLongStick(final World world) {
		super(world);
		
		this.stickPosX = 0;
		this.stickPosY = 0;
		this.stickPosZ = 0;
		this.setSize(0.25f, 0.25f);
		this.ignoreFrustumCheck = true;
	}
	
	public EntityLongStick(final World world, final double entityPosX, final double entityPosY, final double entityPosZ,
		final Entity entity) {
		this(world);
		this.setPosition(entityPosX, entityPosY, entityPosZ);
		this.ignoreFrustumCheck = true;
		this.player = entity;
	}
	
	public EntityLongStick(final World world, final double entityPosX, final double entityPosY, final double entityPosZ,
		final Entity entity, final int blockPosX, final int blockPosY, final int blockPosZ) {
		this(world);
		this.stickPosX = blockPosX;
		this.stickPosY = blockPosY;
		this.stickPosZ = blockPosZ;
	}
	
	public EntityLongStick(final World world, final EntityLivingBase entity) {
		super(world);
		this.stickPosX = 0;
		this.stickPosY = 0;
		this.stickPosZ = 0;
		this.ignoreFrustumCheck = true;
		this.setSize(0.25f, 0.25f);
		this.setPosition(this.posX, this.posY, this.posZ);
	}
	
	@Override
	protected void entityInit() {
	
	}
	
	@Override
	public void writeSpawnData(final ByteBuf data) {
		
		data.writeInt((this.player != null) ? this.player.getEntityId() : 0);
		data.writeInt(this.stickPosX);
		data.writeInt(this.stickPosY);
		data.writeInt(this.stickPosZ);
	}
	
	@Override
	public void readSpawnData(final ByteBuf data) {
		
		this.player = this.worldObj.getEntityByID(data.readInt());
		this.stickPosX = data.readInt();
		this.stickPosY = data.readInt();
		this.stickPosZ = data.readInt();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(final double range) {
		
		double distance = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
		distance *= 64.0;
		return range < distance * distance;
	}
	
	@Override
	@Deprecated
	public void onUpdate() {
		
		// TODO: Fill this in and remove deprecated tag
	}
	
	public void spawnParticle(final Block block, final int metadata, final int count, final double radius,
		final double radiusY, final double offsetY) {
		
		for (int currentParticle = 0; currentParticle < count; currentParticle++) {
			this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
				this.posX + this.worldObj.rand.nextFloat() * radius - radius / 2.0,
				this.posY + offsetY + this.worldObj.rand.nextFloat() * radiusY - radiusY / 2.0,
				this.posZ + this.worldObj.rand.nextFloat() * radius - radius / 2.0, 0.0, 0.0, 0.0);
		}
	}
	
	public void playSound(final String sound, final float volume, final float pitch, final float rpitch) {
		
		this.worldObj.playSound(this.posX, this.posY, this.posZ, sound, volume,
			this.worldObj.rand.nextFloat() * rpitch + pitch, false);
	}
	
	public void playBottom(final Block block) {
		
		if (block.getMaterial().isSolid()) {
			this.worldObj.playSound(this.posX, this.posY - 1.0, this.posZ, block.stepSound.getStepSound(), 0.2f, 1.0f,
				false); // TODO: getPitch is gone
		}
	}
	
	public void playSuction(final float volume) {
		
		this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.magmacube.jump", volume / 2.5f,
			0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f, false);
		this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.magmacube.jump", volume / 2.5f,
			0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f, false);
		this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.magmacube.jump", volume / 2.5f,
			0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f, false);
		this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.magmacube.jump", volume / 2.5f,
			0.25f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f, false);
	}
	
	@Override
	public void writeEntityToNBT(final NBTTagCompound compound) {
	
	}
	
	@Override
	public void readEntityFromNBT(final NBTTagCompound compound) {
	
	}
	
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		
		return 0.0f;
	}
}
