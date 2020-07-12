package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class PickupEventHandler
{
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack    stack  = event.getItem().getItem();
        if( stack.getItem() instanceof ItemAmmo ) {
            for( int i = 0, max = player.inventory.getSizeInventory(); i < max; i++ ) {
                ItemStack invStack = player.inventory.getStackInSlot(i);
                if( invStack.getItem() == ItemRegistry.AMMO_CARTRIDGE ) {
                    AmmoCartridgeInventory cartridge = ItemAmmoCartridge.getInventory(invStack);
                    ItemStack remain = InventoryUtils.addStackToCapability(stack, cartridge, EnumFacing.UP, false);
                    stack.setCount(remain.getCount());
                    if( !ItemStackUtils.isValid(remain) ) {
                        event.setResult(Event.Result.ALLOW);
                        break;
                    }
                }
            }
        }
    }
}