/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity.electrolyte;

import de.sanandrew.mods.sanlib.lib.power.EnergyHelper;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ElectrolyteGeneratorTileEntity
        extends TileEntity
        implements ITickableTileEntity, INamedContainerProvider, INameable
{
    public static final int MAX_FLUX_STORAGE = 500_000;
    public static final int MAX_FLUX_EXTRACT = 1_000;
    public static final int MAX_FLUX_GENERATED = 200;

    protected final ElectrolyteSyncData syncData = new ElectrolyteSyncData(this);

    public final ElectrolyteProcessList processes = new ElectrolyteProcessList();

    public float efficiency;

    private ITextComponent customName;

    final ElectrolyteInventory itemHandler = new ElectrolyteInventory(this::getLevel);
    final ElectrolyteEnergyStorage energyStorage = new ElectrolyteEnergyStorage();

    public int getGeneratedFlux() {
        return this.efficiency < 0.1F ? 0 : Math.min(200, (int) Math.round(Math.pow(1.6D, this.efficiency) / (68.0D + (127433.0D / 177119.0D)) * 80.0D));
    }

    public ElectrolyteGeneratorTileEntity() {
        super(BlockRegistry.ELECTROLYTE_GENERATOR_ENTITY);
    }

    @Override
    public void tick() {
        if( this.level != null && !this.level.isClientSide ) {
            this.energyStorage.resetFluxExtract();

            this.energyStorage.emptyBuffer();

            if( this.energyStorage.isBufferEmpty() && this.energyStorage.fluxAmount < MAX_FLUX_STORAGE ) {
                int fluxEff = this.getGeneratedFlux();

                this.efficiency = 0.0F;

                for( int i = 0; i < ElectrolyteInventory.INPUT_SLOT_COUNT; i++ ) {
                    this.processSlot(i);
                }

                if( this.efficiency > 0.1F ) {
                    this.energyStorage.fillBuffer(fluxEff);
                }
            }

            this.transferEnergy();

            this.energyStorage.updatePrevFlux();
        }
    }

    private void processSlot(int slot) {
        ElectrolyteProcess process = this.processes.get(slot);

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

                process = ElectrolyteProcess.EMPTY;

                this.setChanged();

                if( this.level != null ) {
                    this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                }
            } else {
                process.incrProgress();

                this.setChanged();
            }

            this.efficiency += process.getEfficiency(this.itemHandler);
        }

        if( !process.isValid() && this.level != null ) {
            IElectrolyteRecipe recipe = ElectrolyteManager.INSTANCE.getFuel(this.level, this.itemHandler.extractInsertItem(slot, 1, true));
            if( recipe != null ) {
                process = new ElectrolyteProcess(recipe.getId(), this.itemHandler.extractInsertItem(slot, 1, false));

                this.setChanged();

                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
            }
        }

        this.processes.set(slot, process);
    }

    private void transferEnergy() {
        if( this.level != null && this.energyStorage.fluxExtractPerTick > 0 ) {
            for( Direction direction : Direction.values() ) {
                if( direction == Direction.UP ) {
                    continue;
                }
                Direction otherDir = direction.getOpposite();

                BlockPos adjPos = this.worldPosition.offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
                TileEntity te = this.level.getBlockEntity(adjPos);

                if( te == null || !EnergyHelper.canConnectEnergy(te, otherDir) ) {
                    continue;
                }

                long extractable = EnergyHelper.extractEnergy(this, direction, MAX_FLUX_EXTRACT, true);
                long receivable = EnergyHelper.receiveEnergy(te, otherDir, extractable, false);

                EnergyHelper.extractEnergy(this, direction, receivable, false);

                //noinspection ConstantConditions
                if( this.energyStorage.fluxExtractPerTick <= 0 ) {
                    break;
                }
            }
        }
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);

        ListNBT progressesNbt = nbt.getList("Progress", Constants.NBT.TAG_COMPOUND);
        for( int i = 0, max = progressesNbt.size(); i < max; i++ ) {
            CompoundNBT progNbt = progressesNbt.getCompound(i);
            byte slot = progNbt.getByte("ProgressSlot");
            this.processes.set(slot, new ElectrolyteProcess(progNbt));
        }

        this.energyStorage.deserializeNBT(nbt.getCompound("CapabilityEnergy"));
        this.itemHandler.deserializeNBT(nbt.getCompound("CapabilityInventory"));

        if( nbt.contains("CustomName", Constants.NBT.TAG_STRING) ) {
            this.customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT nbt) {
        super.save(nbt);

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

        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, -1, this.processes.serializeProcessStacks(new CompoundNBT()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.processes.deserializeProcessStacks(pkt.getTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return this.processes.serializeProcessStacks(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.processes.deserializeProcessStacks(tag);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getName();
    }

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

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new ElectrolyteGeneratorContainer(id, playerInventory, this.itemHandler, this.syncData, this.processes);
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }
}
