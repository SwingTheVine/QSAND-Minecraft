package com.SwingTheVine.QSAND.entity.ai;

import java.util.Random;

import com.SwingTheVine.QSAND.entity.SlimeSand;
import com.SwingTheVine.QSAND.handler.ConfigHandler;

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
	
	// Constructor
	public SlimeSandAI(World world, Class <? extends Entity> player, SlimeSand slime) {
		this.world = world; // The current world
		this.player = player; // The current player
		this.slime = slime; // The current slime
		this.sorter = new EntityAINearestAttackableTarget.Sorter(slime); // The list of closest attackable target to furthest
	}
	
	// How to handle attacking
	public static class AISlimeAttack extends EntityAIBase {
        private SlimeSand slime; // The current slime
        private int field_179465_b; // Starts at 300 and decreases every execution that the player has disabled damage

        // Constructor
        public AISlimeAttack(SlimeSand slime) {
            this.slime = slime; // The current slime
            this.setMutexBits(2); // If this task has to run async or sync
        }

        // Returns whether the EntityAIBase should begin execution.
        public boolean shouldExecute() {
            EntityLivingBase entity = this.slime.getAttackTarget(); // Entity to attack
            
            // Returns true if the entity to attack is NOT null,
            //    AND the entity to attack is alive (i.e. not a death animation),
            //    AND the entity to attack is NOT a player,
            //        OR the player has NOT disabled damage, AND no entity is mounted on the slime...
            return (entity == null ? false : (!entity.isEntityAlive() ? false : !(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.disableDamage)) && this.slime.riddenByEntity == null;
        }

        // Execute a one shot task or start executing a continuous task
        public void startExecuting() {
            this.field_179465_b = 300; // Set the variable to 300
            super.startExecuting(); // Run all the code in the super implementation
        }

        // Returns whether an in-progress EntityAIBase should continue executing
        public boolean continueExecuting() {
            EntityLivingBase entity = this.slime.getAttackTarget(); // Entity to attack
            
            // Returns true if the entity to attack is NOT null,
            //    AND the entity to attack is alive (i.e. not a death animation),
            //    AND the entity to attack is a player,
            //    AND the player has NOT disabled damage, AND the 300 variable minus 1 is greater than 0,
            //    AND no entity is mounted on the slime...
            return entity == null ? false : (!entity.isEntityAlive() ? false : (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage ? false : --this.field_179465_b > 0)) && this.slime.riddenByEntity == null;
        }

        // Updates the task
        public void updateTask() {
            this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 20.0F); // Faces the entity to attack
            
            // Unknown
            ((SlimeSandAI.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.slime.rotationYaw, this.slime.canDamagePlayer());
        }
    }

	// Makes the slime face a random direction
	public static class AISlimeFaceRandom extends EntityAIBase {
	        private SlimeSand slime; // The current slime
	        private float slimeYaw; // Slime Yaw rotation in degrees
	        private int field_179460_c; // Unknown
	
	        // Constructor
	        public AISlimeFaceRandom(SlimeSand slime) {
	            this.slime = slime; // The current slime
	            this.setMutexBits(2); // If this task has to run async or sync
	        }
	
	        // Returns whether the EntityAIBase should begin execution.
	        public boolean shouldExecute() {
	        	
	        	// Returns true if the entity to attack is NOT null,
	        	//    AND the slime is on the ground, OR the slime is in the water, OR the slime is in lava...
	            return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava());
	        }
	
	        // Updates the task
	        public void updateTask() {
	        	
	        	// If the field minus 1 is less than or equal to zero...
	            if (--this.field_179460_c <= 0) {
	            	
	                this.field_179460_c = 40 + this.slime.getRNG().nextInt(60); // Set the variable to a number between 40 and 99
	                this.slimeYaw = (float)this.slime.getRNG().nextInt(360); // Set the Yaw rotation to a number between 0 and 359
	            }
	
	            // Rotates the slime
	            ((SlimeSandAI.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.slimeYaw, false);
	        }
	    }
	
	// Allows the slime to hop. Return false in "shouldExecute" to disable
	public static class AISlimeHop extends EntityAIBase {
	        private SlimeSand slime; // The current slime
	
	        // Constructor
	        public AISlimeHop(SlimeSand slime) {
	            this.slime = slime; // The current slime
	            this.setMutexBits(5); // // If this task has to run async or sync
	        }
	
	        // Returns whether the EntityAIBase should begin execution.
	        public boolean shouldExecute() {
	            return true; // Always execute when called
	        }
	
	        // Updates the task
	        public void updateTask() {
	        	
	        	// Sets the speed the slime hops at
	            ((SlimeSandAI.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0D);
	        }
	    }
	
	public static class SlimeMoveHelper extends EntityMoveHelper {
	        private float field_179922_g; // Unknown
	        private int slimeJumpDelay; // How long to wait between jumps
	        private SlimeSand slime; // The current slime
	        private boolean slimeAggro; // If the slime is aggroed
	
	        // Constructor
	        public SlimeMoveHelper(SlimeSand slime) {
	            super(slime); // Runs all the code in the super implementation
	            this.slime = slime; // The current slime
	            
	            // If the user has NOT enabled only MFQM...
	            if (!ConfigHandler.onlyMFQM) {
		            ((PathNavigateGround)slime.getNavigator()).setAvoidsWater(true); // The slime tries to get out of the rain
	            }
	        }
	
	        // Unknown
	        public void func_179920_a(float p_179920_1_, boolean aggro) {
	            this.field_179922_g = p_179920_1_;
	            this.slimeAggro = aggro;
	        }
	
	        // Sets the speed the slime moves at
	        public void setSpeed(double speed) {
	            this.speed = speed; // Sets the speed
	            this.update = true; // Triggers an update of the slime
	        }
	
	        // When an update is triggered on the slime
	        public void onUpdateMoveHelper() {
	        	
	        	// Set the Yaw
	            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.field_179922_g, 30.0F);
	            this.entity.rotationYawHead = this.entity.rotationYaw;
	            this.entity.renderYawOffset = this.entity.rotationYaw;
	
	            // If the slime does NOT need an update...
	            if (!this.update) {
	                this.entity.setMoveForward(0.0F); // Make the slime speed 0
	            } else {
	                this.update = false; // The slime no longer needs an update because we are updating it
	                
	                // If the slime is on the ground...
	                if (this.entity.onGround) {
	                	
	                	// Set the speed to the current speed times the monster speed
	                	this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
	                	
	                	// If the jump delay minus 1 is less than or equal to 0...
	                	if (this.slimeJumpDelay-- <= 0) {
	                		
	                		// Reset the jump delay
		                    this.slimeJumpDelay = this.slime.getJumpDelay();
	
		                    // If something unknown...
	                        if (this.slimeAggro) {
	                            this.slimeJumpDelay /= 10; // Set the jump delay to 10% of what it previously was
	                        }
	                        
	                        // If there is no entity ridding the slime...
	                        if (this.slime.riddenByEntity == null) {
	                        	
	                        	this.slime.getJumpHelper().setJumping(); // Set the slime as jumping
	                        	
	                        	// If the slime makes a sound when it jumps...
	                        	if (this.slime.makesSoundOnJump()) {
	                        		
	                        		// Play the sounds
	                                this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
	                                this.slime.playSound("dig.sand", 0.5f, this.slime.getRNG().nextFloat() * 0.5f + 0.5f);
	                            }
	                        	
	                        	this.slime.moveStrafing = 1.0f - this.slime.getRNG().nextFloat() * 2.0f; // Strafes between 1 and -0.98
	                        	this.slime.moveForward = (float)(this.slime.getSlimeSize()); // Moves forwards
	                        } else {
	                        	this.slimeJumpDelay = 10; // Set the jump delay to 10 when an entity is mounted
	                        }
	                	} else {
	                		// Else if the jump delay minus 1 is greater than 0...
	                		this.slime.moveStrafing = this.slime.moveForward = 0.0F; // Don't move
                            this.entity.setAIMoveSpeed(0.0F); // Don't move
	                	}
                    } else {
                    	
                    	// Sets the movement speed to the current speed times the monster movement speed
                    	this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
                    }
	            }
	        }
	    }
}
