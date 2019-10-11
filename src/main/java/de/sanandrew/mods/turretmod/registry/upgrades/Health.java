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
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class Health
        implements IUpgrade
{
    private final ResourceLocation id;
    private final AttributeModifier modifier;

    Health(String name, String modUUID) {
        this.modifier = new AttributeModifier(UUID.fromString(modUUID), String.format("%s:%s", TmrConstants.ID, name), 0.25D, EntityUtils.ATTR_ADD_PERC_VAL_TO_SUM);
        this.id = new ResourceLocation(TmrConstants.ID, "upgrade." + name);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void initialize(ITurretInst turretInst, ItemStack stack) {
        if( !turretInst.get().world.isRemote ) {
            IAttributeInstance attrib = turretInst.get().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            if( attrib.getModifier(this.modifier.getID()) != null ) {
                attrib.removeModifier(this.modifier);
            }

            attrib.applyModifier(this.modifier);
        }
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        if( !turretInst.get().world.isRemote ) {
            IAttributeInstance attrib = turretInst.get().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            if( attrib.getModifier(this.modifier.getID()) != null ) {
                attrib.removeModifier(this.modifier);
                turretInst.get().setHealth(turretInst.get().getHealth());
            }
        }
    }

    static class MK1
            extends Health
    {
        MK1() {
            super("health.1", "673176FC-51F9-4CBC-BA12-5073B6867644");
        }
    }

    static class MK2
            extends Health
    {
        MK2() {
            super("health.2", "B7E5ADFA-517C-4167-A2FD-E0D31FA6E9BE");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.HEALTH_I;
        }
    }

    static class MK3
            extends Health
    {
        MK3() {
            super("health.3", "D49F43AE-5EA4-4DD2-B08A-9B5F1966C091");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.HEALTH_II;
        }
    }

    static class MK4
            extends Health
    {
        MK4() {
            super("health.4", "9431A60C-B995-4547-B143-2BEDC67467E1");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.HEALTH_III;
        }

        @Nullable
        @Override
        public Range<Integer> getTierRange() {
            return Range.between(4, Integer.MAX_VALUE);
        }
    }
}
