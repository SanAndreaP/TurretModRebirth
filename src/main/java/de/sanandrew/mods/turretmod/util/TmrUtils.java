/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.ITmrUtils;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.ai.EntityAIMoveTowardsTurret;
import de.sanandrew.mods.turretmod.init.TmrConfig;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncAttackTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.text.DecimalFormat;
import java.util.List;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class TmrUtils
        implements ITmrUtils
{
    public static final TmrUtils INSTANCE = new TmrUtils();

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");

    //region SRG-Reflections
    public static int getExperiencePoints(EntityLivingBase target, EntityPlayer player) {
        return ReflectionUtils.invokeCachedMethod(EntityLivingBase.class, target, "getExperiencePoints", "func_70693_a",
                                                  new Class[] { EntityPlayer.class }, new Object[]{ player });
    }
    //endregion

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
        EntityLivingBase turretL = attackingTurret.get();
        target.setAttackTarget(turretL);
        target.setRevengeTarget(turretL);
        PacketRegistry.sendToAllAround(new PacketSyncAttackTarget(target, turretL), turretL.dimension, turretL.posX, turretL.posY, turretL.posZ, 64.0D);

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
}
