package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface IUpgradeProcessor
{
    void onTick();

    boolean hasUpgrade(UUID id);

    boolean hasUpgrade(ITurretUpgrade upg);

    <T extends IUpgradeInstance> T getUpgradeInstance(UUID id);

    void setUpgradeInstance(UUID id, IUpgradeInstance inst);

    void delUpgradeInstance(UUID id);

    boolean tryApplyUpgrade(@Nonnull ItemStack upgStack);

    void writeToNbt(NBTTagCompound nbt);

    void readFromNbt(NBTTagCompound nbt);

    void syncUpgrade(UUID id);
}
