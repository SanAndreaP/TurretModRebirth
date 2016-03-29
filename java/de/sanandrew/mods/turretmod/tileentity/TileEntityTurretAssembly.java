/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import codechicken.lib.inventory.InventoryRange;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class TileEntityTurretAssembly
        extends TileEntity
        implements ISidedInventory, IEnergyHandler, TileClientSync
{
    public static final int MAX_FLUX_STORAGE = 75_000;
    public static final int MAX_FLUX_INSERT = 500;
    private static final int[] SLOTS_INSERT = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
    private static final int[] SLOTS_EXTRACT =  new int[] {0};

    public float robotArmX = 2.0F;
    public float robotArmY = -9.0F;
    public float prevRobotArmX;
    public float prevRobotArmY;
    public float robotMotionX = 0.0F;
    public float robotMotionY = 0.0F;
    public float robotEndX;
    public float robotEndY;
    public boolean isItemRendered = false;
    public Triplet<Float, Float, Float> spawnParticle = null;

    private boolean prevActive;
    public boolean active;

    private int fluxAmount;
    private ItemStack[] assemblyStacks = new ItemStack[19];

    private Map<UUID, Integer> craftingQueue = new HashMap<>();
    private List<UUID> craftingOrder = new ArrayList<>();
    public int ticksCrafted = 0;
    public int maxTicksCrafted = 0;
    public int fluxConsumption = 0;
    public ItemStack currentlyCraftingItem;

    private boolean doSync = false;

    private long ticksExisted = 0L;

    public TileEntityTurretAssembly() {
        this.isItemRendered = false;
    }

    public TileEntityTurretAssembly(boolean itemRendered) {
        this.isItemRendered = itemRendered;
    }

    public void addRecipeToQueue(UUID recipe, int count) {
        if( this.craftingQueue.containsKey(recipe) && this.craftingOrder.contains(recipe) ) {
            this.craftingQueue.put(recipe, this.craftingQueue.get(recipe) + count);
        } else {
            this.craftingQueue.put(recipe, count);
            this.craftingOrder.add(recipe);
        }
    }

    private void initCrafting() {
        if( this.craftingOrder.size() > 0 && (this.assemblyStacks[0] == null || this.assemblyStacks[0].stackSize < this.assemblyStacks[0].getMaxStackSize()) ) {
            UUID currCrf = this.craftingOrder.get(0);
            this.currentlyCraftingItem = TurretAssemblyRecipes.INSTANCE.getRecipeResult(currCrf);
            if( this.currentlyCraftingItem != null && TmrUtils.canStack(this.assemblyStacks[0], this.currentlyCraftingItem, true)
                && TurretAssemblyRecipes.INSTANCE.checkAndConsumeResources(this, currCrf) )
            {
                TurretAssemblyRecipes.RecipeEntry currentlyCrafted = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(currCrf);
                this.maxTicksCrafted = currentlyCrafted.ticksProcessing;
                this.fluxConsumption = currentlyCrafted.fluxPerTick;
                this.ticksCrafted = 0;
                this.active = true;
                this.doSync = true;
            } else {
                this.craftingOrder.remove(0);
                this.craftingOrder.add(currCrf);
            }
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if( !this.worldObj.isRemote ) {
            if( this.active ) {
                if( this.fluxAmount >= this.fluxConsumption ) {
                    this.fluxAmount -= this.fluxConsumption;
                    if( ++this.ticksCrafted >= this.maxTicksCrafted ) {
                        this.ticksCrafted = 0;
                        this.currentlyCraftingItem = null;
                        this.maxTicksCrafted = 0;
                        this.active = false;

                        ItemStack stack = TurretAssemblyRecipes.INSTANCE.getRecipeResult(this.craftingOrder.get(0));

                        if( this.assemblyStacks[0] != null ) {
                            this.assemblyStacks[0].stackSize += stack.stackSize;
                        } else {
                            this.assemblyStacks[0] = stack.copy();
                        }

                        this.markDirty();

                        UUID queue = this.craftingOrder.get(0);
                        int queueCnt = this.craftingQueue.get(queue) - 1;
                        if( queueCnt < 1 ) {
                            this.craftingOrder.remove(0);
                            this.craftingQueue.remove(queue);
                        } else {
                            this.craftingQueue.put(queue, queueCnt);
                        }
                    }

                    this.doSync = true;
                }
            } else {
                this.initCrafting();
            }

            if( this.doSync ) {
                PacketSyncTileEntity.sync(this);
                this.doSync = false;
            }
        } else {
            this.processRobotArm();
        }

        this.prevActive = this.active;
        this.ticksExisted++;
    }

    private void processRobotArm() {
        this.prevRobotArmX = this.robotArmX;
        this.prevRobotArmY = this.robotArmY;

        this.robotArmX += this.robotMotionX;
        this.robotArmY += this.robotMotionY;

        if( this.robotArmX > this.robotEndX && this.robotMotionX > 0.0F ) {
            this.robotArmX = this.robotEndX;
            this.robotMotionX = 0.0F;
        } else if( this.robotArmX < this.robotEndX && this.robotMotionX < 0.0F ) {
            this.robotArmX = this.robotEndX;
            this.robotMotionX = 0.0F;
        }

        if( this.robotArmY > this.robotEndY && this.robotMotionY > 0.0F ) {
            this.robotArmY = this.robotEndY;
            this.robotMotionY = 0.0F;
        } else if( this.robotArmY < this.robotEndY && this.robotMotionY < 0.0F ) {
            this.robotArmY = this.robotEndY;
            this.robotMotionY = 0.0F;
        }

        if( this.active && (!this.prevActive || this.ticksExisted % 20 == 0) ) {
            this.animateRobotArmRng();
        } else if( this.prevActive && !this.active ) {
            this.animateRobotArmReset();
            this.spawnParticle = null;
        }

        if( this.active && this.spawnParticle != null ) {
            TurretModRebirth.proxy.spawnParticle(EnumParticle.ASSEMBLY_SPARK, spawnParticle.getValue0(), spawnParticle.getValue1() + 0.05F, spawnParticle.getValue2(), null);
            this.spawnParticle = null;
        }
    }

    private void animateRobotArmRng() {
        float endX = 4.0F + TmrUtils.RNG.nextFloat() * 6.0F;
        float endY = -3.5F + TmrUtils.RNG.nextFloat() * -6.0F;

        this.robotMotionX = (0.1F + TmrUtils.RNG.nextFloat() * 0.1F) * (endX > this.robotArmX ? 1.0F : -1.0F);
        this.robotMotionY = (0.1F + TmrUtils.RNG.nextFloat() * 0.1F) * (endY > this.robotArmY ? 1.0F : -1.0F);
        this.robotEndX = endX;
        this.robotEndY = endY;
    }

    private void animateRobotArmReset() {
        float endX = 2.0F;
        float endY = -9.0F;

        this.robotMotionX = (0.1F + TmrUtils.RNG.nextFloat() * 0.1F) * (endX > this.robotArmX ? 1.0F : -1.0F);
        this.robotMotionY = (0.1F + TmrUtils.RNG.nextFloat() * 0.1F) * (endY > this.robotArmY ? 1.0F : -1.0F);
        this.robotEndX = endX;
        this.robotEndY = endY;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        this.writeNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.readNBT(nbt);

        this.doSync = true;
    }

    private void writeNBT(NBTTagCompound nbt) {
        nbt.setBoolean("active", this.active);
        nbt.setInteger("flux", this.fluxAmount);
        nbt.setTag("inventory", TmrUtils.writeItemStacksToTag(this.assemblyStacks, 64));

        NBTTagList crfQueue = new NBTTagList();
        for( UUID queue : this.craftingOrder ) {
            NBTTagCompound nbtQueue = new NBTTagCompound();
            nbtQueue.setString("queueUUID", queue.toString());
            nbtQueue.setInteger("queueCount", this.craftingQueue.get(queue));
            crfQueue.appendTag(nbtQueue);
        }
        nbt.setTag("crfQueue", crfQueue);

        nbt.setInteger("ticksCrafted", this.ticksCrafted);
        nbt.setInteger("maxTicksCrafted", this.maxTicksCrafted);
        nbt.setInteger("fluxConsumption", this.fluxConsumption);
        if( this.currentlyCraftingItem != null ) {
            ItemStackUtils.writeStackToTag(this.currentlyCraftingItem, nbt, "currentlyCraftingItem");
        }
    }

    private void readNBT(NBTTagCompound nbt) {
        this.active = nbt.getBoolean("active");
        this.fluxAmount = nbt.getInteger("flux");
        TmrUtils.readItemStacksFromTag(this.assemblyStacks, nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND));

        NBTTagList crfQueue = nbt.getTagList("crfQueue", Constants.NBT.TAG_COMPOUND);
        for( int i = 0; i < crfQueue.tagCount(); i++ ) {
            NBTTagCompound nbtQueue = crfQueue.getCompoundTagAt(i);
            UUID queue = UUID.fromString(nbtQueue.getString("queueUUID"));
            int count = nbtQueue.getInteger("queueCount");
            this.craftingOrder.add(queue);
            this.craftingQueue.put(queue, count);
        }

        this.ticksCrafted = nbt.getInteger("ticksCrafted");
        this.maxTicksCrafted = nbt.getInteger("maxTicksCrafted");
        this.fluxConsumption = nbt.getInteger("fluxConsumption");
        if( nbt.hasKey("currentlyCraftingItem") ) {
            this.currentlyCraftingItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("currentlyCraftingItem"));
        }
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return side == ForgeDirection.DOWN.ordinal() ? SLOTS_EXTRACT : SLOTS_INSERT;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack) && side != ForgeDirection.DOWN.ordinal() && side != ForgeDirection.UP.ordinal();
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == 0 && side == ForgeDirection.DOWN.ordinal();
    }

    @Override
    public int getSizeInventory() {
        return this.assemblyStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.assemblyStacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int size) {
        if( this.assemblyStacks[slot] != null ) {
            ItemStack itemstack;

            if( this.assemblyStacks[slot].stackSize <= size ) {
                itemstack = this.assemblyStacks[slot];
                this.assemblyStacks[slot] = null;
                return itemstack;
            } else {
                itemstack = this.assemblyStacks[slot].splitStack(size);

                if( this.assemblyStacks[slot].stackSize == 0 ) {
                    this.assemblyStacks[slot] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if( this.assemblyStacks[slot] != null ) {
            ItemStack itemstack = this.assemblyStacks[slot];
            this.assemblyStacks[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.assemblyStacks[slot] = stack;

        if( stack != null && stack.stackSize > this.getInventoryStackLimit() ) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() { }

    @Override
    public void closeInventory() { }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot != 0;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergyStored(from) - this.fluxAmount, Math.min(MAX_FLUX_INSERT, maxReceive));

        if( !simulate ) {
            this.fluxAmount += energyReceived;
            this.doSync = true;
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return this.fluxAmount;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return MAX_FLUX_STORAGE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from != ForgeDirection.UP;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.fluxAmount);
        buf.writeBoolean(this.active);
        buf.writeInt(this.ticksCrafted);
        buf.writeInt(this.maxTicksCrafted);
        ByteBufUtils.writeItemStack(buf, this.currentlyCraftingItem);
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks[0]);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fluxAmount = buf.readInt();
        this.active = buf.readBoolean();
        this.ticksCrafted = buf.readInt();
        this.maxTicksCrafted = buf.readInt();
        this.currentlyCraftingItem = ByteBufUtils.readItemStack(buf);
        this.assemblyStacks[0] = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public TileEntity getTile() {
        return this;
    }
}
