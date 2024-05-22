package com.SwingTheVine.QSAND.handler;

import com.SwingTheVine.QSAND.client.player.CustomPlayerGUIRenderer;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;
import com.SwingTheVine.QSAND.items.ItemLongStick;
import com.SwingTheVine.QSAND.manager.QuicksandManager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomAirHandler {
	@SubscribeEvent
    public void onEntityConstructing(final EntityEvent.EntityConstructing event) {
		
		// If the entity is a player...
        if (event.entity instanceof EntityPlayer) {
        	
        	// ...AND the user has disabled realistic drowning for players...
            if (!ConfigHandler.useCustomDrownPlayers) {
                return; // The user disabled this. Don't run
            }
        } else if (!ConfigHandler.useCustomDrownEntities) {
        	// Else if the user has disabled realistic drowning for entities...
        	
            return; // The user disabled this. Don't run
        }
        
        // If the entity is a living base...
        if (event.entity instanceof EntityLivingBase) {
        	
        	// ...AND the entity is a player...
            if (event.entity instanceof EntityPlayer) {
            	
            	// Tries to run this code
                try {
                    event.entity.getDataWatcher().addObject(ConfigHandler.customDrownAirPlayersDW, (Object)300);
                } catch (Exception exception) {
                	
                	// If the Ecp level is less than 5
                    if (ConfigHandler.customDrownAirPlayersDWEcp < 5) {
                    	
                        ConfigHandler.customDrownAirPlayersDWEcp++;
                        final CrashReport report = CrashReport.makeCrashReport((Throwable)exception, "Error initializing Oxygen System. Muddy Air DataWatcher ID conflict: " + ConfigHandler.customDrownAirPlayersDW + " with entity player ");
                        throw new ReportedException(report);
                    }
                }
            } else {
            	
                try {
                    event.entity.getDataWatcher().addObject(ConfigHandler.customDrownAirEntitiesDWEcp, (Object)300);
                } catch (Exception exception) {
                	
                    if (ConfigHandler.customDrownAirEntitiesDWEcp < 5) {
                    	
                    	ConfigHandler.customDrownAirEntitiesDWEcp++;
                        final CrashReport report = CrashReport.makeCrashReport((Throwable)exception, "Error initializing Oxygen System. Muddy Mobs Air DataWatcher ID conflict: " + ConfigHandler.customDrownAirEntitiesDWEcp + " with entity living ");
                        throw new ReportedException(report);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingUpdateEvent(final LivingEvent.LivingUpdateEvent event) {
    	
    	// If entity is a player...
        if (event.entityLiving instanceof EntityPlayer) {
        	
            final EntityPlayer player = (EntityPlayer)event.entityLiving;
            
            // If the code is executing server-side
            if (player.worldObj.isRemote) {
            	
            	// If the current player is not the player...
                if (player != Minecraft.getMinecraft().thePlayer) {
                	
                	// If the player is holding something, AND the player is holding a long stick
                    if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == QSAND_Items.longStick) {
                        
                    	final ItemStack item = player.getCurrentEquippedItem();
                    	
                    	// If the player is using the long stick...
                        if (player.isUsingItem()) {
                        	
                            boolean IsSucStr = false;
                            final int UC = player.getItemInUseCount();
                            final int U_MAX = item.getMaxItemUseDuration();
                            final int U_AL = 100;
                            final int U_HE = U_MAX - U_AL;
                            final int U_CRT = (int)Math.floor(item.getMaxItemUseDuration() / 2);
                            final int U_HV = U_CRT - (int)Math.floor(U_AL / 4);
                            final int U_VH = U_MAX - (int)Math.floor(U_AL / 4);
                            final int U_VE = U_CRT - U_AL;
                            
                            if (ItemLongStick.checkQuicksand(player.worldObj, player) > 1) {
                                IsSucStr = true;
                            }
                            
                            if (IsSucStr) {
                            	
                                if (UC < U_CRT) {
                                	
                                    if (UC > U_HV) {
                                        player.clearItemInUse();
                                        player.setItemInUse(item, U_HE - 1);
                                    } else {
                                        player.clearItemInUse();
                                        player.setItemInUse(item, U_MAX);
                                    }
                                }
                            } else {
                            	
                                if (UC < 2) {
                                    player.clearItemInUse();
                                    player.setItemInUse(item, U_VE - 1);
                                }
                                
                                if (UC > U_VH) {
                                    player.clearItemInUse();
                                    player.setItemInUse(item, U_VE - 1);
                                } else if (UC > U_CRT) {
                                    player.clearItemInUse();
                                    player.setItemInUse(item, U_CRT);
                                }
                            }
                        }
                    }
                } else {
                	ConfigHandler.serverInstancePreRenderYaw = ConfigHandler.serverInstanceRenderYaw;
                	ConfigHandler.serverInstanceRenderYaw = event.entityLiving.renderYawOffset;
                }
            }
            
            if (!ConfigHandler.useCustomDrownPlayers) {
                return;
            }
        } else if (!ConfigHandler.useCustomDrownEntities) {
            return;
        }
        
        if (event.entityLiving.worldObj.isRemote) {
        	
            final int gma = QuicksandManager.getCustomDrownAir((Entity)event.entityLiving);
            
            if (gma < event.entityLiving.getAir()) {
                event.entityLiving.setAir(gma);
            }
        } else if (event.entityLiving instanceof EntityLivingBase) {
        	
            final int gma = QuicksandManager.getCustomDrownAir((Entity)event.entityLiving);
            
            // Use this in place of QSAND_Blocks.blockList to remove the magic numbers
            final Block[] failIfEntityInside = {
            		QSAND_Blocks.mireLiquid,
            		QSAND_Blocks.mireLiquidStable,
            		QSAND_Blocks.larvae,
            		QSAND_Blocks.meatHole,
            		QSAND_Blocks.voidHole};
            
            for (int currentBlockIndex = 0; currentBlockIndex < failIfEntityInside.length; currentBlockIndex++) {
            	
            	// If the entity is inside any of the blocks on the fail list...
            	if ((QuicksandManager.isEntityInsideOfBlock((Entity)event.entityLiving, failIfEntityInside[currentBlockIndex]))) {
            		return;
            	}
            }
            
            for (int currentBlockIndex = 0; currentBlockIndex < CustomPlayerGUIRenderer.blockList.length; currentBlockIndex++) {
            	
            	// If the entity is inside moss
        		if (QuicksandManager.isEntityInsideOfBlockM((Entity)event.entityLiving, QSAND_Blocks.moss)) {
        			
        			if (gma > -1) {
        				QuicksandManager.addCustomDrownAir((Entity)event.entityLiving, -1);
        			}
        			
        			return;
        		} else if (QuicksandManager.isEntityInsideOfBlock((Entity)event.entityLiving, CustomPlayerGUIRenderer.blockList[currentBlockIndex])) {
        			
        			if (gma > -1) {
        				QuicksandManager.addCustomDrownAir((Entity)event.entityLiving, -1);
        			}
        			
        			return;
        		}
            }
            
            if (gma < 300) {
            	QuicksandManager.addCustomDrownAir((Entity)event.entityLiving, Math.min(5, 300 - gma));
            }
        }
    }
    
    
}
