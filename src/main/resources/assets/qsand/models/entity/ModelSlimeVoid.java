package assets.qsand.models.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSlimeVoid extends ModelBase {
	private ModelRenderer slimeBodies;
	public static ModelBase instance;
    
    public ModelSlimeVoid(final int textureOffsetY) {
        this.slimeBodies = new ModelRenderer((ModelBase)this, 0, textureOffsetY);
        if (textureOffsetY == 0) {
            this.slimeBodies.addBox(-4.0f, 16.0f, -4.0f, 8, 8, 8);
            this.slimeBodies.addBox(-3.0f, 17.0f, -5.0f, 6, 6, 10);
            this.slimeBodies.addBox(-5.0f, 17.0f, -3.0f, 10, 6, 6);
            this.slimeBodies.addBox(-3.0f, 15.0f, -3.0f, 6, 10, 6);
        }
        this.slimeBodies.setTextureSize(128, 128);
    }
    
    public void render(final Entity entity, final float p_78088_2_, final float p_78088_3_, final float p_78088_4_, final float p_78088_5_, final float p_78088_6_, final float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entity);
        this.slimeBodies.render(scale);
    }
}
