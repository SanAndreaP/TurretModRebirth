package de.sanandrew.mods.turretmod.api.repairkit;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IRepairKitRegistry
{
    boolean register(TurretRepairKit type);

    List<TurretRepairKit> getRegisteredTypes();

    TurretRepairKit getType(UUID uuid);

    UUID getTypeId(TurretRepairKit type);

    TurretRepairKit getType(ItemStack stack);
}
