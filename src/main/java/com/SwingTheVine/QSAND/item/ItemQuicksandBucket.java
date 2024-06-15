package com.SwingTheVine.QSAND.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemQuicksandBucket extends UniversalBucket {
	
	public final int capacity; // how much the bucket holds
    public final ItemStack empty; // empty item to return and recognize when filling
    public final boolean nbtSensitive;
	
	// Constructor
	public ItemQuicksandBucket() {
		super();
	}
	
	/** Constructor
     * @param capacity - The capacity of the container
     * @param empty - The item to fill the bucket with. The item to place when emptying the bucket. The item is usually the (tile) item form of a fluid
     * @param nbtSensitive - Whether the empty item is NBT sensitive (usually true if empty and full are the same items)
     * @See {@link UniversalBucket#UniversalBucket(capacity, empty, nbtSensitive) Super Implementation}
     */
    public ItemQuicksandBucket(int capacity, ItemStack empty, boolean nbtSensitive)
    {
        this.capacity = capacity;
        this.empty = empty;
        this.nbtSensitive = nbtSensitive;

        this.setMaxStackSize(1);

        this.setCreativeTab(CreativeTabs.tabMisc);
    }
    
    /**
     * BEACON
     * Returns a list of items with the same ID, but different meta (e.g. dye returns 16 items)
     * @param defaultBucket - The item to generate subtypes from
     * @param tab - The tab the item belongs to
     * @param subItems - A list of subtypes of this item that will be registered
     * @See {@link UniversalBucket#getSubItems Super Implementation}
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item defaultBucket, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
        {
            // add all fluids that the bucket can be filled  with
            FluidStack fs = new FluidStack(fluid, capacity);
            ItemStack stack = new ItemStack(this);
            if (fill(stack, fs, true) == fs.amount)
            {
            	
                subItems.add(stack);
            }
        }
    }
}
