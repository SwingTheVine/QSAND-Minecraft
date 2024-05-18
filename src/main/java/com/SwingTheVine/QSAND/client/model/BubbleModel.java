package com.SwingTheVine.QSAND.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BubbleModel extends ModelBase {
	
	private ModelRenderer bubbleBodies;
	
	public BubbleModel() {
		System.out.println("Bubble Model");
		this.bubbleBodies = new ModelRenderer((ModelBase)this, 0, 0);
		this.bubbleBodies.addBox(-4.0F, 1.0F, -4.0F, 8, 8, 8);
		this.bubbleBodies.addBox(-3.0F, 2.0F, -5.0F, 6, 6, 10);
		this.bubbleBodies.addBox(-5.0F, 2.0F, -3.0F, 10, 6, 6);
		this.bubbleBodies.addBox(-3.0F, 0.0F, -3.0F, 6, 10, 6);
		
		this.bubbleBodies.setTextureSize(16, 16);
	}
	
	@Override
	public void render(Entity entity, float timeSinceRender, float limbSwingDistance, float f3, float headYRotation, float headXRotation, float modelYTrans) {
		//super.render(entity, timeSinceRender, limbSwingDistance, f3, headYRotation, headXRotation, 0.5F);
		System.out.println("Render");
		setRotationAngles(timeSinceRender, limbSwingDistance, f3, headYRotation, headXRotation, 0.5F, entity);
		this.bubbleBodies.render(modelYTrans);
	}
	
	@Override
	public void setRotationAngles(float time, float limbSwingDistance, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(time, limbSwingDistance, f2, f3, f4, f5, entity);
	}
}
