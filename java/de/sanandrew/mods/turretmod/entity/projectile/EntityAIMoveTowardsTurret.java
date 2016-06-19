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
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

final class EntityAIMoveTowardsTurret
        extends EntityAIBase
{
    private EntityTurret targetTurret;
    private EntityCreature theEntity;

    private double movePosX;
    private double movePosY;
    private double movePosZ;
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
        if( this.targetTurret == null ) {
            return false;
        } else if( this.targetTurret.getDistanceSqToEntity(this.theEntity) > this.maxDistance * this.maxDistance ) {
            return false;
        } else {
            Vec3 targetPosVec = Vec3.createVectorHelper(this.targetTurret.posX, this.targetTurret.posY, this.targetTurret.posZ);
            Vec3 pathBlockVec = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, targetPosVec);

            if( pathBlockVec == null ) {
                return false;
            } else {
                this.movePosX = pathBlockVec.xCoord;
                this.movePosY = pathBlockVec.yCoord;
                this.movePosZ = pathBlockVec.zCoord;

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
        this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
