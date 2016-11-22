/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class ItemTurretInfo
        extends Item
{
    public ItemTurretInfo() {
        super();
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_info");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advInfo) {
        String[] lines = Lang.translate(this.getUnlocalizedName() + ".desc").split("\\\\n");
        list.addAll(Arrays.asList(lines));

        super.addInformation(stack, player, list, advInfo);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if( world.isRemote ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TINFO, -1, -1, 0);
        }

        return super.onItemRightClick(itemStack, world, player, hand);
    }
}
