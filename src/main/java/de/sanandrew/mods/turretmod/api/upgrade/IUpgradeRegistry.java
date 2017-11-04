package de.sanandrew.mods.turretmod.api.upgrade;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface IUpgradeRegistry
{
    void registerUpgrade(UUID uuid, ITurretUpgrade upgrade);

    ITurretUpgrade getUpgrade(UUID uuid);

    UUID getUpgradeId(ITurretUpgrade upg);

    UUID getUpgradeId(ItemStack stack);

    ITurretUpgrade getUpgrade(ItemStack stack);

    List<ITurretUpgrade> getUpgrades();

    ItemStack getUpgradeItem(UUID uuid);

    ItemStack getUpgradeItem(ITurretUpgrade upgrade);
}
