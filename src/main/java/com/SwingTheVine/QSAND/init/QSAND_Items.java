package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.item.ItemQuicksandBucket;
import com.SwingTheVine.QSAND.item.ItemSpawnEgg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class QSAND_Items {
	
	public static Item test_item;
	public static Item spawnEgg;
	public static Item quicksandBucket;
	public static Item bootsWading;
	public static Item longStick;
	public static Item larvaeRaw;
	
	public static void init() {
		
		test_item = new Item().setUnlocalizedName("test_item").setCreativeTab(QSAND.QSANDTab);
		spawnEgg = new ItemSpawnEgg().setUnlocalizedName("spawn_egg").setCreativeTab(QSAND.QSANDTab);
		quicksandBucket = new ItemQuicksandBucket().setUnlocalizedName("bucket").setCreativeTab(QSAND.QSANDTab);
		
	}
	
	public static void registerItems() {
		
		GameRegistry.registerItem(test_item, test_item.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(spawnEgg, spawnEgg.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(quicksandBucket, quicksandBucket.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		
		registerRenderInventory(test_item);
		registerRenderInventory(spawnEgg);
		registerRenderInventory(quicksandBucket);
	}
	
	public static void registerRenderInventory(final Item item) {
		
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
			new ModelResourceLocation(ModInfo.id + ":" + item.getUnlocalizedName().substring(5), "inventory"));
			
		final String prefix = (item.equals(quicksandBucket) ? "quicksandBucket_" : "item_");
		
		// Registers the item to the ore dictionary for creative tab sorting
		OreDictionary.registerOre(prefix + item.getUnlocalizedName().substring(5),
			new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
	}
}
