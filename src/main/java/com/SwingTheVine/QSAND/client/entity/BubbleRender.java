package com.SwingTheVine.QSAND.client.entity;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.entity.Bubble;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BubbleRender extends Render
{
    private final ResourceLocation field_110637_a;
    
    public BubbleRender() {
        this.field_110637_a = new ResourceLocation("morefunquicksandmod", "textures/blocks/Larvae.png");
    }
    
    public void doRenderBubble(final Bubble bubble, final double par2, final double par4, final double par6, final float par8, final float par9) {
        if (Sys.getTime() - bubble.entitySpawnTime < 0L) {
            return;
        }
        final Tessellator tessellator = Tessellator.instance;
        final IIcon Icon = bubble.block.getIcon(1, bubble.meta);
        this.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        GL11.glEnable(3008);
        GL11.glEnable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        final int xx = (int)Math.floor(bubble.posX);
        final int zz = (int)Math.floor(bubble.posZ);
        final int yy = (int)Math.floor(bubble.posY);
        final int intColor = bubble.block.colorMultiplier((IBlockAccess)bubble.worldObj, xx, yy, zz);
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
        this.renderModel(tessellator, 0.0, 0.0, 0.0, Icon, scx, scy);
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.field_110637_a;
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.doRenderBubble((Bubble)par1Entity, par2, par4, par6, par8, par9);
    }
    
    public void renderModel(final Tessellator tessellator, final double x, final double y, final double z, final IIcon Icon, final float scx, final float scy) {
        GL11.glScalef(scx, scy, scx);
        this.renderCube(tessellator, -4.0, 1.0, -4.0, 8.0, 8.0, 8.0, Icon);
        this.renderCube(tessellator, -3.0, 2.0, -5.0, 6.0, 6.0, 10.0, Icon);
        this.renderCube(tessellator, -5.0, 2.0, -3.0, 10.0, 6.0, 6.0, Icon);
        this.renderCube(tessellator, -3.0, 0.0, -3.0, 6.0, 10.0, 6.0, Icon);
    }
    
    public void renderCube(final Tessellator tessellator, final double x, final double y, final double z, final double xs, final double ys, final double zs, final IIcon Icon) {
        final double minU = Icon.getMinU();
        final double minV = Icon.getMinV();
        final double maxU = minU + (Icon.getMaxU() - minU) / 2.0;
        final double maxV = minV + (Icon.getMaxV() - minV) / 2.0;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 1.0f, 0.0f);
        tessellator.addVertexWithUV(x, y + ys, z, maxU, minV);
        tessellator.addVertexWithUV(x, y + ys, z + zs, maxU, maxV);
        tessellator.addVertexWithUV(x + xs, y + ys, z + zs, minU, maxV);
        tessellator.addVertexWithUV(x + xs, y + ys, z, minU, minV);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, -1.0f, 0.0f);
        tessellator.addVertexWithUV(x + xs, y, z, maxU, minV);
        tessellator.addVertexWithUV(x + xs, y, z + zs, maxU, maxV);
        tessellator.addVertexWithUV(x, y, z + zs, minU, maxV);
        tessellator.addVertexWithUV(x, y, z, minU, minV);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 0.0f, -1.0f);
        tessellator.addVertexWithUV(x, y, z, maxU, minV);
        tessellator.addVertexWithUV(x, y + ys, z, maxU, maxV);
        tessellator.addVertexWithUV(x + xs, y + ys, z, minU, maxV);
        tessellator.addVertexWithUV(x + xs, y, z, minU, minV);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 0.0f, 1.0f);
        tessellator.addVertexWithUV(x + xs, y, z + zs, maxU, minV);
        tessellator.addVertexWithUV(x + xs, y + ys, z + zs, maxU, maxV);
        tessellator.addVertexWithUV(x, y + ys, z + zs, minU, maxV);
        tessellator.addVertexWithUV(x, y, z + zs, minU, minV);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0f, 0.0f, 0.0f);
        tessellator.addVertexWithUV(x, y, z + zs, maxU, minV);
        tessellator.addVertexWithUV(x, y + ys, z + zs, maxU, maxV);
        tessellator.addVertexWithUV(x, y + ys, z, minU, maxV);
        tessellator.addVertexWithUV(x, y, z, minU, minV);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0f, 0.0f, 0.0f);
        tessellator.addVertexWithUV(x + xs, y, z, minU, minV);
        tessellator.addVertexWithUV(x + xs, y + ys, z, minU, maxV);
        tessellator.addVertexWithUV(x + xs, y + ys, z + zs, maxU, maxV);
        tessellator.addVertexWithUV(x + xs, y, z + zs, maxU, minV);
        tessellator.draw();
    }
}
