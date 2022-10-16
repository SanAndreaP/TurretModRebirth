package de.sanandrew.mods.turretmod.tileentity.assembly;

import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteGeneratorEntity;
import net.minecraft.util.IntArray;
import org.apache.commons.lang3.Range;

public final class AssemblySyncData
        extends IntArray
{
    public static final int            ENERGY_STORED    = 0;

    private final TurretAssemblyEntity boundTile;

    public AssemblySyncData(TurretAssemblyEntity tile) {
        super(1);
        this.boundTile = tile;
    }

    public AssemblySyncData() {
        super(1);
        this.boundTile = null;
    }

    @Override
    public int get(int index) {
        if( this.boundTile != null ) {
            switch( index ) {
                case ENERGY_STORED:
                    return this.boundTile.energyStorage.fluxAmount;
            }
        }

        return super.get(index);
    }

    public int getEnergyStored() {
        return this.get(ENERGY_STORED);
    }
}
