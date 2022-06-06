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
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeData;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.ItemUpgrade;
import de.sanandrew.mods.turretmod.item.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.network.SyncUpgradesPacket;
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
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

public final class UpgradeProcessor
        implements IUpgradeProcessor
{
    public static final int SLOTS = 36;
    @Nonnull
    private final NonNullList<ItemStack> upgradeStacks = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final IUpgradeData<?>[]      upgradeData   = new IUpgradeData<?>[SLOTS];

    private final ITurretEntity turret;

    private final BlockingDeque<Integer> changedSlots = new LinkedBlockingDeque<>();

    UpgradeProcessor(ITurretEntity turret) {
        this.turret = turret;
    }

    @Override
    public void onTick() {
        for( IUpgradeData<?> ud : this.upgradeData ) {
            MiscUtils.accept(ud, val -> val.onTick(this.turret));
        }
    }

    private void dropUpgrade(LivingEntity entity, int slot, ItemStack stack) {
        if( ItemStackUtils.isValid(stack) ) {
            if( !entity.level.isClientSide ) {
                ItemEntity itm = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), stack);
                entity.level.addFreshEntity(itm);
            }

            this.setItem(slot, ItemStack.EMPTY);
        }
    }

    private int getUpgradeSlot(ResourceLocation id) {
        final ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getItem(id);
        return IntStream.range(0, SLOTS).filter(slot -> ItemStackUtils.areEqual(upgItemStack, this.upgradeStacks.get(slot), false)).findFirst().orElse(-1);
    }

    @Override
    public boolean hasUpgrade(ResourceLocation id) {
        return this.getUpgradeSlot(id) >= 0;
    }

    @Override
    public boolean hasUpgrade(IUpgrade upg) {
        return this.getUpgradeSlot(upg.getId()) >= 0;
    }

    @Override
    public <T extends IUpgradeData<?>> T getUpgradeData(ResourceLocation id) {
        return ReflectionUtils.getCasted(this.getUpgradeData(this.getUpgradeSlot(id)));
    }

    public IUpgradeData<?> getUpgradeData(int slot) {
        return slot >= 0 ? this.upgradeData[slot] : null;
    }

    @Override
    public void syncUpgrade(ResourceLocation id) {
        LivingEntity te = this.turret.get();

        if( !te.level.isClientSide ) {
            int uSlot = this.getUpgradeSlot(id);

            TurretModRebirth.NETWORK.sendToAllNear(new SyncUpgradesPacket(this.turret, uSlot),
                                                   new PacketDistributor.TargetPoint(te.getX(), te.getY(), te.getZ(), 64.0D, te.level.dimension()));
        }
    }

//    @Override
//    public void setUpgradeData(ResourceLocation id, IUpgradeData<?> inst) {
//        this.upgInstances.put(id, inst);
//        if( inst.getClass().getAnnotation(IUpgradeData.Tickable.class) != null ) {
//            this.upgTickable.put(id, inst);
//        }
//    }

