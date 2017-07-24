/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemAssemblyUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TileEntityTurretAssembly
        extends TileEntityLockable
        implements ISidedInventory, TileClientSync, ITickable
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
    private NonNullList<ItemStack> assemblyStacks = NonNullList.withSize(23, ItemStack.EMPTY);

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
            ItemStack result = TurretAssemblyRegistry.INSTANCE.getRecipeResult(recipe);
            ItemStack currCrfStack = this.currCrafting.getValue(1);
            if( currCrfStack.getCount() + count < 1 ) {
                this.cancelCrafting();
            } else if( ItemStackUtils.isValid(result) && currCrfStack.getCount() + count * result.getCount() <= currCrfStack.getMaxStackSize() ) {
                currCrfStack.grow(count);
                this.doSync = true;
            } else {
                currCrfStack.setCount(currCrfStack.getMaxStackSize());
                this.doSync = true;
            }
        } else if( this.currCrafting == null ) {
            ItemStack stackRes = TurretAssemblyRegistry.INSTANCE.getRecipeResult(recipe);
            TurretAssemblyRegistry.RecipeEntry entry = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(recipe);
            if( entry != null && ItemStackUtils.isValid(stackRes) ) {
                stackRes = stackRes.copy();
                stackRes.setCount(this.automate ? 1 : count);
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
        if( this.currCrafting != null && (!ItemStackUtils.isValid(this.assemblyStacks.get(0)) || this.assemblyStacks.get(0).getCount() < this.assemblyStacks.get(0).getMaxStackSize()) ) {
            UUID currCrfUUID = this.currCrafting.getValue(0);
            ItemStack addStacks = this.currCrafting.<ItemStack>getValue(1).copy();
            ItemStack recipe = TurretAssemblyRegistry.INSTANCE.getRecipeResult(currCrfUUID);
            if( ItemStackUtils.isValid(recipe) ) {
                addStacks.setCount(recipe.getCount());
                if( ItemStackUtils.canStack(this.assemblyStacks.get(0), addStacks, true) && TurretAssemblyRegistry.INSTANCE.checkAndConsumeResources(this, currCrfUUID) ) {
                    TurretAssemblyRegistry.RecipeEntry currentlyCrafted = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(currCrfUUID);
                    if( currentlyCrafted != null ) {
                        this.maxTicksCrafted = currentlyCrafted.ticksProcessing;
                        this.fluxConsumption = MathHelper.ceil(currentlyCrafted.fluxPerTick * (this.hasSpeedUpgrade() ? 1.1F : 1.0F));
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
        return ItemStackUtils.isValid(this.assemblyStacks.get(1)) && this.assemblyStacks.get(1).getItem() == ItemRegistry.assembly_upg_auto;
    }

    public boolean hasSpeedUpgrade() {
        return ItemStackUtils.isValid(this.assemblyStacks.get(2)) && this.assemblyStacks.get(2).getItem() == ItemRegistry.assembly_upg_speed;
    }

    public boolean hasFilterUpgrade() {
        return ItemStackUtils.isValid(this.assemblyStacks.get(3)) && this.assemblyStacks.get(3).getItem() == ItemRegistry.assembly_upg_filter;
    }

    public NonNullList<ItemStack> getFilterStacks() {
        if( this.hasFilterUpgrade() ) {
            return ItemAssemblyUpgrade.Filter.getFilterStacks(this.assemblyStacks.get(3));
        } else {
            return ItemAssemblyUpgrade.Filter.getEmptyInv();
        }
    }

    @Override
    public void update() {
        if( !this.world.isRemote ) {
            if( this.automate && !this.hasAutoUpgrade() ) {
                this.automate = false;
                this.cancelCrafting();
            }

            int maxLoop = this.hasSpeedUpgrade() ? 4 : 1;
            boolean markDirty = false;

            for( int i = 0; i < maxLoop; i++ ) {
                this.isActiveClient = this.isActive;
                if( this.isActive && this.currCrafting != null ) {
                    if( this.fluxAmount >= this.fluxConsumption && this.world.isBlockIndirectlyGettingPowered(this.pos) == 0 ) {
                        this.fluxAmount -= this.fluxConsumption;
                        if( ++this.ticksCrafted >= this.maxTicksCrafted ) {
                            ItemStack stack = TurretAssemblyRegistry.INSTANCE.getRecipeResult(this.currCrafting.getValue(0));
                            if( !ItemStackUtils.isValid(stack) ) {
                                this.cancelCrafting();
                                return;
                            }

                            if( ItemStackUtils.isValid(this.assemblyStacks.get(0)) ) {
                                this.assemblyStacks.get(0).grow(stack.getCount());
                            } else {
                                this.assemblyStacks.set(0, stack.copy());
                            }

                            if( this.assemblyStacks.get(0).getCount() + stack.getCount() > this.assemblyStacks.get(0).getMaxStackSize() ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }
                            if( !TurretAssemblyRegistry.INSTANCE.checkAndConsumeResources(this, this.currCrafting.getValue(0)) ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }

                            if( this.currCrafting.<ItemStack>getValue(1).getCount() > 1 ) {
                                if( !this.automate ) {
                                    this.currCrafting.<ItemStack>getValue(1).shrink(1);
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
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        nbt.setInteger("flux", this.fluxAmount);
        return this.writeNBT(nbt);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("flux", this.fluxAmount);
        return new SPacketUpdateTileEntity(this.pos, 0, this.writeNBT(nbt));
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
        nbt.setTag("inventory", ItemStackUtils.writeItemStacksToTag(this.assemblyStacks, 64));

        if( this.currCrafting != null ) {
            nbt.setString("craftingUUID", this.currCrafting.getValue(0).toString());
            ItemStackUtils.writeStackToTag(this.currCrafting.getValue(1), nbt, "craftingStack");
        }

        nbt.setBoolean("isActive", this.isActive);
        nbt.setInteger("fluxAmount", this.fluxAmount);
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
        ItemStackUtils.readItemStacksFromTag(this.assemblyStacks, nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND));

        if( nbt.hasKey("craftingUUID") && nbt.hasKey("craftingStack") ) {
            this.currCrafting = new Tuple(UUID.fromString(nbt.getString("craftingUUID")), new ItemStack(nbt.getCompoundTag("craftingStack")));
        }

        this.isActive = nbt.getBoolean("isActive");
        this.fluxAmount = nbt.getInteger("fluxAmount");
        this.ticksCrafted = nbt.getInteger("ticksCrafted");
        this.maxTicksCrafted = nbt.getInteger("maxTicksCrafted");
        this.fluxConsumption = nbt.getInteger("fluxConsumption");
        this.automate = nbt.getBoolean("automate");

        if( nbt.hasKey("customName") ) {
            this.customName = nbt.getString("customName");
        }
    }

    private boolean isStackAcceptable(@Nonnull ItemStack stack, int insrtSlot) {
        if( this.hasFilterUpgrade() ) {
            NonNullList<ItemStack> filter = this.getFilterStacks();
            if( ItemStackUtils.isStackInList(stack, filter) ) {
                return ItemStackUtils.areEqual(stack, filter.get(insrtSlot));
            } else {
                return !ItemStackUtils.isValid(filter.get(insrtSlot));
            }
        }

        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_EXTRACT : (side == EnumFacing.UP ? new int[0] : SLOTS_INSERT);
    }

    @Override
    public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {
        return this.isItemValidForSlot(slot, stack) && side != EnumFacing.DOWN && side != EnumFacing.UP;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {
        return slot == 0 && side == EnumFacing.DOWN;
    }

    @Override
    public int getSizeInventory() {
        return this.assemblyStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.assemblyStacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return this.assemblyStacks.get(slot);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slot, int size) {
        if( !this.hasAutoUpgrade() ) {
            this.automate = false;
        }

        if( ItemStackUtils.isValid(this.assemblyStacks.get(slot)) ) {
            ItemStack itemstack;

            if( this.assemblyStacks.get(slot).getCount() <= size ) {
                itemstack = this.assemblyStacks.get(slot);
                this.assemblyStacks.set(slot, ItemStack.EMPTY);
                return itemstack;
            } else {
                itemstack = this.assemblyStacks.get(slot).splitStack(size);

                if( this.assemblyStacks.get(slot).getCount() == 0 ) {
                    this.assemblyStacks.set(slot, ItemStack.EMPTY);
                }

                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int slot) {
        if( ItemStackUtils.isValid(this.assemblyStacks.get(slot)) ) {
            ItemStack itemstack = this.assemblyStacks.get(slot);
            this.assemblyStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        if( !this.hasAutoUpgrade() ) {
            this.automate = false;
        }

        this.assemblyStacks.set(slot, stack);

        if( ItemStackUtils.isValid(stack) && stack.getCount() > this.getInventoryStackLimit() ) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : TmrConstants.ID + ".container.assembly";
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
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return slot != 0 && ItemStackUtils.isValid(stack)
                         && ( (slot > 4 && this.isStackAcceptable(stack, slot - 5)) || (slot == 1 && stack.getItem() == ItemRegistry.assembly_upg_auto)
                                                                                    || (slot == 2 && stack.getItem() == ItemRegistry.assembly_upg_speed)
                                                                                    || (slot == 3 && stack.getItem() == ItemRegistry.assembly_upg_filter) );
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
        for( int i = 0; i < this.assemblyStacks.size(); i++ ) {
            this.assemblyStacks.set(i, ItemStack.EMPTY);
        }
    }

//    @Override
//    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
//        int energyReceived = Math.min(this.getMaxEnergyStored(from) - this.fluxAmount, Math.min(MAX_FLUX_INSERT, maxReceive));
//
//        if( !simulate ) {
//            this.fluxAmount += energyReceived;
//            this.doSync = true;
//        }
//
//        return energyReceived;
//    }
//
//    @Override
//    public int getEnergyStored(EnumFacing from) {
//        return this.fluxAmount;
//    }
//
//    @Override
//    public int getMaxEnergyStored(EnumFacing from) {
//        return MAX_FLUX_STORAGE;
//    }
//
//    @Override
//    public boolean canConnectEnergy(EnumFacing from) {
//        return from != EnumFacing.UP;
//    }

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
            ByteBufUtils.writeItemStack(buf, ItemStack.EMPTY);
        }
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks.get(0));
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks.get(1));
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks.get(2));
        ByteBufUtils.writeItemStack(buf, this.assemblyStacks.get(3));
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
        if( ItemStackUtils.isValid(crfStack) ) {
            this.currCrafting = new Tuple(UUID.fromString(ByteBufUtils.readUTF8String(buf)), crfStack);
        } else {
            this.currCrafting = null;
        }

        if( this.syncStacks ) {
            this.assemblyStacks.set(0, ByteBufUtils.readItemStack(buf));
            this.assemblyStacks.set(1, ByteBufUtils.readItemStack(buf));
            this.assemblyStacks.set(2, ByteBufUtils.readItemStack(buf));
            this.assemblyStacks.set(3, ByteBufUtils.readItemStack(buf));
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
    IEnergyStorage energyStorage = new EnergyStorageAssembly();

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing == EnumFacing.DOWN ) {
                return (T) itemHandlerBottom;
            } else if( facing != EnumFacing.UP ) {
                return (T) itemHandlerSide;
            }
        } else if( facing != EnumFacing.UP && capability == CapabilityEnergy.ENERGY ) {
            return (T) energyStorage;
        }

        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if( facing != EnumFacing.UP ) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
        }

        return super.hasCapability(capability, facing);
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

    private final class EnergyStorageAssembly
            implements IEnergyStorage
    {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int energyReceived = Math.min(this.getMaxEnergyStored() - TileEntityTurretAssembly.this.fluxAmount, Math.min(MAX_FLUX_INSERT, maxReceive));

            if( !simulate ) {
                TileEntityTurretAssembly.this.fluxAmount += energyReceived;
                TileEntityTurretAssembly.this.doSync = true;
            }

            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return TileEntityTurretAssembly.this.fluxAmount;
        }

        @Override
        public int getMaxEnergyStored() {
            return MAX_FLUX_STORAGE;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
