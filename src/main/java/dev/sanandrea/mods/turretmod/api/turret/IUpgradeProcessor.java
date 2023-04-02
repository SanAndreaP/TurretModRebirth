/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.turret;

import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeData;
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

    <T extends IUpgradeData<?>> T getUpgradeData(ResourceLocation id);

//    void setUpgradeData(ResourceLocation id, IUpgradeData<?> inst);

//    void removeUpgradeData(ResourceLocation id);

    void syncUpgrade(ResourceLocation id);

    boolean tryApplyUpgrade(@Nonnull ItemStack upgStack);

    void save(CompoundNBT nbt);

    void load(CompoundNBT nbt);

    boolean canAccessRemotely();
}