//    @Override
//    public void removeUpgradeData(ResourceLocation id) {
//        this.upgInstances.remove(id);
//        this.upgTickable.remove(id);
//    }

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

    public void terminate(int slot, ItemStack slotStack) {
        ItemStack iStack = MiscUtils.get(slotStack, () -> this.upgradeStacks.get(slot));

        IUpgrade upg = UpgradeRegistry.INSTANCE.get(iStack);
        MiscUtils.accept(this.upgradeData[slot], ud -> ud.save(this.turret, iStack.getOrCreateTagElement("UpgradeData")));
        upg.terminate(this.turret, iStack);
        this.upgradeData[slot] = null;
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        ItemStack slotStack = this.upgradeStacks.get(slot);
        if( ItemStackUtils.isValid(slotStack) ) {
            ItemStack itemstack;

            if( slotStack.getCount() <= amount ) {
                this.terminate(slot, slotStack);

                itemstack = slotStack;
                this.upgradeStacks.set(slot, ItemStack.EMPTY);
            } else {
                itemstack = slotStack.split(amount);

                if( slotStack.getCount() == 0 ) {
                    this.upgradeStacks.set(slot, ItemStack.EMPTY);
                }
            }

            this.changedSlots.offerLast(slot);

            this.setChanged();

            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemstack = this.upgradeStacks.get(slot);
        if( ItemStackUtils.isValid(itemstack) ) {
            this.terminate(slot, itemstack);
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
                this.terminate(slot, slotStack);
            }

            if( ItemStackUtils.isValid(stack) ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.get(stack);
                this.upgradeStacks.set(slot, stack);
                MiscUtils.accept(upg.getData(this.turret), ud -> {
                    this.upgradeData[slot] = ud;
                    MiscUtils.accept(stack.getTagElement("UpgradeData"), tag -> ud.load(this.turret, tag));
                });
                upg.initialize(this.turret, stack);
                if( stack.getCount() > this.getMaxStackSize() ) {
                    stack.setCount(this.getMaxStackSize());
                }
            } else {
                this.upgradeData[slot] = null;
                this.upgradeStacks.set(slot, ItemStack.EMPTY);
            }


            if( !this.turret.get().level.isClientSide ) {
                this.changedSlots.offerLast(slot);

                this.setChanged();
            }
        }
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
        LivingEntity turretL = this.turret.get();

        for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
            ItemStack invStack = this.upgradeStacks.get(i);
            if( ItemStackUtils.isValid(invStack) ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.get(invStack);
                IUpgrade dep = upg.getDependantOn();
                if( dep != null && !this.hasUpgrade(dep) ) {
                    dropUpgrade(turretL, i, invStack);
                }
            }
        }

        if( !this.hasUpgrade(Upgrades.UPG_STORAGE_III) ) {
            for( int i = 27, max = this.upgradeStacks.size(); i < max; i++ ) {
                dropUpgrade(turretL, i, this.upgradeStacks.get(i));
            }
        }

        if( !this.hasUpgrade(Upgrades.UPG_STORAGE_II) ) {
            for( int i = 18; i < 27; i++ ) {
                dropUpgrade(turretL, i, this.upgradeStacks.get(i));
            }
        }

        if( !this.hasUpgrade(Upgrades.UPG_STORAGE_I) ) {
            for( int i = 9; i < 18; i++ ) {
                dropUpgrade(turretL, i, this.upgradeStacks.get(i));
            }
        }

        List<Integer> syncSlots = new ArrayList<>();
        this.changedSlots.drainTo(syncSlots);
        if( !syncSlots.isEmpty() ) {
            LivingEntity te = this.turret.get();
            TurretModRebirth.NETWORK.sendToAllNear(new SyncUpgradesPacket(this.turret, syncSlots.stream().mapToInt(i->i).toArray()),
                                                   new PacketDistributor.TargetPoint(te.getX(), te.getY(), te.getZ(), 64.0D, te.level.dimension()));
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return true;
    }

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
            for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                if( this.isUpgradeItemApplicableForSlot(i, upgStack, false) ) {
                    this.setItem(i, upgStack);

                    return true;
                }
            }
        }

        return false;
    }

    public void dropUpgrades() {
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

//    public NonNullList<ItemStack> extractUpgrades() {
//        NonNullList<ItemStack> newList = NonNullList.create();
//        newList.addAll(this.upgradeStacks);
//        this.upgradeStacks.clear();
//        return newList;
//    }

    @Override
    public void save(CompoundNBT nbt) {
        nbt.put("Upgrades", ItemStackUtils.writeItemStacksToTag(this.upgradeStacks, 1, this::callbackWriteUpgStack));
    }

    @Override
    public void load(CompoundNBT nbt) {
        ItemStackUtils.readItemStacksFromTag(this.upgradeStacks, nbt.getList("Upgrades", Constants.NBT.TAG_COMPOUND), this::callbackReadUpgStack);
    }

    private void callbackWriteUpgStack(@Nonnull ItemStack upgStack, int slot, CompoundNBT nbt) {
        MiscUtils.accept(this.upgradeData[slot], data -> data.save(this.turret, nbt));
    }

    private void callbackReadUpgStack(@Nonnull ItemStack upgStack, int slot, CompoundNBT nbt) {
        IUpgrade upg = UpgradeRegistry.INSTANCE.get(upgStack);
        MiscUtils.accept(upg.getData(this.turret), data -> {
            this.upgradeData[slot] = data;
            data.load(this.turret, nbt);
        });
        upg.initialize(this.turret, upgStack);
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
