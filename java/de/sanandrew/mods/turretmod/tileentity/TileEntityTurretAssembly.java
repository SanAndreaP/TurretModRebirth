/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemAssemblyFilter;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import java.util.UUID;

public class TileEntityTurretAssembly
        extends TileEntityLockable
        implements ISidedInventory, IEnergyHandler, IEnergyReceiver, TileClientSync, ITickable
{
    public static final int MAX_FLUX_STORAGE = 75_000;
    public static final int MAX_FLUX_INSERT = 500;
    private static final int[] SLOTS_INSERT = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
    private static final int[] SLOTS_EXTRACT =  new int[] {0};

    public float robotArmX = 2.0F;
    public float robotArmY = -9.0F;
    public float prevRobotArmX;
    public float prevRobotArmY;
    public float robotMotionX = 0.0F;
    public float robotMotionY = 0.0F;
    public float robotEndX;
    public float robotEndY;
    public Tuple spawnParticle = null;

    private boolean prevActive;
    private boolean automate;
    public boolean isActive;
    private boolean isActiveClient;

    private int fluxAmount;
    private ItemStack[] assemblyStacks = new ItemStack[23];

    public boolean syncStacks = true;

    public Tuple currCrafting;
    private int ticksCrafted = 0;
    private int maxTicksCrafted = 0;
    private int fluxConsumption = 0;

    private boolean doSync = false;

    private long ticksExisted = 0L;
    private String customName;

    public TileEntityTurretAssembly() { }

    public void beginCrafting(UUID recipe, int count) {
        if( this.currCrafting != null && recipe.equals(this.currCrafting.getValue(0)) && !this.automate ) {
            ItemStack result = TurretAssemblyRecipes.INSTANCE.getRecipeResult(recipe);
            ItemStack currCrfStack = this.currCrafting.getValue(1);
            if( currCrfStack.stackSize + count < 1 ) {
                this.cancelCrafting();
            } else if( result != null && currCrfStack.stackSize + count * result.stackSize <= currCrfStack.getMaxStackSize() ) {
                currCrfStack.stackSize += count;
                this.doSync = true;
            } else {
                currCrfStack.stackSize = currCrfStack.getMaxStackSize();
                this.doSync = true;
            }
        } else if( this.currCrafting == null ) {
            ItemStack stackRes = TurretAssemblyRecipes.INSTANCE.getRecipeResult(recipe);
            TurretAssemblyRecipes.RecipeEntry entry = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(recipe);
            if( entry != null && stackRes != null ) {
                stackRes = stackRes.copy();
                stackRes.stackSize = this.automate ? 1 : count;
                this.currCrafting = new Tuple(recipe, stackRes);
                this.maxTicksCrafted = entry.ticksProcessing;
                this.doSync = true;
            }
        }
    }

    public void cancelCrafting() {
        this.currCrafting = null;
        this.ticksCrafted = 0;
        this.fluxConsumption = 0;
        this.maxTicksCrafted = 0;
        this.isActive = false;
        this.isActiveClient = false;
        this.doSync = true;
    }

    private void initCrafting() {
        if( this.currCrafting != null && (this.assemblyStacks[0] == null || this.assemblyStacks[0].stackSize < this.assemblyStacks[0].getMaxStackSize()) ) {
            UUID currCrfUUID = this.currCrafting.getValue(0);
            ItemStack addStacks = this.currCrafting.<ItemStack>getValue(1).copy();
            ItemStack recipe = TurretAssemblyRecipes.INSTANCE.getRecipeResult(currCrfUUID);
            if( recipe != null ) {
                addStacks.stackSize = recipe.stackSize;
                if( ItemStackUtils.canStack(this.assemblyStacks[0], addStacks, true) && TurretAssemblyRecipes.INSTANCE.checkAndConsumeResources(this, currCrfUUID) ) {
                    TurretAssemblyRecipes.RecipeEntry currentlyCrafted = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(currCrfUUID);
                    if( currentlyCrafted != null ) {
                        this.maxTicksCrafted = currentlyCrafted.ticksProcessing;
                        this.fluxConsumption = MathHelper.ceiling_float_int(currentlyCrafted.fluxPerTick * (this.hasSpeedUpgrade() ? 1.1F : 1.0F));
                        this.ticksCrafted = 0;
                        this.isActive = true;
                        this.doSync = true;
                    }
                }
            } else {
                this.cancelCrafting();
            }
        }
    }

    public boolean hasAutoUpgrade() {
        return ItemStackUtils.isValid(this.assemblyStacks[1]) && this.assemblyStacks[1].getItem() == ItemRegistry.asbAuto;
    }

    public boolean hasSpeedUpgrade() {
        return ItemStackUtils.isValid(this.assemblyStacks[2]) && this.assemblyStacks[2].getItem() == ItemRegistry.asbSpeed;
    }

    public boolean hasFilterUpgrade() {
        return ItemStackUtils.isValid(this.assemblyStacks[3]) && this.assemblyStacks[3].getItem() == ItemRegistry.asbFilter;
    }

    public ItemStack[] getFilterStacks() {
        if( this.hasFilterUpgrade() ) {
            return ItemRegistry.asbFilter.getFilterStacks(this.assemblyStacks[3], false);
        } else {
            return ItemAssemblyFilter.EMPTY_INV;
        }
    }

    @Override
    public void update() {
        if( !this.worldObj.isRemote ) {
            if( this.automate && !this.hasAutoUpgrade() ) {
                this.automate = false;
                this.cancelCrafting();
            }

            int maxLoop = this.hasSpeedUpgrade() ? 4 : 1;
            boolean markDirty = false;

            for( int i = 0; i < maxLoop; i++ ) {
                this.isActiveClient = this.isActive;
                if( this.isActive && this.currCrafting != null ) {
                    if( this.fluxAmount >= this.fluxConsumption && this.worldObj.isBlockIndirectlyGettingPowered(this.pos) == 0 ) {
                        this.fluxAmount -= this.fluxConsumption;
                        if( ++this.ticksCrafted >= this.maxTicksCrafted ) {
                            ItemStack stack = TurretAssemblyRecipes.INSTANCE.getRecipeResult(this.currCrafting.getValue(0));
                            if( stack == null ) {
                                this.cancelCrafting();
                                return;
                            }

                            if( this.assemblyStacks[0] != null ) {
                                this.assemblyStacks[0].stackSize += stack.stackSize;
                            } else {
                                this.assemblyStacks[0] = stack.copy();
                            }

                            if( this.assemblyStacks[0].stackSize + stack.stackSize > this.assemblyStacks[0].getMaxStackSize() ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }
                            if( !TurretAssemblyRecipes.INSTANCE.checkAndConsumeResources(this, this.currCrafting.getValue(0)) ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }

                            if( this.currCrafting.<ItemStack>getValue(1).stackSize > 1 ) {
                                if( !this.automate ) {
                                    this.currCrafting.<ItemStack>getValue(1).stackSize--;
                                }
                            } else if( !this.automate ) {
                                this.cancelCrafting();
                            }
                            this.ticksCrafted = 0;

                            markDirty = true;
                        }

                        this.doSync = true;
                    } else {
                        this.isActiveClient = false;
                        this.doSync = true;
                    }
                } else {
                    this.initCrafting();
                    this.isActiveClient = false;
                }
            }

            if( markDirty ) {
                this.markDirty();
            }

            if( this.doSync ) {
                PacketSyncTileEntity.sync(this);
                this.doSync = false;
            }
        } else {
            this.processRobotArm();
        }

        this.prevActive = this.isActive;
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

        if( this.isActiveClient && (!this.prevActive || this.ticksExisted % 20 == 0) ) {
            this.animateRobotArmRng();
        } else if( this.prevActive && !this.isActiveClient ) {
            this.animateRobotArmReset();
            this.spawnParticle = null;
        }

        if( this.isActiveClient && this.spawnParticle != null ) {
            TurretModRebirth.proxy.spawnParticle(EnumParticle.ASSEMBLY_SPARK, spawnParticle.getValue(0), spawnParticle.<Double>getValue(1) + 0.05D, spawnParticle.getValue(2), null);
            this.spawnParticle = null;
        }
    }

    private void animateRobotArmRng() {
        float endX = 4.0F + MiscUtils.RNG.randomFloat() * 6.0F;
        float endY = -3.5F + MiscUtils.RNG.randomFloat() * -6.0F;

        this.robotMotionX = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endX > this.robotArmX ? 1.0F : -1.0F);
        this.robotMotionY = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endY > this.robotArmY ? 1.0F : -1.0F);
        this.robotEndX = endX;
        this.robotEndY = endY;
    }

    private void animateRobotArmReset() {
        float endX = 2.0F;
        float endY = -9.0F;

        this.robotMotionX = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endX > this.robotArmX ? 1.0F : -1.0F);
        this.robotMotionY = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endY > this.robotArmY ? 1.0F : -1.0F);
        this.robotEndX = endX;
        this.robotEndY = endY;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.writeNBT(new NBTTagCompound()));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        this.writeNBT(nbt);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.readNBT(nbt);

        this.doSync = true;
    }

    private NBTTagCompound writeNBT(NBTTagCompound nbt) {
        nbt.setBoolean("isActive", this.isActive);
        nbt.setInteger("flux", this.fluxAmount);
        nbt.setTag("inventory", ItemStackUtils.writeItemStacksToTag(this.assemblyStacks, 64));

        if( this.currCrafting != null ) {
            nbt.setString("craftingUUID", this.currCrafting.getValue(0).toString());
            ItemStackUtils.writeStackToTag(this.currCrafting.getValue(1), nbt, "craftingStack");
        }

        nbt.setInteger("ticksCrafted", this.ticksCrafted);
        nbt.setInteger("maxTicksCrafted", this.maxTicksCrafted);
        nbt.setInteger("fluxConsumption", this.fluxConsumption);
        nbt.setBoolean("automate", this.automate);

        if( this.hasCustomName() ) {
            nbt.setString("customName", this.customName);
        }

        return nbt;
    }

    private void readNBT(NBTTagCompound nbt) {
        this.isActive = nbt.getBoolean("isActive");
        this.fluxAmount = nbt.getInteger("flux");
        ItemStackUtils.readItemStacksFromTag(this.assemblyStacks, nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND));

        if( nbt.hasKey("craftingUUID") && nbt.hasKey("craftingStack") ) {
            this.currCrafting = new Tuple(UUID.fromString(nbt.getString("craftingUUID")), ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("craftingStack")));
        }

        this.ticksCrafted = nbt.getInteger("ticksCrafted");
        this.maxTicksCrafted = nbt.getInteger("maxTicksCrafted");
        this.fluxConsumption = nbt.getInteger("fluxConsumption");
        this.automate = nbt.getBoolean("automate");

        if( nbt.hasKey("customName") ) {
            this.customName = nbt.getString("customName");
        }
    }

    private boolean isStackAcceptable(ItemStack stack, int insrtSlot) {
        if( this.hasFilterUpgrade() ) {
            ItemStack[] filter = this.getFilterStacks();
            if( ItemStackUtils.isStackInArray(stack, filter) ) {
                return ItemStackUtils.areEqual(stack, filter[insrtSlot]);
            } else {
                return !ItemStackUtils.isValid(filter[insrtSlot]);
            }
        }

        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_EXTRACT : (side == EnumFacing.UP ? new int[0] : SLOTS_INSERT);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return this.isItemValidForSlot(slot, stack) && side != EnumFacing.DOWN && side != EnumFacing.UP;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return slot == 0 && side == EnumFacing.DOWN;
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
        if( !this.hasAutoUpgrade() ) {
            this.automate = false;
        }

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
    public ItemStack removeStackFromSlot(int slot) {
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
        if( !this.hasAutoUpgrade() ) {
            this.automate = false;
        }

        this.assemblyStacks[slot] = stack;

        if( stack != null && stack.stackSize > this.getInventoryStackLimit() ) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : TurretModRebirth.ID + ".container.assembly";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null;
    }

    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot != 0 && ItemStackUtils.isValid(stack)
                         && ( (slot > 4 && this.isStackAcceptable(stack, slot - 5)) || (slot == 1 && stack.getItem() == ItemRegistry.asbAuto)
                                                                                    || (slot == 2 && stack.getItem() == ItemRegistry.asbSpeed)
                                                                                    || (slot == 3 && stack.getItem() == ItemRegistry.asbFilter) );
    }

    public static final int FIELD_TICKS_CRAFTED = 0;
    public static final int FIELD_MAX_TICKS_CRAFTED = 1;
    public static final int FIELD_FLUX_CONSUMPTION = 2;

    @Override
    public int getField(int id) {
        switch( id ) {
            case FIELD_TICKS_CRAFTED:
                return this.ticksCrafted;
            case FIELD_MAX_TICKS_CRAFTED:
                return this.maxTicksCrafted;
            case FIELD_FLUX_CONSUMPTION:
                return this.fluxConsumption;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch( id ) {
            case FIELD_TICKS_CRAFTED:
                this.ticksCrafted = value;
                break;
            case FIELD_MAX_TICKS_CRAFTED:
                this.maxTicksCrafted = value;
                break;
            case FIELD_FLUX_CONSUMPTION:
                this.fluxConsumption = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 3;
    }

    @Override
    public void clear() {
        for( int i = 0; i < this.assemblyStacks.length; i++ ) {
            this.assemblyStacks[i] = null;
        }
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergyStored(from) - this.fluxAmount, Math.min(MAX_FLUX_INSERT, maxReceive));

        if( !simulate ) {
            this.fluxAmount += energyReceived;
            this.doSync = true;
        }

        return energyReceived;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return this.fluxAmount;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return MAX_FLUX_STORAGE;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from != EnumFacing.UP;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.fluxAmount);
        buf.writeInt(this.fluxConsumption);
        buf.writeBoolean(this.isActive);
        buf.writeInt(this.ticksCrafted);
        buf.writeInt(this.maxTicksCrafted);
        buf.writeBoolean(this.automate);
        buf.writeBoolean(this.isActiveClient);
        if( this.currCrafting != null ) {
            ByteBufUtils.writeItemStack(buf, this.currCrafting.getValue(1));
            ByteBufUtils.writeUTF8String(buf, this.currCrafting.getValue(0).toString());
        } else {
            ByteBufUtils.writeItemStack(buf, null);
        }
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks[0]);
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks[1]);
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks[2]);
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks[3]);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fluxAmount = buf.readInt();
        this.fluxConsumption = buf.readInt();
        this.isActive = buf.readBoolean();
        this.ticksCrafted = buf.readInt();
        this.maxTicksCrafted = buf.readInt();
        this.automate = buf.readBoolean();
        this.isActiveClient = buf.readBoolean();
        ItemStack crfStack = ByteBufUtils.readItemStack(buf);
        if( crfStack != null ) {
            this.currCrafting = new Tuple(UUID.fromString(ByteBufUtils.readUTF8String(buf)), crfStack);
        } else {
            this.currCrafting = null;
        }

        if( this.syncStacks ) {
            this.assemblyStacks[0] = ByteBufUtils.readItemStack(buf);
            this.assemblyStacks[1] = ByteBufUtils.readItemStack(buf);
            this.assemblyStacks[2] = ByteBufUtils.readItemStack(buf);
            this.assemblyStacks[3] = ByteBufUtils.readItemStack(buf);
        }
    }

    public void setAutomated(boolean b) {
        if( this.currCrafting == null ) {
            this.automate = b;
            this.doSync = true;
        }
    }

    public boolean isAutomated() {
        return this.automate;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    IItemHandler itemHandlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    IItemHandler itemHandlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if( facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing == EnumFacing.DOWN ) {
                return (T) itemHandlerBottom;
            } else if( facing != EnumFacing.UP ) {
                return (T) itemHandlerSide;
            }
        }

        return null;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerTurretAssembly(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return "7C1E3396-655F-43DB-BDDE-B7C481936495";
    }
}
