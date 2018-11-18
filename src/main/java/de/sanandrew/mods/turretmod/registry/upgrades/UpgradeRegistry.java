/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class UpgradeRegistry
        implements IUpgradeRegistry
{
    public static final UpgradeRegistry INSTANCE = new UpgradeRegistry();
    public static final IUpgrade EMPTY_UPGRADE = new UpgradeRegistry.EmptyUpgrade();
    private static final IUpgrade NULL_UPGRADE;

    private BiMap<ResourceLocation, IUpgrade> upgrades;

    static {
        INSTANCE.register(EMPTY_UPGRADE);
        NULL_UPGRADE = new SimpleUpgrade("null") {
            @Override
            public boolean isValid() {
                return false;
            }
        };
    }

    private UpgradeRegistry() {
        this.upgrades = HashBiMap.create();
    }

    @Override
    public void register(IUpgrade upgrade) {
        this.upgrades.put(upgrade.getId(), upgrade);

        ItemRegistry.TURRET_UPGRADES.put(upgrade.getId(), new ItemUpgrade(upgrade));
    }

    @Override
    public void registerAll(IUpgrade... upgrade) {
        Arrays.stream(upgrade).forEach(this::register);
    }

    @Override
    public IUpgrade getUpgrade(ResourceLocation id) {
        return this.upgrades.getOrDefault(id, NULL_UPGRADE);
    }

    @Override
    public IUpgrade getUpgrade(@Nonnull ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) || !(stack.getItem() instanceof ItemUpgrade) ) {
            return NULL_UPGRADE;
        }

        return ((ItemUpgrade) stack.getItem()).upgrade;
    }

    @Override
    public List<IUpgrade> getUpgrades() {
        return new ArrayList<>(this.upgrades.values());
    }

    @Override
    @Nonnull
    public ItemStack getUpgradeItem(ResourceLocation id) {
        return new ItemStack(ItemRegistry.TURRET_UPGRADES.getOrDefault(id, ItemRegistry.TURRET_UPGRADES.get(EMPTY_UPGRADE.getId())));
    }

    @Override
    @Nonnull
    public ItemStack getUpgradeItem(IUpgrade upgrade) {
        return getUpgradeItem(upgrade.getId());
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
