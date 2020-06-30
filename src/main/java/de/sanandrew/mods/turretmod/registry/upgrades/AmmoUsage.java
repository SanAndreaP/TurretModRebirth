/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.util.ResourceLocation;

public abstract class AmmoUsage
        implements IUpgrade
{
    private final ResourceLocation id;
    private static final ResourceLocation BOOK_ENTRY_ID = new ResourceLocation(TmrConstants.ID, "upgrade_economy");

    AmmoUsage(String name) {
        this.id = new ResourceLocation(TmrConstants.ID, "upgrade." + name);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public ResourceLocation getBookEntryId() {
        return BOOK_ENTRY_ID;
    }

    static class MK1
            extends AmmoUsage
    {
        MK1() {
            super("ammousage.1");
        }
    }

    static class MK2
            extends AmmoUsage
    {
        MK2() {
            super("ammousage.2");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.ECONOMY_I;
        }
    }

    static class MKInf
            extends AmmoUsage
    {
        MKInf() {
            super("ammousage.inf");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.ECONOMY_II;
        }
    }
}
