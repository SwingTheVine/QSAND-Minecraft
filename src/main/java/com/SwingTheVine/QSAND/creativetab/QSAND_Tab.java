package com.SwingTheVine.QSAND.creativetab;

import java.util.Collections;
import java.util.List;

import com.SwingTheVine.QSAND.init.QSAND_Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Implements the custom creative tab
 * 
 * @since <b>0.4.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrCrayfish</b> - 1.8.9 source code written here:
 * <a href="https://github.com/MrCrayfish/ModdingTutorials/blob/master/src/main/java/com/mrcrayfish/teleportmod/TeleportTab.java">
 * https://github.com/MrCrayfish/ModdingTutorials/blob/master/src/main/java/com/mrcrayfish/teleportmod/TeleportTab.java</a>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class QSAND_Tab extends CreativeTabs {
	
	private final ItemSorter itemSorter = new ItemSorter(); // Constructs a new item sorter for the custom creative tab
	
	public QSAND_Tab(final String label) {
		super(label);
	}
	
	@Override
	public Item getTabIconItem() {
		
		return QSAND_Items.test_item;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void displayAllReleventItems(final List items) {
		
		super.displayAllReleventItems(items);
		
		/*
		 * // Code for finding the NBT tag of a Universal Bucket from the fluid
		 * UniversalBucket mysteryBucket = new UniversalBucket(1, new ItemStack(UniversalBucket.getByNameOrId("qsand:bog")),
		 * true);
		 * ItemStack itemStack = UniversalBucket.getFilledBucket(mysteryBucket, QSAND_Fluids.bog);
		 * items.add(itemStack);
		 * System.out.println(itemStack.getTagCompound());
		 */
		
		// All fluids to display buckets for in the custom creative tab
		final String[] fluidType = {"test_fluid", "bog"};
		
		// Adds every fluid type above as a bucket item in the creative tab
		for (final String fluid : fluidType) {
			final ItemStack bucket = new ItemStack(UniversalBucket.getByNameOrId("forge:bucketFilled")); // Creates a new
																											// universal bucket
																											// instance
			final NBTTagCompound nbtTag = new NBTTagCompound(); // Creates a new NBT Tag
			nbtTag.setString("FluidName", fluid); // Adds an attribute holding the name of the fluid
			nbtTag.setInteger("Amount", 1); // Adds an attribute holding the quantity of fluid in the bucket
			bucket.setTagCompound(nbtTag); // Adds the NBT tag to the universal bucket instance
			// items.add(bucket); // Adds the universal bucket instance to the creative tab
		}
		
		for (int index = 1; index < items.size(); index++) {
			itemSorter.compare((ItemStack) items.get(index - 1), (ItemStack) items.get(index));
		}
		
		// Sort the item list using the ItemSorter instance
		Collections.sort(items, itemSorter);
	}
	
}
