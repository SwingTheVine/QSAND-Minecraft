package com.SwingTheVine.QSAND.client.entity;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.entity.Bubble;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BubbleRender extends Render<Entity> {

	public static Factory factory = new Factory(); // Construct a new factory
    private static final ResourceLocation entityTexture = new ResourceLocation(ModInfo.id, "textures/blocks/Larvae.png"); // The texture the entity will use
    
    // Constructor
    public <T extends Entity> BubbleRender(RenderManager renderManager) {
    	super(renderManager); // Shadows renderManager constructor
    }
    
    public void doRenderBubble(final Bubble bubble, final double par2, final double par4, final double par6, final float par8, final float par9) {
        
    	// If the bubble has been spawned in for longer than the time it should be spawned in...
    	if (Sys.getTime() - bubble.entitySpawnTime < 0L) {
            return; // Don't render the bubble
        }
    	
        final Tessellator tessellator = Tessellator.getInstance(); // Drawing engine I think
        
        //final IIcon Icon = bubble.block.getIcon(1, bubble.entityMetadata);
        // Creates a texture atlas of the texture the bubble will use
        final TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(entityTexture.toString());
        
        this.bindTexture(TextureMap.locationBlocksTexture); // Binds the texture to the model/entity
        
        // Generates a OpenGL 11 matrix
        GL11.glPushMatrix();
        GL11.glEnable(3008);
        GL11.glEnable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        
        // X, Y, Z coordinates of the bubble
        final BlockPos bubblePos = new BlockPos((int)Math.floor(bubble.posX), (int)Math.floor(bubble.posY), (int)Math.floor(bubble.posZ));
        
        // What color the bubble is
        final int intColor = bubble.block.colorMultiplier((IBlockAccess)bubble.worldObj, bubblePos);
        final int red = (0xFF0000 & intColor) / 65536;
        final int green = (0xFF00 & intColor) / 256;
        final int blue = 0xFF & intColor;
        GL11.glColor4f(0.75f * (red / 255.0f), 0.75f * (green / 255.0f), 0.75f * (blue / 255.0f), 0.9f);
        GL11.glEnable(32826);
        
        
        final float tc = Math.min(1.0f, Math.max(0.0f, (Sys.getTime() - bubble.entitySpawnTime) / (float)bubble.entityLiveTime));
        final float tc2 = 1.0f - (float)Math.pow(2.0, -tc * 4.0f);
        final float scx = 0.03125f * bubble.size * tc2;
        final float scy = 0.025f * bubble.size * tc2;
        GL11.glTranslatef((float)par2, (float)par4 - 10.0f * scy + 5.0f * scy * tc2, (float)par6);
        GL11.glRotatef(bubble.randomRotation / 3.1415927f * 180.0f, 0.0f, 1.0f, 0.0f);
        this.renderModel(tessellator, 0.0, 0.0, 0.0, texture, scx, scy);
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    // Obtains the entity's texture
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return this.entityTexture; // Returns the entity's texture
    }
    
    public void doRender(final Entity entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.doRenderBubble((Bubble)entity, par2, par4, par6, par8, par9);
    }
    
    public void renderModel(final Tessellator tessellator, final double x, final double y, final double z, final TextureAtlasSprite texture, final float scx, final float scy) {
        GL11.glScalef(scx, scy, scx);
        this.renderCube(tessellator, -4.0, 1.0, -4.0, 8.0, 8.0, 8.0, texture);
        this.renderCube(tessellator, -3.0, 2.0, -5.0, 6.0, 6.0, 10.0, texture);
        this.renderCube(tessellator, -5.0, 2.0, -3.0, 10.0, 6.0, 6.0, texture);
        this.renderCube(tessellator, -3.0, 0.0, -3.0, 6.0, 10.0, 6.0, texture);
    }
    
    public void renderCube(final Tessellator tessellator, final double x, final double y, final double z, final double xs, final double ys, final double zs, final TextureAtlasSprite texture) {
        final double minU = texture.getMinU(); // Gets the minimum "U"
        final double minV = texture.getMinV(); // Gets the minimum "V"
        final double maxU = minU + (texture.getMaxU() - minU) / 2.0; // Gets the maximum "U"
        final double maxV = minV + (texture.getMaxV() - minV) / 2.0; // Gets the maximum "V"
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer(); // Obtains the world renderer
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
        //tessellator.setNormal(0.0f, 1.0f, 0.0f);
        worldRenderer.pos(x, y + ys, z).tex(maxU, minV).endVertex();
        worldRenderer.pos(x, y + ys, z + zs).tex(maxU, maxV).endVertex();
        worldRenderer.pos(x + xs, y + ys, z + zs).tex(minU, maxV).endVertex();
        worldRenderer.pos(x + xs, y + ys, z).tex(minU, minV).endVertex();
        tessellator.draw(); // Draw the quadrilateral
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
        //tessellator.setNormal(0.0f, -1.0f, 0.0f);
        worldRenderer.pos(x + xs, y, z).tex(maxU, minV).endVertex();
        worldRenderer.pos(x + xs, y, z + zs).tex(maxU, maxV).endVertex();
        worldRenderer.pos(x, y, z + zs).tex(minU, maxV).endVertex();
        worldRenderer.pos(x, y, z).tex(minU, minV).endVertex();
        tessellator.draw(); // Draw the quadrilateral
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
        //tessellator.setNormal(0.0f, 0.0f, -1.0f);
        worldRenderer.pos(x, y, z).tex(maxU, minV).endVertex();
        worldRenderer.pos(x, y + ys, z).tex(maxU, maxV).endVertex();
        worldRenderer.pos(x + xs, y + ys, z).tex(minU, maxV).endVertex();
        worldRenderer.pos(x + xs, y, z).tex(minU, minV).endVertex();
        tessellator.draw(); // Draw the quadrilateral
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
        //tessellator.setNormal(0.0f, 0.0f, 1.0f);
        worldRenderer.pos(x + xs, y, z + zs).tex(maxU, minV).endVertex();
        worldRenderer.pos(x + xs, y + ys, z + zs).tex(maxU, maxV).endVertex();
        worldRenderer.pos(x, y + ys, z + zs).tex(minU, maxV).endVertex();
        worldRenderer.pos(x, y, z + zs).tex(minU, minV).endVertex();
        tessellator.draw(); // Draw the quadrilateral
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
        //tessellator.setNormal(-1.0f, 0.0f, 0.0f);
        worldRenderer.pos(x, y, z + zs).tex(maxU, minV).endVertex();
        worldRenderer.pos(x, y + ys, z + zs).tex(maxU, maxV).endVertex();
        worldRenderer.pos(x, y + ys, z).tex(minU, maxV).endVertex();
        worldRenderer.pos(x, y, z).tex(minU, minV).endVertex();
        tessellator.draw(); // Draw the quadrilateral
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
        //tessellator.setNormal(1.0f, 0.0f, 0.0f);
        worldRenderer.pos(x + xs, y, z).tex(minU, minV).endVertex();
        worldRenderer.pos(x + xs, y + ys, z).tex(minU, minV).endVertex();
        worldRenderer.pos(x + xs, y + ys, z + zs).tex(maxU, maxV).endVertex();
        worldRenderer.pos(x + xs, y, z + zs).tex(maxU, minV).endVertex();
        tessellator.draw(); // Draw the quadrilateral
    }
    
    // The render factory to use
 	public static class Factory implements IRenderFactory<Entity> {

 		// What manager is the factory creating the render for
 		@Override
 		public Render<? super Entity> createRenderFor(RenderManager manager) {
 			return new BubbleRender(manager);
 		}
 		
 	}
}
