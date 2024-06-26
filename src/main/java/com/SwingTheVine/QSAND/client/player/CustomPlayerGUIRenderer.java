package com.SwingTheVine.QSAND.client.player;

import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.util.ConfigHandler;
import com.SwingTheVine.QSAND.util.QuicksandManager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders HUD elements such as obstructing the player's view when inside a block.
 * 
 * @since <b>0.39.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class CustomPlayerGUIRenderer {
	
	// TODO: Find a way to move this to QSAND_Blocks. It fails to work in there for some reason >:(
	public static Block[] blockList = {QSAND_Blocks.test_block, QSAND_Blocks.mud,
		QSAND_Blocks.quicksand/*
								 * ,
								 * QSAND_Blocks.quicksandSoft,
								 * QSAND_Blocks.quicksandJungle,
								 * QSAND_Blocks.moss,
								 * QSAND_Blocks.mire,
								 * QSAND_Blocks.mireLiquid,
								 * QSAND_Blocks.mireLiquidStable,
								 * QSAND_Blocks.meat,
								 * QSAND_Blocks.meatHole,
								 * QSAND_Blocks.voidHole,
								 * QSAND_Blocks.swallowingFlesh,
								 * QSAND_Blocks.larvae
								 */};
		
	/*
	 * public static Block[] sinkableBlockList = {
	 * QSAND_Blocks.bog,
	 * QSAND_Blocks.snowSoft,
	 * QSAND_Blocks.quicksandDry,
	 * QSAND_Blocks.morass,
	 * QSAND_Blocks.quicksand,
	 * QSAND_Blocks.quicksandJungle,
	 * QSAND_Blocks.slimeSinking,
	 * QSAND_Blocks.mire,
	 * QSAND_Blocks.mireLiquid,
	 * QSAND_Blocks.mireLiquidStable,
	 * QSAND_Blocks.moor,
	 * QSAND_Blocks.claySinking,
	 * QSAND_Blocks.tar,
	 * QSAND_Blocks.quicksandCorrupted,
	 * QSAND_Blocks.woolSinking,
	 * QSAND_Blocks.quicksandSoft,
	 * QSAND_Blocks.webDense,
	 * QSAND_Blocks.swallowingFlesh,
	 * QSAND_Blocks.mucus,
	 * QSAND_Blocks.moss,
	 * QSAND_Blocks.clayBrown,
	 * QSAND_Blocks.peatWet,
	 * QSAND_Blocks.wax,
	 * QSAND_Blocks.larvae,
	 * QSAND_Blocks.chocolateLiquid,
	 * QSAND_Blocks.slurry,
	 * QSAND_Blocks.mud,
	 * QSAND_Blocks.gravelSoft,
	 * QSAND_Blocks.honey};
	 */
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderAirGUI(final RenderGameOverlayEvent.Pre event) {
		
		if (event.type == RenderGameOverlayEvent.ElementType.AIR && ConfigHandler.useCustomDrownPlayers) {
			final Minecraft mc = Minecraft.getMinecraft();
			final int gma = QuicksandManager.getCustomDrownAir(mc.thePlayer);
			if (gma < mc.thePlayer.getAir()) {
				mc.thePlayer.setAir(gma);
			}
			if (ConfigHandler.useCustomAirHUD) {
				event.setCanceled(true);
				if (mc.thePlayer.getAir() < 300) {
					// TODO: Possible error when replacing ScaledResolution
					final ScaledResolution resolution = new ScaledResolution(mc);
					final int height = resolution.getScaledHeight();
					final int width = resolution.getScaledWidth();
					final TextureManager getTextureManager = mc.getTextureManager();
					final GuiIngame ingameGUI = mc.ingameGUI;
					getTextureManager.bindTexture(GuiIngame.icons);
					GL11.glPushMatrix();
					GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.9f);
					final IAttributeInstance var10 = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
					int var11 = 0;
					final int var12 = width / 2 + 91;
					final int var13 = height - 39;
					final float var14 = (float) var10.getAttributeValue();
					final float var15 = mc.thePlayer.getAbsorptionAmount();
					final int var16 = MathHelper.ceiling_float_int((var14 + var15) / 2.0f / 10.0f);
					final int var17 = Math.max(10 - (var16 - 2), 3);
					final int var18 = var13 - (var16 - 1) * var17 - 10;
					final int var19 = mc.thePlayer.getAir();
					int var20;
					int var21;
					for (var20 = MathHelper.ceiling_double_int(
						(var19 - 2) * 10.0 / 300.0), var21 = MathHelper.ceiling_double_int(var19 * 10.0 / 300.0)
							- var20, var11 = 0; var11 < var20 + var21; ++var11) {
						if (var11 < var20) {
							mc.ingameGUI.drawTexturedModalRect(var12 - var11 * 8 - 9, var18, 16, 18, 9, 9);
						} else {
							mc.ingameGUI.drawTexturedModalRect(var12 - var11 * 8 - 9, var18, 25, 18, 9, 9);
						}
					}
					GL11.glPopMatrix();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void RenderOverlay(final RenderWorldLastEvent event) {
		
		for (int i = 0; i < blockList.length; i++) {
			// System.out.println(i + " = " + blockList[i]);
			if (QuicksandManager.isEntityInsideOfBlockS(Minecraft.getMinecraft().thePlayer, blockList[i])) {
				TextureAtlasSprite texture;
				
				if (i == 19) {
					final ModelResourceLocation model = new ModelResourceLocation(
						new ModelResourceLocation(
							ModInfo.id + ":" + CustomPlayerGUIRenderer.blockList[i].getUnlocalizedName().substring(5)),
						"sink=0");
					// texture = new ResourceLocation(model.getResourcePath());
					texture = Minecraft.getMinecraft().getTextureMapBlocks()
						.getAtlasSprite(new ResourceLocation(ModInfo.id, model.getResourcePath()).toString());
				} else {
					ModelResourceLocation model;
					try {
						model = new ModelResourceLocation(new ModelResourceLocation(
							ModInfo.id + ":" + CustomPlayerGUIRenderer.blockList[i].getUnlocalizedName().substring(5)),
							"normal");
						// texture = new ResourceLocation(model.getResourcePath());
					} catch (final Exception ignored) {
						// System.out.println("No model found");
						model = new ModelResourceLocation(new ModelResourceLocation(ModInfo.id + ":" + "mud"),
							"normal");
					}
					texture = Minecraft.getMinecraft().getTextureMapBlocks()
						.getAtlasSprite(new ResourceLocation(ModInfo.id, "textures/blocks/mud_0.png").toString());
					// texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(new ResourceLocation(ModInfo.id,
					// model.getResourcePath()).toString());
					// texture = CustomRenderOverlayEvent.quicksandIDS[i].getBlockTextureFromSide(2);
					// minecraft/client/renderer/BlockFluidRenderer
				}
				if (ConfigHandler.forceFirstPerson) {
					Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
				}
				GL11.glMatrixMode(5889);
				GL11.glLoadIdentity();
				final float var3 = 0.07f;
				GL11.glMatrixMode(5888);
				GL11.glLoadIdentity();
				
				// TODO: Possible poor replacement for isSleeping
				if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0
					&& !Minecraft.getMinecraft().thePlayer.isPlayerSleeping()) {
					this.renderInsideOfBlockR(0.0f, texture);
				}
				return;
			}
		}
	}
	
	public void renderInsideOfBlockR(final float p_78446_1_, final TextureAtlasSprite texture) {
		
		// System.out.println(new ResourceLocation(ModInfo.id, "textures/blocks/mud_0.png")); // TextureMap.locationBlocksTexture
		Minecraft.getMinecraft().getTextureManager()
			.bindTexture(new ResourceLocation(ModInfo.id, "textures/blocks/mud_0.png"));
		final Tessellator tessellator = Tessellator.getInstance();
		final float var4 = 0.1f;
		GL11.glColor4f(var4, var4, var4, 1.0f);
		GL11.glDepthFunc(515);
		GL11.glEnable(3008);
		GL11.glEnable(3553);
		GL11.glEnable(2977);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glPushMatrix();
		final float posX1 = -1.0f;
		final float posX2 = 1.0f;
		final float posY1 = -1.0f;
		final float posY2 = 1.0f;
		final float posZ = -0.5f;
		final float minU = texture.getMinU();
		final float maxU = texture.getMaxU();
		final float minV = texture.getMinV();
		final float maxV = texture.getMaxV();
		final WorldRenderer worldRenderer = tessellator.getWorldRenderer(); // Obtains the world renderer
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		worldRenderer.pos(posX1, posY1, posZ).tex(maxU, maxV).endVertex();
		worldRenderer.pos(posX2, posY1, posZ).tex(minU, maxV).endVertex();
		worldRenderer.pos(posX2, posY2, posZ).tex(minU, minV).endVertex();
		worldRenderer.pos(posX1, posY2, posZ).tex(maxU, minV).endVertex();
		tessellator.getInstance().draw(); // Draw the quadrilateral
		GL11.glPopMatrix();
		GL11.glDisable(2977);
		GL11.glDisable(3042);
		GL11.glDepthFunc(515);
		GL11.glEnable(3008);
		GL11.glEnable(3553);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
