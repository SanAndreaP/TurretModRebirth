/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.ShieldHandler;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.TurretForcefield;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.Predicate;

public class ExplosionEventHandler
{
    private static final Predicate<EntityLivingBase> CHK_ENTITY = entity -> {
        if( entity instanceof ITurretInst ) {
            ITurretInst turretInst = (ITurretInst) entity;
            return turretInst.getTurret() instanceof TurretForcefield && turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_EXPLOSIVE);
        }

        return false;
    };

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        float radius = event.getExplosion().size;
        Vec3d expPos = event.getExplosion().getPosition();
        AxisAlignedBB aabb = new AxisAlignedBB(-radius, -radius, -radius, radius, radius, radius).grow(24.0D).offset(expPos.x, expPos.y, expPos.z);

        for( EntityLivingBase living : event.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, aabb, CHK_ENTITY::test) ) {
            ShieldHandler.onExplosion((ITurretInst) living, aabb, event.getAffectedBlocks(), event.getAffectedEntities());
        }
    }
}
