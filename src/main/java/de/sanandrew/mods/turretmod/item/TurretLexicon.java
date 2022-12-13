/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.Resources;
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

public class TurretLexicon
        extends Item
{
    public static final boolean PATCHOULI_AVAILABLE = ModList.get().isLoaded("patchouli");

    public TurretLexicon() {
        super(new Properties().tab(TmrItemGroups.MISC));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        if( !PATCHOULI_AVAILABLE ) {
            tooltip.add(new StringTextComponent("Patchouli is required in order to view this content!").withStyle(TextFormatting.RED));
        }

        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if( PATCHOULI_AVAILABLE && player instanceof ServerPlayerEntity ) {
            vazkii.patchouli.api.PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, Resources.PATCHOULI);

            return ActionResult.success(player.getItemInHand(hand));
        }

        return super.use(world, player, hand);
    }
}
