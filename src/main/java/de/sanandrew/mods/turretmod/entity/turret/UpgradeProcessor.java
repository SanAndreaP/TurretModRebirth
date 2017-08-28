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
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncUpgradeInst;
import de.sanandrew.mods.turretmod.network.PacketUpdateUgradeSlot;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UpgradeProcessor
        implements IInventory, IUpgradeProcessor
{
    @Nonnull
    private final NonNullList<ItemStack> upgradeStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    private final Map<UUID, IUpgradeInstance> upgInstances = new ConcurrentHashMap<>();
    private final Map<UUID, IUpgradeInstance> upgTickable = new ConcurrentHashMap<>();

    private boolean hasChanged = false;
    private EntityTurret turret;

    public UpgradeProcessor(EntityTurret turret) {
        this.turret = turret;
    }

    @Override
    public void onTick() {
        this.upgTickable.forEach((key, val) -> val.onTick(this.turret));

        if( this.hasChanged ) {
            for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                ItemStack invStack = this.upgradeStacks.get(i);
                if( ItemStackUtils.isValid(invStack) ) {
                    ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                    ITurretUpgrade dep = upg.getDependantOn();
                    if( dep != null && !this.hasUpgrade(dep) ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntity(itm);
                        }
                        upg.onRemove(this.turret);
                        this.upgradeStacks.set(i, ItemStack.EMPTY);
                    }
                }
            }

            if( !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_III) ) {
                for( int i = 27, max = this.upgradeStacks.size(); i < max; i++ ) {
                    ItemStack invStack = this.upgradeStacks.get(i);
                    if( ItemStackUtils.isValid(invStack) ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntity(itm);
                        }
                        ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        upg.onRemove(this.turret);
                        this.upgradeStacks.set(i, ItemStack.EMPTY);
                    }
                }
            }

            if( !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_II) ) {
                for( int i = 18; i < 27; i++ ) {
                    ItemStack invStack = this.upgradeStacks.get(i);
                    if( ItemStackUtils.isValid(invStack) ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntity(itm);
                        }
                        ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        upg.onRemove(this.turret);
                        this.upgradeStacks.set(i, ItemStack.EMPTY);
                    }
                }
            }

            if( !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_I) ) {
                for( int i = 9; i < 18; i++ ) {
                    ItemStack invStack = this.upgradeStacks.get(i);
                    if( ItemStackUtils.isValid(invStack) ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntity(itm);
                        }
                        ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        upg.onRemove(this.turret);
                        this.upgradeStacks.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasUpgrade(UUID id) {
        final ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(id);
        return ItemStackUtils.isStackInList(upgItemStack, this.upgradeStacks);
    }

    @Override
    public boolean hasUpgrade(ITurretUpgrade upg) {
        final ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(upg);
        return ItemStackUtils.isStackInList(upgItemStack, this.upgradeStacks);
    }

    @Override
    public <T extends IUpgradeInstance> T getUpgradeInstance(UUID id) {
        return ReflectionUtils.getCasted(this.upgInstances.get(id));
    }

    @Override
    public void setUpgradeInstance(UUID id, IUpgradeInstance inst) {
        this.upgInstances.put(id, inst);
        if( inst.getClass().getAnnotation(IUpgradeInstance.UpgInstTickable.class) != null ) {
            this.upgTickable.put(id, inst);
        }
    }

    @Override
    public void delUpgradeInstance(UUID id) {
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
        return slot >= 0 && slot < this.upgradeStacks.size() ? this.upgradeStacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack slotStack = this.upgradeStacks.get(slot);
        if( ItemStackUtils.isValid(slotStack) ) {
            ItemStack itemstack;

            if( slotStack.getCount() <= amount ) {
                ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(slotStack);
                upg.onRemove(this.turret);

                itemstack = slotStack;
                this.upgradeStacks.set(slot, ItemStack.EMPTY);
                this.markDirty();
                return itemstack;
            } else {
                itemstack = slotStack.splitStack(amount);

                if( slotStack.getCount() == 0 ) {
                    this.upgradeStacks.set(slot, ItemStack.EMPTY);
                }

                this.markDirty();
                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int slot) {
        if( ItemStackUtils.isValid(this.upgradeStacks.get(slot)) ) {
            ItemStack itemstack = this.upgradeStacks.get(slot);
            this.upgradeStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        ItemStack slotStack = this.upgradeStacks.get(slot);
        if( ItemStackUtils.isValid(slotStack) ) {
            ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(slotStack);
            upg.onRemove(this.turret);
        }

        if( ItemStackUtils.isValid(stack) ) {
            ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
            upg.onApply(this.turret);
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

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if( slot >= 9 && !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_I) ) {
            return false;
        }
        if( slot >= 18 && !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_II) ) {
            return false;
        }
        if( slot >= 27 && !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_III) ) {
            return false;
        }

        if( ItemStackUtils.isValid(this.upgradeStacks.get(slot)) ) {
            return false;
        }

        if( stack.getItem() == ItemRegistry.turret_upgrade ) {
            ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
            if( this.hasUpgrade(UpgradeRegistry.INSTANCE.getUpgradeId(upg)) ) {
                return false;
            }

            ITurretUpgrade dep = upg.getDependantOn();
            return dep == null || this.hasUpgrade(dep);
        }

        return false;

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
        ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
        if( !this.hasUpgrade(upg) ) {
            ITurretUpgrade dep = upg.getDependantOn();
            if( dep == null || this.hasUpgrade(dep) ) {
                for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                    if( this.isItemValidForSlot(i, upgStack) ) {
                        this.setInventorySlotContents(i, upgStack);
                        PacketRegistry.sendToAllAround(new PacketUpdateUgradeSlot(this.turret, i, upgStack), this.turret.dimension, this.turret.posX, this.turret.posY, this.turret.posZ, 64.0D);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void dropUpgrades() {
        for( int i = 0; i < this.getSizeInventory(); i++ ) {
            ItemStack stack = this.removeStackFromSlot(i);

            if( ItemStackUtils.isValid(stack) ) {
                float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

                EntityItem entityitem = new EntityItem(this.turret.world, (this.turret.posX + xOff), (this.turret.posY + yOff), (this.turret.posZ + zOff), stack);

                float motionSpeed = 0.05F;
                entityitem.motionX = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed);
                entityitem.motionY = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F);
                entityitem.motionZ = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed);

                this.turret.world.spawnEntity(entityitem);
            }
        }
    }

    @Override
    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setTag("upgInventory", ItemStackUtils.writeItemStacksToTag(this.upgradeStacks, 1, this::callbackWriteUpgStack));
    }

    @Override
    public void readFromNbt(NBTTagCompound nbt) {
        ItemStackUtils.readItemStacksFromTag(this.upgradeStacks, nbt.getTagList("upgInventory", Constants.NBT.TAG_COMPOUND), this::callbackReadUpgStack);
    }

    @Override
    public void syncUpgrade(UUID id) {
        PacketRegistry.sendToAllAround(new PacketSyncUpgradeInst(this.turret, id), this.turret.world.provider.getDimension(),
                                       this.turret.posX, this.turret.posY, this.turret.posZ, 64.0D);
    }

    private void callbackWriteUpgStack(@Nonnull ItemStack upgStack, NBTTagCompound nbt) {
        ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
        upg.onSave(this.turret, nbt);
    }

    private void callbackReadUpgStack(@Nonnull ItemStack upgStack, NBTTagCompound nbt) {
        ITurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
        upg.onLoad(this.turret, nbt);
    }
}
