package com.SwingTheVine.QSAND.entity.effect;

import com.SwingTheVine.QSAND.init.QSAND_Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/** Controls calculations for tentacles that appear in mud.
 * 
 * @since <b>0.32.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class EntityTentaclesMud extends EntityTentacles {
	
	public EntityTentaclesMud(final World world) {
		super(world);
	}
	
	public EntityTentaclesMud(final World world, final double entityPosX, final double entityPosY,
		final double entityPosZ, final Entity target, final int high, final int animationLifeTime) {
		super(world, entityPosX, entityPosY, entityPosZ, target, high, animationLifeTime);
	}
	
	@Override
	public void CheckInQuicksandBlock() {
		
		final Block block1 = this.worldObj.getBlockState(
			new BlockPos((int) Math.floor(this.posX), (int) Math.floor(this.posY - 0.5), (int) Math.floor(this.posZ)))
			.getBlock();
		final Block block2 = this.worldObj.getBlockState(
			new BlockPos((int) Math.floor(this.posX), (int) Math.floor(this.posY + 0.5), (int) Math.floor(this.posZ)))
			.getBlock();
			
		if (block1 != QSAND_Blocks.mud && block1 != QSAND_Blocks.quicksandJungle && block1 != QSAND_Blocks.quicksandSoft
			&& block2 != QSAND_Blocks.mud && block2 != QSAND_Blocks.quicksandJungle
			&& block2 != QSAND_Blocks.quicksandSoft) {
			this.setDead();
		}
	}
	
	@Override
	public void ManipulateWithTerrain() {
		
		final int blockPosX = (int) Math.floor(this.posX);
		final int blockPosZ = (int) Math.floor(this.posZ);
		int blockPosY1 = (int) Math.floor(this.posY - 0.5);
		final int blockPosY2 = (int) Math.floor(this.posY + 0.5);
		
		if (this.worldObj.getTotalWorldTime() % 32L == 0L) {
			
			for (int height = -1; height < 2; height++) {
				
				for (int volume = -1; volume < 2; volume++) {
					
					if (this.worldObj.getBlockState(new BlockPos(blockPosX + height, blockPosY1, blockPosZ + volume))
						.getBlock() == QSAND_Blocks.mud
						|| this.worldObj.getBlockState(new BlockPos(blockPosX + height, blockPosY2, blockPosZ + volume))
							.getBlock() == QSAND_Blocks.mud) {
							
						for (int i = 0; i < 4; ++i) {
							blockPosY1 = blockPosY2 - i + 2;
							
							if (this.worldObj
								.getBlockState(new BlockPos(blockPosX + height, blockPosY1, blockPosZ + volume))
								.getBlock() == QSAND_Blocks.mud
								&& this.worldObj
									.getBlockState(new BlockPos(blockPosX + height, blockPosY1, blockPosZ + volume))
									.getBlock().getMetaFromState(this.worldObj.getBlockState(
										new BlockPos(blockPosX + height, blockPosY1, blockPosZ + volume))) < 3) {
								this.worldObj.setBlockState(
									new BlockPos(blockPosX + height, blockPosY1, blockPosZ + volume),
									(IBlockState) QSAND_Blocks.mud.getBlockState(), 3);
							}
						}
					}
				}
			}
		}
	}
}
