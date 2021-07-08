package de.sanandrew.mods.turretmod.tileentity.electrolyte;

import net.minecraft.util.IntArray;
import org.apache.commons.lang3.Range;

public final class ElectrolyteSyncData
        extends IntArray
{
    public static final int            ENERGY_STORED    = 0;
    public static final int            ENERGY_GENERATED = 1;
    public static final int            EFFICIENCY       = 2;
    public static final Range<Integer> PROGRESSES       = Range.between(EFFICIENCY + 1, EFFICIENCY + 9);
    public static final Range<Integer> MAX_PROGRESSES   = Range.between(PROGRESSES.getMaximum() + 1, PROGRESSES.getMaximum() + 9);

    private static final int MAX_SIZE = MAX_PROGRESSES.getMaximum() + 1;

    private final ElectrolyteGeneratorEntity boundTile;

    public ElectrolyteSyncData(ElectrolyteGeneratorEntity tile) {
        super(MAX_SIZE);
        this.boundTile = tile;
    }

    public ElectrolyteSyncData() {
        super(MAX_SIZE);
        this.boundTile = null;
    }

    @Override
    public int get(int index) {
        if( this.boundTile != null ) {
            switch( index ) {
                case ENERGY_STORED:
                    return this.boundTile.energyStorage.fluxAmount;
                case ENERGY_GENERATED:
                    return this.boundTile.getGeneratedFlux();
                case EFFICIENCY:
                    return Float.floatToIntBits(this.boundTile.efficiency);
            }

            if( PROGRESSES.contains(index) ) {
                return this.boundTile.processes.get(index - PROGRESSES.getMinimum()).progress;
            } else if( MAX_PROGRESSES.contains(index) ) {
                return this.boundTile.processes.get(index - MAX_PROGRESSES.getMinimum()).getMaxProgress(this.boundTile.itemHandler);
            } else {
                return 0;
            }
        }

        return super.get(index);
    }

    public int getEnergyStored() {
        return this.get(ENERGY_STORED);
    }

    public int getEnergyGenerated() {
        return this.get(ENERGY_GENERATED);
    }

    public float getEfficiency() {
        return Float.intBitsToFloat(this.get(EFFICIENCY));
    }

    public int getProgress(int slot) {
        return this.get(slot + PROGRESSES.getMinimum());
    }

    public int getMaxProgress(int slot) {
        return this.get(slot + MAX_PROGRESSES.getMinimum());
    }
}
