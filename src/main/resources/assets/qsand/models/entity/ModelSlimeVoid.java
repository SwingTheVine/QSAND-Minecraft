package assets.qsand.models.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSlimeVoid extends ModelBase {
	ModelRenderer slimeBodies;
    
    public ModelSlimeVoid(final int p_i1157_1_) {
        this.slimeBodies = new ModelRenderer((ModelBase)this, 0, p_i1157_1_);
        if (p_i1157_1_ == 0) {
            this.slimeBodies.addBox(-4.0f, 16.0f, -4.0f, 8, 8, 8);
            this.slimeBodies.addBox(-3.0f, 17.0f, -5.0f, 6, 6, 10);
            this.slimeBodies.addBox(-5.0f, 17.0f, -3.0f, 10, 6, 6);
            this.slimeBodies.addBox(-3.0f, 15.0f, -3.0f, 6, 10, 6);
        }
    }
    
    public void render(final Entity p_78088_1_, final float p_78088_2_, final float p_78088_3_, final float p_78088_4_, final float p_78088_5_, final float p_78088_6_, final float p_78088_7_) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
        this.slimeBodies.render(p_78088_7_);
    }
}
