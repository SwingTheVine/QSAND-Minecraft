package com.SwingTheVine.QSAND.client.player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.SwingTheVine.QSAND.util.BeaconHandler;
import com.SwingTheVine.QSAND.util.FieldsHandler;

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

/** Calculations for if (and what type of overlay) the skin overlay renders.
 * 
 * @since <b>0.39.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class CustomPlayerOverlayRenderer {
	
	private final BeaconHandler beacon = new BeaconHandler(false); // Constructs a beacon handler. Enabled if "true" passed in
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preRenderPlayer(final RenderPlayerEvent.Pre event) {
		// beacon.logBeacon("preRender");
		
		// If the player does NOT equal the current player,
		// OR the player equals the current player, AND the player is NOT in 3rd person view,
		// AND this.checkPlayerMuddyModel
		if ((event.entityPlayer != Minecraft.getMinecraft().thePlayer
			|| (event.entityPlayer == Minecraft.getMinecraft().thePlayer
				&& Minecraft.getMinecraft().gameSettings.thirdPersonView != 0))
			&& this.checkPlayerMuddyModel(event.renderer, event.entityPlayer)) {
			this.registerPlayerMuddyModel(event.renderer, event.entityPlayer); // Register the skin overlay
		}
	}
	
	// Called constantly whilst playing the game
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void handRenderPlayer(final RenderHandEvent event) {
		
		// beacon.logBeacon("handRenderPlayer");
		if (this.checkPlayerMuddyModel(
			(RenderPlayer) ((RendererLivingEntity) Minecraft.getMinecraft().getRenderManager()
				.getEntityRenderObject((Entity) Minecraft.getMinecraft().thePlayer)),
			(EntityPlayer) Minecraft.getMinecraft().thePlayer)) {
			this.registerPlayerMuddyModel(
				(RenderPlayer) ((RendererLivingEntity) Minecraft.getMinecraft().getRenderManager()
					.getEntityRenderObject((Entity) Minecraft.getMinecraft().thePlayer)),
				(EntityPlayer) Minecraft.getMinecraft().thePlayer);
		}
	}
	
	public boolean turnOnRenderMuddyModel(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
		
		beacon.logBeacon("turnOnRenderMuddyModel");
		
		final List<ModelRenderer> list = new ArrayList<ModelRenderer>();
		for (final Object o : renderer.getMainModel().boxList) {
			beacon.logBeacon("Box List", "1", renderer.getMainModel().boxList.toString());
			if (o instanceof ModelRenderer) {
				list.add((ModelRenderer) o);
			}
		}
		for (final ModelRenderer model : list) {
			this.turnOnChilds(model, entityPlayer);
		}
		return true;
	}
	
	public boolean turnOnRenderMuddyModelFirstPerson(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
		
		beacon.logBeacon("turnOnRenderMuddyModelFirstPerson");
		
		this.turnOnChilds(renderer.getMainModel().bipedRightArm, entityPlayer);
		return true;
	}
	
	// Called once upon spawning into a world or logging into a world
	public boolean registerPlayerMuddyModel(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
		
		beacon.logBeacon("registerPlayerMuddyModel");
		
		final List<ModelRenderer> list = new ArrayList<ModelRenderer>();
		for (final Object o : renderer.getMainModel().boxList) {
			beacon.logBeacon("Box List", "2", renderer.getMainModel().boxList.toString());
			if (o instanceof ModelRenderer && !(o instanceof SkinOverlayRenderer)) {
				list.add((ModelRenderer) o);
			}
		}
		for (final ModelRenderer model : list) {
			this.scanChilds(model, entityPlayer);
		}
		System.out.println("(QSAND) REGISTERED CUSTOM MODEL to PLAYER: " + entityPlayer);
		return true;
	}
	
	// Called constantly whilst playing the game
	public boolean checkPlayerMuddyModel(final RenderPlayer renderer, final EntityPlayer entityPlayer) {
		// beacon.logBeacon("checkPlayerMuddyModel");
		
		int isFailed = 0;
		int result = 0;
		final List boxList = new ArrayList(renderer.getMainModel().boxList);
		for (final Object o : renderer.getMainModel().boxList) {
			beacon.logBeacon("Box List", "3", renderer.getMainModel().boxList.toString());
			if (o instanceof ModelRenderer) {
				isFailed = this.checkPlayerMuddyModelChild((ModelRenderer) o, entityPlayer);
				if (isFailed > result) {
					result = isFailed;
				}
				if (isFailed == 2) {
					if (!(o instanceof SkinOverlayRenderer)) {
						continue;
					}
					boxList.remove(o);
				} else {
					this.turnOnChilds((ModelRenderer) o, entityPlayer);
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
				this.deleteChilds((ModelRenderer) o, entityPlayer);
			}
		}
		System.out.println("(QSAND) DELETED CUSTOM MODEL from PLAYER: " + entityPlayer);
		return true;
	}
	
	// Called constantly whilst playing the game
	public int checkPlayerMuddyModelChild(final ModelRenderer model, final EntityPlayer ply) {
		// beacon.logBeacon("checkPlayerMuddyModelChild");
		
		int isFailed = 0;
		if (model != null) {
			if (model.childModels != null) {
				for (final Object o : model.childModels) {
					if (o instanceof ModelRenderer) {
						isFailed = this.checkPlayerMuddyModelChild((ModelRenderer) o, ply);
						if (isFailed == 2) {
							return 2;
						}
						continue;
					}
				}
			}
			if (model instanceof SkinOverlayRenderer && ((SkinOverlayRenderer) model).playerNickname == ply.getName()) {
				if (((SkinOverlayRenderer) model).world != ply.worldObj) {
					return 2;
				}
				if (((SkinOverlayRenderer) model).playerUUID != ply.getUniqueID()) {
					return 2;
				}
				if (((SkinOverlayRenderer) model).player != ply) {
					return 2;
				}
				return 1;
			}
		}
		return isFailed;
	}
	
	// Called a large, finite number of times upon spawning or logging into a world
	public void scanChilds(final ModelRenderer model, final EntityPlayer ply) {
		
		beacon.logBeacon("scanChilds");
		
		if (model != null) {
			if (model.childModels != null && !(model instanceof SkinOverlayRenderer)) {
				for (final Object o : model.childModels) {
					if (o instanceof ModelRenderer && !(o instanceof SkinOverlayRenderer)) {
						this.scanChilds((ModelRenderer) o, ply);
					}
				}
			}
			for (final Object o : model.cubeList) {
				if (!(o instanceof SkinOverlayRenderer) && o instanceof ModelBox) {
					final ModelBox box = (ModelBox) o;
					try {
						final Field field0 = FieldsHandler.findField(ModelRenderer.class, ModelBase.class, 0);
						field0.setAccessible(true);
						final ModelBase mainMdl = (ModelBase) field0.get(model);
						final Field field2 = FieldsHandler.findField(ModelRenderer.class, Integer.TYPE, 0);
						field2.setAccessible(true);
						final int xx = field2.getInt(model);
						final Field field3 = FieldsHandler.findField(ModelRenderer.class, Integer.TYPE, 1);
						field3.setAccessible(true);
						final int yy = field3.getInt(model);
						final SkinOverlayRenderer mm = new SkinOverlayRenderer(mainMdl, xx, yy, ply);
						mm.addBox(box.posX1, box.posY1, box.posZ1, (int) (box.posX2 - box.posX1),
							(int) (box.posY2 - box.posY1), (int) (box.posZ2 - box.posZ1), 0.1f);
						model.addChild(mm);
						mm.showOverlay = false;
					} catch (final Exception e) {
						System.out.println("(QSAND) Cant get mud model!");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	// Called a finite number of times upon player death
	public void deleteChilds(final ModelRenderer model, final EntityPlayer ply) {
		
		beacon.logBeacon("deleteChilds");
		
		if (model != null && model.childModels != null) {
			final List childBoxList = new ArrayList(model.childModels);
			for (final Object o : model.childModels) {
				if (o instanceof SkinOverlayRenderer && ((SkinOverlayRenderer) o).playerNickname == ply.getName()) {
					childBoxList.remove(o);
				}
				if (o instanceof ModelRenderer) {
					this.deleteChilds((ModelRenderer) o, ply);
				}
			}
			model.childModels = new ArrayList(childBoxList);
		}
	}
	
	// Called constantly whilst playing the game
	public void turnOnChilds(final ModelRenderer model, final EntityPlayer player) {
		// beacon.logBeacon("turnOnChilds");
		
		if (model != null) {
			if (model.childModels != null) {
				for (final Object o : model.childModels) {
					if (o instanceof ModelRenderer) {
						this.turnOnChilds((ModelRenderer) o, player);
					}
				}
			}
			if (model instanceof SkinOverlayRenderer) {
				if (((SkinOverlayRenderer) model).player == player) {
					((SkinOverlayRenderer) model).showOverlay = true;
				} else {
					((SkinOverlayRenderer) model).showOverlay = false;
				}
			}
		}
	}
}
