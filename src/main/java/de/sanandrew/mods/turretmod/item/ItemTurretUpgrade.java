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
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemTurretUpgrade
        extends Item
{
    public ItemTurretUpgrade() {
        super();
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
        this.setUnlocalizedName(TmrConstants.ID + ":turret_upgrade");
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(@Nonnull ItemStack stack, World world, List lines, ITooltipFlag advInfo) {
        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
        lines.add(Lang.translate(String.format("%s.%s.name", this.getUnlocalizedName(stack), upg.getName())));
        super.addInformation(stack, world, lines, advInfo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if( this.isInCreativeTab(tab) ) {
            items.addAll(UpgradeRegistry.INSTANCE.getRegisteredTypes().stream().map(UpgradeRegistry.INSTANCE::getUpgradeItem).collect(Collectors.toList()));
        }
    }
}
