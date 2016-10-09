/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ItemTurretControlUnit
        extends Item
{
    private long prevDisplayNameTime = 0;
    private int nameId = 0;

    public ItemTurretControlUnit() {
        super();

        this.setUnlocalizedName(TurretMod.MOD_ID + ":turret_control_unit");
        this.setTextureName(TurretMod.MOD_ID + ":tcu");
        this.setCreativeTab(TmrCreativeTabs.MISC);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        long currDisplayNameTime = System.currentTimeMillis();
        if( this.prevDisplayNameTime + 1000 < currDisplayNameTime ) {
            final int count = 5;
            double indFloat = SAPUtils.RNG.nextInt(20) != 0 ? 1 : SAPUtils.RNG.nextInt(count - 1) + 2;
            this.nameId = MathHelper.ceiling_double_int(indFloat);
        }
        this.prevDisplayNameTime = currDisplayNameTime;
        return SAPUtils.translatePreFormat("%s.name.%d", this.getUnlocalizedName(), this.nameId);
    }
}
