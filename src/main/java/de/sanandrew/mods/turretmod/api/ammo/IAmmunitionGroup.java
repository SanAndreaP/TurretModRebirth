package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface IAmmunitionGroup
{
    ResourceLocation getId();

    String getName();

    ItemStack getIcon();

    ITurret getTurret();
}
