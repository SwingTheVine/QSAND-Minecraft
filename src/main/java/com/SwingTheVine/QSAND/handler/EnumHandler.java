package com.SwingTheVine.QSAND.handler;

import net.minecraft.util.IStringSerializable;

public class EnumHandler {

	public enum test_blockTypes implements IStringSerializable {
		
		ZERO(0, "0"),
		ONE(1, "1"),
		TWO(2, "2"),
		THREE(3, "3"),
		FOUR(4, "4");
		
		private int ID;
		private String name;
		
		// Constructor
		private test_blockTypes(int ID, String name) {
			this.ID = ID;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public int getID() {
			return ID;
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
}