package com.SwingTheVine.QSAND.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLarvae extends TileEntity {
	
	public double phase;
    
	// Constructor
    public TileEntityLarvae() {
        this.phase = 0.0;
    }
    
    public void writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
    }
    
    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
    }
    
    public boolean canUpdate() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = TileEntityLarvae.INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.fromBounds((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1));
        return bb;
    }
}
