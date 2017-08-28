package de.sanandrew.mods.turretmod.api.upgrade;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface IUpgradeRegistry
{
    void registerUpgrade(UUID uuid, ITurretUpgrade upgrade);

    @Nonnull
    ITurretUpgrade getUpgrade(UUID uuid);

    @Nonnull
    UUID getUpgradeId(ITurretUpgrade upg);

    @Nonnull
    UUID getUpgradeId(@Nonnull ItemStack stack);

    @Nonnull
    ITurretUpgrade getUpgrade(@Nonnull ItemStack stack);

    @Nonnull
    List<ITurretUpgrade> getUpgrades();

    @Nonnull
    ItemStack getUpgradeItem(UUID uuid);

    @Nonnull
    ItemStack getUpgradeItem(ITurretUpgrade upgrade);
}
