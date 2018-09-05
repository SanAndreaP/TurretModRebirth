package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface IAmmunitionGroup
{
    UUID getId();

    String getName();

    ItemStack getIcon();

    ITurret getTurret();
}
