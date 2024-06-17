package com.SwingTheVine.QSAND.client.entity;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.block.SinkingBlock;
import com.SwingTheVine.QSAND.entity.effect.EntityBubble;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders bubbles that appear in/on QSAND blocks
 * 
 * @since <b>0.26.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
@SideOnly(Side.CLIENT)
public class BubbleRender extends Render<EntityBubble> {
	
	// static BubbleModel model = new BubbleModel();
	private static ResourceLocation entityTexture = new ResourceLocation(ModInfo.id, "blocks/larvae_0"); // The texture the entity
																											// will use
	
	// Constructor
	public <T extends Entity> BubbleRender(final RenderManager renderManager) {
		super(renderManager);
	}
	
	public void doRenderBubble(final EntityBubble bubble, final double par2, final double par4, final double par6,
		final float par8, final float par9) {
		
		// If the bubble has been spawned in for longer than the time it should be spawned in...
		if (Sys.getTime() - bubble.entitySpawnTime < 0L) {
			return; // Don't render the bubble
		}
		
		// X, Y, Z coordinates of the bubble
		final BlockPos bubblePos = new BlockPos((int) Math.floor(bubble.posX), (int) Math.floor(bubble.posY),
			(int) Math.floor(bubble.posZ));
			
		if (bubble.worldObj.getBlockState(bubblePos.down()).getBlock() == Blocks.air
			|| bubble.worldObj.getBlockState(bubblePos.down()).getBlock() == Blocks.water
			|| bubble.worldObj.getBlockState(bubblePos.down()).getBlock() == Blocks.lava) {
			System.out.println("Fail: " + bubble.worldObj.getBlockState(bubblePos.down()).getBlock());
			return;
		}
		
		final Tessellator tessellator = Tessellator.getInstance(); // Drawing engine I think
		
		if (bubble.block != null) {
			if (bubble.block.getUseOneTexture()) {
				entityTexture = new ResourceLocation(ModInfo.id,
					"blocks/" + bubble.block.getUnlocalizedName().substring(5) + "_0");
			} else {
				entityTexture = new ResourceLocation(ModInfo.id,
					"blocks/" + bubble.block.getUnlocalizedName().substring(5) + "_"
						+ bubble.block.getMetaFromState(bubble.worldObj.getBlockState(bubblePos)));
			}
			System.out.println(entityTexture);
		} else {
			entityTexture = new ResourceLocation(ModInfo.id,
				bubble.worldObj.getBlockState(bubblePos.down()).getBlock().getUnlocalizedName().substring(5) + "_0");
		}
		
		
		// final IIcon Icon = bubble.block.getIcon(1, bubble.entityMetadata);
		// Creates a texture atlas of the texture the bubble will use
		final TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks()
			.getAtlasSprite(entityTexture.toString());
			
		this.bindTexture(TextureMap.locationBlocksTexture); // Binds the texture to the model/entity
		
		// Generates a OpenGL 11 matrix
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_ALPHA_TEST); // 3008
		GL11.glEnable(GL11.GL_TEXTURE_2D); // 3553
		GL11.glEnable(GL11.GL_BLEND); // 3042
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // 770, 771
		
		int intColor;
		
		try {
			try {
				System.out.println("Bubble: " + bubble);
				System.out.println("Bubble.blockpos: " + bubble.worldObj.getBlockState(bubblePos.down()));
				System.out.println("Bubble.block: " + bubble.block);
				// System.out.println("color: " + bubble.block.getQuicksandColorMultiplier(bubble.worldObj, bubblePos.down()));
			} catch (final Exception ignored) {
				System.out.println("The sys logs failed...");
			}
			
			try {
				if (bubble.block == null) {
					System.out.println("null");
					intColor = ((SinkingBlock) bubble.worldObj.getBlockState(bubblePos.down()).getBlock())
						.getQuicksandColorMultiplier(bubble.worldObj, bubblePos.down());
					System.out.println(intColor);
				} else {
					System.out.println("not null");
					intColor = bubble.block.getQuicksandColorMultiplier(bubble.worldObj, bubblePos.down());
					System.out.println(intColor);
				}
			} catch (final Exception ignored) {
				System.out.println("Fail 2");
				intColor = 16777215;
				System.out.println(intColor);
			}
		} catch (final Exception ignored) {
			System.out.println("There was an error obtaining the color multiplier");
			intColor = 16777215;
			System.out.println(intColor);
		}
		
