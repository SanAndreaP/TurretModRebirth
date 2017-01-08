/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.repairkit;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class RepairKits
{
    public static final UUID STANDARD_MK1 = UUID.fromString("89db7dd5-2ded-4e58-96dd-07e47bffa919");
    public static final UUID STANDARD_MK2 = UUID.fromString("36477c40-3eb3-4997-a2ec-3a9a37be86d5");
    public static final UUID STANDARD_MK3 = UUID.fromString("c9ecc3ea-8bfa-4e42-b401-e0475a23d7f6");
    public static final UUID STANDARD_MK4 = UUID.fromString("6b3cbd27-1efa-4ee2-b8c8-35d2988361b9");
    public static final UUID REGEN_MK1 = UUID.fromString("4c44ca3d-4f32-44e6-bf2e-11189ec88a73");

    public static void initialize(IRepairKitRegistry registry) {
        registry.register(new RepairKitStandard("standard_1", STANDARD_MK1, 5.0F, new ResourceLocation(TmrConstants.ID, "repair_kits/repair_kit_std1")));
        registry.register(new RepairKitStandard("standard_2", STANDARD_MK2, 10.0F, new ResourceLocation(TmrConstants.ID, "repair_kits/repair_kit_std2")));
        registry.register(new RepairKitStandard("standard_3", STANDARD_MK3, 15.0F, new ResourceLocation(TmrConstants.ID, "repair_kits/repair_kit_std3")));
        registry.register(new RepairKitStandard("standard_4", STANDARD_MK4, 20.0F, new ResourceLocation(TmrConstants.ID, "repair_kits/repair_kit_std4")));
        registry.register(new RepairKitRegeneration("regen_1", REGEN_MK1, 0.5F, new ResourceLocation(TmrConstants.ID, "repair_kits/repair_kit_reg1"), 0, 900));
    }
}
