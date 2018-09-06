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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class UpgradeRegistry
        implements IUpgradeRegistry
{

    public static final UpgradeRegistry INSTANCE = new UpgradeRegistry();
    public static final UUID EMPTY = UuidUtils.EMPTY_UUID;
    private static final ITurretUpgrade EMPTY_INST;

    private Map<UUID, ITurretUpgrade> uuidToUpgradeMap;
    private Map<ITurretUpgrade, UUID> upgradeToUuidMap;
    private List<String> errored;

    static {
        INSTANCE.registerUpgrade(UpgradeRegistry.EMPTY, new UpgradeRegistry.EmptyUpgrade());
        EMPTY_INST = UpgradeRegistry.INSTANCE.uuidToUpgradeMap.get(UpgradeRegistry.EMPTY);
    }

    private UpgradeRegistry() {
        this.uuidToUpgradeMap = new HashMap<>();
        this.upgradeToUuidMap = new LinkedHashMap<>();
        this.errored = new ArrayList<>();
    }

    @Override
    public void registerUpgrade(UUID uuid, ITurretUpgrade upgrade) {
        this.uuidToUpgradeMap.put(uuid, upgrade);
        this.upgradeToUuidMap.put(upgrade, uuid);
    }

    @Override
    public ITurretUpgrade getUpgrade(UUID uuid) {
        return MiscUtils.defIfNull(this.uuidToUpgradeMap.get(uuid), EMPTY_INST);
    }

    @Override
    public UUID getUpgradeId(ITurretUpgrade upg) {
        return MiscUtils.defIfNull(this.upgradeToUuidMap.get(upg), EMPTY);
    }

    @Override
    public UUID getUpgradeId(@Nonnull ItemStack stack) {
        if( !stack.hasTagCompound() ) {
            return EMPTY;
        }

        String uid = MiscUtils.defIfNull(stack.getTagCompound(), new NBTTagCompound()).getString("upgradeId");
        try {
            return UUID.fromString(uid);
        } catch( IllegalArgumentException ex ) {
            if( !this.errored.contains(uid) ) {
                TmrConstants.LOG.log(Level.WARN, "There was an error at parsing the UUID for a turret_placer upgrade item!", ex);
                this.errored.add(uid);
            }
            return EMPTY;
        }
    }

    @Override
    public ITurretUpgrade getUpgrade(@Nonnull ItemStack stack) {
        if( !ItemStackUtils.isItem(stack, ItemRegistry.TURRET_UPGRADE) || !stack.hasTagCompound() ) {
            return EMPTY_INST;
        }

        return this.getUpgrade(this.getUpgradeId(stack));
    }

    @Override
    public List<ITurretUpgrade> getUpgrades() {
        return new ArrayList<>(this.upgradeToUuidMap.keySet());
    }

    @Override
    @Nonnull
    public ItemStack getUpgradeItem(UUID uuid) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("upgradeId", uuid.toString());
        ItemStack stack = new ItemStack(ItemRegistry.TURRET_UPGRADE, 1);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Override
    @Nonnull
    public ItemStack getUpgradeItem(ITurretUpgrade upgrade) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("upgradeId", this.getUpgradeId(upgrade).toString());
        ItemStack stack = new ItemStack(ItemRegistry.TURRET_UPGRADE, 1);
        stack.setTagCompound(nbt);

        return stack;
    }

    static final class EmptyUpgrade
            implements ITurretUpgrade
    {
        private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/empty");

        @Override
        public String getName() {
            return "empty";
        }

        @Override
        public ResourceLocation getModel() {
            return ITEM_MODEL;
        }

        @Override
        public Range<Integer> getTierRange() {
            return Range.is(-1);
        }
    }
}
