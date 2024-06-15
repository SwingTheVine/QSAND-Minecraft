package com.SwingTheVine.QSAND.creativetab;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemSorter implements Comparator<ItemStack> {
	
	private final Map<ItemStack, OreDictionaryPriority> oreDictPriorityCache = new HashMap<>();
	
	private OreDictionaryPriority getOreDictionaryPriority(final ItemStack itemStack) {
		
		OreDictionaryPriority priority = oreDictPriorityCache.get(itemStack);
		if (priority != null) {
			return priority;
		}
		
		priority = OreDictionaryPriority.None;
		
		for (final int oreID : OreDictionary.getOreIDs(itemStack)) {
			final String oreName = OreDictionary.getOreName(oreID);
			
			if (oreName.startsWith("quicksandBlock_")) {
				priority = OreDictionaryPriority.QuicksandBlock;
				break;
			} else if (oreName.startsWith("quicksandFluid_")) {
				priority = OreDictionaryPriority.QuicksandFluid;
				break;
			} else if (oreName.startsWith("quicksandBucket_")) {
				priority = OreDictionaryPriority.QuicksandBucket;
				break;
			} else if (oreName.startsWith("block_")) {
				priority = OreDictionaryPriority.Block;
				break;
			} else if (oreName.startsWith("item_")) {
				priority = OreDictionaryPriority.Item;
				break;
			}
		}
		
		oreDictPriorityCache.put(itemStack, priority);
		
		return priority;
	}
	
	@Override
	public int compare(final ItemStack stack1, final ItemStack stack2) {
		
		final OreDictionaryPriority priority1 = getOreDictionaryPriority(stack1),
				priority2 = getOreDictionaryPriority(stack2);
				
		if (priority1 == priority2) { // Both stacks have the same priority, order them by their ID/metadata
			final Item item1 = stack1.getItem();
			final Item item2 = stack2.getItem();
			
			if (item1 == item2) { // If the stacks have the same item, order them by their metadata
				return stack1.getMetadata() - stack2.getMetadata();
			} else { // Else order them by their ID
				return Item.getIdFromItem(item1) - Item.getIdFromItem(item2);
			}
		} else { // Stacks have different priorities, order them by priority instead
			return priority1.compareTo(priority2);
		}
	}
}
