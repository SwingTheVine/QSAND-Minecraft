package com.SwingTheVine.QSAND.proxy;

import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ClientProxy extends CommonProxy{
	@Override
	public void registerRenders() {
		QSAND_Blocks.registerRenders();
		QSAND_Items.registerRenders();
	}
	
	public void registerModelQSAND() {
		ModelBakery.registerItemVariants(Item.getItemFromBlock(QSAND_Blocks.test_block),
				new ResourceLocation("QSAND:test_block"));
	}
}
