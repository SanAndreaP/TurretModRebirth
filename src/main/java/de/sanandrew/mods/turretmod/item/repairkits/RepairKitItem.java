/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.repairkits;

import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import de.sanandrew.mods.turretmod.item.TmrItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class RepairKitItem
        extends Item
{
    public final ResourceLocation repairKitId;
    private IRepairKit repairKitCache;

    public RepairKitItem(ResourceLocation repairKitId) {
        super(new Properties().tab(TmrItemGroups.MISC));

        this.repairKitId = repairKitId;
    }

    public IRepairKit getRepairKit() {
        if( this.repairKitCache == null ) {
            this.repairKitCache = RepairKitRegistry.INSTANCE.get(this.repairKitId);
        }

        return this.repairKitCache;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        IRepairKit repairKit = this.getRepairKit();
        repairKit.appendHoverText(stack, world, tooltip, flag);

        if( flag.isAdvanced() ) {
            tooltip.add(new StringTextComponent("type-id: " + repairKit.getId()).withStyle(TextFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, world, tooltip, flag);
    }
}
