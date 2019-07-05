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
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class ReloadTime
        implements IUpgrade
{
    private final ResourceLocation id;
    private final AttributeModifier modifier;

    ReloadTime(String name, String modUUID, double value) {
        this.modifier = new AttributeModifier(UUID.fromString(modUUID), String.format("%s:%s", TmrConstants.ID, name), value, EntityUtils.ATTR_ADD_PERC_VAL_TO_SUM);
        this.id = new ResourceLocation(TmrConstants.ID, "upgrade." + name);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void initialize(ITurretInst turretInst) {
        if( !turretInst.get().world.isRemote ) {
            IAttributeInstance attrib = turretInst.get().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS);
            if( attrib.getModifier(this.modifier.getID()) != null ) {
                attrib.removeModifier(this.modifier);
            }

            attrib.applyModifier(this.modifier);
        }
    }

    @Override
    public void terminate(ITurretInst turretInst) {
        if( !turretInst.get().world.isRemote ) {
            IAttributeInstance attrib = turretInst.get().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS);
            if( attrib.getModifier(this.modifier.getID()) != null ) {
                attrib.removeModifier(this.modifier);
                turretInst.get().setHealth(turretInst.get().getHealth());
            }
        }
    }

    static class MK1
            extends ReloadTime
    {
        MK1() {
            super("reload.1", "E6DAE7D4-A730-4F57-B3F9-61C369033625", -0.15D);
        }
    }

    static class MK2
            extends ReloadTime
    {
        MK2() {
            super("reload.2", "BA6FE867-0EBF-4E1A-9ED9-05E2B47143F8", -0.35D);
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.RELOAD_I;
        }
    }
}
