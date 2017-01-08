/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemTurretUpgrade
        extends Item
{
    public ItemTurretUpgrade() {
        super();
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
        this.setUnlocalizedName(TmrConstants.ID + ":turret_upgrade");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
        lines.add(Lang.translate(String.format("%s.%s.name", this.getUnlocalizedName(stack), upg.getName())));
        super.addInformation(stack, player, lines, advInfo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List itmList) {
        for( TurretUpgrade upgrade : UpgradeRegistry.INSTANCE.getRegisteredTypes() ) {
            itmList.add(UpgradeRegistry.INSTANCE.getUpgradeItem(upgrade));
        }
    }
}
