/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateUgradeSlot;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class UpgradeProcessor
        implements IInventory
{
    private ItemStack[] upgradeStacks = new ItemStack[36];
    private boolean hasChanged = false;

    private EntityTurret turret;

    public UpgradeProcessor(EntityTurret turret) {
        this.turret = turret;
    }

    public void onTick() {
        if( this.hasChanged ) {
            for( int i = 0; i < this.upgradeStacks.length; i++ ) {
                ItemStack invStack = this.upgradeStacks[i];
                if( invStack != null ) {
                    TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(invStack);
                    if( upg != null ) {
                        TurretUpgrade dep = upg.getDependantOn();
                        if( dep != null && !this.hasUpgrade(dep) ) {
                            if( !this.turret.worldObj.isRemote ) {
                                EntityItem itm = new EntityItem(this.turret.worldObj, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                                this.turret.worldObj.spawnEntityInWorld(itm);
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
                        if( !this.turret.worldObj.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.worldObj, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.worldObj.spawnEntityInWorld(itm);
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
                        if( !this.turret.worldObj.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.worldObj, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.worldObj.spawnEntityInWorld(itm);
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
                        if( !this.turret.worldObj.isRemote ) {
                            EntityItem itm = new EntityItem(this.turret.worldObj, this.turret.posX, this.turret.posY, this.turret.posZ, invStack);
                            this.turret.worldObj.spawnEntityInWorld(itm);
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

    public boolean hasUpgrade(UUID uuid) {
        ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(uuid);

        return TmrUtils.isStackInArray(upgItemStack, this.upgradeStacks);
    }

    public boolean hasUpgrade(TurretUpgrade upg) {
        ItemStack upgItemStack = UpgradeRegistry.INSTANCE.getUpgradeItem(upg);

        return TmrUtils.isStackInArray(upgItemStack, this.upgradeStacks);
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
                if( !this.turret.worldObj.isRemote ) {
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
    public ItemStack getStackInSlotOnClosing(int slot) {
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
        if( !this.turret.worldObj.isRemote ) {
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
    public String getInventoryName() {
        return "Upgrades";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
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
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
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

        if( stack != null ) {
            if( stack.getItem() == ItemRegistry.turretUpgrade ) {
                TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
                if( this.hasUpgrade(UpgradeRegistry.INSTANCE.getUpgradeUUID(upg)) ) {
                    return false;
                }

                if( upg != null ) {
                    TurretUpgrade dep = upg.getDependantOn();
                    return dep == null || this.hasUpgrade(dep);
                }
            }
        } else {
            return true;
        }

        return false;

    }

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

    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setTag("upgInventory", TmrUtils.writeItemStacksToTag(this.upgradeStacks, 1, this, "callbackWriteUpgStack"));
    }

    public void readFromNbt(NBTTagCompound nbt) {
        TmrUtils.readItemStacksFromTag(this.upgradeStacks, nbt.getTagList("upgInventory", Constants.NBT.TAG_COMPOUND), this, "callbackReadUpgStack");
    }

    public void callbackWriteUpgStack(ItemStack upgStack, NBTTagCompound nbt) {
        if( upgStack != null ) {
            TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
            if( upg != null ) {
                upg.onSave(this.turret, nbt);
            }
        }
    }

    public void callbackReadUpgStack(ItemStack upgStack, NBTTagCompound nbt) {
        if( upgStack != null ) {
            TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(upgStack);
            if( upg != null ) {
                upg.onLoad(this.turret, nbt);
            }
        }
    }
}
