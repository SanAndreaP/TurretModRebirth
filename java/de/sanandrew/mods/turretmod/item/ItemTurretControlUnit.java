/**
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
import net.minecraft.util.MathHelper;

public class ItemTurretControlUnit
        extends Item
{
    private long prevDisplayNameTime = 0;
    private int nameId = 0;

    public ItemTurretControlUnit() {
        super();

        this.setUnlocalizedName(TurretMod.MOD_ID + ":turretControlUnit");
        this.setTextureName(TurretMod.MOD_ID + ":tcu");
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        long currDisplayNameTime = System.currentTimeMillis();
        if( this.prevDisplayNameTime + 1000 < currDisplayNameTime ) {
            double indFloat = Math.pow(2, SAPUtils.RNG.nextInt(5)) / Math.pow(2, 4) * 5.0D;
            this.nameId = MathHelper.ceiling_double_int(indFloat);
        }
        this.prevDisplayNameTime = currDisplayNameTime;
        return SAPUtils.translatePreFormat("%s.name.%d", this.getUnlocalizedName(), this.nameId);
    }
}
