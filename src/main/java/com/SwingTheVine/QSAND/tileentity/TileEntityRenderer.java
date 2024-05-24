package com.SwingTheVine.QSAND.tileentity;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.block.BlockLarvae;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

public class TileEntityRenderer extends TileEntitySpecialRenderer {
	
	private static final ResourceLocation entityTexture = new ResourceLocation(ModInfo.id, "blocks/larvae_0"); // The texture the entity will use
	
	public void renderLarvae(final TileEntityLarvae par1TileEntityLarvae, final double par2, final double par4, final double par6, final float par8) {
        final double x = 0.0;
        final double y = 0.0;
        final double z = 0.0;
        boolean x_inc = true;
        boolean x_dec = true;
        boolean y_inc = true;
        boolean y_dec = true;
        boolean z_inc = true;
        boolean z_dec = true;
        x_inc = ((BlockLarvae)QSAND_Blocks.larvae).shouldSideBeRendered2((IBlockAccess)par1TileEntityLarvae.getWorld(), par1TileEntityLarvae.getPos().getX() + 1, par1TileEntityLarvae.getPos().getY(), par1TileEntityLarvae.getPos().getZ(), 0);
        x_dec = ((BlockLarvae)QSAND_Blocks.larvae).shouldSideBeRendered2((IBlockAccess)par1TileEntityLarvae.getWorld(), par1TileEntityLarvae.getPos().getX() - 1, par1TileEntityLarvae.getPos().getY(), par1TileEntityLarvae.getPos().getZ(), 0);
        y_inc = ((BlockLarvae)QSAND_Blocks.larvae).shouldSideBeRendered2((IBlockAccess)par1TileEntityLarvae.getWorld(), par1TileEntityLarvae.getPos().getX(), par1TileEntityLarvae.getPos().getY(), par1TileEntityLarvae.getPos().getZ(), 1);
        y_dec = ((BlockLarvae)QSAND_Blocks.larvae).shouldSideBeRendered2((IBlockAccess)par1TileEntityLarvae.getWorld(), par1TileEntityLarvae.getPos().getX(), par1TileEntityLarvae.getPos().getY(), par1TileEntityLarvae.getPos().getZ(), 2);
        z_inc = ((BlockLarvae)QSAND_Blocks.larvae).shouldSideBeRendered2((IBlockAccess)par1TileEntityLarvae.getWorld(), par1TileEntityLarvae.getPos().getX(), par1TileEntityLarvae.getPos().getY(), par1TileEntityLarvae.getPos().getZ() + 1, 0);
        z_dec = ((BlockLarvae)QSAND_Blocks.larvae).shouldSideBeRendered2((IBlockAccess)par1TileEntityLarvae.getWorld(), par1TileEntityLarvae.getPos().getX(), par1TileEntityLarvae.getPos().getY(), par1TileEntityLarvae.getPos().getZ() - 1, 0);
        if (!x_inc && !x_dec && !y_inc && !y_dec && !z_inc && !z_dec) {
            return;
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final int lightValue = QSAND_Blocks.larvae.getMixedBrightnessForBlock(par1TileEntityLarvae.getWorld(), par1TileEntityLarvae.getPos());
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer(); // Obtains the world renderer
        worldRenderer.putBrightness4(lightValue, lightValue, lightValue, lightValue);
        worldRenderer.color(1.0f, 1.0f, 1.0f, 1.0f);
        final TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(entityTexture.toString());
        this.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        final double minU = texture.getMinU();
        final double minV = texture.getMinV();
        final double maxU = texture.getMaxU();
        final double maxV = texture.getMaxV();
        double height = 0.75;
        if (!y_inc) {
            height = 1.0;
        }
        else {
            final double timd = Sys.getTime() / 175.0 + par1TileEntityLarvae.phase;
            final float timf = (float)(timd - Math.floor(timd / 6.28318) * 6.28318);
            height = 0.8 + MathHelper.sin(timf) * 0.025;
        }
        if (height != 1.0) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
            //tessellator.setNormal(0.0f, 1.0f, 0.0f);
            worldRenderer.pos(x, y + height, z).tex(maxU, minV).endVertex();
            worldRenderer.pos(x, y + height, z + 1.0).tex(maxU, maxV).endVertex();
            worldRenderer.pos(x + 1.0, y + height, z + 1.0).tex(minU, maxV).endVertex();
            worldRenderer.pos(x + 1.0, y + height, z).tex(minU, minV).endVertex();
            tessellator.getInstance().draw(); // Draw the quadrilateral
        }
        if (y_dec) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
            //tessellator.setNormal(0.0f, -1.0f, 0.0f);
            worldRenderer.pos(x + 1.0, y, z).tex(maxU, minV).endVertex();
            worldRenderer.pos(x + 1.0, y, z + 1.0).tex(maxU, maxV).endVertex();
            worldRenderer.pos(x, y, z + 1.0).tex(minU, maxV).endVertex();
            worldRenderer.pos(x, y, z).tex(minU, minV).endVertex();
            tessellator.getInstance().draw(); // Draw the quadrilateral
        }
        if (z_dec) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
            //tessellator.setNormal(0.0f, 0.0f, -1.0f);
            worldRenderer.pos(x, y, z).tex(maxU, minV).endVertex();
            worldRenderer.pos(x, y + height, z).tex(maxU, maxV).endVertex();
            worldRenderer.pos(x + 1.0, y + height, z).tex(minU, maxV).endVertex();
            worldRenderer.pos(x + 1.0, y, z).tex(minU, minV).endVertex();
            tessellator.getInstance().draw(); // Draw the quadrilateral
        }
        if (z_inc) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
            //tessellator.setNormal(0.0f, 0.0f, 1.0f);
            worldRenderer.pos(x + 1.0, y, z + 1.0).tex(maxU, minV).endVertex();
            worldRenderer.pos(x + 1.0, y + height, z + 1.0).tex(maxU, maxV).endVertex();
            worldRenderer.pos(x, y + height, z + 1.0).tex(minU, maxV).endVertex();
            worldRenderer.pos(x, y, z + 1.0).tex(minU, minV).endVertex();
            tessellator.getInstance().draw(); // Draw the quadrilateral
        }
        if (x_dec) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
            //tessellator.setNormal(-1.0f, 0.0f, 0.0f);
            worldRenderer.pos(x, y, z + 1.0).tex(maxU, minV).endVertex();
            worldRenderer.pos(x, y + height, z + 1.0).tex(maxU, maxV).endVertex();
            worldRenderer.pos(x, y + height, z).tex(minU, maxV).endVertex();
            worldRenderer.pos(x, y, z).tex(minU, minV).endVertex();
            tessellator.getInstance().draw(); // Draw the quadrilateral
        }
        if (x_inc) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); // Start drawing a quadrilateral in the OpenGL engine
            //tessellator.setNormal(1.0f, 0.0f, 0.0f);
            worldRenderer.pos(x + 1.0, y, z).tex(minU, minV).endVertex();
            worldRenderer.pos(x + 1.0, y + height, z).tex(minU, maxV).endVertex();
            worldRenderer.pos(x + 1.0, y + height, z).tex(maxU, maxV).endVertex();
            worldRenderer.pos(x + 1.0, y, z + 1.0).tex(maxU, minV).endVertex();
            tessellator.getInstance().draw(); // Draw the quadrilateral
        }
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity par1TileEntity, final double par2, final double par4, final double par6, final float par8, int destroyStage) {
        this.renderLarvae((TileEntityLarvae)par1TileEntity, par2, par4, par6, par8);
    }
}
