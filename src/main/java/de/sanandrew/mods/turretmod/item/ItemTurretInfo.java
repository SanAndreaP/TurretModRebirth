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
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ItemTurretInfo
        extends Item
{
    public ItemTurretInfo() {
        super();
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setUnlocalizedName(TmrConstants.ID + ":turret_info");
        this.setRegistryName(TmrConstants.ID, "turret_info");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, World world, List<String> list, ITooltipFlag advInfo) {
        String[] lines = LangUtils.translate(this.getUnlocalizedName() + ".desc").split("\\\\n");
        list.addAll(Arrays.asList(lines));

        super.addInformation(stack, world, list, advInfo);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if( world.isRemote ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TINFO, -1, -1, 0);
        }

        return super.onItemRightClick(world, player, hand);
    }
}
