package com.SwingTheVine.QSAND.client.entity;

import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.entity.monster.EntitySlimeVoid;

import assets.qsand.models.entity.ModelSlimeVoid;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders Void Slime entities
 * 
 * @since <b>0.42.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
@SideOnly(Side.CLIENT)
public class SlimeVoidRender extends RenderLiving<EntitySlimeVoid> {
	
	private static final ResourceLocation entityTexture = new ResourceLocation(ModInfo.id,
		EntitySlimeVoid.textureLocation); // The texture the entity will use
	private static ModelBase model = new ModelSlimeVoid(0);
	private static float shadowSize = 0.5F;
	
	public SlimeVoidRender(final RenderManager renderManager, final ModelBase modelBase, final float shadowSize) {
		super(renderManager, modelBase, shadowSize);
	}
	
	@Override
	public void doRender(final EntitySlimeVoid entity, final double x, final double y, final double z,
		final float entityYaw, final float partialTicks) {
		
		this.shadowSize = 0.25F * entity.getSlimeSize();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	protected int shouldRenderPass(final EntitySlimeVoid slime, final int p_77032_2_, final float p_77032_3_) {
		
		if (slime.isInvisible()) {
			return 0;
		}
		if (p_77032_2_ == 0) {
			// this.setRenderPassModel(this.model);
			GL11.glEnable(2977);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			return 1;
		}
		if (p_77032_2_ == 1) {
			GL11.glDisable(3042);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
		return -1;
	}
	
	@Override
	protected void preRenderCallback(final EntitySlimeVoid entity, final float partialTickTime) {
		
		final float slimeSize = entity.getSlimeSize();
		final float var4 = (entity.squishFactorPrev + (entity.squishFactor - entity.squishFactorPrev) * partialTickTime)
			/ (slimeSize * 0.5f + 1.0f);
		final float var5 = 1.0f / (var4 + 1.0f);
		final float scaleFactor = 1.25f;
		GL11.glScalef(var5 * slimeSize * scaleFactor, 0.75f / var5 * slimeSize * scaleFactor,
			var5 * slimeSize * scaleFactor);
	}
	
	protected int shouldRenderPass(final EntityLivingBase entity, final int p_77032_2_, final float p_77032_3_) {
		
		return this.shouldRenderPass((EntitySlimeVoid) entity, p_77032_2_, p_77032_3_);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(final EntitySlimeVoid entity) {
		
		return entityTexture;
	}
	
	// The render factory to use
	public static class Factory implements IRenderFactory {
		
		// What manager is the factory creating the render for
		@Override
		public SlimeVoidRender createRenderFor(final RenderManager manager) {
			
			return new SlimeVoidRender(manager, model, shadowSize);
		}
	}
}
