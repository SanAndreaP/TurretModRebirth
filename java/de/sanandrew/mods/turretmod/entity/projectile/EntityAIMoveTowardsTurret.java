/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;

final class EntityAIMoveTowardsTurret
        extends EntityAIBase
{
    private EntityTurret targetTurret;
    private EntityCreature theEntity;

    private Path turretPath;
    private double speed;
    private float maxDistance;

    public EntityAIMoveTowardsTurret(EntityCreature doer, EntityTurret target, double speed, float maxDistance) {
        this.theEntity = doer;
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.targetTurret = target;
        this.setMutexBits(1);
    }

    public void setNewTurret(EntityTurret turret) {
        this.targetTurret = turret;
    }

    @Override
    public boolean shouldExecute() {
        if( this.targetTurret == null || !this.targetTurret.isEntityAlive() ) {
            return false;
        } else if( this.targetTurret.getDistanceSqToEntity(this.theEntity) > this.maxDistance * this.maxDistance ) {
            return false;
        } else {
            Vec3d targetPosVec = new Vec3d(this.targetTurret.posX, this.targetTurret.posY, this.targetTurret.posZ);
            Vec3d pathBlockVec = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 8, 7, targetPosVec);

            if( pathBlockVec == null ) {
                return false;
            } else {
                this.turretPath = this.theEntity.getNavigator().getPathToXYZ(pathBlockVec.xCoord, pathBlockVec.yCoord, pathBlockVec.zCoord);

                return true;
            }
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.theEntity.getNavigator().noPath() && this.targetTurret != null && this.targetTurret.isEntityAlive() && this.targetTurret.getDistanceSqToEntity(this.theEntity) < this.maxDistance * this.maxDistance;
    }

    @Override
    public void resetTask() {
        this.targetTurret = null;
    }

    @Override
    public void startExecuting() {
        this.theEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("turretRangeMod", this.targetTurret.getTargetProcessor().getRange(), 0));
        this.theEntity.getNavigator().setPath(this.turretPath, this.speed);
    }
}
