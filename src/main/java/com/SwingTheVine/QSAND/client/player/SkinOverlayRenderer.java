package com.SwingTheVine.QSAND.client.player;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.util.BeaconHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/** Renders the skin overlay on the player.
 * 
 * @since <b>0.39.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class SkinOverlayRenderer extends ModelRenderer {
	
	private final BeaconHandler beacon = new BeaconHandler(false); // Constructs a beacon handler. Enabled if "true" passed in
	
	public EntityPlayer player;
	public UUID playerUUID;
	public String playerNickname;
	public World world;
	public boolean showOverlay;
	public static final ResourceLocation[] skinOverlayMud = {
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay0.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay1.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay2.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay3.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay4.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay5.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay6.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay7.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay8.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/mud/MudOverlay9.png")};
		
	public static final ResourceLocation[] skinOverlaySlime = {
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay0.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay1.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay2.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay3.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay4.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay5.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay6.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay7.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay8.png"),
		new ResourceLocation(ModInfo.id, "textures/entity/player/overlay/slime/SlimeOverlay9.png")};
		
	// Constructor
	public SkinOverlayRenderer(final ModelBase model, final String string) {
		super(model, string); // Runs the code in the super implementation of this function
		this.showOverlay = false; // Disables the overlay
	}
	
	// Constructor
	public SkinOverlayRenderer(final ModelBase model) {
		super(model); // Runs the code in the super implementation of this function
		this.showOverlay = false; // Disables the overlay
	}
	
	// Constructor
	public SkinOverlayRenderer(final ModelBase model, final EntityPlayer player) {
		this(model); // Runs the code in the super implementation of this function
		this.player = player; // Sets the current player to the player for this instance
		this.playerUUID = player.getUniqueID(); // Sets the UUID of the current player to the UUID for this instance
		this.playerNickname = player.getName(); // Sets the command line name of the player to the command line name for this
												// instance
		this.world = this.player.worldObj; // Sets the current world to the world for this instance
	}
	
	// Constructor
	public SkinOverlayRenderer(final ModelBase model, final int x, final int y) {
		super(model, x, y); // Runs the code in the super implementation of this function
		this.showOverlay = false; // Disables the overlay
	}
	
	// Constructor
	public SkinOverlayRenderer(final ModelBase model, final int x, final int y, final EntityPlayer player) {
		this(model, x, y); // Runs the code in the super implementation of this function
		this.player = player; // Sets the current player to the player for this instance
		this.playerUUID = player.getUniqueID(); // Sets the UUID of the current player to the UUID for this instance
		this.playerNickname = player.getName(); // Sets the command line name of the player to the command line name for this
												// instance
		this.world = this.player.worldObj; // Sets the current world to the world for this instance
	}
	
	@Override
	public void render(final float translationAmplifer) {
		
		// If the model is NOT being shown...
		if (!this.showModel) {
			return; // There is no visible player. Don't render the overlay
		}
		
		// If the overlay is marked as NOT being shown...
		if (!this.showOverlay) {
			return; // The overlay is disabled. Don't render the overlay
		}
		
		// Obtains and stores a local copy of the custom properties of the current player
		final PlayerMudManager properties = PlayerMudManager.get(this.player);
		
		final int mudLevel = properties.getMudLevel();
		final int mudType = properties.getMudType();
		final int mudTime = properties.getMudTime();
		
		beacon.logBeacon("render");
		beacon.logBeacon("mudLevel", "1", mudLevel);
		beacon.logBeacon("mudType", "1", mudType);
		beacon.logBeacon("mudTime", "1", mudTime);
		
		if (mudTime > 50 && mudType >= 0 && mudType <= QSAND_Blocks.blockObjectList.length / 5 && mudLevel > 0) {
			
			final int color = (Integer) QSAND_Blocks.blockObjectList[(mudType * 5) + 1];
			final float red = (color >> 16 & 0xFF) / 255.0f;
			final float green = (color >> 8 & 0xFF) / 255.0f;
			final float blue = (color & 0xFF) / 255.0f;
			
			GL11.glColor4f(red, green, blue, Math.min(1.0f, mudTime / 1000.0f));
			GL11.glDepthFunc(515);
			GL11.glEnable(3008);
			GL11.glEnable(3553);
			GL11.glEnable(2977);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			
			if (mudType == 0 || mudType == 1 || mudType == 3 || mudType == 6 || mudType == 7 || mudType == 8
				|| mudType == 15 || mudType == 19 || mudType == 20 || mudType == 21 || mudType == 25 || mudType == 28
				|| mudType == 31) {
				
				Minecraft.getMinecraft().renderEngine.bindTexture(skinOverlaySlime[mudLevel - 1]);
			} else {
				Minecraft.getMinecraft().renderEngine.bindTexture(skinOverlayMud[mudLevel - 1]);
			}
			
			super.render(translationAmplifer); // Runs the code in the super implementation of this function
			
			// TODO: AbstractClientPlayer may not be the replacement for EntityClientPlayerMP
			if (this.player instanceof AbstractClientPlayer) {
				Minecraft.getMinecraft().renderEngine
					.bindTexture(((AbstractClientPlayer) this.player).getLocationSkin());
			} else {
				Minecraft.getMinecraft().renderEngine
					.bindTexture(((EntityOtherPlayerMP) this.player).getLocationSkin());
			}
			
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDisable(2977);
			GL11.glDisable(3042);
			GL11.glDepthFunc(515);
			GL11.glEnable(3008);
			GL11.glEnable(3553);
		}
		
		this.showOverlay = false;
	}
}
