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
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class ItemAssemblyUpgrade
        extends Item
{
    ItemAssemblyUpgrade() {
        super();
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(LangUtils.translate(this.getTranslationKey() + ".ttip"));
    }

    static class Automation
        extends ItemAssemblyUpgrade
    {
        Automation() {
            super();
            this.setTranslationKey(TmrConstants.ID + ":turret_assembly_auto");
            this.setRegistryName(TmrConstants.ID, "assembly_upg_auto");
        }
    }

    static class Speed
        extends ItemAssemblyUpgrade
    {
        Speed() {
            super();
            this.setTranslationKey(TmrConstants.ID + ":turret_assembly_speed");
            this.setRegistryName(TmrConstants.ID, "assembly_upg_speed");
        }
    }

    static class Redstone
            extends ItemAssemblyUpgrade
    {
        Redstone() {
            super();
            this.setTranslationKey(TmrConstants.ID + ":turret_assembly_redstone");
            this.setRegistryName(TmrConstants.ID, "assembly_upg_redstone");
        }
    }

    public static class Filter
        extends ItemAssemblyUpgrade
    {
        Filter() {
            super();
            this.setTranslationKey(TmrConstants.ID + ":turret_assembly_filter");
            this.setRegistryName(TmrConstants.ID, "assembly_upg_filter");
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
            super.addInformation(stack, worldIn, tooltip, flagIn);

            NBTTagCompound nbt = stack.getTagCompound();
            if( nbt != null && nbt.hasKey("filteredStacks") ) {
                tooltip.add(TextFormatting.ITALIC + LangUtils.translate(this.getTranslationKey() + ".conf"));
            } else {
                tooltip.addAll(Arrays.asList(LangUtils.translate(this.getTranslationKey() + ".inst").split("\\n")));
            }
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
            if( !world.isRemote ) {
                if( player.isSneaking() ) {
                    setFilterStacks(player.getHeldItem(hand), null);
                    player.inventoryContainer.detectAndSendChanges();
                } else {
                    TurretModRebirth.proxy.openGui(player, EnumGui.TASSEMBLY_FLT, 0, 0, 0);
                }
            }

            return super.onItemRightClick(world, player, hand);
        }

        public static NonNullList<ItemStack> getFilterStacks(@Nonnull ItemStack stack) {
            NonNullList<ItemStack> stacks = getEmptyInv();
            NBTTagCompound nbt = stack.getTagCompound();

            if( nbt != null && nbt.hasKey("filteredStacks") ) {
                ItemStackUtils.readItemStacksFromTag(stacks, nbt.getTagList("filteredStacks", Constants.NBT.TAG_COMPOUND));
            }

            return stacks;
        }

        public static void setFilterStacks(@Nonnull ItemStack stack, NonNullList<ItemStack> inv) {
            NBTTagCompound nbt = stack.getTagCompound();
            if( nbt == null ) {
                nbt = new NBTTagCompound();
            }

            if( inv == null || inv.size() < 1 ) {
                if( nbt.hasKey("filteredStacks") ) {
                    nbt.removeTag("filteredStacks");
                }
            } else {
                NBTTagList list = ItemStackUtils.writeItemStacksToTag(inv, 1);
                nbt.setTag("filteredStacks", list);
            }
            stack.setTagCompound(nbt);
        }

        public static NonNullList<ItemStack> getEmptyInv() {
            return NonNullList.withSize(18, ItemStackUtils.getEmpty());
        }
    }
}
