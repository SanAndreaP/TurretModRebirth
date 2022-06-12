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
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
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
            turretInst.syncState();
        }
    }
}
