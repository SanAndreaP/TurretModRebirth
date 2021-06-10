package de.sanandrew.mods.turretmod.entity.projectile.delegate;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.api.ResourceLocations;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileInst;
import de.sanandrew.mods.turretmod.api.ammo.Projectile;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
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

        this.ricochetSound = ResourceLocations.SOUND_RICOCHET_ARROW;

        this.texture = ResourceLocations.TEXTURE_ENTITY_CROSSBOW_BOLT;
    }

    @Override
    public void onPostEntityDamage(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, Entity target, DamageSource damageSrc) {
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
}
