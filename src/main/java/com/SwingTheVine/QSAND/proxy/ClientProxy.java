package com.SwingTheVine.QSAND.proxy;

import com.SwingTheVine.QSAND.init.QSAND_Items;

public class ClientProxy extends CommonProxy{
	@Override
	public void registerRenders() {
		QSAND_Items.registerRenders();
	}
}
