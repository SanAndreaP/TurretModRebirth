/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.entity.turret.TargetProcessor;
import dev.sanandrea.mods.turretmod.entity.turret.TurretRegistry;
import dev.sanandrea.mods.turretmod.entity.turret.UpgradeProcessor;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import dev.sanandrea.mods.turretmod.tileentity.TurretCrateEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class TurretCrateInventory
        implements IInventory, INBTSerializable<CompoundNBT>
{
    public static final int SIZE_UPGRADE_STORAGE = 36;
    public static final int SLOT_AMMO = SIZE_UPGRADE_STORAGE + 1;

    private final TurretCrateEntity      tile;
    private final NonNullList<ItemStack> upgrades = NonNullList.withSize(SIZE_UPGRADE_STORAGE, ItemStack.EMPTY);
    private final NonNullList<ItemStack> ammo = NonNullList.create();
    private ItemStack turretStack = ItemStack.EMPTY;
    private int ammoCntCache = -1;

    public TurretCrateInventory(TurretCrateEntity tile) {
        this.tile = tile;
    }

    @Override
    public int getContainerSize() {
        return SIZE_UPGRADE_STORAGE + 2;
    }

    @Override
    public boolean isEmpty() {
        return !ItemStackUtils.isValid(this.turretStack) && (this.ammo.isEmpty() || this.ammo.stream().noneMatch(ItemStackUtils::isValid))
                    && this.upgrades.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        if( index == 0 ) {
            return this.turretStack;
        } else if( index >= 1 && index <= SIZE_UPGRADE_STORAGE ) {
            return this.upgrades.get(index - 1);
        } else if( index > SIZE_UPGRADE_STORAGE && index - SIZE_UPGRADE_STORAGE <= this.ammo.size() ) {
            return this.ammo.get(index - SIZE_UPGRADE_STORAGE - 1);
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = this.getItem(index);
        if( ItemStackUtils.isValid(stack) ) {
            ItemStack itemstack;

            if( stack.getCount() <= count ) {
                itemstack = stack;
                this.removeItemNoUpdate(index);
            } else {
                itemstack = stack.split(count);

                if( stack.getCount() == 0 ) {
                    this.removeItemNoUpdate(index);
                }
                if( index == SLOT_AMMO ) {
                    this.reduceAmmoList();
                }
            }

            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if( index == 0 ) {
            ItemStack stack = this.turretStack;
            this.turretStack = ItemStack.EMPTY;
            return stack;
        } else if( index >= 1 && index <= SIZE_UPGRADE_STORAGE ) {
            return this.upgrades.set(index - 1, ItemStack.EMPTY);
        } else if( index == SLOT_AMMO && !this.ammo.isEmpty() ) {
            ItemStack removed = this.ammo.remove(0);
            this.reduceAmmoList();
            return removed;
        }

        return ItemStack.EMPTY;
    }

    private void reduceAmmoList() {
        NonNullList<ItemStack> combinedList = ItemStackUtils.getCompactItems(this.ammo, this.getMaxStackSize());
        this.ammo.clear();
        this.ammo.addAll(combinedList);
        this.ammoCntCache = -1;
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) ) {
            this.removeItemNoUpdate(index);
            this.ammoCntCache = -1;
        }
    }

    public void replaceSafeUpgrade() {
        for( int i = 0; i < SIZE_UPGRADE_STORAGE; i++ ) {
            ItemStack upgStack = this.upgrades.get(i);
            if( UpgradeRegistry.INSTANCE.isType(upgStack, Upgrades.TURRET_SAFE) ) {
                this.upgrades.set(i, UpgradeRegistry.INSTANCE.getItem(UpgradeRegistry.INSTANCE.getEmptyUpgrade().getId()));
            }
        }
    }

    @Override
    public void setChanged() { /* no-op */ }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        BlockPos tilePos = this.tile.getBlockPos();
        return player.level.getBlockEntity(tilePos) == this.tile && player.distanceToSqr(tilePos.getX() + 0.5D, tilePos.getY() + 0.5D, tilePos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public void clearContent() {
        this.turretStack = ItemStack.EMPTY;
        this.upgrades.clear();
        this.ammo.clear();
        this.ammoCntCache = -1;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        ItemStackUtils.writeStackToTag(this.turretStack, nbt, "TurretItem");
        nbt.put("InventoryUpgrades", ItemStackUtils.writeItemStacksToTag(this.upgrades, 64));
        nbt.put("InventoryAmmo", ItemStackUtils.writeItemStacksToTag(this.ammo, 64));

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.turretStack = ItemStack.of(nbt.getCompound("TurretItem"));
        ItemStackUtils.readItemStacksFromTag(this.upgrades, nbt.getList("InventoryUpgrades", Constants.NBT.TAG_COMPOUND));

        ListNBT ammoTag = nbt.getList("InventoryAmmo", Constants.NBT.TAG_COMPOUND);
        if( !ammoTag.isEmpty() ) {
            this.ammo.addAll(NonNullList.withSize(ammoTag.size(), ItemStack.EMPTY));
            this.ammoCntCache = -1;
            ItemStackUtils.readItemStacksFromTag(this.ammo, ammoTag);
        }
    }

    public void insertTurret(ITurretEntity turretInst) {
        this.turretStack = TurretRegistry.INSTANCE.getItem(turretInst);

        this.ammo.clear();
        this.ammo.addAll(((TargetProcessor) turretInst.getTargetProcessor()).extractAmmoItems());
        this.ammoCntCache = -1;

        this.upgrades.clear();
        NonNullList<ItemStack> tUpgrades = ((UpgradeProcessor) turretInst.getUpgradeProcessor()).extractUpgrades();
        for( int i = 0, max = this.upgrades.size(); i < max; i++ ) {
            this.upgrades.set(i, tUpgrades.get(i));
        }
    }

    public int getAmmoCount() {
        if( this.ammoCntCache < 0 ) {
            this.ammoCntCache = 0;
            this.ammo.forEach(a -> this.ammoCntCache += a.getCount());
        }

        return this.ammoCntCache;
    }
}
