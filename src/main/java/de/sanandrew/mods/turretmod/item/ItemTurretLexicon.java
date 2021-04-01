/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.init.Resources;
import de.sanandrew.mods.turretmod.init.TmrItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTurretLexicon
        extends Item
{
    public static final boolean PATCHOULI_AVAILABLE = ModList.get().isLoaded("patchouli");

    public ItemTurretLexicon() {
        super(new Properties().group(TmrItemGroups.MISC));
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        if( !PATCHOULI_AVAILABLE ) {
            tooltip.add(new StringTextComponent("Patchouli is required in order to view this content!").mergeStyle(TextFormatting.RED));
        }

        super.addInformation(stack, world, tooltip, flag);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if( PATCHOULI_AVAILABLE ) {
            if( player instanceof ServerPlayerEntity ) {
                vazkii.patchouli.api.PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, Resources.PATCHOULI.resource);

                return ActionResult.resultSuccess(player.getHeldItem(hand));
            }
        }

        return super.onItemRightClick(world, player, hand);
    }
}
