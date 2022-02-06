/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.item.ItemUpgrade;
import de.sanandrew.mods.turretmod.item.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class UpgradeProcessor
        implements IUpgradeProcessor
{
    @Nonnull
    private final NonNullList<ItemStack> upgradeStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    private final Map<ResourceLocation, IUpgradeInstance<?>> upgInstances = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, IUpgradeInstance<?>> upgTickable = new ConcurrentHashMap<>();

    private boolean hasChanged = false;
    private final ITurretEntity turret;

    private final Deque<IUpgrade> firstSynchronize = new ConcurrentLinkedDeque<>();

    UpgradeProcessor(ITurretEntity turret) {
        this.turret = turret;
    }

    @Override
    public void onTick() {
        this.upgTickable.forEach((key, val) -> val.onTick(this.turret));

        if( this.hasChanged ) {
            LivingEntity turretL = this.turret.get();

            for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                ItemStack invStack = this.upgradeStacks.get(i);
                if( ItemStackUtils.isValid(invStack) ) {
                    IUpgrade upg = UpgradeRegistry.INSTANCE.get(invStack);
                    IUpgrade dep = upg.getDependantOn();
                    if( dep != null && !this.hasUpgrade(dep) ) {
                        dropUpgrade(turretL, i, invStack, upg);
                    }
                }
            }

            if( !this.hasUpgrade(Upgrades.UPG_STORAGE_III) ) {
                for( int i = 27, max = this.upgradeStacks.size(); i < max; i++ ) {
                    dropUpgrade(turretL, i, this.upgradeStacks.get(i), null);
                }
            }

            if( !this.hasUpgrade(Upgrades.UPG_STORAGE_II) ) {
                for( int i = 18; i < 27; i++ ) {
                    dropUpgrade(turretL, i, this.upgradeStacks.get(i), null);
                }
            }

            if( !this.hasUpgrade(Upgrades.UPG_STORAGE_I) ) {
                for( int i = 9; i < 18; i++ ) {
                    dropUpgrade(turretL, i, this.upgradeStacks.get(i), null);
                }
            }
        }

        while( !this.firstSynchronize.isEmpty() ) {
            //TODO: sync with clients
//            UpgradeRegistry.INSTANCE.syncWithClients(this.turret, this.firstSynchronize.pollFirst().getId());
        }
    }

    private void dropUpgrade(LivingEntity entity, int slot, ItemStack stack, IUpgrade upg) {
        if( ItemStackUtils.isValid(stack) ) {
            if( !entity.level.isClientSide ) {
                ItemEntity itm = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), stack);
                entity.level.addFreshEntity(itm);
            }
            upg = upg != null ? upg : UpgradeRegistry.INSTANCE.get(stack);
            upg.terminate(this.turret, stack);
            this.upgradeStacks.set(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean hasUpgrade(ResourceLocation id) {
        final ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getItem(id);
        return this.upgradeStacks.stream().anyMatch(currStack -> ItemStackUtils.areEqual(upgItemStack, currStack, false));
    }

    @Override
    public boolean hasUpgrade(IUpgrade upg) {
        final ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getItem(upg.getId());
        return this.upgradeStacks.stream().anyMatch(currStack -> ItemStackUtils.areEqual(upgItemStack, currStack, false));
    }

    @Override
    public <T extends IUpgradeInstance<?>> T getUpgradeInstance(ResourceLocation id) {
        return ReflectionUtils.getCasted(this.upgInstances.get(id));
    }

    @Override
    public void setUpgradeInstance(ResourceLocation id, IUpgradeInstance<?> inst) {
        this.upgInstances.put(id, inst);
        if( inst.getClass().getAnnotation(IUpgradeInstance.Tickable.class) != null ) {
            this.upgTickable.put(id, inst);
        }
    }

    @Override
    public void delUpgradeInstance(ResourceLocation id) {
        this.upgInstances.remove(id);
        this.upgTickable.remove(id);
    }

    @Override
    public int getContainerSize() {
        return upgradeStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.upgradeStacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    @Nonnull
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.upgradeStacks.size() ? this.upgradeStacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        ItemStack slotStack = this.upgradeStacks.get(slot);
        if( ItemStackUtils.isValid(slotStack) ) {
            ItemStack itemstack;

            if( slotStack.getCount() <= amount ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.get(slotStack);
                upg.terminate(this.turret, slotStack);

                itemstack = slotStack;
                this.upgradeStacks.set(slot, ItemStack.EMPTY);
            } else {
                itemstack = slotStack.split(amount);

                if( slotStack.getCount() == 0 ) {
                    this.upgradeStacks.set(slot, ItemStack.EMPTY);
                }
            }

            this.setChanged();

            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        if( ItemStackUtils.isValid(this.upgradeStacks.get(slot)) ) {
            ItemStack itemstack = this.upgradeStacks.get(slot);
            this.upgradeStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        ItemStack slotStack = this.upgradeStacks.get(slot);
        if( !ItemStackUtils.areEqual(slotStack, stack) ) {
            if( ItemStackUtils.isValid(slotStack) ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.get(slotStack);
                upg.terminate(this.turret, slotStack);
            }

            if( ItemStackUtils.isValid(stack) ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.get(stack);
                upg.initialize(this.turret, stack);
                if( upg.getClass().getAnnotation(IUpgrade.InitSynchronizeClient.class) != null ) {
                    this.firstSynchronize.offerLast(upg);
                }
            }
        }

        this.upgradeStacks.set(slot, stack);

        if( ItemStackUtils.isValid(stack) && stack.getCount() > this.getMaxStackSize() ) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

//    @Nullable
//    @Override
//    public ITextComponent getCustomName() {
//        return IUpgradeProcessor.super.getCustomName();
//    }
//
//    @Override
//    public String getName() {
//        return "Upgrades";
//    }
//
//    @Override
//    public boolean hasCustomName() {
//        return false;
//    }
//
//    @Override
//    public ITextComponent getContainerName() {
//        return new TextComponentString(this.getName());
//    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        this.hasChanged = true;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return true;
    }

//    @Override
//    public boolean isUsableByPlayer(PlayerEntity player) { return true; }
//
//    @Override
//    public void openContainer(PlayerEntity player) {}
//
//    @Override
//    public void closeInventory(PlayerEntity player) {}

    private boolean isUpgradeItemApplicable(ItemStack stack) {
        if( stack.getItem() instanceof ItemUpgrade ) {
            IUpgrade upg = ((ItemUpgrade) stack.getItem()).upgrade;
            if( this.hasUpgrade(upg) ) {
                return false;
            }
            if( !UpgradeRegistry.INSTANCE.isApplicable(upg, this.turret.getDelegate()) ) {
                return false;
            }

            IUpgrade dep = upg.getDependantOn();
            return dep == null || this.hasUpgrade(dep);
        }

        return false;
    }

    private boolean isUpgradeItemApplicableForSlot(int slot, ItemStack stack, boolean checkItem) {
        if( !checkItem || this.isUpgradeItemApplicable(stack) ) {
            if( slot >= 9 && !this.hasUpgrade(Upgrades.UPG_STORAGE_I) ) {
                return false;
            }

            if( slot >= 18 && !this.hasUpgrade(Upgrades.UPG_STORAGE_II) ) {
                return false;
            }

            if( slot >= 27 && !this.hasUpgrade(Upgrades.UPG_STORAGE_III) ) {
                return false;
            }

            return !ItemStackUtils.isValid(this.upgradeStacks.get(slot));
        }

        return false;
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        return isUpgradeItemApplicableForSlot(slot, stack, true);
    }

    @Override
    public void clearContent() {
        this.upgradeStacks.clear();
    }

    @Override
    public boolean tryApplyUpgrade(@Nonnull ItemStack upgStack) {
        if( this.isUpgradeItemApplicable(upgStack) ) {
            LivingEntity turretL = this.turret.get();
            for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                if( this.isUpgradeItemApplicableForSlot(i, upgStack, false) ) {
                    this.setItem(i, upgStack);
                    //TODO: sync upgrade slot
//                    PacketRegistry.sendToAllAround(new PacketUpdateUgradeSlot(this.turret, i, upgStack), turretL.dimension, turretL.posX, turretL.posY, turretL.posZ, 64.0D);
                    return true;
                }
            }
        }

        return false;
    }

    void dropUpgrades() {
        LivingEntity turretL = this.turret.get();

        for( int i = 0, max = this.getContainerSize(); i < max; i++ ) {
            ItemStack stack = this.removeItemNoUpdate(i);

            if( ItemStackUtils.isValid(stack) ) {
                float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

                ItemEntity entityitem = new ItemEntity(turretL.level, (turretL.getX() + xOff), (turretL.getY() + yOff), (turretL.getZ() + zOff), stack);

                float motionSpeed = 0.05F;
                entityitem.setDeltaMovement(((float)MiscUtils.RNG.randomGaussian() * motionSpeed),
                                            ((float)MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F),
                                            ((float)MiscUtils.RNG.randomGaussian() * motionSpeed));

                turretL.level.addFreshEntity(entityitem);
            }
        }
    }

    public NonNullList<ItemStack> extractUpgrades() {
        NonNullList<ItemStack> newList = NonNullList.create();
        newList.addAll(this.upgradeStacks);
        this.upgradeStacks.clear();
        return newList;
    }

    @Override
    public void save(CompoundNBT nbt) {
        nbt.put("upgInventory", ItemStackUtils.writeItemStacksToTag(this.upgradeStacks, 1, this::callbackWriteUpgStack));
    }

    @Override
    public void load(CompoundNBT nbt) {
        if( nbt != null ) {
            ItemStackUtils.readItemStacksFromTag(this.upgradeStacks, nbt.getList("upgInventory", Constants.NBT.TAG_COMPOUND), this::callbackReadUpgStack);
        }
    }

    private void callbackWriteUpgStack(@Nonnull ItemStack upgStack, CompoundNBT nbt) {
        IUpgrade upg = UpgradeRegistry.INSTANCE.get(upgStack);
        upg.onSave(this.turret, nbt);
    }

    private void callbackReadUpgStack(@Nonnull ItemStack upgStack, CompoundNBT nbt) {
        IUpgrade upg = UpgradeRegistry.INSTANCE.get(upgStack);
        upg.initialize(this.turret, upgStack);
        upg.onLoad(this.turret, nbt);
        if( upg.getClass().getAnnotation(IUpgrade.InitSynchronizeClient.class) != null ) {
            this.firstSynchronize.offerLast(upg);
        }
    }

    @Override
    public boolean canAccessRemotely() {
        return this.hasUpgrade(Upgrades.REMOTE_ACCESS);
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        //TODO: create container
//        return new ElectrolyteGeneratorContainer(id, playerInventory, this.itemHandler, this.syncData, this.processes);
        return null;
    }
}
