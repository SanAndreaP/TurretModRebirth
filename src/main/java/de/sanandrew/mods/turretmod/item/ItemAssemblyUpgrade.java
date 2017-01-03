/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class ItemAssemblyUpgrade
        extends Item
{
    public ItemAssemblyUpgrade() {
        super();
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        super.addInformation(stack, player, lines, advInfo);

        lines.add(Lang.translate(this.getUnlocalizedName() + ".ttip"));
    }

    public static class Automation
        extends ItemAssemblyUpgrade
    {
        public Automation() {
            super();
            this.setUnlocalizedName(TurretModRebirth.ID + ":turret_assembly_auto");
        }
    }

    public static class Speed
        extends ItemAssemblyUpgrade
    {
        public Speed() {
            super();
            this.setUnlocalizedName(TurretModRebirth.ID + ":turret_assembly_speed");
        }
    }

    public static class Filter
        extends ItemAssemblyUpgrade
    {
        public Filter() {
            super();
            this.setUnlocalizedName(TurretModRebirth.ID + ":turret_assembly_filter");
        }

        @Override
        @SuppressWarnings("unchecked")
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
            super.addInformation(stack, player, lines, advInfo);

            NBTTagCompound nbt = stack.getTagCompound();
            if( nbt != null && nbt.hasKey("filterStacks") ) {
                lines.add(TextFormatting.ITALIC + Lang.translate(this.getUnlocalizedName() + ".conf"));
            } else {
                lines.add(Lang.translate(this.getUnlocalizedName() + ".inst"));
            }
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
            if( !world.isRemote ) {
                if( player.isSneaking() ) {
                    setFilterStacks(stack, null);
                    player.inventoryContainer.detectAndSendChanges();
                } else {
                    TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TASSEMBLY_FLT, 0, 0, 0);
                }
            }

            return super.onItemRightClick(stack, world, player, hand);
        }

        public static ItemStack[] getFilterStacks(ItemStack stack) {
            ItemStack[] stacks = getEmptyInv();
            NBTTagCompound nbt = stack.getTagCompound();

            if( nbt != null && nbt.hasKey("filteredStacks") ) {
                ItemStackUtils.readItemStacksFromTag(stacks, nbt.getTagList("filterStacks", Constants.NBT.TAG_COMPOUND));
            }

            return stacks;
        }

        public static void setFilterStacks(ItemStack stack, ItemStack[] inv) {
            NBTTagCompound nbt = stack.getTagCompound();
            if( nbt == null ) {
                nbt = new NBTTagCompound();
            }

            if( inv == null || inv.length < 1 ) {
                if( nbt.hasKey("filterStacks") ) {
                    nbt.removeTag("filterStacks");
                }
            } else {
                NBTTagList list = ItemStackUtils.writeItemStacksToTag(inv, 1);
                nbt.setTag("filterStacks", list);
            }
            stack.setTagCompound(nbt);
        }

        public static ItemStack[] getEmptyInv() {
            return new ItemStack[18];
        }
    }
}
