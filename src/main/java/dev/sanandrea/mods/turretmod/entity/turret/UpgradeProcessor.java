/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.turret.IUpgradeProcessor;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeData;
import dev.sanandrea.mods.turretmod.init.TurretModRebirth;
import dev.sanandrea.mods.turretmod.inventory.container.TcuUpgradesContainer;
import dev.sanandrea.mods.turretmod.item.ItemUpgrade;
import dev.sanandrea.mods.turretmod.item.TurretControlUnit;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import dev.sanandrea.mods.turretmod.network.SyncUpgradesPacket;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;
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

    @SuppressWarnings("java:S1452")
    public IUpgradeData<?> getUpgradeData(int slot) {
        return slot >= 0 ? this.upgradeData[slot] : null;
    }

    @Override
    public void syncUpgrade(ResourceLocation id) {
        if( !this.turret.get().level.isClientSide ) {
            syncUpgrades(this.getUpgradeSlot(id));
        }
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

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    private void dropUpgrades(LivingEntity tEntity, int from, int max, Predicate<ItemStack> check) {
        do {
            ItemStack stack = this.upgradeStacks.get(from);
            if( check.test(stack) ) {
                this.dropUpgrade(tEntity, from, stack);
            }
        } while( ++from < max );
    }

    @Override
    public void setChanged() {
        LivingEntity tEntity = this.turret.get();

        this.dropUpgrades(tEntity, 0, SLOTS, s -> {
            if( ItemStackUtils.isValid(s) ) {
                IUpgrade dep = UpgradeRegistry.INSTANCE.get(s).getDependantOn();

                return dep != null && !this.hasUpgrade(dep);
            }

            return false;
        });

        if( !this.hasUpgrade(Upgrades.UPG_STORAGE_III) ) {
            this.dropUpgrades(tEntity, 27, SLOTS, s -> true);
        }

        if( !this.hasUpgrade(Upgrades.UPG_STORAGE_II) ) {
            this.dropUpgrades(tEntity, 18, 27, s -> true);
        }

        if( !this.hasUpgrade(Upgrades.UPG_STORAGE_I) ) {
            this.dropUpgrades(tEntity, 9, 18, s -> true);
        }

        List<Integer> syncSlots = new ArrayList<>();
        this.changedSlots.drainTo(syncSlots);
        if( !syncSlots.isEmpty() ) {
            this.syncUpgrades(syncSlots.stream().mapToInt(i->i).toArray());
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return true;
    }

    private void syncUpgrades(int... slots) {
        LivingEntity te = this.turret.get();
        TurretModRebirth.NETWORK.sendToAllNear(new SyncUpgradesPacket(this.turret, slots),
                                               new PacketDistributor.TargetPoint(te.getX(), te.getY(), te.getZ(), 64.0D, te.level.dimension()));
    }

    @SuppressWarnings("java:S1871")
    private boolean isUpgradeItemApplicable(ItemStack stack) {
        if( stack.getItem() instanceof ItemUpgrade ) {
            IUpgrade upg = ((ItemUpgrade) stack.getItem()).upgrade;

            // check if this turret has the creative upgrade or if there's upgrades present when the inserted upgrade is the creative one.
            // deny insertion if either of those are true
            if( !upg.isCompatibleWithCreativeUpgrade() && this.hasUpgrade(Upgrades.CREATIVE) ) {
                return false;
            } else if(upg.getId().equals(Upgrades.CREATIVE.getId())
                      && this.upgradeStacks.stream().map(UpgradeRegistry.INSTANCE::get).filter(u -> u.isValid() && !u.isCompatibleWithCreativeUpgrade()).findFirst().orElse(null) != null )
            {
                return false;
            }

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

    public NonNullList<ItemStack> extractUpgrades() {
        NonNullList<ItemStack> newList = NonNullList.create();
        newList.addAll(this.upgradeStacks);
        this.clearContent();
        return newList;
    }

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

    @Nonnull
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new TcuUpgradesContainer(id, playerInventory, this.turret, TurretControlUnit.UPGRADES, false, true);
    }
}
