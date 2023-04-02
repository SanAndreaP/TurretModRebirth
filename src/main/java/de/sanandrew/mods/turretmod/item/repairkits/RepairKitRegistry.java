/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.repairkits;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RepairKitRegistry
        implements IRepairKitRegistry
{
    public static final RepairKitRegistry INSTANCE = new RepairKitRegistry();

    private final Map<ResourceLocation, IRepairKit> repairKits;
    private final Collection<IRepairKit> repairKitCollection;

    private static final IRepairKit EMPTY = new IRepairKit()
    {
        private final ResourceLocation id = new ResourceLocation("null");

        @Override
        public float getBaseRestorationAmount() { return 0; }

        @Nonnull
        @Override
        public ResourceLocation getId() { return this.id; }

        @Override
        public boolean isApplicable(@Nonnull ITurretEntity turret) { return false; }

        @Override
        public boolean isValid() { return false; }
    };

    private RepairKitRegistry() {
        this.repairKits = new HashMap<>();

        this.repairKitCollection = Collections.unmodifiableCollection(this.repairKits.values());
    }

    @Override
    public void register(@Nonnull IRepairKit obj) {
        if( this.repairKits.containsKey(obj.getId()) ) {
            String msg = String.format("The repair kit ID %s is already registered!", obj.getId());
            TmrConstants.LOG.log(Level.ERROR, msg, new InvalidParameterException());
            return;
        }

        if( obj.getBaseRestorationAmount() < 0 ) {
            String msg = String.format("Repair kit ID %s provides less than 0 HP!", obj.getId());
            TmrConstants.LOG.log(Level.ERROR, msg, new InvalidParameterException());
            return;
        }

        this.repairKits.put(obj.getId(), obj);

        ItemRegistry.TURRET_REPAIRKITS.put(obj.getId(), new RepairKitItem(obj.getId()));
    }

    @Override
    public void registerItems(DeferredRegister<Item> register, String modId) {
        ItemRegistry.TURRET_REPAIRKITS.entrySet().stream().filter(e -> e.getKey().getNamespace().equals(modId))
                                      .forEach(e -> register.register(e.getKey().getPath(), e::getValue));
    }

    @Nonnull
    @Override
    public Collection<IRepairKit> getAll() {
        return this.repairKitCollection;
    }

    @Nonnull
    @Override
    public IRepairKit get(ResourceLocation id) {
        return this.repairKits.getOrDefault(id, EMPTY);
    }

    @Nonnull
    @Override
    public IRepairKit get(ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof RepairKitItem ) {
            return ((RepairKitItem) stack.getItem()).getRepairKit();
        }

        return EMPTY;
    }

    @Nonnull
    @Override
    public IRepairKit getDefault() {
        return EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack getItem(ResourceLocation id, int count) {
        if( !this.get(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get repair kit item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_REPAIRKITS.get(id), count);
    }

    public ItemStack getItem(@Nonnull IRepairKit repairKit, int count) {
        return this.getItem(repairKit.getId(), count);
    }
}
