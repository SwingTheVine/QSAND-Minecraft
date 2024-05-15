package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class QSAND_Items {
	public static Item test_item;
	public static Item bootsWading;
	
	public static void init() {
		test_item = new Item().setUnlocalizedName("test_item").setCreativeTab(QSAND.QSANDTab);
	}
	
	public static void registerItems() {
		GameRegistry.registerItem(test_item, test_item.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		registerRenderInventory(test_item);
	}
	
	public static void registerRenderInventory(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModInfo.id + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}
