package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public interface IUpgradeProcessor
{
    void onTick();

    boolean hasUpgrade(UUID uuid);

    boolean hasUpgrade(TurretUpgrade upg);

    boolean tryApplyUpgrade(ItemStack upgStack);

    void writeToNbt(NBTTagCompound nbt);

    void readFromNbt(NBTTagCompound nbt);
}
