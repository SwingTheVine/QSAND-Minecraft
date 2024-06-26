package com.SwingTheVine.QSAND.init;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.block.IMetaBlockName;
import com.SwingTheVine.QSAND.block.ItemBlockMeta;
import com.SwingTheVine.QSAND.block.SinkingBlockFluidClassic;
import com.SwingTheVine.QSAND.fluid.FluidBog;
import com.SwingTheVine.QSAND.fluid.FluidTest;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/** Registers the models for this mod
 * <p>
 * Big thanks to Choonster for having the only open source reference for custom fluids in 1.8.9!
 * The reference has comments to which is a first for 1.8.9 Minecraft mods <3
 * 
 * @since <b>0.51.0</b>
 * @author SwingTheVine - Additional comments
 * @author Choonster - 1.8.9 source code written here: <a href=
 * "https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.8.9/src/main/java/com/choonster/testmod3/init/ModFluids.java">
 * https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.8.9/src/main/java/com/choonster/testmod3/init/ModFluids.java</a>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class QSAND_Fluids {
	
	public static Fluid test_fluid;
	public static Fluid bog;
	
	/** The fluids registered by this mod. Includes fluids that were already registered by another mod. */
	public static final Set<Fluid> modFluids = new HashSet<>();
	
	/** The fluid blocks from this mod only. Doesn't include blocks for fluids that were already registered by another mod. */
	public static final Set<IFluidBlock> modFluidBlocks = new HashSet<>();
	
	public static void registerFluids() {
		
		// Registers the test fluid
		test_fluid = createFluid("test_fluid", true,
			fluid -> fluid.setDensity(2500).setViscosity(8500).setTemperature(288),
			fluid -> new FluidTest(fluid, new MaterialLiquid(MapColor.grassColor)));
			
		// Registers the bog fluid
		bog = createFluid("bog", true, fluid -> fluid.setDensity(2500).setViscosity(8500).setTemperature(288),
			fluid -> (SinkingBlockFluidClassic) new FluidBog(fluid, new MaterialLiquid(MapColor.grassColor)));
			
		registerBucket(test_fluid); // Registers the bucket for the test fluid
		registerBucket(bog); // Registers the bucket for the bog fluid
	}
	
	/** Create a {@link Fluid} and its {@link IFluidBlock}, or use the existing ones if a fluid has already been registered with
	 * the same name.
	 *
	 * @param name The name of the fluid
	 * @param hasFlowIcon Does the fluid have a flow icon?
	 * @param fluidPropertyApplier A function that sets the properties of the {@link Fluid}
	 * @param blockFactory A function that creates the {@link IFluidBlock}
	 * @return The fluid and block */
	private static <T extends Block & IFluidBlock & IMetaBlockName> Fluid createFluid(final String name,
		final boolean hasFlowIcon, final Consumer<Fluid> fluidPropertyApplier, final Function<Fluid, T> blockFactory) {
		
		final String texturePrefix = ModInfo.id + ":blocks/fluids/";
		
		final ResourceLocation still = new ResourceLocation(texturePrefix + name + "_still");
		final ResourceLocation flowing = hasFlowIcon ? new ResourceLocation(texturePrefix + name + "_flowing") : still;
		
		Fluid fluid = new Fluid(name, still, flowing);
		final boolean useOwnFluid = FluidRegistry.registerFluid(fluid);
		
		if (useOwnFluid) {
			fluidPropertyApplier.accept(fluid);
			registerFluidBlock(blockFactory.apply(fluid));
		} else {
			fluid = FluidRegistry.getFluid(name);
		}
		
		modFluids.add(fluid);
		
		final ItemStack bucket = new ItemStack(UniversalBucket.getByNameOrId("forge:bucketFilled"), 1,
			OreDictionary.WILDCARD_VALUE); // Creates a new universal bucket instance
		final NBTTagCompound nbtTag = new NBTTagCompound(); // Creates a new NBT Tag
		nbtTag.setString("FluidName", fluid.getName()); // Adds an attribute holding the name of the fluid
		nbtTag.setInteger("Amount", 1); // Adds an attribute holding the quantity of fluid in the bucket
		bucket.setTagCompound(nbtTag); // Adds the NBT tag to the universal bucket instance to fill it with the fluid
		OreDictionary.registerOre("quicksandBucket_", bucket); // Registers the filled bucket to the ore dictionary for creative
																// tab sorting
		
		return fluid;
	}
	
	private static <T extends Block & IFluidBlock & IMetaBlockName> T registerFluidBlock(final T block) {
		
		block.setRegistryName(block.getFluid().getName());
		block.setUnlocalizedName(block.getFluid().getName());
		block.setCreativeTab(QSAND.QSANDTab);
		GameRegistry.registerBlock(block, ItemBlockMeta.class);
		
		OreDictionary.registerOre("quicksandFluid_" + block.getUnlocalizedName().substring(5),
			new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE)); // Registers the fluid block to the ore dictionary for
																	// creative tab sorting
			
		modFluidBlocks.add(block);
		
		return block;
	}
	
	private static void registerBucket(final Fluid fluid) {
		
		FluidRegistry.addBucketForFluid(fluid);
	}
}
