/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.inventory.ContainerElectrolyteGenerator;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public class TileEntityElectrolyteGenerator
        extends TileEntityLockable
        implements ISidedInventory, TileClientSync, IEnergyProvider, ITickable
{
    public static final int MAX_FLUX_STORAGE = 500_000;
    public static final int MAX_FLUX_EXTRACT = 1_000;
    public static final int MAX_FLUX_GENERATED = 200;

    public int fluxExtractPerTick;
    public short[] progress = new short[9];
    public short[] maxProgress = new short[this.progress.length];
    public float effectiveness;

    private ItemStack[] invStacks = new ItemStack[23];
    private static final int[] SLOTS_INSERT = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final int[] SLOTS_PROCESSING = new int[] {9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int[] SLOTS_EXTRACT = new int[] {18, 19, 20, 21, 22};
    private ItemStack[] progExcessComm = new ItemStack[this.progress.length];
    private ItemStack[] progExcessRare = new ItemStack[this.progress.length];

    private int fluxAmount;
    private int prevFluxAmount;
    private boolean doSync;

    private int fluxBuffer;

    private static final Map<Item, Fuel> FUELS = new HashMap<>(3);

    private String customName;

    public TileEntityElectrolyteGenerator() { }

    @SuppressWarnings("ConstantConditions")
    public static void initializeRecipes() {
        FUELS.put(Items.POTATO, new Fuel(1.0F, (short) 200, new ItemStack(Items.SUGAR, 1), new ItemStack(Items.BAKED_POTATO, 1)));
        FUELS.put(Items.CARROT, new Fuel(1.0F, (short) 200, new ItemStack(Items.SUGAR, 1), new ItemStack(Items.REDSTONE, 1)));
        FUELS.put(Items.POISONOUS_POTATO, new Fuel(1.2F, (short) 150, new ItemStack(Items.SUGAR, 1), new ItemStack(Items.NETHER_WART, 1)));
        FUELS.put(Items.APPLE, new Fuel(1.3F, (short) 220, new ItemStack(Items.WHEAT_SEEDS, 1), new ItemStack(Items.GOLD_NUGGET, 1)));

        int currInd = 0;
        for( String recp : TmrConfiguration.electrolyteAdditRecipes ) {
            String[] elem = recp.split(",");
            if( elem.length == 5 ) {
                Item result = Item.getByNameOrId(elem[0].trim().replaceAll("<(.*?)>", "$1"));
                if( result != null ) {
                    try {
                        Item trash = Item.getByNameOrId(elem[3].trim().replaceAll("<(.*?)>", "$1"));
                        if( trash != null ) {
                            float multi = Float.valueOf(elem[1].trim());
                            short decayTicks = Short.valueOf(elem[2].trim());
                            Item treasure = Item.getByNameOrId(elem[4].trim().replaceAll("<(.*?)>", "$1"));

                            FUELS.put(result, new Fuel(multi, decayTicks, new ItemStack(trash, 1),  new ItemStack(treasure, 1)));
                        } else {
                            TurretModRebirth.LOG.log(Level.WARN, String.format("Cannot add electrolyte item #%d from config! Cannot find trash item %s, skipping recipe.",
                                                                               currInd, elem[3].trim()));
                        }
                    } catch( NumberFormatException ex ) {
                        TurretModRebirth.LOG.log(Level.WARN, String.format("Cannot parse numbers for electrolyte item #%d in config! Skipping recipe.", currInd));
                    }
                } else {
                    TurretModRebirth.LOG.log(Level.WARN, String.format("Cannot add electrolyte item #%d from config! Cannot find electrolyte item %s, skipping recipe.",
                                                                       currInd, elem[0].trim()));
                }
            } else {
                TurretModRebirth.LOG.log(Level.WARN, String.format("Cannot add electrolyte item #%d from config! Invalid parameter count: expected 5, got %d, skipping recipe.",
                                                                   currInd, elem.length));
            }
            currInd++;
        }
    }

    public static Map<Item, Fuel> getFuels() {
        return new HashMap<>(FUELS);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_EXTRACT : side == EnumFacing.UP ? new int[0] : SLOTS_INSERT;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return this.isItemValidForSlot(slot, stack) && side != EnumFacing.DOWN && side != EnumFacing.UP;
    }

    public int getGeneratedFlux() {
        return this.effectiveness < 0.1F ? 0 : Math.min(200, (int) Math.round(Math.pow(1.6D, this.effectiveness) / (68.0D + (127433.0D / 177119.0D)) * 80.0D));
    }

    @Override
    public void update() {
        if( !this.worldObj.isRemote ) {
            this.fluxExtractPerTick = Math.min(this.fluxAmount, MAX_FLUX_EXTRACT);

            float prevEffective = this.effectiveness;

            if( this.fluxBuffer > 0 ) {
                int fluxSubtracted = Math.min(MAX_FLUX_STORAGE - this.fluxAmount, Math.min(MAX_FLUX_GENERATED, this.fluxBuffer));
                this.fluxBuffer -= fluxSubtracted;
                this.fluxAmount += fluxSubtracted;
            }

            if( this.fluxBuffer <= MAX_FLUX_GENERATED && this.fluxAmount < MAX_FLUX_STORAGE ) {
                int fluxEff = this.getGeneratedFlux();

                this.effectiveness = 0.0F;

                for( int i = 0; i < SLOTS_PROCESSING.length; i++ ) {
                    if( this.invStacks[SLOTS_PROCESSING[i]] != null ) {
                        if( this.progExcessComm[i] != null && !InventoryUtils.canStackFitInInventory(this.progExcessComm[i], this, true, 64, SLOTS_EXTRACT[0], SLOTS_EXTRACT[SLOTS_EXTRACT.length - 1]) ) {
                            continue;
                        }
                        if( this.progExcessRare[i] != null && !InventoryUtils.canStackFitInInventory(this.progExcessRare[i], this, true, 64, SLOTS_EXTRACT[0], SLOTS_EXTRACT[SLOTS_EXTRACT.length - 1]) ) {
                            continue;
                        }

                        if( this.progress[i] <= 0 ) {
                            if( this.progExcessComm[i] != null ) {
                                InventoryUtils.addStackToInventory(this.progExcessComm[i], this, true, 64, SLOTS_EXTRACT[0], SLOTS_EXTRACT[SLOTS_EXTRACT.length - 1]);
                            }
                            if( this.progExcessRare[i] != null ) {
                                InventoryUtils.addStackToInventory(this.progExcessRare[i], this, true, 64, SLOTS_EXTRACT[0], SLOTS_EXTRACT[SLOTS_EXTRACT.length - 1]);
                            }
                            this.invStacks[SLOTS_PROCESSING[i]] = null;
                            this.markDirty();
                        } else {
                            this.effectiveness += FUELS.get(this.invStacks[SLOTS_PROCESSING[i]].getItem()).effect;
                            this.progress[i]--;
                        }
                        this.doSync = true;
                    }

                    if( this.invStacks[SLOTS_PROCESSING[i]] == null && this.invStacks[SLOTS_INSERT[i]] != null ) {
                        this.invStacks[SLOTS_PROCESSING[i]] = this.invStacks[SLOTS_INSERT[i]].copy();
                        this.invStacks[SLOTS_PROCESSING[i]].stackSize = 1;
                        if( --this.invStacks[SLOTS_INSERT[i]].stackSize < 1 ) {
                            this.invStacks[SLOTS_INSERT[i]] = null;
                        }

                        Fuel fuel = FUELS.get(this.invStacks[SLOTS_PROCESSING[i]].getItem());
                        this.progress[i] = fuel.ticksProc;
                        this.maxProgress[i] = fuel.ticksProc;
                        this.progExcessComm[i] = MiscUtils.RNG.randomInt(10) == 0 ? fuel.trash.copy() : null;
                        this.progExcessRare[i] = fuel.treasure != null && MiscUtils.RNG.randomInt(100) == 0 ? fuel.treasure.copy() : null;

                        this.markDirty();
                        this.doSync = true;
                    }
                }

                if( this.effectiveness > 0.1F ) {
                    this.fluxBuffer += fluxEff;
                }
            }

            if( prevEffective < this.effectiveness - 0.01F || prevEffective > this.effectiveness + 0.01F ) {
                this.doSync = true;
            }

            if( this.fluxExtractPerTick > 0 ) {
                for( EnumFacing direction : EnumFacing.VALUES ) {
                    if( direction == EnumFacing.UP ) {
                        continue;
                    }
                    EnumFacing otherDir = direction.getOpposite();

                    BlockPos adjPos = this.pos.add(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
                    TileEntity te = this.worldObj.getTileEntity(adjPos);

                    if( te instanceof IEnergyReceiver ) {
                        IEnergyReceiver receiver = (IEnergyReceiver) te;

                        if( !receiver.canConnectEnergy(otherDir) ) {
                            continue;
                        }

                        int extractable = this.extractEnergy(direction, MAX_FLUX_EXTRACT, true);
                        int receivable = receiver.receiveEnergy(otherDir, extractable, false);

                        this.extractEnergy(direction, receivable, false);
                    }

                    if( this.fluxExtractPerTick <= 0 ) {
                        break;
                    }
                }
            }

            if( this.prevFluxAmount != this.fluxAmount ) {
                this.doSync = true;
            }

            if( this.doSync ) {
                PacketRegistry.sendToAllAround(new PacketSyncTileEntity(this), this.worldObj.provider.getDimension(), this.pos, 64.0D);
            }

            this.prevFluxAmount = this.fluxAmount;
        }
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return side == EnumFacing.DOWN && ArrayUtils.contains(SLOTS_EXTRACT, slot);
    }

    @Override
    public int getSizeInventory() {
        return this.invStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.invStacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int size) {
        if( this.invStacks[slot] != null ) {
            ItemStack itemstack;

            if( this.invStacks[slot].stackSize <= size ) {
                itemstack = this.invStacks[slot];
                this.invStacks[slot] = null;
                return itemstack;
            } else {
                itemstack = this.invStacks[slot].splitStack(size);

                if( this.invStacks[slot].stackSize == 0 ) {
                    this.invStacks[slot] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeNbt(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readNbt(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readNbt(pkt.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeNbt(nbt);
        return new SPacketUpdateTileEntity(this.pos, 0, nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        this.writeNbt(nbt);

        return nbt;
    }

    private NBTTagCompound writeNbt(NBTTagCompound nbt) {
        nbt.setInteger("fluxAmount", this.fluxAmount);
        nbt.setInteger("fluxBuffer", this.fluxBuffer);
        NBTTagList progress = new NBTTagList();
        for( short s : this.progress ) {
            progress.appendTag(new NBTTagShort(s));
        }
        for( short s : this.maxProgress ) {
            progress.appendTag(new NBTTagShort(s));
        }
        nbt.setTag("progress", progress);

        nbt.setTag("inventory", ItemStackUtils.writeItemStacksToTag(this.invStacks, 64));

        if( this.hasCustomName() ) {
            nbt.setString("customName", this.customName);
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.readNbt(nbt);

        for( int i = 0; i < this.progress.length; i++ ) {
            if( this.invStacks[SLOTS_PROCESSING[i]] != null ) {
                Fuel fuel = FUELS.get(this.invStacks[SLOTS_PROCESSING[i]].getItem());
                this.progExcessComm[i] = MiscUtils.RNG.randomInt(100) == 0 ? fuel.trash.copy() : null;
                this.progExcessRare[i] = fuel.treasure != null && MiscUtils.RNG.randomInt(100) == 0 ? fuel.treasure.copy() : null;
            }
        }
    }

    private void readNbt(NBTTagCompound nbt) {
        this.fluxAmount = nbt.getInteger("fluxAmount");
        this.fluxBuffer = nbt.getInteger("fluxBuffer");
        NBTTagList progress = nbt.getTagList("progress", Constants.NBT.TAG_SHORT);
        for( int i = 0; i < this.progress.length; i++ ) {
            this.progress[i] = ((NBTTagShort) progress.get(i)).getShort();
        }
        for( int i = 0; i < this.maxProgress.length; i++ ) {
            this.maxProgress[i] = ((NBTTagShort) progress.get(i + this.progress.length)).getShort();
        }

        ItemStackUtils.readItemStacksFromTag(this.invStacks, nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND));

        if( nbt.hasKey("customName", Constants.NBT.TAG_STRING) ) {
            this.customName = nbt.getString("customName");
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if( this.invStacks[slot] != null ) {
            ItemStack itemstack = this.invStacks[slot];
            this.invStacks[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.invStacks[slot] = stack;

        int stackLimit = ArrayUtils.contains(SLOTS_EXTRACT, slot) ? 64 : this.getInventoryStackLimit();
        if( stack != null && stack.stackSize > stackLimit ) {
            stack.stackSize = stackLimit;
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : BlockRegistry.potatoGenerator.getUnlocalizedName() + ".name";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
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
        if( !ItemStackUtils.isValid(stack) ) {
            return false;
        }

        if( ArrayUtils.contains(SLOTS_INSERT, slot) ) {
            return FUELS.containsKey(stack.getItem()) && this.invStacks[slot] == null;
        }

        return ArrayUtils.contains(SLOTS_EXTRACT, slot);
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
        for( int i = 0; i < this.invStacks.length; i++ ) {
            this.invStacks[i] = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.fluxAmount);
        buf.writeFloat(this.effectiveness);
        for( short s : this.progress ) {
            buf.writeShort(s);
        }
        for( short s : this.maxProgress ) {
            buf.writeShort(s);
        }
        for( int slot : SLOTS_PROCESSING ) {
            ByteBufUtils.writeItemStack(buf, this.getStackInSlot(slot));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fluxAmount = buf.readInt();
        this.effectiveness = buf.readFloat();
        for( int i = 0; i < this.progress.length; i++ ) {
            this.progress[i] = buf.readShort();
        }
        for( int i = 0; i < this.maxProgress.length; i++ ) {
            this.maxProgress[i] = buf.readShort();
        }
        for( int slot : SLOTS_PROCESSING ) {
            this.setInventorySlotContents(slot, ByteBufUtils.readItemStack(buf));
        }
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.fluxExtractPerTick, Math.min(MAX_FLUX_EXTRACT, maxExtract));

        if( !simulate ) {
            this.fluxAmount -= energyExtracted;
            this.fluxExtractPerTick -= energyExtracted;
        }

        return energyExtracted;
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

    public static boolean isSlotProcessing(int slot) {
        return ArrayUtils.contains(SLOTS_PROCESSING, slot);
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    public static Fuel getFuel(Item item) {
        return FUELS.get(item);
    }

    private IItemHandler itemHandlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    private IItemHandler itemHandlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing == EnumFacing.DOWN ) {
                return (T) itemHandlerBottom;
            } else if( facing != EnumFacing.UP ) {
                return (T) itemHandlerSide;
            }
        }

        return null;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerElectrolyteGenerator(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return "CA1169C7-56D3-4E10-8F09-3016C52D570A";
    }

    public static final class Fuel
    {
        public final float effect;
        public final short ticksProc;
        public final ItemStack trash;
        public final ItemStack treasure;

        public Fuel(float effectiveness, int ticksProcessing, ItemStack trash, ItemStack treasure) {
            this.effect = effectiveness;
            this.ticksProc = (short) ticksProcessing;
            this.trash = trash;
            this.treasure = treasure;
        }
    }
}
