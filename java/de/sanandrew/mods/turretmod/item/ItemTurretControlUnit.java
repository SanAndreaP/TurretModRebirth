/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
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
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setRegistryName("turret_control_unit");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        long currDisplayNameTime = System.currentTimeMillis();
        if( this.prevDisplayNameTime + 1000 < currDisplayNameTime ) {
            final int count = 5;
            double indFloat = TmrUtils.RNG.nextInt(20) != 0 ? 1 : TmrUtils.RNG.nextInt(count - 1) + 2;
            this.nameId = MathHelper.ceiling_double_int(indFloat);
        }
        this.prevDisplayNameTime = currDisplayNameTime;
        return Lang.translate("%s.name.%d", this.getUnlocalizedName(), this.nameId);
    }
}
