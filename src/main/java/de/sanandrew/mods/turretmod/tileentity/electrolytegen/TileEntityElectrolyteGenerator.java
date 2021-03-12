/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import de.sanandrew.mods.sanlib.lib.power.EnergyHelper;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.inventory.ContainerElectrolyteGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityElectrolyteGenerator
        extends TileEntity
        implements ITickableTileEntity, INamedContainerProvider, INameable
{
    public static final int MAX_FLUX_STORAGE = 500_000;
    public static final int MAX_FLUX_EXTRACT = 1_000;
    public static final int MAX_FLUX_GENERATED = 200;

    public static final int SYNC_SIZE = 2 + 9 + 9;

    protected final ElectrolyteData generatorData = new ElectrolyteData(this);

    public final NonNullList<ElectrolyteProcess> processes = NonNullList.withSize(9, ElectrolyteProcess.NULL_PROCESS);

    public float efficiency;

    private ITextComponent customName;

    final ElectrolyteInventory itemHandler = new ElectrolyteInventory(this::getWorld);
    final ElectrolyteEnergyStorage energyStorage = new ElectrolyteEnergyStorage();
//    public final ElectrolyteItemStackHandler containerItemHandler = new ElectrolyteItemStackHandler(this.itemHandler);

    public int getGeneratedFlux() {
        return this.efficiency < 0.1F ? 0 : Math.min(200, (int) Math.round(Math.pow(1.6D, this.efficiency) / (68.0D + (127433.0D / 177119.0D)) * 80.0D));
    }

    public TileEntityElectrolyteGenerator() {
        super(BlockRegistry.ELECTROLYTE_GENERATOR_ENTITY);
    }

    @Override
    public void tick() {
        if( this.world != null && !this.world.isRemote ) {
            this.energyStorage.resetFluxExtract();

//            float prevEffective = this.efficiency;

            this.energyStorage.emptyBuffer();

            if( this.energyStorage.isBufferEmpty() && this.energyStorage.fluxAmount < MAX_FLUX_STORAGE ) {
                int fluxEff = this.getGeneratedFlux();

                this.efficiency = 0.0F;

                for( int i = 0, max = this.processes.size(); i < max; i++ ) {
                    this.processSlot(i);
                }

                if( this.efficiency > 0.1F ) {
                    this.energyStorage.fillBuffer(fluxEff);
                }
            }

//            if( prevEffective < this.efficiency - 0.01F || prevEffective > this.efficiency + 0.01F ) {
//                this.markDirty();
//            }

            this.transferEnergy();

//            if( this.energyStorage.hasFluxChanged() ) {
//                this.doSync = true;
//            }

//            if( this.doSync ) {
//                TurretModRebirth.network.sendToAllNear(new PacketSyncTileEntity(this),
//                                                       new PacketDistributor.TargetPoint(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64.0D, this.world.getDimensionKey()));
//            }

            this.energyStorage.updatePrevFlux();
        }
    }

    private void processSlot(int slot) {
        ElectrolyteProcess process     = this.processes.get(slot);
//        boolean            markAsDirty = false;

        if( process.isValid() ) {
            ItemStack trashStack = process.getTrashStack(this.itemHandler);
            if( ItemStackUtils.isValid(trashStack) && this.itemHandler.isOutputFull(trashStack) ) {
                return;
            }

            ItemStack treasureStack = process.getTreasureStack(this.itemHandler);
            if( ItemStackUtils.isValid(treasureStack) && this.itemHandler.isOutputFull(treasureStack) ) {
                return;
            }

            if( process.hasFinished(this.itemHandler) ) {
                if( ItemStackUtils.isValid(trashStack) ) {
                    this.itemHandler.addExtraction(trashStack);
                }
                if( ItemStackUtils.isValid(treasureStack) ) {
                    this.itemHandler.addExtraction(treasureStack);
                }

                process = ElectrolyteProcess.NULL_PROCESS;
//                markAsDirty = true;

                this.markDirty();
            } else {
                process.incrProgress();
            }

            this.efficiency += process.getEfficiency(this.itemHandler);
//            this.doSync = true;
        }

        if( !process.isValid() ) {
            IElectrolyteRecipe recipe = ElectrolyteManager.INSTANCE.getFuel(this.world, this.itemHandler.extractInsertItem(slot, true));
            if( recipe != null ) {
                process = new ElectrolyteProcess(recipe.getId(), this.itemHandler.extractInsertItem(slot, false));

//                markAsDirty = true;
//                this.doSync = true;
                this.markDirty();
            }
        }

        this.processes.set(slot, process);

//        if( markAsDirty ) {
//            this.markDirty();
//        }
    }

    private void transferEnergy() {
        if( this.world != null && this.energyStorage.fluxExtractPerTick > 0 ) {
            for( Direction direction : Direction.values() ) {
                if( direction == Direction.UP ) {
                    continue;
                }
                Direction otherDir = direction.getOpposite();

                BlockPos adjPos = this.pos.add(direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
                TileEntity te = this.world.getTileEntity(adjPos);

                if( te == null || !EnergyHelper.canConnectEnergy(te, otherDir) ) {
                    continue;
                }

                long extractable = EnergyHelper.extractEnergy(this, direction, MAX_FLUX_EXTRACT, true);
                long receivable = EnergyHelper.receiveEnergy(te, otherDir, extractable, false);

                EnergyHelper.extractEnergy(this, direction, receivable, false);

                if( this.energyStorage.fluxExtractPerTick <= 0 ) {
                    break;
                }
            }
        }
    }

//    private CompoundNBT writeNbt(CompoundNBT nbt) {
//        ListNBT progressesNbt = new ListNBT();
//        for( int i = 0, max = this.processes.length; i < max; i++ ) {
//            if( this.processes[i] != null ) {
//                CompoundNBT progNbt = new CompoundNBT();
//                progNbt.setByte("progressSlot", (byte) i);
//                this.processes[i].writeToNBT(progNbt);
//                progressesNbt.appendTag(progNbt);
//            }
//        }
//        nbt.setTag("progress", progressesNbt);
//
//        nbt.setTag("cap_energy", this.energyStorage.serializeNBT());
//
//        if( this.hasCustomName() ) {
//            nbt.setString("customName", this.customName);
//        }
//
//        return nbt;
//    }

//    private void readNbt(NBTTagCompound nbt) {
//        Arrays.fill(this.processes, null);
//        NBTTagList progressesNbt = nbt.getTagList("progress", Constants.NBT.TAG_COMPOUND);
//        for( int i = 0, max = progressesNbt.tagCount(); i < max; i++ ) {
//            NBTTagCompound progNbt = progressesNbt.getCompoundTagAt(i);
//            byte slot = progNbt.getByte("progressSlot");
//            this.processes[slot] = new ElectrolyteProcess(progNbt);
//        }
//
//        this.energyStorage.deserializeNBT(nbt.getCompoundTag("cap_energy"));
//
//        if( nbt.hasKey("customName", Constants.NBT.TAG_STRING) ) {
//            this.customName = nbt.getString("customName");
//        }
//    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);

        ListNBT progressesNbt = nbt.getList("Progress", Constants.NBT.TAG_COMPOUND);
        for( int i = 0, max = progressesNbt.size(); i < max; i++ ) {
            CompoundNBT progNbt = progressesNbt.getCompound(i);
            byte slot = progNbt.getByte("ProgressSlot");
            this.processes.set(slot, new ElectrolyteProcess(progNbt));
        }

        this.energyStorage.deserializeNBT(nbt.getCompound("CapabilityEnergy"));
        this.itemHandler.deserializeNBT(nbt.getCompound("CapabilityInventory"));

        if( nbt.contains("CustomName", Constants.NBT.TAG_STRING) ) {
            this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
        }

//        if( nbt.hasKey("customName", Constants.NBT.TAG_STRING) ) {
//            this.customName = nbt.getString("customName");
//        }

//        this.readNbt(nbt);

    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);

        ListNBT progressesNbt = new ListNBT();
        for( int i = 0, max = this.processes.size(); i < max; i++ ) {
            CompoundNBT progNbt = new CompoundNBT();
            progNbt.putByte("ProgressSlot", (byte) i);
            this.processes.get(i).write(progNbt);
            progressesNbt.add(progNbt);
        }
        nbt.put("Progress", progressesNbt);

        nbt.put("CapabilityEnergy", this.energyStorage.serializeNBT());
        nbt.put("CapabilityInventory", this.itemHandler.serializeNBT());

        if( this.customName != null ) {
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }

//        return nbt;


//        this.writeNbt(nbt);

        return nbt;
    }

