/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class CapabilityEventHandler
{
    private static final ResourceLocation CAP_AMMO_CARTRIDGE = new ResourceLocation(TmrConstants.ID, "ammo.cartridge");

    @SubscribeEvent
    public static void attachCapabilityItemStack(AttachCapabilitiesEvent<ItemStack> event) {
        if( event.getObject().getItem() instanceof ItemAmmoCartridge ) {
            event.addCapability(CAP_AMMO_CARTRIDGE, new AmmoCartridgeInventory(event.getObject()));
        }
    }
}
