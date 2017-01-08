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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class UpgradeProcessor
        implements IInventory, IUpgradeProcessor
{
    private ItemStack[] upgradeStacks = new ItemStack[36];
    private boolean hasChanged = false;

    private EntityTurret turret;

    public UpgradeProcessor(EntityTurret turret) {
        this.turret = turret;
    }

    @Override
    public void onTick() {
        if( this.hasChanged ) {
            for( int i = 0; i < this.upgradeStacks.length; i++ ) {
                ItemStack invStack = this.upgradeStacks[i];
                if( invStack != null ) {
                    TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                    if( upg != null ) {
                        TurretUpgrade dep = upg.getDependantOn();
                        if( dep != null && !this.hasUpgrade(dep) ) {
                            if( !this.turret.world.isRemote ) {
                                EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                                this.turret.world.spawnEntityInWorld(itm);
                            }
                            upg.onRemove(this.turret);
                            this.upgradeStacks[i] = null;
                        }
                    }
                }
            }

            if( !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_III) ) {
                for( int i = 27; i < this.upgradeStacks.length; i++ ) {
                    ItemStack invStack = this.upgradeStacks[i];
                    if( invStack != null ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntityInWorld(itm);
                        }
                        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        if( upg != null ) {
                            upg.onRemove(this.turret);
                        }
                        this.upgradeStacks[i] = null;
                    }
                }
            }

            if( !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_II) ) {
                for( int i = 18; i < 27; i++ ) {
                    ItemStack invStack = this.upgradeStacks[i];
                    if( invStack != null ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntityInWorld(itm);
                        }
                        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        if( upg != null ) {
                            upg.onRemove(this.turret);
                        }
                        this.upgradeStacks[i] = null;
                    }
                }
            }

            if( !this.hasUpgrade(UpgradeRegistry.UPG_STORAGE_I) ) {
                for( int i = 9; i < 18; i++ ) {
                    ItemStack invStack = this.upgradeStacks[i];
                    if( invStack != null ) {
                        if( !this.turret.world.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.world.spawnEntityInWorld(itm);
                        }
                        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                        if( upg != null ) {
                            upg.onRemove(this.turret);
                        }
                        this.upgradeStacks[i] = null;
                    }
                }
            }
        }
    }

    @Override
    public boolean hasUpgrade(UUID uuid) {
        ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(uuid);

        return ItemStackUtils.isStackInArray(upgItemStack, this.upgradeStacks);
    }

    @Override
    public boolean hasUpgrade(TurretUpgrade upg) {
        ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(upg);

        return ItemStackUtils.isStackInArray(upgItemStack, this.upgradeStacks);
    }

    @Override
    public int getSizeInventory() {
        return upgradeStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < this.upgradeStacks.length ? this.upgradeStacks[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if( this.upgradeStacks[slot] != null ) {
            ItemStack itemstack;

            if( this.upgradeStacks[slot].stackSize <= amount ) {
                if( !this.turret.world.isRemote ) {
                    TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(this.upgradeStacks[slot]);
                    if( upg != null ) {
                        upg.onRemove(this.turret);
                    }
                }
                itemstack = this.upgradeStacks[slot];
                this.upgradeStacks[slot] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.upgradeStacks[slot].splitStack(amount);

                if( this.upgradeStacks[slot].stackSize == 0 ) {
                    this.upgradeStacks[slot] = null;
                }

                this.markDirty();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if( this.upgradeStacks[slot] != null ) {
            ItemStack itemstack = this.upgradeStacks[slot];
            this.upgradeStacks[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if( !this.turret.world.isRemote ) {
            if( this.upgradeStacks[slot] != null && stack == null ) {
                TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(this.upgradeStacks[slot]);
                if( upg != null ) {
                    upg.onRemove(this.turret);
                }
            } else if( this.upgradeStacks[slot] == null && stack != null ) {
                TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
                if( upg != null ) {
                    upg.onApply(this.turret);
                }
            }
        }

        this.upgradeStacks[slot] = stack;

        if( stack != null && stack.stackSize > this.getInventoryStackLimit() ) {
            stack.stackSize = this.getInventoryStackLimit();
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

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

        if( this.upgradeStacks[slot] != null ) {
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
        for( int i = 0; i < this.upgradeStacks.length; i++ ) {
            this.upgradeStacks[i] = null;
        }
    }

    @Override
    public boolean tryApplyUpgrade(ItemStack upgStack) {
        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
        if( upg != null && !this.hasUpgrade(upg) ) {
            TurretUpgrade dep = upg.getDependantOn();
            if( dep == null || this.hasUpgrade(dep) ) {
                for( int i = 0; i < this.upgradeStacks.length; i++ ) {
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

                this.turret.world.spawnEntityInWorld(entityitem);
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

    private void callbackWriteUpgStack(ItemStack upgStack, NBTTagCompound nbt) {
        if( upgStack != null ) {
            TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
            if( upg != null ) {
                upg.onSave(this.turret, nbt);
            }
        }
    }

    private void callbackReadUpgStack(ItemStack upgStack, NBTTagCompound nbt) {
        if( upgStack != null ) {
            TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
            if( upg != null ) {
                upg.onLoad(this.turret, nbt);
            }
        }
    }
}
