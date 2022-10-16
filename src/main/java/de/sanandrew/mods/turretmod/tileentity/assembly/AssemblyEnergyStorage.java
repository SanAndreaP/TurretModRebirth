/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.assembly;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public final class AssemblyEnergyStorage
        implements IEnergyStorage, INBTSerializable<CompoundNBT>
{
    public static final int MAX_FLUX_STORAGE = 75_000;
    public static final int MAX_FLUX_INSERT = 500;

    int fluxAmount;
    private int prevFluxAmount;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergyStored() - this.fluxAmount, Math.min(MAX_FLUX_INSERT, maxReceive));

        if( !simulate ) {
            this.fluxAmount += energyReceived;
        }

        return energyReceived;
    }

    void updatePrevFlux() {
        this.prevFluxAmount = this.fluxAmount;
    }

    boolean hasFluxChanged() {
        return this.prevFluxAmount != this.fluxAmount;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return this.fluxAmount;
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("FluxAmount", this.fluxAmount);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.fluxAmount = nbt.getInt("FluxAmount");
    }
}
