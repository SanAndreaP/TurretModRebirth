/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.repairkit;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;

public class RepairKits
{
    public static final IRepairKit STANDARD_MK1 = new RepairKitStandard("1", 5.0F);
    public static final IRepairKit STANDARD_MK2 = new RepairKitStandard("2", 10.0F);
    public static final IRepairKit STANDARD_MK3 = new RepairKitStandard("3", 15.0F);
    public static final IRepairKit STANDARD_MK4 = new RepairKitStandard("4", 20.0F);
    public static final IRepairKit REGEN_MK1 = new RepairKitRegeneration("1", 0.5F, 0, 900);

    public static void initialize(IRepairKitRegistry registry) {
        IRegistry.registerAll(registry,
                              STANDARD_MK1, STANDARD_MK2, STANDARD_MK3, STANDARD_MK4, REGEN_MK1);
    }
}
