package com.SwingTheVine.QSAND.init;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.block.BlockBog;
import com.SwingTheVine.QSAND.block.SinkingBlockFluidClassic;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

// Big thanks to Choonster for having the only open source reference for custom fluids in 1.8.9!
// It has comments too <3
// Choonster: https://forums.minecraftforge.net/profile/70776-choonster/
// Source: https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.8.9/src/main/java/com/choonster/testmod3/init/ModFluids.java
public class QSAND_Fluids {
	public static Fluid test_fluid;
	public static Fluid bog;
	
	/**
	 * The fluids registered by this mod. Includes fluids that were already registered by another mod.
	 */
	public static final Set<Fluid> modFluids = new HashSet<>();
	
	/**
	 * The fluid blocks from this mod only. Doesn't include blocks for fluids that were already registered by another mod.
	 */
	public static final Set<IFluidBlock> modFluidBlocks = new HashSet<>();
	
	public static void registerFluids() {
		
		// Registers the test fluid
		test_fluid = createFluid("test_fluid", true, 
				fluid -> fluid.setDensity(2500).setViscosity(8500).setTemperature(288),
				fluid -> new BlockFluidClassic(fluid, new MaterialLiquid(MapColor.grassColor)));
		
		// Registers the bog fluid
		bog = createFluid("bog", true, 
				fluid -> fluid.setDensity(2500).setViscosity(8500).setTemperature(288),
				fluid -> (SinkingBlockFluidClassic)new BlockBog(fluid, new MaterialLiquid(MapColor.grassColor)));
		
		registerBucket(test_fluid); // Registers the bucket for the test fluid
		registerBucket(bog); // Registers the bucket for the bog fluid
	}
	
	/**
	 * Create a {@link Fluid} and its {@link IFluidBlock}, or use the existing ones if a fluid has already been registered with the same name.
	 *
	 * @param name                 The name of the fluid
	 * @param hasFlowIcon          Does the fluid have a flow icon?
	 * @param fluidPropertyApplier A function that sets the properties of the {@link Fluid}
	 * @param blockFactory         A function that creates the {@link IFluidBlock}
	 * @return The fluid and block
	 */
	private static <T extends Block & IFluidBlock> Fluid createFluid(String name, boolean hasFlowIcon, Consumer<Fluid> fluidPropertyApplier, Function<Fluid, T> blockFactory) {
		final String texturePrefix = ModInfo.id + ":blocks/";

		ResourceLocation still = new ResourceLocation(texturePrefix + name + "_still");
		ResourceLocation flowing = hasFlowIcon ? new ResourceLocation(texturePrefix + name + "_flowing") : still;

		Fluid fluid = new Fluid(name, still, flowing);
		boolean useOwnFluid = FluidRegistry.registerFluid(fluid);

		if (useOwnFluid) {
			fluidPropertyApplier.accept(fluid);
			registerFluidBlock(blockFactory.apply(fluid));
		} else {
			fluid = FluidRegistry.getFluid(name);
		}

		modFluids.add(fluid);

		return fluid;
	}
	
	private static <T extends Block & IFluidBlock> T registerFluidBlock(T block) {
		block.setRegistryName(block.getFluid().getName());
		block.setUnlocalizedName(block.getFluid().getName());
		block.setCreativeTab(QSAND.QSANDTab);
		GameRegistry.registerBlock(block);

		modFluidBlocks.add(block);

		return block;
	}
	
	private static void registerBucket(Fluid fluid) {
		FluidRegistry.addBucketForFluid(fluid);
	}
}
