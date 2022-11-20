/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.UpgradeRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemUpgrade
        extends Item
{
    public final IUpgrade upgrade;

    public ItemUpgrade(IUpgrade upgrade) {
        super(new Properties().tab(TmrItemGroups.UPGRADES));
        this.upgrade = upgrade;

        //TODO: figure out if overrides are still necessary
//        this.addPropertyOverride(new ResourceLocation("onTurret"), (stack, world, entity) -> entity instanceof EntityTurret ? 1 : 0);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack iStack, @Nullable World level, List<ITextComponent> lines, ITooltipFlag flag) {
        IUpgrade       prereq   = this.upgrade.getDependantOn();
        ITurret[]      turretWL = this.upgrade.getApplicableTurrets();
        Range<Integer> rng      = this.upgrade.getTierRange();

        boolean hasPrereq = prereq != null && prereq.isValid();
        boolean hasTurretWL = turretWL != null && turretWL.length > 0;
        boolean hasRng = rng != null && rng.getMinimum() > 0;

        if( TurretModRebirth.PROXY.isSneakPressed() ) {
            if( hasPrereq ) {
                lines.add(new TranslationTextComponent(Lang.ITEM_UPGRADE.get("requires")));
                lines.add(new StringTextComponent(String.format("  %s", UpgradeRegistry.INSTANCE.getItem(prereq.getId()).getDisplayName().getString())).withStyle(TextFormatting.GOLD));
            }
            if( hasTurretWL ) {
                lines.add(new TranslationTextComponent(Lang.ITEM_UPGRADE.get("applicable_to")));
                for( ITurret t : turretWL ) {
                    lines.add(new StringTextComponent(String.format("  %s", TurretRegistry.INSTANCE.getItem(t.getId()).getDisplayName().getString())).withStyle(TextFormatting.GOLD));
                }
            }
            if( hasRng ) {
                int from = rng.getMinimum();
                int to = rng.getMaximum();

                if( from == to ) {
                    lines.add(new TranslationTextComponent(Lang.ITEM_UPGRADE.get("tier_range_single")));
                    lines.add(new StringTextComponent(String.format("  %d", from)).withStyle(TextFormatting.GOLD));
                } else {
                    lines.add(new TranslationTextComponent(Lang.ITEM_UPGRADE.get("tier_range_multiple")));
                    lines.add(new StringTextComponent(String.format("  %d - %d", from, to)).withStyle(TextFormatting.GOLD));
                }
            }
        } else if( hasPrereq || hasTurretWL || hasRng ) {
            lines.add(new TranslationTextComponent(Lang.ITEM_UPGRADE.get("shift_details")).withStyle(TextFormatting.ITALIC));
        }
    }

    //    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        IUpgrade       prereq   = this.upgrade.getDependantOn();
//        ITurret[]      turretWL = this.upgrade.getApplicableTurrets();
//        Range<Integer> rng      = this.upgrade.getTierRange();
//
//        boolean hasPrereq = prereq != null && prereq.isValid();
//        boolean hasTurretWL = turretWL != null && turretWL.length > 0;
//        boolean hasRng = rng != null && rng.getMinimum() > 0;
//
//        if( TurretModRebirth.proxy.isPlayerPressingShift() ) {
//            if( hasPrereq ) {
//                tooltip.add(LangUtils.translate(Lang.ITEM_UPGRADE.get("requires")));
//                tooltip.add(ChatFormatting.GOLD + String.format("  %s", UpgradeRegistry.INSTANCE.getItem(prereq.getId()).getDisplayName()));
//            }
//            if( hasTurretWL ) {
//                tooltip.add(LangUtils.translate(Lang.ITEM_UPGRADE.get("applicable_to")));
//                for( ITurret t : turretWL ) {
//                    tooltip.add(ChatFormatting.GOLD + String.format("  %s", TurretRegistry.INSTANCE.getItem(t.getId()).getDisplayName()));
//                }
//            }
//            if( hasRng ) {
//                int from = rng.getMinimum();
//                int to = rng.getMaximum();
//
//                if( from == to ) {
//                    tooltip.add(LangUtils.translate(Lang.ITEM_UPGRADE.get("tier_range_single")));
//                    tooltip.add(ChatFormatting.GOLD + String.format("  %d", from));
//                } else {
//                    tooltip.add(LangUtils.translate(Lang.ITEM_UPGRADE.get("tier_range_multiple")));
//                    tooltip.add(ChatFormatting.GOLD + String.format("  %d - %d", from, to));
//                }
//            }
//        } else if( hasPrereq || hasTurretWL || hasRng ) {
//            tooltip.add(ChatFormatting.ITALIC + LangUtils.translate(Lang.ITEM_UPGRADE.get("shift_details")));
//        }
//    }
}
