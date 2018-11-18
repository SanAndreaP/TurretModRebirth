package de.sanandrew.mods.turretmod.api.upgrade;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface IUpgradeRegistry
{
    void register(IUpgrade upgrade);

    void registerAll(IUpgrade... upgrade);

    @Nonnull
    IUpgrade getUpgrade(ResourceLocation id);

    @Nonnull
    IUpgrade getUpgrade(@Nonnull ItemStack stack);

    @Nonnull
    List<IUpgrade> getUpgrades();

    @Nonnull
    ItemStack getUpgradeItem(ResourceLocation id);

    @Nonnull
    ItemStack getUpgradeItem(IUpgrade upgrade);

    void syncWithServer(ITurretInst turret, ResourceLocation upgradeId);

    void syncWithClients(ITurretInst turret, ResourceLocation upgradeId);

    IUpgrade getEmptyUpgrade();
}
