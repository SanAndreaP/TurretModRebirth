/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemAssemblyUpgrade
        extends Item
{
    public ItemAssemblyUpgrade(String type) {
        super();
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_assembly_" + type);
        this.setTextureName(TurretModRebirth.ID + ":upgrades/assembly_" + type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        super.addInformation(stack, player, lines, advInfo);

        lines.add(StatCollector.translateToLocal(this.getUnlocalizedName() + ".ttip"));
    }
}
