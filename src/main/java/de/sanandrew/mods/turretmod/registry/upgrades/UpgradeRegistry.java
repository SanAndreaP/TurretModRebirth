/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemUpgrade;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncUpgradeInst;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.*;

public final class UpgradeRegistry
        implements IUpgradeRegistry
{
    public static final UpgradeRegistry INSTANCE = new UpgradeRegistry();
    public static final IUpgrade EMPTY_UPGRADE = new UpgradeRegistry.EmptyUpgrade();
    private static final IUpgrade NULL_TYPE;

    private Map<ResourceLocation, IUpgrade> upgradeFromRL;
    private Collection<IUpgrade> upgrades;

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
    public void register(IUpgrade type) {
        this.upgradeFromRL.put(type.getId(), type);

        ItemRegistry.TURRET_UPGRADES.put(type.getId(), new ItemUpgrade(type));
    }

    @Override
    public IUpgrade getType(ResourceLocation id) {
        return this.upgradeFromRL.getOrDefault(id, NULL_TYPE);
    }

    @Override
    public IUpgrade getType(@Nonnull ItemStack item) {
        if( !ItemStackUtils.isValid(item) || !(item.getItem() instanceof ItemUpgrade) ) {
            return NULL_TYPE;
        }

        return ((ItemUpgrade) item.getItem()).upgrade;
    }

    @Override
    public Collection<IUpgrade> getTypes() {
        return this.upgrades;
    }

    @Override
    @Nonnull
    public ItemStack getItem(ResourceLocation id) {
        if( !this.getType(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get upgrade item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_UPGRADES.get(id), 1);
    }

    @Override
    public void syncWithServer(ITurretInst turret, ResourceLocation upgradeId) {
        PacketRegistry.sendToServer(new PacketSyncUpgradeInst(turret, upgradeId));
    }

    @Override
    public void syncWithClients(ITurretInst turret, ResourceLocation upgradeId) {
        EntityLiving turretL = turret.get();
        if( !turretL.world.isRemote ) {
            PacketRegistry.sendToAllAround(new PacketSyncUpgradeInst(turret, upgradeId), turretL.world.provider.getDimension(), turretL.posX, turretL.posY, turretL.posZ, 64.0D);
        }
    }

    @Override
    public IUpgrade getEmptyUpgrade() {
        return EMPTY_UPGRADE;
    }

    static final class EmptyUpgrade
            implements IUpgrade
    {
        private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.empty");

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
