package com.SwingTheVine.QSAND.client.entity;

import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeSand;

import assets.qsand.models.entity.ModelSlimeSand;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SlimeSandRender extends RenderLiving<EntitySlimeSand> {

    private static final ResourceLocation entityTexture = new ResourceLocation(ModInfo.id, EntitySlimeSand.textureLocation); // The texture the entity will use
    private static ModelBase model = new ModelSlimeSand(0);
    private static float shadowSize = 0.5F;
    
    public SlimeSandRender(final RenderManager renderManager, final ModelBase modelBase, final float shadowSize) {
        super(renderManager, modelBase, shadowSize);
    }
    
    @Override
    public void doRender(EntitySlimeSand entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.shadowSize = 0.25F * (float)entity.getSlimeSize();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    protected int shouldRenderPass(final EntitySlimeSand slime, final int p_77032_2_, final float p_77032_3_) {
        if (slime.isInvisible()) {
            return 0;
        }
        if (p_77032_2_ == 0) {
            final float var4 = slime.ticksExisted + p_77032_3_;
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            final float uh = var4 * 0.0025f + MathHelper.cos(var4 * 0.02f) * 0.005f;
            final float vv = -var4 * 0.005f;
            GL11.glTranslatef(uh, vv, 0.0f);
            //this.setRenderPassModel(this.model);
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
    
    @Override
    protected void preRenderCallback(EntitySlimeSand entity, float partialTickTime) {
        final float slimeSize = (float)entity.getSlimeSize();
        final float var4 = (entity.squishFactorPrev + (entity.squishFactor - entity.squishFactorPrev) * partialTickTime) / (slimeSize * 0.5f + 1.0f);
        final float var5 = 1.0f / (var4 + 1.0f);
        final float scaleFactor = 1.1f;
        GL11.glScalef(var5 * slimeSize * scaleFactor, 0.75f / var5 * slimeSize * scaleFactor, var5 * slimeSize * scaleFactor);
    }
    
    protected int shouldRenderPass(final EntityLivingBase entity, final int p_77032_2_, final float p_77032_3_) {
        return this.shouldRenderPass((EntitySlimeSand)entity, p_77032_2_, p_77032_3_);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntitySlimeSand entity) {
        return entityTexture;
    }
    
    // The render factory to use
  	public static class Factory implements IRenderFactory {
  		
  		// What manager is the factory creating the render for
  		@Override
  		public SlimeSandRender createRenderFor(RenderManager manager) {
  			return new SlimeSandRender(manager, model, shadowSize);
  		}
  	}
}
