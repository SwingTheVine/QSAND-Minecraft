package com.SwingTheVine.QSAND.proxy;

import com.SwingTheVine.QSAND.client.entity.BubbleRender;
import com.SwingTheVine.QSAND.client.entity.SlimeMudRender;
import com.SwingTheVine.QSAND.client.entity.SlimeSandRender;
import com.SwingTheVine.QSAND.client.entity.SlimeTarRender;
import com.SwingTheVine.QSAND.client.entity.SlimeVoidRender;
import com.SwingTheVine.QSAND.client.model.ModdedModelManager;
import com.SwingTheVine.QSAND.entity.effect.EntityBubble;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeMud;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeSand;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeTar;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeVoid;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/** Implements client-side rendering
 * 
 * @since <b>0.1.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrCrayfish's code to 1.8.9
 * @author <b>MrCrayfish</b> - 1.7.10 source code written here: <a href=
 * "https://github.com/MrCrayfish/ModdingTutorials/blob/master/src/main/java/com/mrcrayfish/teleportmod/proxy/ClientProxy.java">
 * https://github.com/MrCrayfish/ModdingTutorials/blob/master/src/main/java/com/mrcrayfish/teleportmod/proxy/ClientProxy.java</a>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
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
		
		RenderingRegistry.registerEntityRenderingHandler(EntityBubble.class, new BubbleRender.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntitySlimeVoid.class, new SlimeVoidRender.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntitySlimeMud.class, new SlimeMudRender.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntitySlimeSand.class, new SlimeSandRender.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntitySlimeTar.class, new SlimeTarRender.Factory());
	}
	
	// Registers the models for the fluids
	@Override
	public void registerFluidModels() {
		
		ModdedModelManager.INSTANCE.registerFluidModels();
	}
	
	// Registers the models for the items
	@Override
	public void registerItemModels() {
		
		// Registers the different renders/skins for each item variant/metadata
		ModelBakery.registerItemVariants(Item.getItemFromBlock(QSAND_Blocks.test_block),
			new ResourceLocation("QSAND:test_block"));
	}
}
