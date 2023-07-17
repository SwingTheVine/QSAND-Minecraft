package com.SwingTheVine.QSAND;

import com.SwingTheVine.QSAND.init.QSAND_Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class QSAND_Tab extends CreativeTabs{

	public QSAND_Tab(String label) {
		super(label);
	}

	@Override
	public Item getTabIconItem() {
		return QSAND_Items.test_item;
	}

}
