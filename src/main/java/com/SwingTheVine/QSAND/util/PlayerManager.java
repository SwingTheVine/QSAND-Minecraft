package com.SwingTheVine.QSAND.util;

import com.SwingTheVine.QSAND.client.player.PlayerMudManager;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Manages player events
 * 
 * @since <b>0.35.0</b>
 * @author <b>SwingTheVine</b> - Improved and updated MrBlackGoo's code to 1.8.9
 * @author <b>MrBlackGoo</b> - 1.7.10 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class PlayerManager {
	
	private final BeaconHandler beacon = new BeaconHandler(false); // Constructs a beacon handler. Enabled if "true" passed in
	
	@SubscribeEvent
	public void onEntityConstructing(final EntityEvent.EntityConstructing event) {
		
		if (event.entity instanceof EntityPlayer && PlayerMudManager.get((EntityPlayer) event.entity) == null) {
			PlayerMudManager.register((EntityPlayer) event.entity);
		}
	}
	
	@SubscribeEvent
	public void onLivingDeathEvent(final LivingDeathEvent event) {
		
		if (!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer) {
			final PlayerMudManager props = PlayerMudManager.get((EntityPlayer) event.entityLiving);
			props.setMudLevel(0);
			props.setMudType(-1);
			props.setMudTime(0);
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdateEvent(final LivingEvent.LivingUpdateEvent event) {
		
		if (!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer) {
			final PlayerMudManager props = PlayerMudManager.get((EntityPlayer) event.entityLiving);
			final int ml = props.getMudLevel();
			final int mtp = props.getMudType();
			final int mt = props.getMudTime();
			if (mtp >= 0 && mt > 0) {
				if (event.entityLiving.isWet()) {
					if (!QuicksandManager.isEntityInsideOfBlockL(event.entityLiving, QSAND_Blocks.mireLiquid)
						&& !QuicksandManager.isEntityInsideOfBlockL(event.entityLiving, QSAND_Blocks.mireLiquidStable)
						&& props.getMudTime() > 0) {
						props.addMudTime(-5 - props.getMudTime() / 100);
						props.setMudTime(Math.max(props.getMudTime(), 0));
						if (event.entityLiving.worldObj.getTotalWorldTime() % 16L == 0L && ml > 5) {
							props.setMudLevel(ml - 1);
						}
					}
				} else {
					if (event.entityLiving.worldObj.getTotalWorldTime() % 1024L == 0L && ml > 5 && mt < 1000) {
						props.setMudLevel(ml - 1);
					}
					if (event.entityLiving.worldObj.getTotalWorldTime() % 2L == 0L
						&& props.getMudTime() > QuicksandManager.getLastMudType(mtp)) {
						props.addMudTime(-1);
					}
				}
			}
		}
	}
}
