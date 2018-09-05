/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class UpgradeAmmoStorage
        implements ITurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/ammo_storage");
    private final AttributeModifier modifier;

    private final String name;

    UpgradeAmmoStorage() {
        this.name = "ammo_storage";
        this.modifier = new AttributeModifier(UUID.fromString("3D3C0F11-E31A-4472-92BB-E1BE0354844E"), String.format("%s:%s", TmrConstants.ID, "ammoCapacityUpg"), 1.0D,
                                              EntityUtils.ATTR_ADD_PERC_VAL_TO_SUM);
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
    public ITurretUpgrade getDependantOn() {
        return null;
    }

    @Override
    public void onApply(ITurretInst turretInst) {
        if( !turretInst.getEntity().world.isRemote ) {
            IAttributeInstance attrib = turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY);
            if( attrib.getModifier(modifier.getID()) != null ) {
                attrib.removeModifier(modifier);
            }

            attrib.applyModifier(modifier);
        }
    }

    @Override
    public void onRemove(ITurretInst turretInst) {
        if( !turretInst.getEntity().world.isRemote ) {
            IAttributeInstance attrib = turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY);
            if( attrib.getModifier(modifier.getID()) != null ) {
                attrib.removeModifier(modifier);
                turretInst.getTargetProcessor().dropExcessAmmo();
                turretInst.updateState();
            }
        }
    }
}
