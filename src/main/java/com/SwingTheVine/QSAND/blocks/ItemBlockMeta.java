package com.SwingTheVine.QSAND.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlock {

	public ItemBlockMeta(Block block) {
        super(block);
        if (!(block instanceof IMetaBlockName)) {
            throw new IllegalArgumentException(String.format("The given Block %s is not an instance of IMetaBlockName.", block.getUnlocalizedName()));
        }
        this.setHasSubtypes(true);
    }
	
	public int getMetadata(int damage){
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + ((IMetaBlockName)this.block).getSpecialName(stack);
    }

}
