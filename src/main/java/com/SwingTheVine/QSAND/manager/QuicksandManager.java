package com.SwingTheVine.QSAND.manager;

import com.SwingTheVine.QSAND.init.QSAND_Blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.BlockFluidClassic;

public class QuicksandManager {
	
	public static boolean isEntityInsideOfBlock(final Entity triggeringEntity, final Block block) {
        double overCor = 0.0; // If the fluid is a full block
        
        // If the block is a liquid, AND the block is a classic fluid...
        if (block.getMaterial().isLiquid() && block instanceof BlockFluidClassic) {
            overCor = ((((BlockFluidClassic)block).getMaxRenderHeightMeta() == -1) ? 0.0 : 0.15);
        }
        
        final double triggEntityHeight = triggeringEntity.posY + triggeringEntity.getEyeHeight() - 0.075 + overCor; // How tall the entity is. Measured from camera POV to bottom of entity
        final int triggEntityPosX = MathHelper.floor_double(triggeringEntity.posX); // Triggering entity's X position
        final int triggEntityMaxPosY = MathHelper.floor_float((float)MathHelper.floor_double(triggEntityHeight)); // Triggering entity's maximum Y position floored
        final int triggEntityPosZ = MathHelper.floor_double(triggeringEntity.posZ); // Triggering entity's Z position
        
        // Checks what the block above the entity's head is (if true) or what block the entity's head is in.
        // In other words, it returns what block the entity is inside of
        Block triggEntityInsideBlockLower = (block == QSAND_Blocks.moss)
        		? triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY + 1, triggEntityPosZ)).getBlock()
				: triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY, triggEntityPosZ)).getBlock();
        
		// Same as before except it is checking one block higher
        Block triggEntityInsideBlockHigher = (block == QSAND_Blocks.moss)
        		? triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY + 2, triggEntityPosZ)).getBlock()
				: triggeringEntity.worldObj.getBlockState(new BlockPos(triggEntityPosX, triggEntityMaxPosY + 1, triggEntityPosZ)).getBlock();
		
		// Returns true if the top half of the entity is inside this block,
        //    AND this block is a moss block,
        //       OR the block above the entity is a solid block, AND the top of the solid block is higher than the entity's height,
        //       OR the top of this block is higher than the top of the entity
        return triggEntityInsideBlockLower == block && (block == QSAND_Blocks.moss || (triggEntityInsideBlockHigher.getMaterial().isSolid() && triggEntityMaxPosY + 1 > triggEntityHeight) || triggEntityMaxPosY + triggEntityInsideBlockLower.getBlockBoundsMaxY() > triggEntityHeight);
    }
}