		final int red = (0xFF0000 & intColor) / 65536;
		final int green = (0xFF00 & intColor) / 256;
		final int blue = 0xFF & intColor;
		GlStateManager.color(red, green, blue);
		GL11.glColor4f(red, green, blue, 0.9f);
		// GL11.glColor4f(0.75f * (red / 255.0f), 0.75f * (green / 255.0f), 0.75f * (blue / 255.0f), 0.9f);
		GL11.glEnable(32826);
		
		
		final float tc = Math.min(1.0f,
			Math.max(0.0f, (Sys.getTime() - bubble.entitySpawnTime) / (float) bubble.entityLiveTime));
		final float tc2 = 1.0f - (float) Math.pow(2.0, -tc * 4.0f);
		final float scx = 0.03125f * bubble.size * tc2;
		final float scy = 0.025f * bubble.size * tc2;
		GL11.glTranslatef((float) par2, (float) par4 - 10.0f * scy + 5.0f * scy * tc2, (float) par6);
		GL11.glRotatef(bubble.randomRotation / 3.1415927f * 180.0f, 0.0f, 1.0f, 0.0f);
		this.renderModel(tessellator, 0.0, 0.0, 0.0, texture, scx, scy);
		GL11.glDisable(32826);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_BLEND); // 3042
		GL11.glPopMatrix();
	}
	
	// Obtains the entity's texture
	@Override
	protected ResourceLocation getEntityTexture(final EntityBubble bubble) {
		
		return this.entityTexture; // Returns the entity's texture
	}
	
	@Override
	public void doRender(final EntityBubble bubble, final double par2, final double par4, final double par6,
		final float par8, final float par9) {
		
		this.doRenderBubble(bubble, par2, par4, par6, par8, par9);
	}
	
	public void renderModel(final Tessellator tessellator, final double x, final double y, final double z,
		final TextureAtlasSprite texture, final float scx, final float scy) {
		
		GL11.glScalef(scx, scy, scx);
		this.renderCube(tessellator, -4.0, 1.0, -4.0, 8.0, 8.0, 8.0, texture);
		this.renderCube(tessellator, -3.0, 2.0, -5.0, 6.0, 6.0, 10.0, texture);
		this.renderCube(tessellator, -5.0, 2.0, -3.0, 10.0, 6.0, 6.0, texture);
		this.renderCube(tessellator, -3.0, 0.0, -3.0, 6.0, 10.0, 6.0, texture);
	}
	
	public void renderCube(final Tessellator tessellator, final double x, final double y, final double z,
		final double xs, final double ys, final double zs, final TextureAtlasSprite texture) {
		
		final double minU = texture.getMinU(); // Gets the minimum "U"
		final double minV = texture.getMinV(); // Gets the minimum "V"
		final double maxU = minU + (texture.getMaxU() - minU) / 2.0; // Gets the maximum "U"
		final double maxV = minV + (texture.getMaxV() - minV) / 2.0; // Gets the maximum "V"
		final WorldRenderer worldRenderer = tessellator.getWorldRenderer(); // Obtains the world renderer
		
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL
																				// engine
		worldRenderer.pos(x, y + ys, z).tex(maxU, minV).endVertex();
		worldRenderer.pos(x, y + ys, z + zs).tex(maxU, maxV).endVertex();
		worldRenderer.pos(x + xs, y + ys, z + zs).tex(minU, maxV).endVertex();
		worldRenderer.pos(x + xs, y + ys, z).tex(minU, minV).endVertex();
		tessellator.getInstance().draw(); // Draw the quadrilateral
		
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL
																				// engine
		worldRenderer.pos(x + xs, y, z).tex(maxU, minV).endVertex();
		worldRenderer.pos(x + xs, y, z + zs).tex(maxU, maxV).endVertex();
		worldRenderer.pos(x, y, z + zs).tex(minU, maxV).endVertex();
		worldRenderer.pos(x, y, z).tex(minU, minV).endVertex();
		tessellator.getInstance().draw(); // Draw the quadrilateral
		
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL
																				// engine
		worldRenderer.pos(x, y, z).tex(maxU, minV).endVertex();
		worldRenderer.pos(x, y + ys, z).tex(maxU, maxV).endVertex();
		worldRenderer.pos(x + xs, y + ys, z).tex(minU, maxV).endVertex();
		worldRenderer.pos(x + xs, y, z).tex(minU, minV).endVertex();
		tessellator.getInstance().draw(); // Draw the quadrilateral
		
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL
																				// engine
		worldRenderer.pos(x + xs, y, z + zs).tex(maxU, minV).endVertex();
		worldRenderer.pos(x + xs, y + ys, z + zs).tex(maxU, maxV).endVertex();
		worldRenderer.pos(x, y + ys, z + zs).tex(minU, maxV).endVertex();
		worldRenderer.pos(x, y, z + zs).tex(minU, minV).endVertex();
		tessellator.getInstance().draw(); // Draw the quadrilateral
		
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL
																				// engine
		worldRenderer.pos(x, y, z + zs).tex(maxU, minV).endVertex();
		worldRenderer.pos(x, y + ys, z + zs).tex(maxU, maxV).endVertex();
		worldRenderer.pos(x, y + ys, z).tex(minU, maxV).endVertex();
		worldRenderer.pos(x, y, z).tex(minU, minV).endVertex();
		tessellator.getInstance().draw(); // Draw the quadrilateral
		
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL
																				// engine
		worldRenderer.pos(x + xs, y, z).tex(minU, minV).endVertex();
		worldRenderer.pos(x + xs, y + ys, z).tex(minU, minV).endVertex();
		worldRenderer.pos(x + xs, y + ys, z + zs).tex(maxU, maxV).endVertex();
		worldRenderer.pos(x + xs, y, z + zs).tex(maxU, minV).endVertex();
		tessellator.getInstance().draw(); // Draw the quadrilateral
	}
	
	// The render factory to use
	public static class Factory implements IRenderFactory {
		
		// What manager is the factory creating the render for
		@Override
		public BubbleRender createRenderFor(final RenderManager manager) {
			
			return new BubbleRender(manager);
		}
	}
}
