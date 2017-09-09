/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades.shield;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class UpgradeShieldPersonal
        implements ITurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/pers_shield");
    private final String name;

    public UpgradeShieldPersonal() {
        this.name = "pers_shield";
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
    public boolean isTurretApplicable(ITurret turret) {
        return true;
    }

    @Override
    public void onApply(ITurretInst turretInst) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        ShieldPersonal shield = new ShieldPersonal(20.0F);
        turretInst.getUpgradeProcessor().setUpgradeInstance(upgId, shield);
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        ShieldPersonal shield = new ShieldPersonal(nbt.getFloat("shieldValue"));
        shield.recovery = nbt.getFloat("shieldRecovery");
        turretInst.getUpgradeProcessor().setUpgradeInstance(upgId, shield);
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        ShieldPersonal shield = turretInst.getUpgradeProcessor().getUpgradeInstance(upgId);
        if( shield != null ) {
            nbt.setFloat("shieldValue", shield.value);
            nbt.setFloat("shieldRecovery", shield.recovery);
        }
    }

    @Override
    public void onRemove(ITurretInst turretInst) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        turretInst.getUpgradeProcessor().<ShieldPersonal>getUpgradeInstance(upgId).value = 0;
        turretInst.getUpgradeProcessor().delUpgradeInstance(upgId);
    }
}
