/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.repairkits;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.item.repairkits.delegate.RegenRepairKit;
import de.sanandrew.mods.turretmod.item.repairkits.delegate.SimpleRepairKit;
import net.minecraft.util.ResourceLocation;

public final class RepairKits
{
    public static final IRepairKit STD_MK_1 = new SimpleRepairKit(new ResourceLocation(TmrConstants.ID, "standard_repair_kit_mk1"), 10.0F);
    public static final IRepairKit STD_MK_2 = new SimpleRepairKit(new ResourceLocation(TmrConstants.ID, "standard_repair_kit_mk2"), 20.0F);
    public static final IRepairKit STD_MK_3 = new SimpleRepairKit(new ResourceLocation(TmrConstants.ID, "standard_repair_kit_mk3"), 30.0F);
    public static final IRepairKit STD_MK_4 = new SimpleRepairKit(new ResourceLocation(TmrConstants.ID, "standard_repair_kit_mk4"), 40.0F);

    public static final IRepairKit REG_MK_1 = new RegenRepairKit(new ResourceLocation(TmrConstants.ID, "regeneration_repair_kit_mk1"), 45 * 20, 0);

    private RepairKits() { }

    public static void register(IRepairKitRegistry registry) {
        registry.registerAll(STD_MK_1, STD_MK_2, STD_MK_3, STD_MK_4, REG_MK_1);
    }
}
