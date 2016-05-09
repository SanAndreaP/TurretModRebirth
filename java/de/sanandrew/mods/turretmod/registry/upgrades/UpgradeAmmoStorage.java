/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class UpgradeAmmoStorage
        implements TurretUpgrade
{
    private AttributeModifier modifier = new AttributeModifier(UUID.fromString("3D3C0F11-E31A-4472-92BB-E1BE0354844E"), String.format("%s:%s", TurretModRebirth.ID, "ammoCapacityUpg"), 320.0D,
                                                               TmrUtils.ATTR_ADD_VAL_TO_BASE);

    private final String name;

    public UpgradeAmmoStorage() {
        this.name = "ammo_storage";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getModId() {
        return TurretModRebirth.ID;
    }

    @Override
    public String getIconTexture() {
        return TurretModRebirth.ID + ":upgrades/" + this.name;
    }

    @Override
    public TurretUpgrade getDependantOn() {
        return null;
    }

    @Override
    public boolean isTurretApplicable(Class<? extends EntityTurret> turretCls) {
        return true;
    }

    @Override
    public void onApply(EntityTurret turret) {
        if( !turret.worldObj.isRemote ) {
            IAttributeInstance attrib = turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY);
            if( attrib.getModifier(modifier.getID()) != null ) {
                attrib.removeModifier(modifier);
            }

            attrib.applyModifier(modifier);
        }
    }

    @Override
    public void onRemove(EntityTurret turret) {
        if( !turret.worldObj.isRemote ) {
            IAttributeInstance attrib = turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY);
            if( attrib.getModifier(modifier.getID()) != null ) {
                attrib.removeModifier(modifier);
                turret.getTargetProcessor().dropExcessAmmo();
                turret.updateState();
            }
        }
    }

    @Override
    public void onLoad(EntityTurret turret, NBTTagCompound nbt) {}

    @Override
    public void onSave(EntityTurret turret, NBTTagCompound nbt) {}
}
