/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.event.TargetingEvent;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncAttackTarget;
import de.sanandrew.mods.turretmod.registry.turret.TurretCryolator;
import de.sanandrew.mods.turretmod.registry.turret.TurretShotgun;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.ShieldHandler;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.TurretForcefield;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.smarttargeting.AdvTargetSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class TargetingEventHandler
{
    @SubscribeEvent
    public void onProcessorTick(TargetingEvent.ProcessorTick event) {
        ITurretInst turretInst = event.processor.getTurret();

        if( turretInst.getTurret() instanceof TurretForcefield ) {
            event.setCanceled(true);

            ShieldHandler.onTargeting(turretInst, event.processor);
        }
    }

    @SubscribeEvent
    public void onTargetCheck(TargetingEvent.TargetCheck event) {
        ITurretInst turretInst = event.processor.getTurret();

        if( turretInst.getTurret() instanceof TurretCryolator && event.target instanceof EntityLivingBase && ((EntityLivingBase) event.target).isPotionActive(MobEffects.SLOWNESS) ) {
            event.setResult(Event.Result.DENY);
        }

        if( event.processor.getTurret().getUpgradeProcessor().hasUpgrade(Upgrades.SMART_TGT) ) {
            AdvTargetSettings settings = event.processor.getTurret().getUpgradeProcessor().getUpgradeInstance(Upgrades.SMART_TGT);
            if( settings != null ) {
                List<Entity> entities = turretInst.getTargetProcessor().getValidTargetList();
                if( !settings.isTargetValid(event.target, turretInst, entities) ) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public void onShooting(TargetingEvent.Shooting event) {
        if( event.processor.getTurret() instanceof TurretShotgun ) {
            boolean hadProjectile = false;
            for( int i = 0; i < 6; i++ ) {
                Entity projectile = event.processor.getProjectile();
                if( projectile != null ) {
                    event.processor.getTurret().get().world.spawnEntity(projectile);
                    hadProjectile = true;
                } else {
                    break;
                }
            }

            if( hadProjectile ) {
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
        ITurretInst turret = event.processor.getTurret();
        if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.ECONOMY_INF) && event.processor.getAmmoCount() == event.processor.getMaxAmmoCapacity() ) {
            event.setResult(Event.Result.DENY);
        } else {
            if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.ECONOMY_I) && MiscUtils.RNG.randomFloat() < 0.1F ) {
                event.setResult(Event.Result.DENY);
            }
            if( turret.getUpgradeProcessor().hasUpgrade(Upgrades.ECONOMY_II) && MiscUtils.RNG.randomFloat() < 0.35F ) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onEntityAttackTarget(LivingSetAttackTargetEvent event) {
        Entity e = event.getEntity();
        if( event.getTarget() == null && !e.world.isRemote ) {
            PacketRegistry.sendToAllAround(new PacketSyncAttackTarget(e, null), e.dimension, e.posX, e.posY, e.posZ, 64.0D);
        }
    }
}
