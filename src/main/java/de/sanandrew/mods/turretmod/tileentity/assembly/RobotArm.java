package de.sanandrew.mods.turretmod.tileentity.assembly;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;

class RobotArm
{
    private float robotArmX = 2.0F;
    private float robotArmY = -9.0F;
    private float prevRobotArmX = 0.0F;
    private float prevRobotArmY = 0.0F;
    private float robotMotionX;
    private float robotMotionY;
    private float robotEndX;
    private float robotEndY;
    private double spawnParticleX;
    private double spawnParticleY;
    private double spawnParticleZ;

    private boolean prevActive;
    private int ticksToNextPos = 0;

    void process(boolean isActive, boolean spedUp) {
        this.prevRobotArmX = this.robotArmX;
        this.prevRobotArmY = this.robotArmY;

        this.robotArmX += this.robotMotionX;
        this.robotArmY += this.robotMotionY;

        if( (this.robotArmX > this.robotEndX && this.robotMotionX > 0.0F) || (this.robotArmX < this.robotEndX && this.robotMotionX < 0.0F) ) {
            this.robotArmX = this.robotEndX;
            this.robotMotionX = 0.0F;
        }

        if( (this.robotArmY > this.robotEndY && this.robotMotionY > 0.0F) || (this.robotArmY < this.robotEndY && this.robotMotionY < 0.0F) ) {
            this.robotArmY = this.robotEndY;
            this.robotMotionY = 0.0F;
        }

        if( isActive ) {
            if( !this.prevActive || this.ticksToNextPos >= 20 ){
                this.animateRng(spedUp);
                this.ticksToNextPos = 0;
            } else {
                this.ticksToNextPos++;
            }
        } else {
            this.animateReset(spedUp);
//            this.spawnParticle = null;
        }

        //TODO: spawn particles
//        if( this.isActiveClient && this.spawnParticle != null ) {
//            EnumEffect.ASSEMBLY_SPARK.addEffect(true, this.world.provider.getDimension(),
//                                                spawnParticle.getValue(0), spawnParticle.<Double>getValue(1) + 0.05D, spawnParticle.getValue(2));
//            this.spawnParticle = null;
//        }

        this.prevActive = isActive;
    }

    private void animateRng(boolean spedUp) {
        this.animate(4.0F + MiscUtils.RNG.randomFloat() * 6.0F, -3.5F + MiscUtils.RNG.randomFloat() * -6.0F, spedUp);
    }

    private void animateReset(boolean spedUp) {
        this.animate(2.0F, -9.0F, spedUp);
    }

    private void animate(float x, float y, boolean spedUp) {
        float speedMulti = (spedUp ? 4.0F : 1.0F);
        this.robotMotionX = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (x > this.robotArmX ? 1.0F : -1.0F) * speedMulti;
        this.robotMotionY = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (y > this.robotArmY ? 1.0F : -1.0F) * speedMulti;
        this.robotEndX = x;
        this.robotEndY = y;
    }
}
