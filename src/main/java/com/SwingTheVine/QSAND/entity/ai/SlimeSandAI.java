package com.SwingTheVine.QSAND.entity.ai;

import java.util.Random;

import com.SwingTheVine.QSAND.entity.SlimeSand;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class SlimeSandAI {

	private World world;
	private Class <? extends Entity> player;
	private SlimeSand slime;
	private final EntityAINearestAttackableTarget.Sorter sorter;
	
	private Random random = new Random(30);
	
	public SlimeSandAI(World world, Class <? extends Entity> player, SlimeSand slime) {
		this.world = world;
		this.player = player;
		this.slime = slime;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(slime);
	}
	
	public static class AISlimeAttack extends EntityAIBase {
        private SlimeSand slime;
        private int field_179465_b;

        public AISlimeAttack(SlimeSand slime) {
            this.slime = slime;
            this.setMutexBits(2);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            EntityLivingBase entity = this.slime.getAttackTarget();
            return (entity == null ? false : (!entity.isEntityAlive() ? false : !(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.disableDamage)) && this.slime.riddenByEntity == null;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.field_179465_b = 300;
            super.startExecuting();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting() {
            EntityLivingBase entity = this.slime.getAttackTarget();
            return entity == null ? false : (!entity.isEntityAlive() ? false : (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage ? false : --this.field_179465_b > 0)) && this.slime.riddenByEntity == null;
        }

        /**
         * Updates the task
         */
        public void updateTask() {
            this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 20.0F);
            ((SlimeSandAI.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.slime.rotationYaw, this.slime.canDamagePlayer());
        }
    }

	public static class AISlimeFaceRandom extends EntityAIBase {
	        private SlimeSand slime;
	        private float field_179459_b;
	        private int field_179460_c;
	
	        public AISlimeFaceRandom(SlimeSand slime) {
	            this.slime = slime;
	            this.setMutexBits(2);
	        }
	
	        /**
	         * Returns whether the EntityAIBase should begin execution.
	         */
	        public boolean shouldExecute() {
	            return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava());
	        }
	
	        /**
	         * Updates the task
	         */
	        public void updateTask() {
	            if (--this.field_179460_c <= 0) {
	                this.field_179460_c = 40 + this.slime.getRNG().nextInt(60);
	                this.field_179459_b = (float)this.slime.getRNG().nextInt(360);
	            }
	
	            ((SlimeSandAI.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.field_179459_b, false);
	        }
	    }
	
	public static class AISlimeHop extends EntityAIBase {
	        private SlimeSand slime;
	
	        public AISlimeHop(SlimeSand slime) {
	            this.slime = slime;
	            this.setMutexBits(5);
	        }
	
	        /**
	         * Returns whether the EntityAIBase should begin execution.
	         */
	        public boolean shouldExecute() {
	            return true;
	        }
	
	        /**
	         * Updates the task
	         */
	        public void updateTask() {
	            ((SlimeSandAI.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0D);
	        }
	    }
	
	public static class SlimeMoveHelper extends EntityMoveHelper {
	        private float field_179922_g;
	        private int slimeJumpDelay;
	        private SlimeSand slime;
	        private boolean field_179923_j;
	
	        public SlimeMoveHelper(SlimeSand slime) {
	            super(slime);
	            this.slime = slime;
	            ((PathNavigateGround)slime.getNavigator()).setAvoidsWater(true);
	        }
	
	        public void func_179920_a(float p_179920_1_, boolean p_179920_2_) {
	            this.field_179922_g = p_179920_1_;
	            this.field_179923_j = p_179920_2_;
	        }
	
	        public void setSpeed(double speed) {
	            this.speed = speed;
	            this.update = true;
	        }
	
	        public void onUpdateMoveHelper() {
	            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.field_179922_g, 30.0F);
	            this.entity.rotationYawHead = this.entity.rotationYaw;
	            this.entity.renderYawOffset = this.entity.rotationYaw;
	
	            if (!this.update) {
	                this.entity.setMoveForward(0.0F);
	            } else {
	                this.update = false;
	                
	                if (this.entity.onGround) {
	                	this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
	                	
	                	if (this.slimeJumpDelay-- <= 0) {
	                		
		                    this.slimeJumpDelay = this.slime.getJumpDelay();
	
	                        if (this.field_179923_j) {
	                            this.slimeJumpDelay /= 10;
	                        }
	                        
	                        if (this.slime.riddenByEntity == null) {
	                        	this.slime.getJumpHelper().setJumping();
	                        	
	                        	if (this.slime.makesSoundOnJump()) {
	                                this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
	                                this.slime.playSound("dig.sand", 0.5f, this.slime.getRNG().nextFloat() * 0.5f + 0.5f);
	                            }
	                        	
	                        	this.slime.moveStrafing = 1.0f - this.slime.getRNG().nextFloat() * 2.0f;
	                        	this.slime.moveForward = (float)(this.slime.getSlimeSize());
	                        } else {
	                        	this.slimeJumpDelay = 10;
	                        }
	                	} else {
	                		this.slime.moveStrafing = this.slime.moveForward = 0.0F;
                            this.entity.setAIMoveSpeed(0.0F);
	                	}
                    } else {
                    	this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
                    }
	            }
	        }
	    }
}
