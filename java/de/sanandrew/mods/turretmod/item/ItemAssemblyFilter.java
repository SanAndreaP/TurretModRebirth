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
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class ItemAssemblyFilter
        extends Item
{
    public static final ItemStack[] EMPTY_INV = new ItemStack[18];

    public ItemAssemblyFilter() {
        super();
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
        this.setRegistryName("turret_assembly_filter");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        super.addInformation(stack, player, lines, advInfo);

        lines.add(Lang.translate(this.getUnlocalizedName() + ".ttip"));
        lines.add(Lang.translate(this.getUnlocalizedName() + ".inst"));
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("filterStacks") ) {
            lines.add(TextFormatting.ITALIC + Lang.translate(this.getUnlocalizedName() + ".conf"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if( !world.isRemote ) {
            if( player.isSneaking() ) {
                //noinspection ConstantConditions
                stack.setTagCompound(null);
                player.inventoryContainer.detectAndSendChanges();
            } else {
                TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TASSEMBLY_FLT, 0, 0, 0);
            }
        }

        return super.onItemRightClick(stack, world, player, hand);
    }

    public ItemStack[] getFilterStacks(ItemStack stack, boolean configure) {
        ItemStack[] stacks = new ItemStack[18];
        NBTTagCompound nbt = stack.getTagCompound();

        if( nbt == null && configure ) {
            nbt = new NBTTagCompound();
            NBTTagList list = TmrUtils.writeItemStacksToTag(stacks, 1);
            nbt.setTag("filterStacks", list);
            stack.setTagCompound(nbt);
        } else if( nbt != null && nbt.hasKey("filterStacks") ) {
            TmrUtils.readItemStacksFromTag(stacks, nbt.getTagList("filterStacks", Constants.NBT.TAG_COMPOUND));
        } else {
            return EMPTY_INV;
        }

        return stacks;
    }

    public void setFilterStacks(ItemStack stack, ItemStack[] inv) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt == null ) {
            nbt = new NBTTagCompound();
        }

        NBTTagList list = TmrUtils.writeItemStacksToTag(inv, 1);
        nbt.setTag("filterStacks", list);
        stack.setTagCompound(nbt);
    }
}
