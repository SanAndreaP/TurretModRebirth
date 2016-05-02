package de.sanandrew.mods.turretmod.registry.medpack;

import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import org.apache.logging.log4j.Level;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
public class RepairKitRegistry
{
    public static final RepairKitRegistry INSTANCE = new RepairKitRegistry();

    public static final UUID STANDARD_MK1 = UUID.fromString("89db7dd5-2ded-4e58-96dd-07e47bffa919");
    public static final UUID REGEN_MK1 = UUID.fromString("4c44ca3d-4f32-44e6-bf2e-11189ec88a73");

    private final Map<UUID, TurretRepairKit> kitsFromUUID;

    private RepairKitRegistry() {
        this.kitsFromUUID = new HashMap<>();
    }

    public boolean registerMedpack(TurretRepairKit type) {
        if( type == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Cannot register NULL as Repair Kit!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Repair Kit %s has an empty/NULL name! Cannot register the Void.", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getUUID() == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Repair Kit %s has no UUID! How am I supposed to differentiate all the screws?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( this.kitsFromUUID.containsKey(type.getUUID()) ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("The UUID of the Repair Kit %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        this.kitsFromUUID.put(type.getUUID(), type);

        return true;
    }

    public List<TurretRepairKit> getRegisteredTypes() {
        return new ArrayList<>(this.kitsFromUUID.values());
    }

    public TurretRepairKit getRepairKit(UUID uuid) {
        return this.kitsFromUUID.get(uuid);
    }

    public void initialize() {
        this.registerMedpack(new RepairKitStandard("standard_1", STANDARD_MK1, 5.0F, "repair_kit_std1"));
        this.registerMedpack(new RepairKitStandard("standard_2", UUID.fromString("36477c40-3eb3-4997-a2ec-3a9a37be86d5"), 10.0F, "repair_kit_std2"));
        this.registerMedpack(new RepairKitStandard("standard_3", UUID.fromString("c9ecc3ea-8bfa-4e42-b401-e0475a23d7f6"), 15.0F, "repair_kit_std3"));
        this.registerMedpack(new RepairKitStandard("standard_4", UUID.fromString("6b3cbd27-1efa-4ee2-b8c8-35d2988361b9"), 20.0F, "repair_kit_std4"));
        this.registerMedpack(new RepairKitRegeneration("regen_1", REGEN_MK1, 0.5F, "repair_kit_reg1", 0, 900));
    }
}
