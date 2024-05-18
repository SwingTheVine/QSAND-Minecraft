package com.SwingTheVine.QSAND.proxy;

import com.SwingTheVine.QSAND.client.entity.BubbleRender;
import com.SwingTheVine.QSAND.client.entity.SlimeSandRender;
import com.SwingTheVine.QSAND.entity.SlimeSand;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	// Registers the renders for the blocks, items, etc.
	@Override
	public void registerRenders() {
		QSAND_Blocks.registerRenders(); // Registers the renders for the blocks
		QSAND_Items.registerRenders(); // Registers the renders for the items
	}
	
	// Registers the renders for the entities
	@Override
	public void registerEntityRenders() {
		System.out.println("Register Entity Renders");
		RenderingRegistry.registerEntityRenderingHandler((Class)BubbleRender.class, BubbleRender.factory);
		RenderingRegistry.registerEntityRenderingHandler(SlimeSand.class, new SlimeSandRender.Factory());
		//RenderingRegistry.registerEntityRenderingHandler(SlimeSand.class, new SlimeSandRender(Minecraft.getMinecraft().getRenderManager(), new ModelSlimeVoid(0), 0.5F));
	}
	
	// Registers the models for the items
	@Override
	public void registerModelQSAND() {
		
		// Registers the different renders/skins for each item variant/metadata
		ModelBakery.registerItemVariants(Item.getItemFromBlock(QSAND_Blocks.test_block),
				new ResourceLocation("QSAND:test_block"));
	}
}
