/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.entity.projectile.delegate;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileEntity;
import de.sanandrew.mods.turretmod.api.ammo.Projectile;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrossbowBolt
        extends Projectile
{
    public CrossbowBolt(ResourceLocation id) {
        super(id);

        this.ricochetSound = Resources.SOUND_RICOCHET_ARROW;

        this.texture = Resources.TEXTURE_ENTITY_CROSSBOW_BOLT;
    }

    @Override
    public void onPostEntityDamage(@Nullable ITurretEntity turret, @Nonnull IProjectileEntity projectile, Entity target, DamageSource damageSrc) {
        if( target instanceof LivingEntity && projectile.getAmmunition() == Ammunitions.TIPPED_BOLT ) {
            String subtype = projectile.getAmmunitionSubtype();
            if( !Strings.isNullOrEmpty(subtype) ) {
                Potion potion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(subtype));
                if( potion != null ) {
                    for( EffectInstance effect : potion.getEffects() ) {
                        ((LivingEntity) target).addEffect(new EffectInstance(effect.getEffect(), Math.max(effect.getDuration() / 8, 1),
                                                                             effect.getAmplifier(), effect.isAmbient(),
                                                                             effect.isVisible(), effect.showIcon()));
                    }
                }
            }
        }
    }

    @Override
    public String getCustomRenderClass() {
        return TurretModRebirth.PROXY.getRenderClassProvider().getCrossbowBoltRenderClass();
    }
}
