/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.tileentity.assembly;

import net.minecraft.util.IntArray;

public final class AssemblySyncData
        extends IntArray
{
    public static final int ENERGY_STORED         = 0;
    public static final int CRAFTING_PROGRESS     = 1;
    public static final int MAX_CRAFTING_PROGRESS = 2;
    public static final int CRAFTING_COUNT = 3;

    private final TurretAssemblyEntity boundTile;

    public AssemblySyncData(TurretAssemblyEntity tile) {
        super(4);
        this.boundTile = tile;
    }

    public AssemblySyncData() {
        this(null);
    }

    @Override
    public int get(int index) {
        if( this.boundTile != null ) {
            switch( index ) {
                case ENERGY_STORED:
                    return this.boundTile.energyStorage.fluxAmount;
                case CRAFTING_PROGRESS:
                    return this.boundTile.getCurrentRecipeId() != null ? this.boundTile.getTicksCrafted() : 0;
                case MAX_CRAFTING_PROGRESS:
                    return this.boundTile.getCurrentRecipeId() != null ? this.boundTile.getMaxTicksCrafted() : 1;
                case CRAFTING_COUNT:
                    if( this.boundTile.getCurrentRecipeId() != null ) {
                        return this.boundTile.isAutomated() ? -1 : this.boundTile.getCraftingAmount();
                    } else {
                        return 0;
                    }
                default: // no-op
            }
        }

        return super.get(index);
    }

    public int getEnergyStored() {
        return this.get(ENERGY_STORED);
    }

    @SuppressWarnings("java:S3518")
    public float getCraftingProgress() {
        float max = Math.max(this.get(MAX_CRAFTING_PROGRESS), 1);
        return this.get(CRAFTING_PROGRESS) / max;
    }

    public int getCraftingAmount() {
        return this.get(CRAFTING_COUNT);
    }
}
