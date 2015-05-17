/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import de.sanandrew.core.manpack.util.EnumAttrModifierOperation;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.entity.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class TUpgradeAmmoStorage
        extends TurretUpgrade
{
    AttributeModifier modifier = new AttributeModifier(UUID.fromString("3D3C0F11-E31A-4472-92BB-E1BE0354844E"), "ammoCapacityUpg", 320.0D, EnumAttrModifierOperation.ADD_VAL_TO_BASE.ordinal());

    public TUpgradeAmmoStorage() {
        super(TurretMod.MOD_ID, "ammoStorage", "upgrades/ammo_storage");
    }

    @Override
    public void onApply(AEntityTurretBase turret) {
        if( !turret.worldObj.isRemote ) {
            turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).applyModifier(modifier);
        }
    }

    @Override
    public void onRemove(AEntityTurretBase turret) {
        if( !turret.worldObj.isRemote ) {
            turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).removeModifier(modifier);

            int decrAmmo = turret.getAmmo() - turret.getMaxAmmo();
            ItemStack[] removedAmmo = turret.myInfo.getDepletedAmmoStacks(decrAmmo);
            if( removedAmmo != null && removedAmmo.length > 0 ) {
                for( ItemStack droppedStack : removedAmmo ) {
                    turret.entityDropItem(droppedStack, 0.0F);
                }

                turret.depleteAmmo(decrAmmo);
            }
        }
    }
}
