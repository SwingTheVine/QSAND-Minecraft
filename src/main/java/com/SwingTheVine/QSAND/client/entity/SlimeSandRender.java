package com.SwingTheVine.QSAND.client.entity;

import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.entity.SlimeSand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class SlimeSandRender extends RenderLiving {

	public static Factory factory = new Factory(); // Construct a new factory
    private static final ResourceLocation entityTexture = new ResourceLocation(ModInfo.id, SlimeSand.textureLocation); // The texture the entity will use
    private static ModelBase scaleAmount;
    private static float shadowOpaque;
    
    public SlimeSandRender(final RenderManager p_i1267_1_, final ModelBase p_i1267_2_, final float p_i1267_3_) {
        super(p_i1267_1_, p_i1267_2_, p_i1267_3_);
        this.scaleAmount = p_i1267_2_;
        this.shadowOpaque = p_i1267_3_;
    }
    
    protected int shouldRenderPass(final SlimeSand p_77032_1_, final int p_77032_2_, final float p_77032_3_) {
        if (p_77032_1_.isInvisible()) {
            return 0;
        }
        if (p_77032_2_ == 0) {
            final float var4 = p_77032_1_.ticksExisted + p_77032_3_;
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            final float uh = var4 * 0.0025f + MathHelper.cos(var4 * 0.02f) * 0.005f;
            final float vv = -var4 * 0.005f;
            GL11.glTranslatef(uh, vv, 0.0f);
            //this.setRenderPassModel(this.scaleAmount);
            GL11.glMatrixMode(5888);
            GL11.glEnable(2977);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            return 1;
        }
        if (p_77032_2_ == 1) {
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (p_77032_2_ == 2) {
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
        }
        return -1;
    }
    
    protected void preRenderCallback(final SlimeSand p_77041_1_, final float p_77041_2_) {
        final float var3 = (float)p_77041_1_.getSlimeSize();
        final float var4 = (p_77041_1_.squishFactorPrev + (p_77041_1_.squishFactor - p_77041_1_.squishFactorPrev) * p_77041_2_) / (var3 * 0.5f + 1.0f);
        final float var5 = 1.0f / (var4 + 1.0f);
        final float scaleFactor = 1.1f;
        GL11.glScalef(var5 * var3 * scaleFactor, 0.75f / var5 * var3 * scaleFactor, var5 * var3 * scaleFactor);
    }
    
    protected ResourceLocation getEntityTexture() {
        return entityTexture;
    }
    
    protected void preRenderCallback(final EntityLivingBase p_77041_1_, final float p_77041_2_) {
        this.preRenderCallback((SlimeSand)p_77041_1_, p_77041_2_);
    }
    
    protected int shouldRenderPass(final EntityLivingBase p_77032_1_, final int p_77032_2_, final float p_77032_3_) {
        return this.shouldRenderPass((SlimeSand)p_77032_1_, p_77032_2_, p_77032_3_);
    }
    
    protected ResourceLocation getEntityTexture(final Entity p_110775_1_) {
        return this.getEntityTexture((SlimeSand)p_110775_1_);
    }
    
    // The render factory to use
  	public static class Factory implements IRenderFactory<SlimeSand> {

  		// What manager is the factory creating the render for
  		@Override
  		public Render<? super SlimeSand> createRenderFor(RenderManager manager) {
  			return new SlimeSandRender(Minecraft.getMinecraft().getRenderManager(), scaleAmount, shadowOpaque);
  		}
  	}
}
