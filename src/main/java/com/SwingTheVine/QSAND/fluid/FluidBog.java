package com.SwingTheVine.QSAND.fluid;

import com.SwingTheVine.QSAND.ModInfo;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidBog extends Fluid {
	
	public static final ResourceLocation bogStill = new ResourceLocation(ModInfo.id, "textures/blocks/bog_0");
	public static final ResourceLocation bogFlowing = new ResourceLocation(ModInfo.id, "textures/blocks/bog_flowing");
	
	public FluidBog() {
		super("Bog", bogStill, bogFlowing);
		this.setDensity(2500);
		this.setViscosity(8500);
		this.setTemperature(288);
	}
}
