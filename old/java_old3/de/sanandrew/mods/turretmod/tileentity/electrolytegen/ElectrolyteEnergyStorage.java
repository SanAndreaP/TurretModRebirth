/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

final class ElectrolyteEnergyStorage
        implements IEnergyStorage, INBTSerializable<NBTTagCompound>
{
    int fluxAmount;
    private int prevFluxAmount;
    int fluxExtractPerTick;
    private int fluxBuffer;

    void resetFluxExtract() {
        this.fluxExtractPerTick = Math.min(this.fluxAmount, TileEntityElectrolyteGenerator.MAX_FLUX_EXTRACT);
    }

    void updatePrevFlux() {
        this.prevFluxAmount = this.fluxAmount;
    }

    boolean hasFluxChanged() {
        return this.fluxAmount != this.prevFluxAmount;
    }

    void emptyBuffer() {
        if( this.fluxBuffer > 0 ) {
            int fluxSubtracted = Math.min(TileEntityElectrolyteGenerator.MAX_FLUX_STORAGE - this.fluxAmount, Math.min(TileEntityElectrolyteGenerator.MAX_FLUX_GENERATED, this.fluxBuffer));
            this.fluxBuffer -= fluxSubtracted;
            this.fluxAmount += fluxSubtracted;
        }
    }

    void fillBuffer(int amount) {
        this.fluxBuffer += amount;
    }

    boolean isBufferEmpty() {
        return this.fluxBuffer <= TileEntityElectrolyteGenerator.MAX_FLUX_GENERATED;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.fluxExtractPerTick, Math.min(TileEntityElectrolyteGenerator.MAX_FLUX_EXTRACT, maxExtract));

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
        return TileEntityElectrolyteGenerator.MAX_FLUX_STORAGE;
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
        nbt.setInteger("fluxAmount", this.fluxAmount);
        nbt.setInteger("fluxBuffer", this.fluxBuffer);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.fluxAmount = nbt.getInteger("fluxAmount");
        this.fluxBuffer = nbt.getInteger("fluxBuffer");
    }
}
