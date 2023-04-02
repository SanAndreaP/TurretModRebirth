/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.tileentity.assembly;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class RobotArm
{
    private float    robotArmX = 2.0F;
    private float    robotArmY = -9.0F;
    private float    prevRobotArmX = 0.0F;
    private float    prevRobotArmY = 0.0F;
    private float    robotMotionX;
    private float    robotMotionY;
    private float    robotEndX;
    private float    robotEndY;
    private Vector3d spawnParticlePos;

    private boolean prevActive;
    private int ticksToNextPos = 0;

    void process(World level, boolean isActive, boolean spedUp) {
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
        }

        if( this.spawnParticlePos != null ) {
            TurretModRebirth.PROXY.spawnParticle(level, ParticleTypes.MYCELIUM, this.spawnParticlePos, 0, 0.0F, 0.2F, 0.0F, 1.0F);

            this.spawnParticlePos = null;
        }

        this.prevActive = isActive;
    }

    public void spawnParticle(double x, double y, double z) {
        this.spawnParticlePos = new Vector3d(x, y, z);
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

    public float getArmX(float partTicks) {
        return Math.max(2.0F, Math.min(12.0F, this.prevRobotArmX + (this.robotArmX - this.prevRobotArmX) * partTicks)) - 7.0F;
    }

    public float getArmZ(float partTicks) {
        return Math.max(-11.0F, Math.min(-3.0F, this.prevRobotArmY + (this.robotArmY - this.prevRobotArmY) * partTicks));
    }

    public boolean isInBuildVicinity() {
        return this.robotArmX >= 4.0F && this.robotArmX <= 10.0F && this.robotArmY <= -3.5F && this.robotArmY >= -9.5F;
    }
}
