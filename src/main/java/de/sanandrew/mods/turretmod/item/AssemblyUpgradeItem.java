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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.inventory.container.AmmoCartridgeContainer;
import de.sanandrew.mods.turretmod.inventory.container.AssemblyFilterContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class AssemblyUpgradeItem
        extends Item
{
    AssemblyUpgradeItem() {
        super(new Properties().tab(TmrItemGroups.MISC));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(new TranslationTextComponent(this.getDescriptionId() + ".ttip").withStyle(TextFormatting.GRAY));
    }

    static class Simple
        extends AssemblyUpgradeItem
    {
        Simple() {
            super();
        }
    }

    public static class Filter
        extends AssemblyUpgradeItem
    {
        public static final String NBT_FILTER_ITEMS = "FilterItems";

        Filter() {
            super();
        }

        @Override
        public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
            super.appendHoverText(stack, level, tooltip, flagIn);

            if( getFilterStacks(stack).stream().anyMatch(ItemStackUtils::isValid) ) {
                tooltip.add(new TranslationTextComponent(this.getDescriptionId() + ".conf").withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY));
            } else {
                tooltip.add(new TranslationTextComponent(this.getDescriptionId() + ".inst.1").withStyle(TextFormatting.GRAY));
                tooltip.add(new TranslationTextComponent(this.getDescriptionId() + ".inst.2").withStyle(TextFormatting.GRAY));
            }
        }

        @Nonnull
        @Override
        public ActionResult<ItemStack> use(World level, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
            if( !level.isClientSide ) {
                if( player.isShiftKeyDown() ) {
                    setFilterStacks(player.getItemInHand(hand), null);
                    player.inventoryMenu.broadcastChanges();
                } else if( player instanceof ServerPlayerEntity ) {
                    ITextComponent title = player.getItemInHand(hand).getHoverName();
                    NetworkHooks.openGui((ServerPlayerEntity) player, new AssemblyFilterContainer.Provider(hand, title),
                                         pb -> pb.writeBoolean(hand == Hand.MAIN_HAND));
                }
            }

            return super.use(level, player, hand);
        }

        public static NonNullList<ItemStack> getFilterStacks(@Nonnull ItemStack stack) {
            NonNullList<ItemStack> stacks = getEmptyInv();
            CompoundNBT tag = stack.getTag();

            if( tag != null && tag.contains(NBT_FILTER_ITEMS) ) {
                ItemStackUtils.readItemStacksFromTag(stacks, tag.getList(NBT_FILTER_ITEMS, Constants.NBT.TAG_COMPOUND));
            }

            return stacks;
        }

        public static void setFilterStacks(@Nonnull ItemStack stack, NonNullList<ItemStack> inv) {
            CompoundNBT nbt = MiscUtils.get(stack.getTag(), CompoundNBT::new);

            if( inv == null || inv.isEmpty() ) {
                if( nbt.contains(NBT_FILTER_ITEMS) ) {
                    nbt.remove(NBT_FILTER_ITEMS);
                }
            } else {
                ListNBT list = ItemStackUtils.writeItemStacksToTag(inv, 1);
                nbt.put(NBT_FILTER_ITEMS, list);
            }
            stack.setTag(nbt);
        }

        public static NonNullList<ItemStack> getEmptyInv() {
            return NonNullList.withSize(18, ItemStack.EMPTY);
        }
    }
}
