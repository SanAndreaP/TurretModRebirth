package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface IUpgradeProcessor
        extends IInventory, IContainerProvider
{
    void onTick();

    boolean hasUpgrade(ResourceLocation id);

    boolean hasUpgrade(IUpgrade upg);

    <T extends IUpgradeInstance<?>> T getUpgradeInstance(ResourceLocation id);

    void setUpgradeInstance(ResourceLocation id, IUpgradeInstance<?> inst);

    void delUpgradeInstance(ResourceLocation id);

    boolean tryApplyUpgrade(@Nonnull ItemStack upgStack);

    void save(CompoundNBT nbt);

    void load(CompoundNBT nbt);

    boolean canAccessRemotely();
}
