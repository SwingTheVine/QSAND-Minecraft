package com.SwingTheVine.QSAND.client.player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.SwingTheVine.QSAND.handler.FieldsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomPlayerOverlayRenderer {
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void preRenderPlayer(final RenderPlayerEvent.Pre event) {
		
		// If the player does NOT equal the current player,
		//    OR the player equals the current player, AND the player is NOT in 3rd person view,
		//    AND this.checkPlayerMuddyModel
        if ((event.entityPlayer != Minecraft.getMinecraft().thePlayer || (event.entityPlayer == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) && this.checkPlayerMuddyModel(event.renderer, event.entityPlayer)) {
            this.registerPlayerMuddyModel(event.renderer, event.entityPlayer); // Register the skin overlay
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void handRenderPlayer(final RenderHandEvent event) {
        if (this.checkPlayerMuddyModel((RenderPlayer)((RendererLivingEntity)Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((Entity)Minecraft.getMinecraft().thePlayer)), (EntityPlayer)Minecraft.getMinecraft().thePlayer)) {
            this.registerPlayerMuddyModel((RenderPlayer)((RendererLivingEntity)Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((Entity)Minecraft.getMinecraft().thePlayer)), (EntityPlayer)Minecraft.getMinecraft().thePlayer);
        }
    }
    
    public boolean turnOnRenderMuddyModel(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
        final List<ModelRenderer> list = new ArrayList<ModelRenderer>();
        for (final Object o : renderer.getMainModel().boxList) {
            if (o instanceof ModelRenderer) {
                list.add((ModelRenderer)o);
            }
        }
        for (final ModelRenderer model : list) {
            this.turnOnChilds(model, entityPlayer);
        }
        return true;
    }
    
    public boolean turnOnRenderMuddyModelFirstPerson(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
        this.turnOnChilds(renderer.getMainModel().bipedRightArm, entityPlayer);
        return true;
    }
    
    public boolean registerPlayerMuddyModel(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
        final List<ModelRenderer> list = new ArrayList<ModelRenderer>();
        for (final Object o : renderer.getMainModel().boxList) {
            if (o instanceof ModelRenderer && !(o instanceof SkinOverlayRenderer)) {
                list.add((ModelRenderer)o);
            }
        }
        for (final ModelRenderer model : list) {
            this.scanChilds(model, entityPlayer);
        }
        System.out.println("(QSAND) REGISTERED CUSTOM MODEL to PLAYER: " + entityPlayer);
        return true;
    }
    
    public boolean checkPlayerMuddyModel(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
        int isFailed = 0;
        int result = 0;
        final List boxList = new ArrayList(renderer.getMainModel().boxList);
        for (final Object o : renderer.getMainModel().boxList) {
            if (o instanceof ModelRenderer) {
                isFailed = this.checkPlayerMuddyModelChild((ModelRenderer)o, entityPlayer);
                if (isFailed > result) {
                    result = isFailed;
                }
                if (isFailed == 2) {
                    if (!(o instanceof SkinOverlayRenderer)) {
                        continue;
                    }
                    boxList.remove(o);
                }
                else {
                    this.turnOnChilds((ModelRenderer)o, entityPlayer);
                }
            }
        }
        if (result == 0) {
            return true;
        }
        if (result == 1) {
            return false;
        }
        renderer.getMainModel().boxList = new ArrayList(boxList);
        for (final Object o : renderer.getMainModel().boxList) {
            if (o instanceof ModelRenderer) {
                this.deleteChilds((ModelRenderer)o, entityPlayer);
            }
        }
        System.out.println("(QSAND) DELETED CUSTOM MODEL from PLAYER: " + entityPlayer);
        return true;
    }
    
    public int checkPlayerMuddyModelChild(final ModelRenderer model, final EntityPlayer ply) {
        int isFailed = 0;
        if (model != null) {
            if (model.childModels != null) {
                for (final Object o : model.childModels) {
                    if (o instanceof ModelRenderer) {
                        isFailed = this.checkPlayerMuddyModelChild((ModelRenderer)o, ply);
                        if (isFailed == 2) {
                            return 2;
                        }
                        continue;
                    }
                }
            }
            if (model instanceof SkinOverlayRenderer && ((SkinOverlayRenderer)model).playerNickname == ply.getName()) {
                if (((SkinOverlayRenderer)model).world != ply.worldObj) {
                    return 2;
                }
                if (((SkinOverlayRenderer)model).playerUUID != ply.getUniqueID()) {
                    return 2;
                }
                if (((SkinOverlayRenderer)model).player != ply) {
                    return 2;
                }
                return 1;
            }
        }
        return isFailed;
    }
    
    public void scanChilds(final ModelRenderer model, final EntityPlayer ply) {
        if (model != null) {
            if (model.childModels != null && !(model instanceof SkinOverlayRenderer)) {
                for (final Object o : model.childModels) {
                    if (o instanceof ModelRenderer && !(o instanceof SkinOverlayRenderer)) {
                        this.scanChilds((ModelRenderer)o, ply);
                    }
                }
            }
            for (final Object o : model.cubeList) {
                if (!(o instanceof SkinOverlayRenderer) && o instanceof ModelBox) {
                    final ModelBox box = (ModelBox)o;
                    try {
                        final Field field0 = FieldsHandler.findField(ModelRenderer.class, ModelBase.class, 0);
                        field0.setAccessible(true);
                        final ModelBase mainMdl = (ModelBase)field0.get(model);
                        final Field field2 = FieldsHandler.findField(ModelRenderer.class, Integer.TYPE, 0);
                        field2.setAccessible(true);
                        final int xx = field2.getInt(model);
                        final Field field3 = FieldsHandler.findField(ModelRenderer.class, Integer.TYPE, 1);
                        field3.setAccessible(true);
                        final int yy = field3.getInt(model);
                        final SkinOverlayRenderer mm = new SkinOverlayRenderer(mainMdl, xx, yy, ply);
                        mm.addBox(box.posX1, box.posY1, box.posZ1, (int)(box.posX2 - box.posX1), (int)(box.posY2 - box.posY1), (int)(box.posZ2 - box.posZ1), 0.1f);
                        model.addChild((ModelRenderer)mm);
                        mm.showOverlay = false;
                    }
                    catch (Exception e) {
                        System.out.println("(QSAND) Cant get mud model!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void deleteChilds(final ModelRenderer model, final EntityPlayer ply) {
        if (model != null && model.childModels != null) {
            final List childBoxList = new ArrayList(model.childModels);
            for (final Object o : model.childModels) {
                if (o instanceof SkinOverlayRenderer && ((SkinOverlayRenderer)o).playerNickname == ply.getName()) {
                    childBoxList.remove(o);
                }
                if (o instanceof ModelRenderer) {
                    this.deleteChilds((ModelRenderer)o, ply);
                }
            }
            model.childModels = new ArrayList(childBoxList);
        }
    }
    
    public void turnOnChilds(final ModelRenderer model, final EntityPlayer ply) {
        if (model != null) {
            if (model.childModels != null) {
                for (final Object o : model.childModels) {
                    if (o instanceof ModelRenderer) {
                        this.turnOnChilds((ModelRenderer)o, ply);
                    }
                }
            }
            if (model instanceof SkinOverlayRenderer) {
                if (((SkinOverlayRenderer)model).player == ply) {
                    ((SkinOverlayRenderer)model).showOverlay = true;
                }
                else {
                    ((SkinOverlayRenderer)model).showOverlay = false;
                }
            }
        }
    }
}
