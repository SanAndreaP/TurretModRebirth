/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.turret.TargetingEvent;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.smarttargeting.AdvTargetSettings;
import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class TargetingEventHandler
{
    private TargetingEventHandler() { }

    @SubscribeEvent
    public static void onProcessorTick(TargetingEvent.ProcessorTick event) {
        ITurretEntity turret = event.processor.getTurret();

        //TODO: reimplement forcefield turret
//        if( turret.getDelegate() instanceof TurretForcefield ) {
//            event.setCanceled(true);
//
//            ForcefieldHandler.onTargeting(turret, event.processor);
//        }
    }

    @SubscribeEvent
    public static void onTargetCheck(TargetingEvent.TargetCheck event) {
        ITurretEntity turret = event.processor.getTurret();

        //TODO: reimplement cryolator turret
//        if( turret.getDelegate() instanceof TurretCryolator && event.target instanceof LivingEntity && ((LivingEntity) event.target).hasEffect(Effects.MOVEMENT_SLOWDOWN) ) {
//            event.setResult(Event.Result.DENY);
//        }

        if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.SMART_TGT) ) {
            AdvTargetSettings settings = event.processor.getTurret().getUpgradeProcessor().getUpgradeData(Upgrades.SMART_TGT.getId());
            if( settings != null ) {
                List<Entity> entities = turret.getTargetProcessor().getValidTargetList();
                if( !settings.isTargetValid(event.target, turret, entities, event.isLast) ) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onShooting(TargetingEvent.Shooting event) {
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
    public static void onAmmoConsumption(TargetingEvent.ConsumeAmmo event) {
        ITurretEntity turret = event.processor.getTurret();
        IUpgradeProcessor upgProcessor = turret.getUpgradeProcessor();

        if( upgProcessor.hasUpgrade(Upgrades.CREATIVE) ) {
            event.setResult(Event.Result.DENY);
            return;
        }

        if( upgProcessor.hasUpgrade(Upgrades.ECONOMY_INF) && event.processor.getAmmoCount() == event.processor.getMaxAmmoCapacity() ) {
            event.setResult(Event.Result.DENY);
        } else {
            if( upgProcessor.hasUpgrade(Upgrades.ECONOMY_I) && MiscUtils.RNG.randomFloat() < 0.15F ) {
                event.setResult(Event.Result.DENY);
            }
            if( upgProcessor.hasUpgrade(Upgrades.ECONOMY_II) && MiscUtils.RNG.randomFloat() < 0.35F ) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
