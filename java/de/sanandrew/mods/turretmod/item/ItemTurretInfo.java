/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTurretInfo
        extends Item
{
    public ItemTurretInfo() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_info");
//        this.setTextureName(TurretModRebirth.ID + ":upgrades/assembly_filter");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if( world.isRemote ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TINFO, -1, -1, 0);
        }

        return super.onItemRightClick(stack, world, player);
    }
}
