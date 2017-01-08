/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.event.TargetingEvent;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class TargetingEvents
{
    @SubscribeEvent
    public void onTargetCheck(TargetingEvent.TargetCheck event) {
        EntityTurret turret = event.processor.getTurret();

        if( turret instanceof EntityTurretCryolator && event.target instanceof EntityLivingBase && ((EntityLivingBase) event.target).isPotionActive(MobEffects.SLOWNESS) ) {
            event.setResult(Event.Result.DENY);
        }

        if( event.processor.getTurret().getUpgradeProcessor().hasUpgrade(UpgradeRegistry.SMART_TGT) ) {
            List entities = turret.world.getEntitiesWithinAABB(turret.getClass(), turret.getTargetProcessor().getAdjustedRange(true));

            for( Object eObj : entities ) {
                if( eObj instanceof EntityTurret ) {
                    EntityTurret otherTurret = (EntityTurret) eObj;
                    if( eObj != turret && otherTurret.getTargetProcessor().getTarget() == event.target && otherTurret.getTargetProcessor().hasAmmo() ) {
                        event.setResult(Event.Result.DENY);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onShooting(TargetingEvent.Shooting event) {
        if( event.processor.getTurret() instanceof EntityTurretShotgun ) {
            if( event.processor.hasAmmo() ) {
                for( int i = 0; i < 6; i++ ) {
                    Entity projectile = event.processor.getProjectile();
                    assert projectile != null;
                    event.processor.getTurret().world.spawnEntityInWorld(projectile);
                }
                event.processor.playSound(event.processor.getTurret().getShootSound(), 1.8F);
                event.processor.getTurret().setShooting();
                event.processor.decrAmmo();
                event.setResult(Event.Result.ALLOW);
            } else {
                event.processor.playSound(event.processor.getTurret().getNoAmmoSound(), 1.0F);
                event.setResult(Event.Result.DENY);
            }

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAmmoConsumption(TargetingEvent.ConsumeAmmo event) {
        EntityTurret turret = event.processor.getTurret();
        if( turret.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.ECONOMY_INF) && event.processor.getAmmoCount() == event.processor.getMaxAmmoCapacity() ) {
            event.setResult(Event.Result.DENY);
        } else {
            if( turret.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.ECONOMY_I) && MiscUtils.RNG.randomFloat() < 0.1F ) {
                event.setResult(Event.Result.DENY);
            }
            if( turret.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.ECONOMY_II) && MiscUtils.RNG.randomFloat() < 0.35F ) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
