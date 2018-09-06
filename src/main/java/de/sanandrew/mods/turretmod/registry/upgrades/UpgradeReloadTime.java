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
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class UpgradeReloadTime
        implements ITurretUpgrade
{
    private final ResourceLocation itemModel;

    private final String name;
    private final AttributeModifier modifier;

    public UpgradeReloadTime(String name, String modUUID, double value) {
        this.name = name;
        this.modifier = new AttributeModifier(UUID.fromString(modUUID), String.format("%s:%s", TmrConstants.ID, name), value, EntityUtils.ATTR_ADD_PERC_VAL_TO_SUM);
        this.itemModel = new ResourceLocation(TmrConstants.ID, "upgrades/" + name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    @Override
    public void onApply(ITurretInst turretInst) {
        if( !turretInst.getEntity().world.isRemote ) {
            IAttributeInstance attrib = turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS);
            if( attrib.getModifier(this.modifier.getID()) != null ) {
                attrib.removeModifier(this.modifier);
            }

            attrib.applyModifier(this.modifier);
        }
    }

    @Override
    public void onRemove(ITurretInst turretInst) {
        if( !turretInst.getEntity().world.isRemote ) {
            IAttributeInstance attrib = turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS);
            if( attrib.getModifier(this.modifier.getID()) != null ) {
                attrib.removeModifier(this.modifier);
                turretInst.getEntity().setHealth(turretInst.getEntity().getHealth());
            }
        }
    }

    public static class UpgradeReloadTimeMK1
            extends UpgradeReloadTime
    {
        public UpgradeReloadTimeMK1() {
            super("reload_i", "E6DAE7D4-A730-4F57-B3F9-61C369033625", -0.15D);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return null;
        }
    }

    public static class UpgradeReloadTimeMK2
            extends UpgradeReloadTime
    {
        private final ITurretUpgrade dependant;

        public UpgradeReloadTimeMK2() {
            super("reload_ii", "BA6FE867-0EBF-4E1A-9ED9-05E2B47143F8", -0.35D);
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(Upgrades.RELOAD_I);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return this.dependant;
        }
    }
}
