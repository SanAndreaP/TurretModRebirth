/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.turretmod.api.event.TargetingEvent;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TargetingEventHandler
{
    @SubscribeEvent
    public void onProcessorTick(TargetingEvent.ProcessorTick event) {
        ITurretEntity turret = event.processor.getTurret();

        //TODO: reimplement forcefield turret
//        if( turret.getDelegate() instanceof TurretForcefield ) {
//            event.setCanceled(true);
//
//            ForcefieldHandler.onTargeting(turret, event.processor);
//        }
    }

    @SubscribeEvent
    public void onTargetCheck(TargetingEvent.TargetCheck event) {
        ITurretEntity turret = event.processor.getTurret();

        //TODO: reimplement cryolator turret
//        if( turret.getDelegate() instanceof TurretCryolator && event.target instanceof LivingEntity && ((LivingEntity) event.target).hasEffect(Effects.MOVEMENT_SLOWDOWN) ) {
//            event.setResult(Event.Result.DENY);
//        }

        //TODO: reimplement upgrades
//        if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.SMART_TGT) ) {
//            AdvTargetSettings settings = event.processor.getTurretInst().getUpgradeProcessor().getUpgradeInstance(Upgrades.SMART_TGT.getId());
//            if( settings != null ) {
//                List<Entity> entities = turret.getTargetProcessor().getValidTargetList();
//                if( !settings.isTargetValid(event.target, turret, entities) ) {
//                    event.setResult(Event.Result.DENY);
//                }
//            }
//        }
    }

    @SubscribeEvent
    public void onShooting(TargetingEvent.Shooting event) {
        ITurretEntity turret = event.processor.getTurret();

        //TODO: reimplement shotgun turret
//        if( turret.getDelegate() instanceof TurretShotgun ) {
//            boolean hadProjectile = false;
//            for( int i = 0; i < 6; i++ ) {
//                Entity projectile = event.processor.getProjectile();
//                if( projectile != null ) {
//                    turret.get().level.addFreshEntity(projectile);
//                    hadProjectile = true;
//                } else {
//                    break;
//                }
//            }
//
//            if( hadProjectile ) {
//                event.processor.playSound(event.processor.getTurret().getShootSound(), 1.8F);
//                event.processor.getTurret().setShooting();
//                event.processor.decrAmmo();
//                event.setResult(Event.Result.ALLOW);
//            } else {
//                event.processor.playSound(event.processor.getTurret().getNoAmmoSound(), 1.0F);
//                event.setResult(Event.Result.DENY);
//            }
//
//            event.setCanceled(true);
//        }
    }

    @SubscribeEvent
    public void onAmmoConsumption(TargetingEvent.ConsumeAmmo event) {
        ITurretEntity turret = event.processor.getTurret();

        //TODO: reimplement upgrades
//        if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.ECONOMY_INF) && event.processor.getAmmoCount() == event.processor.getMaxAmmoCapacity() ) {
//            event.setResult(Event.Result.DENY);
//        } else {
//            if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.ECONOMY_I) && MiscUtils.RNG.randomFloat() < 0.15F ) {
//                event.setResult(Event.Result.DENY);
//            }
//            if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.ECONOMY_II) && MiscUtils.RNG.randomFloat() < 0.35F ) {
//                event.setResult(Event.Result.DENY);
//            }
//        }
    }

    //TODO: ??????????
//    @SubscribeEvent
//    public void onEntityAttackTarget(LivingSetAttackTargetEvent event) {
//        Entity e = event.getEntity();
//        if( event.getTarget() == null && !e.level.isClientSide ) {
////            TurretModRebirth.NETWORK.sendToAllNear(new PacketSyncAttackTarget(e, null), e.dimension, e.posX, e.posY, e.posZ, 64.0D);
//        }
//    }
}
