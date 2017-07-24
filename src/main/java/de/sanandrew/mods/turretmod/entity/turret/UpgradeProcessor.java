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
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateUgradeSlot;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class UpgradeProcessor
        implements IInventory, IUpgradeProcessor
{
    @Nonnull
    private NonNullList<ItemStack> upgradeStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    private boolean hasChanged = false;

    private EntityTurret turret;

    public UpgradeProcessor(EntityTurret turret) {
        this.turret = turret;
    }

    @Override
    public void onTick() {
        if( this.hasChanged ) {
            for( int i = 0, max = this.upgradeStacks.size(); i < max; i++ ) {
                ItemStack invStack = this.upgradeStacks.get(i);
                if( ItemStackUtils.isValid(invStack) ) {
                    TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                    if( upg != null ) {
                        TurretUpgrade dep = upg.getDependantOn();
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
            }

            if( !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_III) ) {
                for( int i = 27, max = this.upgradeStacks.size(); i < max; i++ ) {
                    ItemStack invStack = this.upgradeStacks.get(i);
                    if( ItemStackUtils.isValid(invStack) ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntity(itm);
                        }
                        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        if( upg != null ) {
                            upg.onRemove(this.turret);
                        }
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
                        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        if( upg != null ) {
                            upg.onRemove(this.turret);
                        }
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
                        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        if( upg != null ) {
                            upg.onRemove(this.turret);
                        }
                        this.upgradeStacks.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasUpgrade(UUID uuid) {
        final ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(uuid);
        return ItemStackUtils.isStackInList(upgItemStack, this.upgradeStacks);
    }

    @Override
    public boolean hasUpgrade(TurretUpgrade upg) {
        final ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(upg);
        return ItemStackUtils.isStackInList(upgItemStack, this.upgradeStacks);
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
                if( !this.turret.world.isRemote ) {
                    TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(slotStack);
                    if( upg != null ) {
                        upg.onRemove(this.turret);
                    }
                }
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
        if( !this.turret.world.isRemote ) {
            ItemStack slotStack = this.upgradeStacks.get(slot);
            if( ItemStackUtils.isValid(slotStack) && ItemStackUtils.isValid(stack) ) {
                TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(slotStack);
                if( upg != null ) {
                    upg.onRemove(this.turret);
                }
            } else if( !ItemStackUtils.isValid(slotStack) && ItemStackUtils.isValid(stack) ) {
                TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
                if( upg != null ) {
                    upg.onApply(this.turret);
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
            TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
            if( this.hasUpgrade(UpgradeRegistry.INSTANCE.getUpgradeUUID(upg)) ) {
                return false;
            }

            if( upg != null ) {
                TurretUpgrade dep = upg.getDependantOn();
                return dep == null || this.hasUpgrade(dep);
            }
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
        this.upgradeStacks.replaceAll(itemStack -> ItemStack.EMPTY);
    }

    @Override
    public boolean tryApplyUpgrade(@Nonnull ItemStack upgStack) {
        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
        if( upg != null && !this.hasUpgrade(upg) ) {
            TurretUpgrade dep = upg.getDependantOn();
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

    private void callbackWriteUpgStack(@Nonnull ItemStack upgStack, NBTTagCompound nbt) {
        if( upgStack != null ) {
            TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
            if( upg != null ) {
                upg.onSave(this.turret, nbt);
            }
        }
    }

    private void callbackReadUpgStack(@Nonnull ItemStack upgStack, NBTTagCompound nbt) {
        if( upgStack != null ) {
            TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
            if( upg != null ) {
                upg.onLoad(this.turret, nbt);
            }
        }
    }
}
