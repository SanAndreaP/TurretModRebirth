/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item.upgrades.delegate;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public abstract class Health
        implements IUpgrade
{
    private final ResourceLocation id;
    private final AttributeModifier modifier;

    Health(String name, String modUUID) {
        this.modifier = new AttributeModifier(UUID.fromString(modUUID), String.format("%s:%s", TmrConstants.ID, name), 0.25D, AttributeModifier.Operation.MULTIPLY_BASE);
        this.id = new ResourceLocation(TmrConstants.ID, name + "_upgrade");
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void initialize(ITurretEntity turretInst, ItemStack stack) {
        if( !turretInst.get().level.isClientSide ) {
            EntityUtils.tryRemoveModifier(turretInst.get(), Attributes.MAX_HEALTH, this.modifier);
            EntityUtils.tryApplyModifier(turretInst.get(), Attributes.MAX_HEALTH, this.modifier, true);
        }
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
        if( !turretInst.get().level.isClientSide && EntityUtils.tryRemoveModifier(turretInst.get(), Attributes.MAX_HEALTH, this.modifier) ) {
            turretInst.get().setHealth(turretInst.get().getHealth());
        }
    }

    public static class MK1
            extends Health
    {
        public MK1() {
            super("health_1", "673176FC-51F9-4CBC-BA12-5073B6867644");
        }
    }

    public static class MK2
            extends Health
    {
        public MK2() {
            super("health_2", "B7E5ADFA-517C-4167-A2FD-E0D31FA6E9BE");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.HEALTH_I;
        }
    }

    public static class MK3
            extends Health
    {
        public MK3() {
            super("health_3", "D49F43AE-5EA4-4DD2-B08A-9B5F1966C091");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.HEALTH_II;
        }

        @Nullable
        @Override
        public Range<Integer> getTierRange() {
            return Range.between(1, 4);
        }
    }

    public static class MK4
            extends Health
    {
        public MK4() {
            super("health_4", "9431A60C-B995-4547-B143-2BEDC67467E1");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.HEALTH_III;
        }

        @Nullable
        @Override
        public Range<Integer> getTierRange() {
            return Range.between(1, 3);
        }
    }
}
