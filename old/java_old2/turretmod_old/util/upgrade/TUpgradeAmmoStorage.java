/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util.upgrade;

import de.sanandrew.core.manpack.util.EnumAttrModifierOperation;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.entity.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.client.util.TurretInfoApi;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class TUpgradeAmmoStorage
        extends TurretUpgradeBase
{
    AttributeModifier modifier = new AttributeModifier(UUID.fromString("3D3C0F11-E31A-4472-92BB-E1BE0354844E"), "ammoCapacityUpg", 320.0D, EnumAttrModifierOperation.ADD_VAL_TO_BASE.ordinal());

    public TUpgradeAmmoStorage() {
        super("ammoStorage", "upgrades/ammo_storage");
    }

    @Override
    public void onApply(Turret turret) {
        if( !turret.getEntity().worldObj.isRemote ) {
            turret.getEntity().getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).applyModifier(modifier);
        }
    }

    @Override
    public void onRemove(Turret turret) {
        if( !turret.getEntity().worldObj.isRemote ) {
            turret.getEntity().getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).removeModifier(modifier);

            int decrAmmo = turret.getAmmo() - turret.getMaxAmmo();
            ItemStack[] removedAmmo = TurretInfoApi.getDepletedAmmoStacks(decrAmmo);
            if( removedAmmo != null && removedAmmo.length > 0 ) {
                for( ItemStack droppedStack : removedAmmo ) {
                    turret.getEntity().entityDropItem(droppedStack, 0.0F);
                }

                turret.depleteAmmo(decrAmmo);
            }
        }
    }
}
