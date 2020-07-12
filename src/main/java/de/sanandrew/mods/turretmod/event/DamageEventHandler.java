/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.ShieldPersonal;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class DamageEventHandler
{
    private static final String DISABLE_XP_DROP_TAG = TmrConstants.ID + ":disable_xp_drop";

    @SubscribeEvent
    public static void onDamage(LivingHurtEvent event) {
        if( event.getEntity() instanceof EntityTurret ) {
            EntityTurret turret = (EntityTurret) event.getEntity();
            if( !turret.world.isRemote ) {
                IUpgradeProcessor proc = turret.getUpgradeProcessor();
                if( proc.hasUpgrade(Upgrades.SHIELD_PERSONAL) ) {
                    ShieldPersonal upgInst = proc.getUpgradeInstance(Upgrades.SHIELD_PERSONAL.getId());
                    float restDmg = upgInst.damage(event.getAmount());
                    if( restDmg <= 0.0F ) {
                        event.setCanceled(true);
                    } else {
                        event.setAmount(restDmg);
                    }
                    UpgradeRegistry.INSTANCE.syncWithClients(turret, Upgrades.SHIELD_PERSONAL.getId());
                }
                if( !event.isCanceled() && proc.hasUpgrade(Upgrades.TURRET_SAFE) && turret.getHealth() - event.getAmount() <= 0.001F ) {
                    TileEntityTurretCrate crate = turret.dismantle();
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
        EntityLivingBase corpse = event.getEntityLiving();
        if( dmgSrc instanceof EntityTurretProjectile.ITurretDamageSource && corpse.world instanceof WorldServer ) {
            ITurretInst turret = ((EntityTurretProjectile.ITurretDamageSource) dmgSrc).getTurretInst();
            if( turret != null && turret.getUpgradeProcessor().hasUpgrade(Upgrades.LEVELING) ) {
                LevelStorage lvlStorage = turret.getUpgradeProcessor().getUpgradeInstance(Upgrades.LEVELING.getId());
                if( lvlStorage != null && lvlStorage.getXp() < LevelStorage.maxXp ) {
                    EntityPlayer faker = FakePlayerFactory.getMinecraft((WorldServer) corpse.world);

                    int xp = TmrUtils.getExperiencePoints(event.getEntityLiving(), faker);
                    xp = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(corpse, faker, xp);

                    lvlStorage.addXp(xp);

                    corpse.addTag(DISABLE_XP_DROP_TAG);
                }
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
    public static void onEnderTeleport(EnderTeleportEvent event) {
        Entity t = event.getEntity();
        if( t instanceof EntityLiving ) {
            EntityLivingBase e = ((EntityLiving) t).getAttackTarget();
            if( e instanceof ITurretInst && ((ITurretInst) e).getUpgradeProcessor().hasUpgrade(Upgrades.ENDER_TOXIN_I) ) {
                event.setCanceled(true);
            }
        }
    }
}
