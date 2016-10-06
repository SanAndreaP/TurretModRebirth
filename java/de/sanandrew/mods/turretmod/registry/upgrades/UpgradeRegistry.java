/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class UpgradeRegistry
{
    public static final UUID EMPTY = new UUID(0L, 0L);
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
    public static final UUID SMART_TGT = UUID.fromString("12435AB9-5AA3-4DB9-9B76-7943BA71597A");
    public static final UUID UPG_ECONOMY_I = UUID.fromString("A8F29058-C8B7-400D-A7F4-4CEDE627A7E8");
    public static final UUID UPG_ECONOMY_II = UUID.fromString("2A76A2EB-0EA3-4EB0-9EC2-61E579361306");
    public static final UUID UPG_ECONOMY_INF = UUID.fromString("C3CF3EE9-8314-4766-A5E0-6033DB3EE9DB");

    public static final UpgradeRegistry INSTANCE = new UpgradeRegistry();

    private Map<UUID, TurretUpgrade> upgradeToUuidMap = new HashMap<>();
    private Map<TurretUpgrade, UUID> uuidToUpgradeMap = new HashMap<>();
    private List<TurretUpgrade> upgradeList = new ArrayList<>();

    private static TurretUpgrade emptyInst;

    private List<String> errored = new ArrayList<>();

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
        return MiscUtils.defIfNull(this.upgradeToUuidMap.get(uuid), emptyInst);
    }

    public UUID getUpgradeUUID(TurretUpgrade upg) {
        return MiscUtils.defIfNull(this.uuidToUpgradeMap.get(upg), EMPTY);
    }

    public TurretUpgrade getUpgrade(ItemStack stack) {
        if( stack == null || !stack.hasTagCompound() ) {
            return emptyInst;
        }

        String uid = MiscUtils.defIfNull(stack.getTagCompound(), new NBTTagCompound()).getString("upgradeId");
        try {
            return this.getUpgrade(UUID.fromString(uid));
        } catch( IllegalArgumentException ex ) {
            if( !this.errored.contains(uid) ) {
                TurretModRebirth.LOG.log(Level.WARN, "There was an error at parsing the UUID for a turret upgrade item!", ex);
                this.errored.add(uid);
            }
            return emptyInst;
        }
    }

    public TurretUpgrade[] getRegisteredTypes() {
        return this.upgradeList.toArray(new TurretUpgrade[this.upgradeList.size()]);
    }

    public void initialize() {
        this.registerUpgrade(EMPTY, new EmptyUpgrade());
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
        this.registerUpgrade(SMART_TGT, new UpgradeSmartTargeting());
        this.registerUpgrade(UPG_ECONOMY_I, new UpgradeAmmoUsage.UpgradeAmmoUseI());
        this.registerUpgrade(UPG_ECONOMY_II, new UpgradeAmmoUsage.UpgradeAmmoUseII());
        this.registerUpgrade(UPG_ECONOMY_INF, new UpgradeAmmoUsage.UpgradeAmmoUseInf());

        emptyInst = this.upgradeToUuidMap.get(EMPTY);
    }

    public ItemStack getUpgradeItem(UUID uuid) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("upgradeId", uuid.toString());
        ItemStack stack = new ItemStack(ItemRegistry.turretUpgrade, 1);
        stack.setTagCompound(nbt);

        return stack;
    }

    public ItemStack getUpgradeItem(TurretUpgrade upgrade) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("upgradeId", this.getUpgradeUUID(upgrade).toString());
        ItemStack stack = new ItemStack(ItemRegistry.turretUpgrade, 1);
        stack.setTagCompound(nbt);

        return stack;
    }

    private static final class EmptyUpgrade
            implements TurretUpgrade
    {
        private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "upgrades/empty");

        @Override
        public String getName() {
            return "empty";
        }

        @Override
        public String getModId() {
            return TurretModRebirth.ID;
        }

        @Override
        public ResourceLocation getModel() {
            return ITEM_MODEL;
        }

        @Override
        public TurretUpgrade getDependantOn() {
            return null;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_EMPTY;
        }

        @Override
        public boolean isTurretApplicable(Class<? extends EntityTurret> turretCls) {
            return false;
        }

        @Override
        public void onApply(EntityTurret turret) { }

        @Override
        public void onLoad(EntityTurret turret, NBTTagCompound nbt) { }

        @Override
        public void onSave(EntityTurret turret, NBTTagCompound nbt) { }

        @Override
        public void onRemove(EntityTurret turret) { }
    }
}
