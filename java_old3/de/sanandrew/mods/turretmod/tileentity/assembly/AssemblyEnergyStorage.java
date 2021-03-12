/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.assembly;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

final class AssemblyEnergyStorage
        implements IEnergyStorage, INBTSerializable<NBTTagCompound>
{
    int fluxAmount;
    private int prevFluxAmount;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergyStored() - this.fluxAmount, Math.min(TileEntityTurretAssembly.MAX_FLUX_INSERT, maxReceive));

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
        return TileEntityTurretAssembly.MAX_FLUX_STORAGE;
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
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("fluxAmount", this.fluxAmount);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.fluxAmount = nbt.getInteger("fluxAmount");
    }
}
