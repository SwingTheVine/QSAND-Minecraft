package com.SwingTheVine.QSAND.items;

import java.util.List;

import com.SwingTheVine.QSAND.QSAND;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSpawnEgg extends ItemMonsterPlacer {

	public ItemSpawnEgg() {
        this.setHasSubtypes(true);
        this.setCreativeTab(QSAND.QSANDTab);
        this.setMaxStackSize(64);
    }
	
	/**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
	@Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		
        for (String name : net.minecraftforge.fml.common.registry.EntityRegistry.getEggs().keySet()) {
        	
        	ItemStack stack = new ItemStack(itemIn);
            net.minecraft.nbt.NBTTagCompound nbt = new net.minecraft.nbt.NBTTagCompound();
            nbt.setString("entity_name", name);
            stack.setTagCompound(nbt);
            subItems.add(stack);
        }
    }
}
