/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.entity.projectile.TurretProjectileEntity;
import de.sanandrew.mods.turretmod.entity.turret.TurretEntity;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling.LevelData;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.shield.ShieldData;
import de.sanandrew.mods.turretmod.tileentity.TurretCrateEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class DamageEventHandler
{
    private static final String DISABLE_XP_DROP_TAG = TmrConstants.ID + ":disable_xp_drop";

    private DamageEventHandler() { }

    @SubscribeEvent
    public static void onDamage(LivingHurtEvent event) {
        if( event.getEntity() instanceof TurretEntity ) {
            TurretEntity turret = (TurretEntity) event.getEntity();
            if( !turret.level.isClientSide ) {
                IUpgradeProcessor proc = turret.getUpgradeProcessor();

                if( proc.hasUpgrade(Upgrades.SHIELD_PERSONAL) ) {
                    ShieldData shieldData = proc.getUpgradeData(Upgrades.SHIELD_PERSONAL.getId());
                    float      restDmg = shieldData.damage(event.getAmount());
                    if( restDmg <= 0.0F ) {
                        event.setCanceled(true);
                    } else {
                        event.setAmount(restDmg);
                    }
                    proc.syncUpgrade(Upgrades.SHIELD_PERSONAL.getId());
                }
                if( proc.hasUpgrade(Upgrades.CREATIVE) ) {
                    event.setCanceled(true);
                }

                if( !event.isCanceled() && proc.hasUpgrade(Upgrades.TURRET_SAFE) && turret.getHealth() - event.getAmount() <= 0.001F ) {
                    TurretCrateEntity crate = turret.dismantle();
                    if( crate != null ) {
                        crate.getInventory().replaceSafeUpgrade();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        DamageSource dmgSrc = event.getSource();
        LivingEntity corpse = event.getEntityLiving();

        ITurretEntity turret = null;
        if( dmgSrc instanceof TurretProjectileEntity.ITurretDamageSource ) {
            turret = ((TurretProjectileEntity.ITurretDamageSource) dmgSrc).getTurretInst();
        } else {
            Entity lastHurtByMob = corpse.getLastHurtByMob();
            if( lastHurtByMob instanceof ITurretEntity ) {
                turret = (ITurretEntity) lastHurtByMob;
            }
        }

        if( turret != null && corpse.level instanceof ServerWorld && EntityUtils.shouldDropExperience(corpse)
            && turret.getUpgradeProcessor().hasUpgrade(Upgrades.LEVELING) )
        {
            LevelData lvlStorage = turret.getUpgradeProcessor().getUpgradeData(Upgrades.LEVELING.getId());
            if( lvlStorage != null ) {
                PlayerEntity faker = FakePlayerFactory.getMinecraft((ServerWorld) corpse.level);

                int xp = EntityUtils.getExperienceReward(event.getEntityLiving(), faker);
                xp = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(corpse, faker, xp);

                lvlStorage.addXp(xp);

                corpse.addTag(DISABLE_XP_DROP_TAG);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onXpDrop(LivingExperienceDropEvent event) {
        if( event.getEntity().getTags().contains(DISABLE_XP_DROP_TAG) ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        Entity t = event.getEntity();
        if( t instanceof LivingEntity ) {
            LivingEntity e = ((LivingEntity) t).getLastHurtByMob();
            if( e instanceof ITurretEntity ) {
                IUpgradeProcessor proc = ((ITurretEntity) e).getUpgradeProcessor();
                if( proc.hasUpgrade(Upgrades.ENDER_TOXIN_I) || proc.hasUpgrade(Upgrades.CREATIVE) ) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
