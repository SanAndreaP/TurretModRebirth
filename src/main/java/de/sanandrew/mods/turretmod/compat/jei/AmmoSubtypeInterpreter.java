package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import mezz.jei.api.ISubtypeRegistry;
import net.minecraft.item.ItemStack;

class AmmoSubtypeInterpreter
        implements ISubtypeRegistry.ISubtypeInterpreter
{
    static final AmmoSubtypeInterpreter INSTANCE = new AmmoSubtypeInterpreter();

    @Override
    public String apply(ItemStack stack) {
        if( !stack.hasTagCompound() || !(stack.getItem() instanceof ItemAmmo) ) {
            return ISubtypeRegistry.ISubtypeInterpreter.NONE;
        }

        return MiscUtils.defIfNull(AmmunitionRegistry.INSTANCE.getSubtype(stack), ISubtypeRegistry.ISubtypeInterpreter.NONE);
    }
}
