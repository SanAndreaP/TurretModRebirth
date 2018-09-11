/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.ITmrUtils;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.ai.EntityAIMoveTowardsTurret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class TmrUtils
        implements ITmrUtils
{
    public static final TmrUtils INSTANCE = new TmrUtils();

    @Override
    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        TurretModRebirth.proxy.openGui(player, id, x, y, z);
    }

    @Override
    public boolean canPlayerEditAll() {
        return TmrConfig.Server.playerCanEditAll;
    }

    @Override
    public boolean canOpEditAll() {
        return TmrConfig.Server.opCanEditAll;
    }

    @Override
    public <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        return EntityUtils.getPassengersOfClass(e, psgClass);
    }

    @Override
    public void addForcefield(Entity e, IForcefieldProvider provider) {
        TurretModRebirth.proxy.addForcefield(e, provider);
    }

    @Override
    public boolean hasForcefield(Entity e, Class<? extends IForcefieldProvider> providerCls) {
        return TurretModRebirth.proxy.hasForcefield(e, providerCls);
    }

    @Override
    public void setEntityTarget(EntityCreature target, final ITurretInst attackingTurret) {
        EntityLivingBase turretL = attackingTurret.getEntity();
        target.setAttackTarget(turretL);
        target.setRevengeTarget(turretL);

        List<EntityAIMoveTowardsTurret> aiLst = EntityUtils.getAisFromTaskList(target.tasks.taskEntries, EntityAIMoveTowardsTurret.class);
        if( aiLst.size() < 1 ) {
            target.tasks.addTask(10, new EntityAIMoveTowardsTurret(target, attackingTurret, 1.1D, 64.0F));
        } else {
            aiLst.forEach(aiTgtFollow -> {
                if( !aiTgtFollow.shouldContinueExecuting() ) {
                    aiTgtFollow.setNewTurret(attackingTurret);
                }
            });
        }
    }

    public static float wrap360(float angle) {
        return angle > 360.0F ? wrap360(angle - 360.0F) : angle < 0 ? wrap360(angle + 360.0F) : angle;
    }
}
