/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import de.sanandrew.mods.sanlib.lib.power.EnergyHelper;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public class TileEntityElectrolyteGenerator
        extends TileEntity
        implements TileClientSync, ITickable
{
    public static final int MAX_FLUX_STORAGE = 500_000;
    public static final int MAX_FLUX_EXTRACT = 1_000;
    public static final int MAX_FLUX_GENERATED = 200;

    public int fluxExtractPerTick;
    public ItemStack[] processStacks = new ItemStack[9];
    public short[] progress = new short[this.processStacks.length];
    public short[] maxProgress = new short[this.processStacks.length];
    public float effectiveness;

    private ItemStack[] progExcessComm = new ItemStack[this.processStacks.length];
    private ItemStack[] progExcessRare = new ItemStack[this.processStacks.length];

    private int fluxAmount;
    private int prevFluxAmount;
    private int fluxBuffer;

    private boolean doSync;

    private static final Map<Item, Fuel> FUELS = new HashMap<>(6);

    private String customName;

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

    public int getGeneratedFlux() {
        return this.effectiveness < 0.1F ? 0 : Math.min(200, (int) Math.round(Math.pow(1.6D, this.effectiveness) / (68.0D + (127433.0D / 177119.0D)) * 80.0D));
    }

    @Override
    public void update() {
        if( !this.world.isRemote ) {
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

                for( int i = 0; i < processStacks.length; i++ ) {
                    if( this.processStacks[i] != null ) {
                        if( this.progExcessComm[i] != null && !this.itemHandler.canAddExtraction(this.progExcessComm[i]) ) {
                            continue;
                        }
                        if( this.progExcessRare[i] != null && !this.itemHandler.canAddExtraction(this.progExcessRare[i]) ) {
                            continue;
                        }

                        if( this.progress[i] <= 0 ) {
                            if( this.progExcessComm[i] != null ) {
                                this.itemHandler.addExtraction(this.progExcessComm[i]);
                            }
                            if( this.progExcessRare[i] != null ) {
                                this.itemHandler.addExtraction(this.progExcessRare[i]);
                            }
                            this.processStacks[i] = null;
                            this.markDirty();
                        } else {
                            this.effectiveness += FUELS.get(this.processStacks[i].getItem()).effect;
                            this.progress[i]--;
                        }
                        this.doSync = true;
                    }

                    ItemStack insrtStack = this.itemHandler.extractInsertItem(i, true);
                    if( this.processStacks[i] == null && ItemStackUtils.isValid(insrtStack) ) {
                        this.processStacks[i] = this.itemHandler.extractInsertItem(i, false);

                        Fuel fuel = FUELS.get(this.processStacks[i].getItem());
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
                    TileEntity te = this.world.getTileEntity(adjPos);

                    if( te == null || !EnergyHelper.canConnectEnergy(te, otherDir) ) {
                        continue;
                    }

                    long extractable = EnergyHelper.extractEnergy(this, direction, MAX_FLUX_EXTRACT, true);
                    long receivable = EnergyHelper.receiveEnergy(te, otherDir, extractable, false);

                    EnergyHelper.extractEnergy(this, direction, receivable, false);

                    if( this.fluxExtractPerTick <= 0 ) {
                        break;
                    }
                }
            }

            if( this.prevFluxAmount != this.fluxAmount ) {
                this.doSync = true;
            }

            if( this.doSync ) {
                PacketRegistry.sendToAllAround(new PacketSyncTileEntity(this), this.world.provider.getDimension(), this.pos, 64.0D);
            }

            this.prevFluxAmount = this.fluxAmount;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        nbt.setInteger("fluxAmount", this.fluxAmount);
        return this.writeNbt(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readNbt(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.getNbtCompound();
        this.readNbt(nbt);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeNbt(nbt);
        nbt.setInteger("fluxAmount", this.fluxAmount);
        return new SPacketUpdateTileEntity(this.pos, 0, nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setTag("cap_inventory", this.itemHandler.serializeNBT());
        nbt.setTag("cap_energy", this.energyStorage.serializeNBT());

        this.writeNbt(nbt);

        return nbt;
    }

    private NBTTagCompound writeNbt(NBTTagCompound nbt) {
        nbt.setInteger("fluxBuffer", this.fluxBuffer);
        NBTTagList progress = new NBTTagList();
        for( int i = 0; i < this.processStacks.length; i++ ) {
            if( ItemStackUtils.isValid(this.processStacks[i]) ) {
                NBTTagCompound progNbt = new NBTTagCompound();
                progNbt.setByte("progressSlot", (byte) i);
                progNbt.setShort("progress", this.progress[i]);
                progNbt.setShort("progressMax", this.maxProgress[i]);
                ItemStackUtils.writeStackToTag(this.processStacks[i], progNbt, "progressItem");
                progress.appendTag(progNbt);
            }
        }
        nbt.setTag("progress", progress);

        if( this.hasCustomName() ) {
            nbt.setString("customName", this.customName);
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.readNbt(nbt);

        this.itemHandler.deserializeNBT(nbt.getCompoundTag("cap_inventory"));
        this.energyStorage.deserializeNBT(nbt.getCompoundTag("cap_energy"));

        for( int i = 0; i < this.processStacks.length; i++ ) {
            if( ItemStackUtils.isValid(this.processStacks[i]) ) {
                Fuel fuel = FUELS.get(this.processStacks[i].getItem());
                this.progExcessComm[i] = MiscUtils.RNG.randomInt(100) == 0 ? fuel.trash.copy() : null;
                this.progExcessRare[i] = fuel.treasure != null && MiscUtils.RNG.randomInt(100) == 0 ? fuel.treasure.copy() : null;
            }
        }
    }

    private void readNbt(NBTTagCompound nbt) {
        if( nbt.hasKey("fluxAmount") ) {
            this.fluxAmount = nbt.getInteger("fluxAmount");
        }
        this.fluxBuffer = nbt.getInteger("fluxBuffer");
        NBTTagList progress = nbt.getTagList("progress", Constants.NBT.TAG_COMPOUND);
        for( int i = 0, max = progress.tagCount(); i < max; i++ ) {
            NBTTagCompound progNbt = progress.getCompoundTagAt(i);
            byte slot = progNbt.getByte("progressSlot");
            this.progress[slot] = progNbt.getShort("progress");
            this.maxProgress[slot] = progNbt.getShort("progressMax");
            this.processStacks[slot] = ItemStack.loadItemStackFromNBT(progNbt.getCompoundTag("progressItem"));
        }

        if( nbt.hasKey("customName", Constants.NBT.TAG_STRING) ) {
            this.customName = nbt.getString("customName");
        }
    }

    public String getName() {
        return this.hasCustomName() ? this.customName : BlockRegistry.electrolyte_generator.getUnlocalizedName() + ".name";
    }

    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.fluxAmount);
        buf.writeFloat(this.effectiveness);
        for( int i = 0; i < this.processStacks.length; i++ ) {
            buf.writeShort(this.progress[i]);
            buf.writeShort(this.maxProgress[i]);
            ByteBufUtils.writeItemStack(buf, this.processStacks[i]);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fluxAmount = buf.readInt();
        this.effectiveness = buf.readFloat();
        for( int i = 0; i < this.processStacks.length; i++ ) {
            this.progress[i] = buf.readShort();
            this.maxProgress[i] = buf.readShort();
            this.processStacks[i] = ByteBufUtils.readItemStack(buf);
        }
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    public static Fuel getFuel(Item item) {
        return FUELS.get(item);
    }

    private MyItemStackHandler itemHandler = new MyItemStackHandler();
    public ContainerItemStackHandler containerItemHandler = new ContainerItemStackHandler(itemHandler);
    private MyEnergyStorageGen energyStorage = new MyEnergyStorageGen();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing != EnumFacing.UP ) {
                return (T) itemHandler;
            }
        } else if( capability == CapabilityEnergy.ENERGY ) {
            if( facing != EnumFacing.UP ) {
                return (T) energyStorage;
            }
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

    private final class MyEnergyStorageGen
            implements IEnergyStorage, INBTSerializable<NBTTagCompound>
    {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int energyExtracted = Math.min(TileEntityElectrolyteGenerator.this.fluxExtractPerTick, Math.min(MAX_FLUX_EXTRACT, maxExtract));

            if( !simulate ) {
                TileEntityElectrolyteGenerator.this.fluxAmount -= energyExtracted;
                TileEntityElectrolyteGenerator.this.fluxExtractPerTick -= energyExtracted;
            }

            return energyExtracted;
        }

        @Override
        public int getEnergyStored() {
            return TileEntityElectrolyteGenerator.this.fluxAmount;
        }

        @Override
        public int getMaxEnergyStored() {
            return MAX_FLUX_STORAGE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }


        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("energy", this.getEnergyStored());
            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            TileEntityElectrolyteGenerator.this.fluxAmount = nbt.getInteger("energy");
        }
    }

    private static final class ContainerItemStackHandler
            extends ItemStackHandler
    {
        private final MyItemStackHandler parentHandler;

        public ContainerItemStackHandler(MyItemStackHandler handler) {
            super(handler.getStacksArray());
            this.parentHandler = handler;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return this.parentHandler.insertItem(slot, stack, simulate);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return this.parentHandler.getStackLimit(slot, stack);
        }

        @Override
        public void onLoad() {
            super.onLoad();
            this.stacks = this.parentHandler.getStacksArray();
        }
    }

    private final class MyItemStackHandler
            extends ItemStackHandler
    {
        public MyItemStackHandler() {
            super(14);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            this.validateSlotIndex(slot);
            if( slot < 9 && FUELS.containsKey(stack.getItem()) && this.stacks[slot] == null ) {
                return super.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return slot < 9 ? 1 : super.getStackLimit(slot, stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if( slot > 8 ) {
                return super.extractItem(slot, amount, simulate);
            }

            return null;
        }

        private boolean canAddExtraction(ItemStack stack) {
            ItemStack myStack = stack.copy();
            for( int i = 9; i < 14 && ItemStackUtils.isValid(myStack); i++ ) {
                myStack = super.insertItem(i, myStack, true);
            }

            return !ItemStackUtils.isValid(myStack);
        }

        private void addExtraction(ItemStack stack) {
            ItemStack myStack = stack.copy();
            for( int i = 9; i < 14 && ItemStackUtils.isValid(myStack); i++ ) {
                myStack = super.insertItem(i, myStack, false);
            }
        }

        private ItemStack extractInsertItem(int slot, boolean simulate) {
            if( slot < 9 ) {
                return super.extractItem(slot, 1, simulate);
            }

            return null;
        }

        ItemStack[] getStacksArray() {
            return this.stacks;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            TileEntityElectrolyteGenerator.this.containerItemHandler.onLoad();
        }
    }
}
