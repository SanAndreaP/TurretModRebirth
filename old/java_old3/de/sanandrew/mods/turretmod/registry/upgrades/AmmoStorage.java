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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class AmmoStorage
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade_ammo_storage");
    private static final AttributeModifier MODIFIER = new AttributeModifier(UUID.fromString("3D3C0F11-E31A-4472-92BB-E1BE0354844E"), String.format("%s:%s", TmrConstants.ID, "ammoCapacityUpg"), 1.0D,
                                                                            EntityUtils.ATTR_ADD_PERC_VAL_TO_SUM);

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretInst turretInst, ItemStack stack) {
        if( !turretInst.get().world.isRemote ) {
            EntityUtils.tryRemoveModifier(turretInst.get(), TurretAttributes.MAX_AMMO_CAPACITY, MODIFIER);
            EntityUtils.tryApplyModifier(turretInst.get(), TurretAttributes.MAX_AMMO_CAPACITY, MODIFIER);
        }
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        if( !turretInst.get().world.isRemote ) {
            if( EntityUtils.tryRemoveModifier(turretInst.get(), TurretAttributes.MAX_AMMO_CAPACITY, MODIFIER) ) {
                turretInst.getTargetProcessor().dropExcessAmmo();
                turretInst.updateState();
            }
        }
    }
}
