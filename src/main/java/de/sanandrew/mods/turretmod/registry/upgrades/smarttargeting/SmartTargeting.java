/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades.smarttargeting;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SmartTargeting
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.smarttgt");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretInst turretInst, ItemStack stack) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new AdvTargetSettings());
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new AdvTargetSettings(nbt));
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        AdvTargetSettings settings = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
        if( settings != null ) {
            settings.writeToNbt(nbt);
        }
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
    }
}
