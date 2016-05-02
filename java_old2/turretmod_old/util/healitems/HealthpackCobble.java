/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util.healitems;

import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretHealthpack;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class HealthpackCobble
        implements TurretHealthpack
{
    private static final ItemStack ITEM = new ItemStack(Blocks.cobblestone);

    @Override
    public String getName() {
        return "cobble";
    }

    @Override
    public float getAmount() {
        return 10;
    }

    @Override
    public ItemStack getHealItem() {
        return ITEM;
    }

    @Override
    public boolean isApplicablToTurret(Turret turret) {
        return turret instanceof EntityTurretCrossbow;
    }
}
