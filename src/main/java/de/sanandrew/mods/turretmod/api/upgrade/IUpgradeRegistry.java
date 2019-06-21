package de.sanandrew.mods.turretmod.api.upgrade;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface IUpgradeRegistry
        extends IRegistry<IUpgrade>
{
    boolean isType(@Nonnull ItemStack item, ResourceLocation id);

    boolean isType(@Nonnull ItemStack item, IUpgrade type);

    void syncWithServer(ITurretInst turret, ResourceLocation upgradeId);

    void syncWithClients(ITurretInst turret, ResourceLocation upgradeId);

    IUpgrade getEmptyUpgrade();
}