//    @Override
//    public SPacketUpdateTileEntity getUpdatePacket() {
//        NBTTagCompound nbt = new NBTTagCompound();
//        this.writeNbt(nbt);
//        return new SPacketUpdateTileEntity(this.pos, 0, nbt);
//    }
//
//    @Override
//    public NBTTagCompound getUpdateTag() {
//        return this.writeNbt(super.getUpdateTag());
//    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getTranslatedName();
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
//        NBTTagCompound nbt = pkt.getNbtCompound();
//        this.readNbt(nbt);
//    }
//
//    @Override
//    public void handleUpdateTag(NBTTagCompound tag) {
//        super.handleUpdateTag(tag);
//        this.readNbt(tag);
//    }

//    @Override
//    public boolean hasCapability(Capability<?> capability, Direction facing) {
//        if( facing != EnumFacing.UP ) {
//            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
//        }
//
//        return super.hasCapability(capability, facing);
//    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing != Direction.UP ) {
                return this.itemHandler.getLO();
            }
        } else if( capability == CapabilityEnergy.ENERGY ) {
            if( facing != Direction.UP ) {
                return LazyOptional.of(() -> (T) this.energyStorage);
            }
        }

        return super.getCapability(capability, facing);
    }

//    @Override
//    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
//        return oldState.getBlock() != newSate.getBlock();
//    }

//    public String getName() {
//        return this.hasCustomName() ? this.customName : BlockRegistry.ELECTROLYTE_GENERATOR.getTranslationKey() + ".name";
//    }
//
//    public boolean hasCustomName() {
//        return this.customName != null && !this.customName.isEmpty();
//    }


    @Nonnull
    @Override
    public ITextComponent getName() {
        return this.getDisplayName();
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.customName;
    }

//    public boolean isUseableByPlayer(PlayerEntity player) {
//        return this.world != null && this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
//    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new ContainerElectrolyteGenerator(id, playerInventory, this.itemHandler, this.generatorData);
    }

//    @Override
//    public void toBytes(ByteBuf buf) {
//        buf.writeInt(this.energyStorage.fluxAmount);
//        buf.writeFloat(this.efficiency);
//        for( ElectrolyteProcess process : this.processes ) {
//            if( process != null ) {
//                buf.writeBoolean(true);
//                process.writeToByteBuf(buf);
//            } else {
//                buf.writeBoolean(false);
//            }
//        }
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        this.energyStorage.fluxAmount = buf.readInt();
//        this.efficiency = buf.readFloat();
//        for( int i = 0, max = this.processes.length; i < max; i++ ) {
//            if( buf.readBoolean() ) {
//                this.processes[i] = new ElectrolyteProcess(buf);
//            } else {
//                this.processes[i] = null;
//            }
//        }
//    }
//
//    @Override
//    public TileEntity getTile() {
//        return this;
//    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }
}
