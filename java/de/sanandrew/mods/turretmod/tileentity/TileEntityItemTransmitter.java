/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import de.sanandrew.core.manpack.util.helpers.InventoryUtils;
import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.util.ParticleProxy;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.mutable.MutableInt;

public class TileEntityItemTransmitter
        extends TileEntity
        implements ISidedInventory
{
    private static final int MAX_TICKS_REQUEST_STAY = 100;

    private Quartet<Turret, RequestType, ItemStack, MutableInt> request;
    private ItemStack bufItem;
    private int ticksExisted = 0;

    public boolean hasRequest() {
        return this.request != null;
    }

    public boolean isMyRequest(Turret turret) {
        return this.request != null && this.request.getValue0() == turret;
    }

    public boolean requestItem(Turret turret, RequestType requestType, ItemStack stack) {
        if( this.request == null ) {
            this.request = Quartet.with(turret, requestType, stack, new MutableInt(MAX_TICKS_REQUEST_STAY));
            return true;
        }

        return false;
    }

    @Override
    public void updateEntity() {
        if( !this.worldObj.isRemote ) {
            if( this.ticksExisted % 20 == 0 && this.request != null ) {
                this.request.getValue3().decrement();

                if( this.bufItem != null ) {
                    if( this.request.getValue3().getValue() <= 0 || this.request.getValue0() == null || !this.request.getValue0().getEntity().isEntityAlive() ) {
                        this.updateRequestAndItem(0);
                    } else {
                        switch( this.request.getValue1() ) {
                            case AMMO:
                                int removed = this.request.getValue0().addAmmo(this.bufItem);
                                this.updateRequestAndItem(removed);
                                EntityLiving el = this.request.getValue0().getEntity();
                                TurretMod.particleProxy.spawnParticle(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId,
                                                                      ParticleProxy.ITEM_TRANSMITTER, Triplet.with(el.posX, el.posY, el.posZ)
                                );
                                break;
                        }
                    }
                } else {
                    if( this.request.getValue3().getValue() <= 0 || this.request.getValue0() == null || !this.request.getValue0().getEntity().isEntityAlive() ) {
                        this.request = null;
                    }
                }
            } else if( this.request == null && this.bufItem != null ) {
                this.updateRequestAndItem(0);
            }
        }

        this.ticksExisted++;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if( this.bufItem != null ) {
            nbt.setTag("bufferItem", this.bufItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if( nbt.hasKey("bufferItem") ) {
            this.bufItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("bufferItem"));
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    private void updateRequestAndItem(int removed) {
        if( removed > 0 ) {
            if( this.request == null ) {
                this.updateRequestAndItem(0);
                return;
            }

            this.request.getValue0().getEntity().playSound(TurretMod.MOD_ID + ":collect.ia_get", 1.0F, 1.0F);
            this.bufItem.stackSize -= removed;
            if( this.bufItem.stackSize <= 0 ) {
                this.bufItem = null;
            } else {
                this.request.getValue3().setValue(MAX_TICKS_REQUEST_STAY);
            }
        } else {
            if( this.bufItem != null ) {
                for( ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS ) {
                    TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
                    if( te instanceof IInventory ) {
                        this.bufItem = InventoryUtils.addStackToInventory(this.bufItem, (IInventory) te);
                        if( this.bufItem == null ) {
                            break;
                        }
                    }
                }

                if( this.bufItem != null ) {
                    for( ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS ) {
                        if( dir == ForgeDirection.UP ) {
                            continue;
                        }

                        int blockX = this.xCoord + dir.offsetX;
                        int blockY = this.yCoord + dir.offsetY;
                        int blockZ = this.zCoord + dir.offsetZ;

                        if( !this.worldObj.getBlock(blockX, blockY, blockZ).isNormalCube(this.worldObj, blockX, blockY, blockZ) ) {
                            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, blockX + 0.5D, blockY + 0.5D, blockZ + 0.5D, this.bufItem));
                            this.bufItem = null;
                            break;
                        }
                    }

                    if( this.bufItem != null ) {
                        this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord + 0.5D, this.yCoord + 1.5D, this.zCoord + 0.5D, this.bufItem));
                        this.bufItem = null;
                    }
                }
            }

            this.request = null;
        }
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.bufItem;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return this.bufItem;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if( stack != null && this.request != null ) {
            this.bufItem = stack;
            if( stack.stackSize > this.getInventoryStackLimit() ) {
                stack.stackSize = this.getInventoryStackLimit();
            }
        }
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return this.request != null ? this.request.getValue2().stackSize : 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return this.request != null && ItemUtils.areStacksEqual(stack, this.request.getValue2(), true) && this.request.getValue2().stackSize > 0;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return dir != ForgeDirection.UP ? new int[]{0} : new int[0];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return ForgeDirection.getOrientation(side) != ForgeDirection.UP && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    public enum RequestType
    {
        AMMO, HEALTH
    }
}
