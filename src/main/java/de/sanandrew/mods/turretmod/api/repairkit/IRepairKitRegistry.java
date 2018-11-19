package de.sanandrew.mods.turretmod.api.repairkit;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IRepairKitRegistry
{
    void register(IRepairKit type);

    void registerAll(IRepairKit... types);

    List<IRepairKit> getTypes();

    @Nonnull
    IRepairKit getType(ResourceLocation id);

    @Nonnull
    IRepairKit getType(@Nonnull ItemStack stack);

    @Nonnull
    ItemStack getItem(IRepairKit type);
}
