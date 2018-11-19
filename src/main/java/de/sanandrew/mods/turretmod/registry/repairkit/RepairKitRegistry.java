/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.repairkit;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemRepairKit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RepairKitRegistry
        implements IRepairKitRegistry
{
    public static final RepairKitRegistry INSTANCE = new RepairKitRegistry();

    private final BiMap<ResourceLocation, IRepairKit> repairKits;

    private RepairKitRegistry() {
        this.repairKits = HashBiMap.create();
    }

    @Override
    public void register(IRepairKit type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Repair Kit!", new InvalidParameterException());
            return;
        }

        if( this.repairKits.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of the Repair Kit %s is already registered! Use another UUID. JUST DO IT!", type.getId()), new InvalidParameterException());
            return;
        }

        this.repairKits.put(type.getId(), type);

        ItemRegistry.TURRET_REPAIRKITS.put(type.getId(), new ItemRepairKit(type));
    }

    @Override
    public void registerAll(IRepairKit... types) {
        Arrays.stream(types).forEach(this::register);
    }

    @Override
    public List<IRepairKit> getTypes() {
        return new ArrayList<>(this.repairKits.values());
    }

    @Override
    @Nonnull
    public IRepairKit getType(ResourceLocation id) {
        return MiscUtils.defIfNull(this.repairKits.get(id), EMPTY_REPKIT);
    }

    @Override
    @Nonnull
    public IRepairKit getType(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemRepairKit ) {
            return ((ItemRepairKit) stack.getItem()).kit;
        }

        return EMPTY_REPKIT;
    }

    @Nonnull
    @Override
    public ItemStack getItem(IRepairKit type) {
        return type != null && type.isValid() && this.repairKits.containsValue(type) ? new ItemStack(ItemRegistry.TURRET_REPAIRKITS.get(type.getId()), 1) : ItemStack.EMPTY;
    }

    private static final IRepairKit EMPTY_REPKIT = new IRepairKit()
    {
        @Override public ResourceLocation getId() { return new ResourceLocation("null"); }
        @Override public float getHealAmount() { return 0; }
        @Override public boolean isApplicable(ITurretInst turret) { return false; }
        @Override public boolean isValid() { return false; }
    };
}
