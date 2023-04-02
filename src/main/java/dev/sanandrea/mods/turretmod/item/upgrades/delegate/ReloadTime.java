/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades.delegate;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.turret.TurretAttributes;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class ReloadTime
        implements IUpgrade
{
    private final ResourceLocation id;
    private final AttributeModifier modifier;

    ReloadTime(String name, String modUUID, double value) {
        this.modifier = new AttributeModifier(UUID.fromString(modUUID), String.format("%s:%s", TmrConstants.ID, name), value, AttributeModifier.Operation.MULTIPLY_BASE);
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
            EntityUtils.tryRemoveModifier(turretInst.get(), TurretAttributes.MAX_RELOAD_TICKS, this.modifier);
            EntityUtils.tryApplyModifier(turretInst.get(), TurretAttributes.MAX_RELOAD_TICKS, this.modifier, true);
        }
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
        if( !turretInst.get().level.isClientSide ) {
            EntityUtils.tryRemoveModifier(turretInst.get(), TurretAttributes.MAX_RELOAD_TICKS, this.modifier);
        }
    }

    public static class MK1
            extends ReloadTime
    {
        public MK1() {
            super("reload_1", "E6DAE7D4-A730-4F57-B3F9-61C369033625", -0.15D);
        }
    }

    public static class MK2
            extends ReloadTime
    {
        public MK2() {
            super("reload_2", "BA6FE867-0EBF-4E1A-9ED9-05E2B47143F8", -0.35D);
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.RELOAD_I;
        }
    }
}
