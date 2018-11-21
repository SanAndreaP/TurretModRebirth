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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.Arrays;

public final class RepairKitRegistry
        implements IRepairKitRegistry
{
    public static final RepairKitRegistry INSTANCE = new RepairKitRegistry();

    private final BiMap<ResourceLocation, IRepairKit> repairKitMap;
    private final NonNullList<IRepairKit> repairKitList;

    private RepairKitRegistry() {
        this.repairKitMap = HashBiMap.create();
        this.repairKitList = NonNullList.create();
    }

    @Override
    public void register(IRepairKit type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Repair Kit!", new InvalidParameterException());
            return;
        }

        if( this.repairKitMap.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of the Repair Kit %s is already registered! Use another UUID. JUST DO IT!", type.getId()), new InvalidParameterException());
            return;
        }

        this.repairKitMap.put(type.getId(), type);
        this.repairKitList.add(type);

        ItemRegistry.TURRET_REPAIRKITS.put(type.getId(), new ItemRepairKit(type));
    }

    @Override
    public void registerAll(IRepairKit... types) {
        Arrays.stream(types).forEach(this::register);
    }

    @Override
    public NonNullList<IRepairKit> getTypes() {
        return NonNullList.from(NULL_TYPE, this.repairKitMap.values().toArray(new IRepairKit[0]));
    }

    @Override
    @Nonnull
    public IRepairKit getType(ResourceLocation id) {
        return MiscUtils.defIfNull(this.repairKitMap.get(id), NULL_TYPE);
    }

    @Override
    @Nonnull
    public IRepairKit getType(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemRepairKit ) {
            return ((ItemRepairKit) stack.getItem()).kit;
        }

        return NULL_TYPE;
    }

    @Override
    public ItemStack getItem(ResourceLocation id) {
        if( !this.repairKitMap.getOrDefault(id, NULL_TYPE).isValid() ) {
            throw new IllegalArgumentException("Cannot get repair kit item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_REPAIRKITS.get(id), 1);
    }

    private static final IRepairKit NULL_TYPE = new IRepairKit()
    {
        @Override public ResourceLocation getId() { return new ResourceLocation("null"); }
        @Override public float getHealAmount() { return 0; }
        @Override public boolean isApplicable(ITurretInst turret) { return false; }
        @Override public boolean isValid() { return false; }
    };
}
