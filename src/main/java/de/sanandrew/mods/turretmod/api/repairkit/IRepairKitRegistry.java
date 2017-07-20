package de.sanandrew.mods.turretmod.api.repairkit;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface IRepairKitRegistry
{
    boolean register(TurretRepairKit type);

    List<TurretRepairKit> getRegisteredTypes();

    @Nonnull
    TurretRepairKit getType(UUID uuid);

    UUID getTypeId(TurretRepairKit type);

    @Nonnull
    TurretRepairKit getType(@Nonnull ItemStack stack);
}
