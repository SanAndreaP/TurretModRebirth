/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import de.sanandrew.mods.turretmod.item.ItemAssemblyFilter;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.lib.javatuples.Pair;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

//TODO: make it TileEntityLockable
public class TileEntityTurretAssembly
        extends TileEntity
        implements ISidedInventory, IEnergyHandler, TileClientSync, ITickable
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
    public boolean isItemRendered = false;
    public Triplet<Float, Float, Float> spawnParticle = null;

    private boolean prevActive;
    private boolean automate;
    public boolean isActive;
    private boolean isActiveClient;

    private int fluxAmount;
    private ItemStack[] assemblyStacks = new ItemStack[23];

    public boolean syncStacks = true;

    public Pair<UUID, ItemStack> currCrafting;
    private int ticksCrafted = 0;
    private int maxTicksCrafted = 0;
    private int fluxConsumption = 0;

    private boolean doSync = false;

    private long ticksExisted = 0L;
    private String customName;

    public TileEntityTurretAssembly() {
        this.isItemRendered = false;
    }

    public TileEntityTurretAssembly(boolean itemRendered) {
        this.isItemRendered = itemRendered;
    }

    public void beginCrafting(UUID recipe, int count) {
        if( this.currCrafting != null && recipe.equals(this.currCrafting.getValue0()) && !this.automate ) {
            if( this.currCrafting.getValue1().stackSize + count < 1 ) {
                this.cancelCrafting();
            } else if( this.currCrafting.getValue1().stackSize + count * TurretAssemblyRecipes.INSTANCE.getRecipeResult(recipe).stackSize <= this.currCrafting.getValue1().getMaxStackSize() ) {
                this.currCrafting.getValue1().stackSize += count;
                this.doSync = true;
            } else {
                this.currCrafting.getValue1().stackSize = this.currCrafting.getValue1().getMaxStackSize();
                this.doSync = true;
            }
        } else if( this.currCrafting == null ) {
            ItemStack stackRes = TurretAssemblyRecipes.INSTANCE.getRecipeResult(recipe);
            if( stackRes != null ) {
                stackRes = stackRes.copy();
                stackRes.stackSize = this.automate ? 1 : count;
                this.currCrafting = Pair.with(recipe, stackRes);
                this.maxTicksCrafted = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(recipe).ticksProcessing;
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
        this.doSync = true;
    }

    private void initCrafting() {
        if( this.currCrafting != null && (this.assemblyStacks[0] == null || this.assemblyStacks[0].stackSize < this.assemblyStacks[0].getMaxStackSize()) ) {
            UUID currCrfUUID = this.currCrafting.getValue0();
            ItemStack addStacks = this.currCrafting.getValue1().copy();
            ItemStack recipe = TurretAssemblyRecipes.INSTANCE.getRecipeResult(currCrfUUID);
            if( recipe != null ) {
                addStacks.stackSize = recipe.stackSize;
                if( TmrUtils.canStack(this.assemblyStacks[0], addStacks, true) && TurretAssemblyRecipes.INSTANCE.checkAndConsumeResources(this, currCrfUUID) ) {
                    TurretAssemblyRecipes.RecipeEntry currentlyCrafted = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(currCrfUUID);
                    this.maxTicksCrafted = currentlyCrafted.ticksProcessing;
                    this.fluxConsumption = MathHelper.ceiling_float_int(currentlyCrafted.fluxPerTick * (this.hasSpeedUpgrade() ? 1.1F : 1.0F));
                    this.ticksCrafted = 0;
                    this.isActive = true;
                    this.doSync = true;
                }
            } else {
                this.cancelCrafting();
            }
        }
    }

    public boolean hasAutoUpgrade() {
        return ItemStackUtils.isValidStack(this.assemblyStacks[1]) && this.assemblyStacks[1].getItem() == ItemRegistry.asbAuto;
    }

    public boolean hasSpeedUpgrade() {
        return ItemStackUtils.isValidStack(this.assemblyStacks[2]) && this.assemblyStacks[2].getItem() == ItemRegistry.asbSpeed;
    }

    public boolean hasFilterUpgrade() {
        return ItemStackUtils.isValidStack(this.assemblyStacks[3]) && this.assemblyStacks[3].getItem() == ItemRegistry.asbFilter;
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
                            ItemStack stack = TurretAssemblyRecipes.INSTANCE.getRecipeResult(this.currCrafting.getValue0());

                            if( this.assemblyStacks[0] != null ) {
                                this.assemblyStacks[0].stackSize += stack.stackSize;
                            } else {
                                this.assemblyStacks[0] = stack.copy();
                            }

                            if( this.assemblyStacks[0].stackSize + stack.stackSize > this.assemblyStacks[0].getMaxStackSize() ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }
                            if( !TurretAssemblyRecipes.INSTANCE.checkAndConsumeResources(this, this.currCrafting.getValue0()) ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }

                            if( this.currCrafting.getValue1().stackSize > 1 ) {
                                if( !this.automate ) {
                                    this.currCrafting.getValue1().stackSize--;
                                }
                            } else if( !this.automate ) {
                                this.currCrafting = null;
                                this.maxTicksCrafted = 0;
                                this.isActive = false;
                                this.isActiveClient = false;
                                this.fluxConsumption = 0;
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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readNBT(pkt.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeNBT(nbt);
        return new SPacketUpdateTileEntity(this.pos, 0, nbt);
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

    private void writeNBT(NBTTagCompound nbt) {
        nbt.setBoolean("isActive", this.isActive);
        nbt.setInteger("flux", this.fluxAmount);
        nbt.setTag("inventory", TmrUtils.writeItemStacksToTag(this.assemblyStacks, 64));

        if( this.currCrafting != null ) {
            nbt.setString("craftingUUID", this.currCrafting.getValue0().toString());
            ItemStackUtils.writeStackToTag(this.currCrafting.getValue1(), nbt, "craftingStack");
        }

        nbt.setInteger("ticksCrafted", this.ticksCrafted);
        nbt.setInteger("maxTicksCrafted", this.maxTicksCrafted);
        nbt.setInteger("fluxConsumption", this.fluxConsumption);
        nbt.setBoolean("automate", this.automate);
    }

    private void readNBT(NBTTagCompound nbt) {
        this.isActive = nbt.getBoolean("isActive");
        this.fluxAmount = nbt.getInteger("flux");
        TmrUtils.readItemStacksFromTag(this.assemblyStacks, nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND));

        if( nbt.hasKey("craftingUUID") && nbt.hasKey("craftingStack") ) {
            this.currCrafting = Pair.with(UUID.fromString(nbt.getString("craftingUUID")), ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("craftingStack")));
        }

        this.ticksCrafted = nbt.getInteger("ticksCrafted");
        this.maxTicksCrafted = nbt.getInteger("maxTicksCrafted");
        this.fluxConsumption = nbt.getInteger("fluxConsumption");
        this.automate = nbt.getBoolean("automate");
    }

    private boolean isStackAcceptable(ItemStack stack, int insrtSlot) {
        if( this.hasFilterUpgrade() ) {
            ItemStack[] filter = this.getFilterStacks();
            if( TmrUtils.isStackInArray(stack, filter) ) {
                return TmrUtils.areStacksEqual(stack, filter[insrtSlot], TmrUtils.NBT_COMPARATOR_FIXD);
            } else {
                return !ItemStackUtils.isValidStack(filter[insrtSlot]);
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
        return "";
    }

    @Override
    public boolean hasCustomName() {
        return false;
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
        return slot != 0 && ItemStackUtils.isValidStack(stack)
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
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
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
            ByteBufUtils.writeItemStack(buf, this.currCrafting.getValue1());
            ByteBufUtils.writeUTF8String(buf, this.currCrafting.getValue0().toString());
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
            this.currCrafting = Pair.with(UUID.fromString(ByteBufUtils.readUTF8String(buf)), crfStack);
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

    net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.WEST);

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
        if( facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing == EnumFacing.DOWN ) {
                return (T) handlerBottom;
            } else {
                return (T) handlerSide;
            }
        }

        return super.getCapability(capability, facing);
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}