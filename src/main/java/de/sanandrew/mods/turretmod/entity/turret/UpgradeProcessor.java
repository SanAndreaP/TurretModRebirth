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
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import de.sanandrew.mods.turretmod.item.ItemUpgrade;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateUgradeSlot;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class UpgradeProcessor
        implements IUpgradeProcessor
{
    @Nonnull
    private final NonNullList<ItemStack> upgradeStacks = NonNullList.withSize(36, ItemStackUtils.getEmpty());
    private final Map<ResourceLocation, IUpgradeInstance<?>> upgInstances = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, IUpgradeInstance<?>> upgTickable = new ConcurrentHashMap<>();

    private boolean hasChanged = false;
    private final ITurretInst turret;

    private Deque<IUpgrade> firstSynchronize = new ConcurrentLinkedDeque<>();

    UpgradeProcessor(ITurretInst turret) {
        this.turret = turret;
    }

    @Override
    public void onTick() {
        this.upgTickable.forEach((key, val) -> val.onTick(this.turret));

        if( this.hasChanged ) {
            EntityLiving turretL = this.turret.get();

            for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                ItemStack invStack = this.upgradeStacks.get(i);
                if( ItemStackUtils.isValid(invStack) ) {
                    IUpgrade upg = UpgradeRegistry.INSTANCE.getObject(invStack);
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
            UpgradeRegistry.INSTANCE.syncWithClients(this.turret, this.firstSynchronize.pollFirst().getId());
        }
    }

    private void dropUpgrade(EntityLiving entity, int slot, ItemStack stack, IUpgrade upg) {
        if( ItemStackUtils.isValid(stack) ) {
            if( !entity.world.isRemote ) {
                EntityItem itm = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack);
                entity.world.spawnEntity(itm);
            }
            upg = upg != null ? upg : UpgradeRegistry.INSTANCE.getObject(stack);
            upg.terminate(this.turret, stack);
            this.upgradeStacks.set(slot, ItemStackUtils.getEmpty());
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
    public int getSizeInventory() {
        return upgradeStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.upgradeStacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < this.upgradeStacks.size() ? this.upgradeStacks.get(slot) : ItemStackUtils.getEmpty();
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack slotStack = this.upgradeStacks.get(slot);
        if( ItemStackUtils.isValid(slotStack) ) {
            ItemStack itemstack;

            if( slotStack.getCount() <= amount ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.getObject(slotStack);
                upg.terminate(this.turret, slotStack);

                itemstack = slotStack;
                this.upgradeStacks.set(slot, ItemStackUtils.getEmpty());
            } else {
                itemstack = slotStack.splitStack(amount);

                if( slotStack.getCount() == 0 ) {
                    this.upgradeStacks.set(slot, ItemStackUtils.getEmpty());
                }
            }

            this.markDirty();

            return itemstack;
        } else {
            return ItemStackUtils.getEmpty();
        }
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int slot) {
        if( ItemStackUtils.isValid(this.upgradeStacks.get(slot)) ) {
            ItemStack itemstack = this.upgradeStacks.get(slot);
            this.upgradeStacks.set(slot, ItemStackUtils.getEmpty());
            return itemstack;
        } else {
            return ItemStackUtils.getEmpty();
        }
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        ItemStack slotStack = this.upgradeStacks.get(slot);
        if( !ItemStackUtils.areEqual(slotStack, stack) ) {
            if( ItemStackUtils.isValid(slotStack) ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.getObject(slotStack);
                upg.terminate(this.turret, slotStack);
            }

            if( ItemStackUtils.isValid(stack) ) {
                IUpgrade upg = UpgradeRegistry.INSTANCE.getObject(stack);
                upg.initialize(this.turret, stack);
                if( upg.getClass().getAnnotation(IUpgrade.InitSynchronizeClient.class) != null ) {
                    this.firstSynchronize.offerLast(upg);
                }
            }
        }

        this.upgradeStacks.set(slot, stack);

        if( ItemStackUtils.isValid(stack) && stack.getCount() > this.getInventoryStackLimit() ) {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
    }

    @Override
    public String getName() {
        return "Upgrades";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {
        this.hasChanged = true;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) { return true; }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    private boolean isUpgradeItemApplicable(ItemStack stack) {
        if( stack.getItem() instanceof ItemUpgrade ) {
            IUpgrade upg = ((ItemUpgrade) stack.getItem()).upgrade;
            if( this.hasUpgrade(upg) ) {
                return false;
            }
            if( !UpgradeRegistry.INSTANCE.isApplicable(upg, this.turret.getTurret()) ) {
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
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return isUpgradeItemApplicableForSlot(slot, stack, true);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) { }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.upgradeStacks.clear();
    }

    @Override
    public boolean tryApplyUpgrade(@Nonnull ItemStack upgStack) {
        if( this.isUpgradeItemApplicable(upgStack) ) {
            EntityLiving turretL = this.turret.get();
            for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                if( this.isUpgradeItemApplicableForSlot(i, upgStack, false) ) {
                    this.setInventorySlotContents(i, upgStack);
                    PacketRegistry.sendToAllAround(new PacketUpdateUgradeSlot(this.turret, i, upgStack), turretL.dimension, turretL.posX, turretL.posY, turretL.posZ, 64.0D);
                    return true;
                }
            }
        }

        return false;
    }

    void dropUpgrades() {
        EntityLiving turretL = this.turret.get();

        for( int i = 0; i < this.getSizeInventory(); i++ ) {
            ItemStack stack = this.removeStackFromSlot(i);

            if( ItemStackUtils.isValid(stack) ) {
                float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

                EntityItem entityitem = new EntityItem(turretL.world, (turretL.posX + xOff), (turretL.posY + yOff), (turretL.posZ + zOff), stack);

                float motionSpeed = 0.05F;
                entityitem.motionX = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed);
                entityitem.motionY = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F);
                entityitem.motionZ = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed);

                turretL.world.spawnEntity(entityitem);
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
    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setTag("upgInventory", ItemStackUtils.writeItemStacksToTag(this.upgradeStacks, 1, this::callbackWriteUpgStack));
    }

    @Override
    public void readFromNbt(NBTTagCompound nbt) {
        if( nbt != null ) {
            ItemStackUtils.readItemStacksFromTag(this.upgradeStacks, nbt.getTagList("upgInventory", Constants.NBT.TAG_COMPOUND), this::callbackReadUpgStack);
        }
    }

    private void callbackWriteUpgStack(@Nonnull ItemStack upgStack, NBTTagCompound nbt) {
        IUpgrade upg = UpgradeRegistry.INSTANCE.getObject(upgStack);
        upg.onSave(this.turret, nbt);
    }

    private void callbackReadUpgStack(@Nonnull ItemStack upgStack, NBTTagCompound nbt) {
        IUpgrade upg = UpgradeRegistry.INSTANCE.getObject(upgStack);
        upg.initialize(this.turret, upgStack);
        upg.onLoad(this.turret, nbt);
        if( upg.getClass().getAnnotation(IUpgrade.InitSynchronizeClient.class) != null ) {
            this.firstSynchronize.offerLast(upg);
        }
    }
}
