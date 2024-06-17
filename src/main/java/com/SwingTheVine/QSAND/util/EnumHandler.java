package com.SwingTheVine.QSAND.util;

import net.minecraft.util.IStringSerializable;

/** I don't remember what this does.
 * 
 * @since <b>0.3.0</b>
 * @author <b>Unknown</b>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class EnumHandler {
	
	public enum test_blockTypes implements IStringSerializable {
		
		ZERO(0, "0"), ONE(1, "1"), TWO(2, "2"), THREE(3, "3"), FOUR(4, "4");
		
		private final int ID;
		private final String name;
		
		// Constructor
		private test_blockTypes(final int ID, final String name) {
			this.ID = ID;
			this.name = name;
		}
		
		@Override
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
