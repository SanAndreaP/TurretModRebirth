package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.IRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("UnusedReturnValue")
public interface ITurretRegistry
        extends IRegistry<ITurret>
{
    ITurret getType(Class<? extends ITurret> clazz);

    @Nonnull
    ItemStack getItem(ITurretInst turretInst);
}
