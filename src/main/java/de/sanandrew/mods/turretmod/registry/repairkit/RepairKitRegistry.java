/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.repairkit;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.TurretRepairKit;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RepairKitRegistry
        implements IRepairKitRegistry
{
    public static final RepairKitRegistry INSTANCE = new RepairKitRegistry();

    private final Map<UUID, TurretRepairKit> kitsFromUUID;
    private final Map<TurretRepairKit, UUID> uuidFromKits;
    private final List<TurretRepairKit> kits;

    private RepairKitRegistry() {
        this.kitsFromUUID = new HashMap<>();
        this.uuidFromKits = new HashMap<>();
        this.kits = new ArrayList<>();
    }

    @Override
    public boolean register(TurretRepairKit type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Repair Kit!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Repair Kit %s has an empty/NULL name! Cannot register the Void.", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getUUID() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Repair Kit %s has no UUID! How am I supposed to differentiate all the screws?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( this.kitsFromUUID.containsKey(type.getUUID()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of the Repair Kit %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        this.kitsFromUUID.put(type.getUUID(), type);
        this.uuidFromKits.put(type, type.getUUID());
        this.kits.add(type);

        return true;
    }

    @Override
    public List<TurretRepairKit> getRegisteredTypes() {
        return new ArrayList<>(this.kits);
    }

    @Override
    @Nonnull
    public TurretRepairKit getType(UUID uuid) {
        return MiscUtils.defIfNull(this.kitsFromUUID.get(uuid), EMPTY_REPKIT);
    }

    @Override
    public UUID getTypeId(TurretRepairKit type) {
        return this.uuidFromKits.get(type);
    }

    @Override
    @Nonnull
    public TurretRepairKit getType(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isItem(stack, ItemRegistry.REPAIR_KIT) ) {
            NBTTagCompound nbt = stack.getTagCompound();
            if( nbt != null ) {
                if( nbt.hasKey("repKitType") ) {
                    String typeUUID = nbt.getString("repKitType");
                    try {
                        return this.getType(UUID.fromString(typeUUID));
                    } catch( IllegalArgumentException ex ) {
                        return EMPTY_REPKIT;
                    }
                }
            }
        }

        return EMPTY_REPKIT;
    }

    private static final UUID EMPTY = UuidUtils.EMPTY_UUID;

    private static final TurretRepairKit EMPTY_REPKIT = new TurretRepairKit()
    {
        @Override public String getName() { return "EMPTY"; }
        @Override public UUID getUUID() { return EMPTY; }
        @Override public float getHealAmount() { return 0; }
        @Override public boolean isApplicable(ITurretInst turret) { return false; }
        @Override public ResourceLocation getModel() { return null; }
    };
}
