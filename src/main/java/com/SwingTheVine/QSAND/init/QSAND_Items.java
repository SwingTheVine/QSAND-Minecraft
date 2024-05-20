package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.items.ItemSpawnEgg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class QSAND_Items {
	public static Item test_item;
	public static Item spawnEgg;
	public static Item bootsWading;
	public static Item longStick;
	
	public static void init() {
		test_item = new Item().setUnlocalizedName("test_item").setCreativeTab(QSAND.QSANDTab);
		spawnEgg = new ItemSpawnEgg().setUnlocalizedName("spawn_egg");
	}
	
	public static void registerItems() {
		GameRegistry.registerItem(test_item, test_item.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(spawnEgg, spawnEgg.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		registerRenderInventory(test_item);
		registerRenderInventory(spawnEgg);
	}
	
	public static void registerRenderInventory(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModInfo.id + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}
