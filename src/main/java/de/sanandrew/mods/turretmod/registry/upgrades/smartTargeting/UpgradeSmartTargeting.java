/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades.smarttargeting;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class UpgradeSmartTargeting
        implements ITurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/smart_tgt");
    private final String name;

    public UpgradeSmartTargeting() {
        this.name = "smart_tgt";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResourceLocation getModel() {
        return ITEM_MODEL;
    }

    @Override
    public void onApply(ITurretInst turretInst) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        AdvTargetSettings settings = new AdvTargetSettings();
        turretInst.getUpgradeProcessor().setUpgradeInstance(upgId, settings);
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        AdvTargetSettings settings = new AdvTargetSettings();
        settings.loadFromNbt(nbt);
        turretInst.getUpgradeProcessor().setUpgradeInstance(upgId, settings);
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        AdvTargetSettings settings = turretInst.getUpgradeProcessor().getUpgradeInstance(upgId);
        if( settings != null ) {
            settings.writeToNbt(nbt);
        }
    }

    @Override
    public void onRemove(ITurretInst turretInst) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        turretInst.getUpgradeProcessor().delUpgradeInstance(upgId);
    }
}
