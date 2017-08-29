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
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class UpgradeAmmoStorage
        implements ITurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/ammo_storage");
    private AttributeModifier modifier = new AttributeModifier(UUID.fromString("3D3C0F11-E31A-4472-92BB-E1BE0354844E"), String.format("%s:%s", TmrConstants.ID, "ammoCapacityUpg"), 320.0D,
                                                               EntityUtils.ATTR_ADD_VAL_TO_BASE);

    private final String name;

    public UpgradeAmmoStorage() {
        this.name = "ammo_storage";
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
    public boolean isTurretApplicable(Class<? extends EntityTurret> turretCls) {
        return true;
    }

    @Override
    public void onApply(EntityTurret turret) {
        if( !turret.world.isRemote ) {
            IAttributeInstance attrib = turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY);
            if( attrib.getModifier(modifier.getID()) != null ) {
                attrib.removeModifier(modifier);
            }

            attrib.applyModifier(modifier);
        }
    }

    @Override
    public void onRemove(EntityTurret turret) {
        if( !turret.world.isRemote ) {
            IAttributeInstance attrib = turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY);
            if( attrib.getModifier(modifier.getID()) != null ) {
                attrib.removeModifier(modifier);
                turret.getTargetProcessor().dropExcessAmmo();
                EntityTurret.utils.updateTurretState(turret);
            }
        }
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.UPG_AMMO_STG;
    }
}
