/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.ITurret;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeRegistry;
import dev.sanandrea.mods.turretmod.item.ItemRegistry;
import dev.sanandrea.mods.turretmod.item.ItemUpgrade;
import dev.sanandrea.mods.turretmod.item.upgrades.delegate.SimpleUpgrade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class UpgradeRegistry
        implements IUpgradeRegistry
{
    public static final UpgradeRegistry INSTANCE = new UpgradeRegistry();
    public static final IUpgrade EMPTY_UPGRADE = new EmptyUpgrade();
    private static final IUpgrade NULL_TYPE;

    private final Map<ResourceLocation, IUpgrade> upgradeFromRL;
    private final Collection<IUpgrade>            upgrades;

    static {
        INSTANCE.register(EMPTY_UPGRADE);
        NULL_TYPE = new SimpleUpgrade("null") {
            @Override
            public boolean isValid() {
                return false;
            }
        };
    }

    private UpgradeRegistry() {
        this.upgradeFromRL = new HashMap<>();
        this.upgrades = Collections.unmodifiableCollection(this.upgradeFromRL.values());
    }

    @Override
    public void register(@Nonnull IUpgrade obj) {
        this.upgradeFromRL.put(obj.getId(), obj);

        ItemRegistry.TURRET_UPGRADES.put(obj.getId(), new ItemUpgrade(obj));
    }

    @Nonnull
    @Override
    public IUpgrade getDefault() {
        return NULL_TYPE;
    }

    @Nonnull
    @Override
    public IUpgrade get(ResourceLocation id) {
        return this.upgradeFromRL.getOrDefault(id, NULL_TYPE);
    }

    @Nonnull
    @Override
    public IUpgrade get(@Nonnull ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) || !(stack.getItem() instanceof ItemUpgrade) ) {
            return NULL_TYPE;
        }

        return ((ItemUpgrade) stack.getItem()).upgrade;
    }

    @Override
    public boolean isType(@Nonnull ItemStack stack, ResourceLocation id) {
        IUpgrade itmType = this.get(stack);

        return itmType.isValid() && itmType.getId().equals(id);
    }

    @Override
    public boolean isType(@Nonnull ItemStack stack, IUpgrade obj) {
        IUpgrade itmType = this.get(stack);

        return obj.isValid() && itmType.isValid() && itmType.getId().equals(obj.getId());
    }

    @Nonnull
    @Override
    public Collection<IUpgrade> getAll() {
        return this.upgrades;
    }

    @Override
    @Nonnull
    public ItemStack getItem(ResourceLocation id, int count) {
        if( !this.get(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get upgrade item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_UPGRADES.get(id), count);
    }

    @Override
    public IUpgrade getEmptyUpgrade() {
        return EMPTY_UPGRADE;
    }

    @Override
    public boolean isApplicable(IUpgrade upgrade, ITurret turret) {
        return this.isApplicable(upgrade, turret, false);
    }

    @Override
    public boolean isApplicable(IUpgrade upgrade, ITurret turret, boolean isSpecialized) {
        if( !upgrade.isValid() ) {
            return false;
        }

        ITurret[] applicables = upgrade.getApplicableTurrets();
        boolean isEmpty = applicables == null || applicables.length == 0;

        if( isSpecialized && isEmpty ) {
            return false;
        }

        Range<Integer> tierRange = upgrade.getTierRange();
        if( tierRange != null && !tierRange.contains(turret.getTier()) ) {
            return false;
        }

        return isEmpty || Arrays.asList(applicables).contains(turret);
    }

    @Override
    public void registerItems(DeferredRegister<Item> register, final String modId) {
        ItemRegistry.TURRET_UPGRADES.entrySet().stream().filter(e -> e.getKey().getNamespace().equals(modId))
                                    .forEach(e -> register.register(e.getKey().getPath(), e::getValue));
    }

    static final class EmptyUpgrade
            implements IUpgrade
    {
        private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "empty_upgrade");

        @Nonnull
        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public Range<Integer> getTierRange() {
            return Range.is(-1);
        }

    }
}