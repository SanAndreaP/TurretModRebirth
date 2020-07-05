package de.sanandrew.mods.turretmod.client.model.item;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

public class ColorCartridge
        implements IItemColor
{
    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if( tintIndex == 1 ) {
            AmmoCartridgeInventory inv = ItemAmmoCartridge.getInventory(stack);
            if( inv != null ) {
                IAmmunition ammo = inv.getAmmoType();
                String subtype = inv.getAmmoSubtype();
                if( ammo != null && !Strings.isNullOrEmpty(subtype) ) {
                    ItemStack s = AmmunitionRegistry.INSTANCE.getItem(ammo.getId(), subtype);
                    return Minecraft.getMinecraft().getItemColors().colorMultiplier(s, 0);
                }
            }
        }

        return -1;
    }
}
