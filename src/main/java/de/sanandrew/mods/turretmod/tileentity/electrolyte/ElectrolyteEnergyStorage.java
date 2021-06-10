/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolyte;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

final class ElectrolyteEnergyStorage
        implements IEnergyStorage, INBTSerializable<CompoundNBT>
{
    int fluxAmount;
    private int prevFluxAmount;
    int fluxExtractPerTick;
    private int fluxBuffer;

    void resetFluxExtract() {
        this.fluxExtractPerTick = Math.min(this.fluxAmount, ElectrolyteGeneratorTileEntity.MAX_FLUX_EXTRACT);
    }

    void updatePrevFlux() {
        this.prevFluxAmount = this.fluxAmount;
    }

    boolean hasFluxChanged() {
        return this.fluxAmount != this.prevFluxAmount;
    }

    void emptyBuffer() {
        if( this.fluxBuffer > 0 ) {
            int fluxSubtracted = Math.min(ElectrolyteGeneratorTileEntity.MAX_FLUX_STORAGE - this.fluxAmount, Math.min(ElectrolyteGeneratorTileEntity.MAX_FLUX_GENERATED, this.fluxBuffer));
            this.fluxBuffer -= fluxSubtracted;
            this.fluxAmount += fluxSubtracted;
        }
    }

    void fillBuffer(int amount) {
        this.fluxBuffer += amount;
    }

    boolean isBufferEmpty() {
        return this.fluxBuffer <= ElectrolyteGeneratorTileEntity.MAX_FLUX_GENERATED;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.fluxExtractPerTick, Math.min(ElectrolyteGeneratorTileEntity.MAX_FLUX_EXTRACT, maxExtract));

        if( !simulate ) {
            this.fluxAmount -= energyExtracted;
            this.fluxExtractPerTick -= energyExtracted;
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return this.fluxAmount;
    }

    @Override
    public int getMaxEnergyStored() {
        return ElectrolyteGeneratorTileEntity.MAX_FLUX_STORAGE;
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("fluxAmount", this.fluxAmount);
        nbt.putInt("fluxBuffer", this.fluxBuffer);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.fluxAmount = nbt.getInt("fluxAmount");
        this.fluxBuffer = nbt.getInt("fluxBuffer");
    }
}
