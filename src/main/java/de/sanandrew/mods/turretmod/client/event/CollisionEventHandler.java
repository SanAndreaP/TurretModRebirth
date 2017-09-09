/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * fixes the collision for players... basically makes the turret solid for players, walk-through for anything else.
 */
public class CollisionEventHandler
{
    @SubscribeEvent
    public void onCollision(GetCollisionBoxesEvent event) {
        Entity entityIn = event.getEntity();
        AxisAlignedBB aabb = event.getAabb();

        if( entityIn instanceof EntityPlayer ) {
            List<EntityTurret> turrets = entityIn.world.getEntitiesWithinAABB(EntityTurret.class, aabb.grow(0.25D));
            for( EntityTurret turret : turrets ) {
                if( !entityIn.isRidingSameEntity(turret) ) {
                    AxisAlignedBB entityBB = turret.getEntityBoundingBox();

                    if( entityBB.intersects(aabb) ) {
                        event.getCollisionBoxesList().add(entityBB);
                    }
                }
            }
        }
    }
}
