package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.item.ammo.AmmoCartridgeItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmoItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class PickupEventHandler
{
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack    stack  = event.getItem().getItem();
        if( stack.getItem() instanceof AmmoItem ) {
            if( AmmoCartridgeItem.putAmmoInPlayerCartridge(stack, player) ) {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }
}
