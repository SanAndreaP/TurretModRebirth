/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradePrsShield;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DamageEventHandler
{
    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        if( event.getEntity() instanceof EntityTurret ) {
            IUpgradeProcessor proc = ((EntityTurret) event.getEntity()).getUpgradeProcessor();
            if( proc.hasUpgrade(UpgradeRegistry.SHIELD) ) {
                UpgradePrsShield.Shield upgInst = proc.getUpgradeInstance(UpgradeRegistry.SHIELD);
                float restDmg = upgInst.damage(event.getAmount());
                if( restDmg <= 0.0F ) {
                    event.setCanceled(true);
                } else {
                    event.setAmount(restDmg);
                }
                proc.syncUpgrade(UpgradeRegistry.SHIELD);
            }
        }
    }
}
