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
    public void register(IUpgrade obj) {
        this.upgradeFromRL.put(obj.getId(), obj);

        ItemRegistry.TURRET_UPGRADES.put(obj.getId(), new ItemUpgrade(obj));
    }

    @Nonnull
    @Override
    public IUpgrade getDefaultObject() {
        return NULL_TYPE;
    }

    @Override
    public IUpgrade getObject(ResourceLocation id) {
        return this.upgradeFromRL.getOrDefault(id, NULL_TYPE);
    }

    @Override
    public IUpgrade getObject(@Nonnull ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) || !(stack.getItem() instanceof ItemUpgrade) ) {
            return NULL_TYPE;
        }

        return ((ItemUpgrade) stack.getItem()).upgrade;
    }

    @Override
    public boolean isType(@Nonnull ItemStack stack, ResourceLocation id) {
        IUpgrade itmType = this.getObject(stack);

        return itmType.isValid() && itmType.getId().equals(id);
    }

    @Override
    public boolean isType(@Nonnull ItemStack stack, IUpgrade obj) {
        IUpgrade itmType = this.getObject(stack);

        return obj.isValid() && itmType.isValid() && itmType.getId().equals(obj.getId());
    }

    @Override
    public Collection<IUpgrade> getObjects() {
        return this.upgrades;
    }

    @Override
    @Nonnull
    public ItemStack getItem(ResourceLocation id) {
        if( !this.getObject(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get upgrade item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_UPGRADES.get(id), 1);
    }

    @Override
    public void syncWithServer(ITurretInst turretInst, ResourceLocation id) {
        PacketRegistry.sendToServer(new PacketSyncUpgradeInst(turretInst, id));
    }

    @Override
    public void syncWithClients(ITurretInst turretInst, ResourceLocation id) {
        EntityLiving turretL = turretInst.get();
        if( !turretL.world.isRemote ) {
            PacketRegistry.sendToAllAround(new PacketSyncUpgradeInst(turretInst, id), turretL.world.provider.getDimension(), turretL.posX, turretL.posY, turretL.posZ, 64.0D);
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
        private static final ResourceLocation BOOK_ENTRY_ID = new ResourceLocation(TmrConstants.ID, "upgrade_empty");

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public Range<Integer> getTierRange() {
            return Range.is(-1);
        }

        @Override
        public ResourceLocation getBookEntryId() {
            return BOOK_ENTRY_ID;
        }
    }
}
