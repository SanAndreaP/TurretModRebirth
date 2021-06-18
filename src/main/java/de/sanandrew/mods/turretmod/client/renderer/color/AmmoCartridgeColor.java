package de.sanandrew.mods.turretmod.client.renderer.color;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ammo.AmmoCartridgeItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class AmmoCartridgeColor
        implements IItemColor
{
    @Override
    public int getColor(@Nonnull ItemStack stack, int tintIndex) {
        if( tintIndex == 1 ) {
            AmmoCartridgeInventory inv = AmmoCartridgeItem.getInventory(stack);
            if( inv != null ) {
                IAmmunition ammo = inv.getAmmoType();
                String subtype = inv.getAmmoSubtype();
                if( ammo != null && !Strings.isNullOrEmpty(subtype) ) {
                    ItemStack s = AmmunitionRegistry.INSTANCE.getItem(ammo, subtype);
                    return Minecraft.getInstance().getItemColors().getColor(s, 0);
                }
            }
        }

        return -1;
    }
}
