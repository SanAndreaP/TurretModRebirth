/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UpgradeRegistry
{
    public static final UUID UPG_STORAGE_I = UUID.fromString("1749478F-2A8E-4C56-BC03-6C76CB5DE921");
    public static final UUID UPG_STORAGE_II = UUID.fromString("DEFFE281-A2F5-488A-95C1-E9A3BB6E0DD1");
    public static final UUID UPG_STORAGE_III = UUID.fromString("50DB1AC3-1CCD-4CB0-AD5A-0777C548655D");
    public static final UUID AMMO_STORAGE = UUID.fromString("2C850D81-0C01-47EA-B3AD-86E4FF523521");
    public static final UUID HEALTH_I = UUID.fromString("13218AB7-3DA6-461D-9882-13482291164B");
    public static final UUID HEALTH_II = UUID.fromString("612A78CB-ED0C-4990-B1F3-041BE8171B1A");
    public static final UUID HEALTH_III = UUID.fromString("2239A7BB-DD38-4764-9FFC-6E04934F9B3C");
    public static final UUID HEALTH_IV = UUID.fromString("FF6CC60F-EEC7-40C5-92D8-A614DFA06777");
    public static final UUID RELOAD_I = UUID.fromString("4ED4E813-E2D8-43E9-B499-9911E214C5E9");
    public static final UUID RELOAD_II = UUID.fromString("80877F84-F03D-4ED8-A9D3-BAF6DF4F3BF1");

    public static final UpgradeRegistry INSTANCE = new UpgradeRegistry();

    private Map<UUID, TurretUpgrade> upgradeToUuidMap = new HashMap<>();
    private Map<TurretUpgrade, UUID> uuidToUpgradeMap = new HashMap<>();
    private List<TurretUpgrade> upgradeList = new ArrayList<>();

    public void registerUpgrade(UUID uuid, TurretUpgrade upgrade) {
        if( this.upgradeToUuidMap.containsKey(uuid) ) {
            this.upgradeList.set(this.upgradeList.indexOf(this.upgradeToUuidMap.get(uuid)), upgrade);
        } else {
            this.upgradeList.add(upgrade);
        }
        this.upgradeToUuidMap.put(uuid, upgrade);
        this.uuidToUpgradeMap.put(upgrade, uuid);
    }

    public TurretUpgrade getUpgrade(UUID uuid) {
        return this.upgradeToUuidMap.get(uuid);
    }

    public TurretUpgrade getUpgrade(int index) {
        return this.upgradeList.get(index);
    }

    public UUID getUpgradeUUID(TurretUpgrade upg) {
        return this.uuidToUpgradeMap.get(upg);
    }

    public TurretUpgrade getUpgrade(ItemStack stack) {
        if( stack == null ) {
            return null;
        }

        int dmg = stack.getItemDamage() - 1;
        if( dmg >= 0 && dmg < this.upgradeList.size() ) {
            return this.upgradeList.get(dmg);
        } else {
            return null;
        }
    }

    public int getRegisteredCount() {
        return this.upgradeList.size();
    }

    public TurretUpgrade[] getRegisteredUpgrades() {
        return this.upgradeList.toArray(new TurretUpgrade[this.upgradeList.size()]);
    }

    public void initialize() {
        this.registerUpgrade(UPG_STORAGE_I, new UpgradeUpgStorage.UpgradeStorageMK1());
        this.registerUpgrade(UPG_STORAGE_II, new UpgradeUpgStorage.UpgradeStorageMK2());
        this.registerUpgrade(UPG_STORAGE_III, new UpgradeUpgStorage.UpgradeStorageMK3());
        this.registerUpgrade(AMMO_STORAGE, new UpgradeAmmoStorage());
        this.registerUpgrade(HEALTH_I, new UpgradeHealth.UpgradeHealthMK1());
        this.registerUpgrade(HEALTH_II, new UpgradeHealth.UpgradeHealthMK2());
        this.registerUpgrade(HEALTH_III, new UpgradeHealth.UpgradeHealthMK3());
        this.registerUpgrade(HEALTH_IV, new UpgradeHealth.UpgradeHealthMK4());
        this.registerUpgrade(RELOAD_I, new UpgradeReloadTime.UpgradeReloadTimeMK1());
        this.registerUpgrade(RELOAD_II, new UpgradeReloadTime.UpgradeReloadTimeMK2());
    }

    public ItemStack getUpgradeItem(UUID uuid) {
        return new ItemStack(ItemRegistry.turretUpgrade, 1, this.upgradeList.indexOf(this.upgradeToUuidMap.get(uuid)) + 1);
    }

    public ItemStack getUpgradeItem(TurretUpgrade upgrade) {
        return new ItemStack(ItemRegistry.turretUpgrade, 1, this.upgradeList.indexOf(upgrade) + 1);
    }
}
