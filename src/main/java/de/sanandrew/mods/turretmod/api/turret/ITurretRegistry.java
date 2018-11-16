package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
public interface ITurretRegistry
{
    List<ITurret> getTurrets();

    ITurret getTurret(ResourceLocation location);

    ITurret getTurret(Class<? extends ITurret> clazz);

    boolean registerTurret(ITurret type);

    @Nonnull
    ItemStack getTurretItem(ITurret type);

    @Nonnull
    ItemStack getTurretItem(ITurretInst turretInst);

    ITurret getTurret(@Nonnull ItemStack stack);
}
