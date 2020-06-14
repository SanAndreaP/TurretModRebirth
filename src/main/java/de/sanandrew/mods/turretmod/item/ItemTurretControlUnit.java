/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class ItemTurretControlUnit
        extends Item
{
    private static final int EE_NAME_COUNT = 5;

    private long prevDisplayNameTime = 0;
    private int nameId = 0;

    public ItemTurretControlUnit() {
        super();
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setTranslationKey(TmrConstants.ID + ":turret_control_unit");
        this.setRegistryName(TmrConstants.ID, "turret_control_unit");
    }

    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        long currDisplayNameTime = System.currentTimeMillis();
        if( this.prevDisplayNameTime + 1000 < currDisplayNameTime ) {
            if( MiscUtils.RNG.randomInt(20) != 0 ) {
                this.nameId = 0;
            } else {
                this.nameId = MathHelper.ceil(MiscUtils.RNG.randomInt(EE_NAME_COUNT - 1) + 2);
            }
        }

        this.prevDisplayNameTime = currDisplayNameTime;

        if( this.nameId < 1 ) {
            return super.getItemStackDisplayName(stack);
        } else {
            return LangUtils.translate(String.format("%s.name.%d", this.getTranslationKey(), this.nameId));
        }
    }
}
