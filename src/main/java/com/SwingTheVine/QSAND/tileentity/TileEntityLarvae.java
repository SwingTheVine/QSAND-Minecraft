package com.SwingTheVine.QSAND.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Adds the tile entity for the Larvae block
 * 
 * @since <b>0.45.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class TileEntityLarvae extends TileEntity {
	
	public double phase;
	
	// Constructor
	public TileEntityLarvae() {
		this.phase = 0.0;
	}
	
	@Override
	public void writeToNBT(final NBTTagCompound compound) {
		
		super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound compound) {
		
		super.readFromNBT(compound);
	}
	
	public boolean canUpdate() {
		
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		
		AxisAlignedBB bb = TileEntityLarvae.INFINITE_EXTENT_AABB;
		bb = AxisAlignedBB.fromBounds(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1,
			this.pos.getY() + 1, this.pos.getZ() + 1);
		return bb;
	}
}
