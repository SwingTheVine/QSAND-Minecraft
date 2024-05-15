package com.SwingTheVine.QSAND.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BubbleModel extends ModelBase {
	
	public ModelRenderer box1 = new ModelRenderer(this, 0, 0);
	
	public BubbleModel() {
		textureWidth = 16;
		textureHeight = 16;
		System.out.println("Bubble Model");
		this.box1 = new ModelRenderer(this, 0, 0);
		this.box1.addBox(0F, 0F, 0F, 2, 2, 2);
		this.box1.setTextureSize(16, 16);
	}
	
	@Override
	public void render(Entity entity, float timeSinceRender, float limbSwingDistance, float f3, float headYRotation, float headXRotation, float modelYTrans) {
		super.render(entity, timeSinceRender, limbSwingDistance, f3, headYRotation, headXRotation, 0.5F);
		System.out.println("Render");
		setRotationAngles(timeSinceRender, limbSwingDistance, f3, headYRotation, headXRotation, 0.5F, entity);
		this.box1.render(modelYTrans);
	}
	
	@Override
	public void setRotationAngles(float time, float limbSwingDistance, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(time, limbSwingDistance, f2, f3, f4, f5, entity);
	}
}
