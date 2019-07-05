/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades.shield;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class UpgradeShieldPersonal
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.shield.personal");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretInst turretInst) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new ShieldPersonal(20.0F));
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new ShieldPersonal(nbt.getFloat("shieldValue"), nbt.getFloat("shieldRecovery")));
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        ShieldPersonal shield = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
        if( shield != null ) {
            nbt.setFloat("shieldValue", shield.value);
            nbt.setFloat("shieldRecovery", shield.recovery);
        }
    }

    @Override
    public void terminate(ITurretInst turretInst) {
        turretInst.getUpgradeProcessor().<ShieldPersonal>getUpgradeInstance(ID).value = 0;
        turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
    }
}
