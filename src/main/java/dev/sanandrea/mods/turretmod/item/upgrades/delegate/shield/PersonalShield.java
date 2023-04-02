/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades.delegate.shield;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PersonalShield
    implements IUpgrade
//        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "personal_shield_upgrade");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IUpgradeData<?> getData(ITurretEntity turretInst) {
        return new ShieldData(20.0F);
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
        turretInst.removeClientForcefield(ShieldData.class);
    }

    //
//    @Override
//    public void initialize(ITurretInst turretInst, ItemStack stack) {
//        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new ShieldPersonal(20.0F));
//    }
//
//    @Override
//    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
//        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new ShieldPersonal(nbt.getFloat("shieldValue"), nbt.getFloat("shieldRecovery")));
//    }
//
//    @Override
//    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
//        ShieldPersonal shield = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
//        if( shield != null ) {
//            nbt.setFloat("shieldValue", shield.value);
//            nbt.setFloat("shieldRecovery", shield.recovery);
//        }
//    }
//
//    @Override
//    public void terminate(ITurretInst turretInst, ItemStack stack) {
//        turretInst.getUpgradeProcessor().<ShieldPersonal>getUpgradeInstance(ID).value = 0;
//        turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
//    }
}
