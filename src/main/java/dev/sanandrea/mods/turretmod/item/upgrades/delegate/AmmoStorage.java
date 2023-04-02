/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades.delegate;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.turret.TurretAttributes;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.network.SyncTurretStatePacket;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class AmmoStorage
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo_storage_upgrade");
    private static final AttributeModifier MODIFIER = new AttributeModifier(UUID.fromString("3D3C0F11-E31A-4472-92BB-E1BE0354844E"),
                                                                            String.format("%s:%s", TmrConstants.ID, "ammoCapacityUpg"), 1.0D,
                                                                            AttributeModifier.Operation.MULTIPLY_BASE);

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretEntity turretInst, ItemStack stack) {
        if( !turretInst.get().level.isClientSide ) {
            EntityUtils.tryRemoveModifier(turretInst.get(), TurretAttributes.MAX_AMMO_CAPACITY, MODIFIER);
            EntityUtils.tryApplyModifier(turretInst.get(), TurretAttributes.MAX_AMMO_CAPACITY, MODIFIER, true);
        }
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
        if( !turretInst.get().level.isClientSide && EntityUtils.tryRemoveModifier(turretInst.get(), TurretAttributes.MAX_AMMO_CAPACITY, MODIFIER) ) {
            turretInst.getTargetProcessor().dropExcessAmmo();
            turretInst.syncState(SyncTurretStatePacket.AMMO);
        }
    }
}
