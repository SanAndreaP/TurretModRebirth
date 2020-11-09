/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.effect;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleAssemblySpark
        extends Particle
{
    public ParticleAssemblySpark(World world, double x, double y, double z, double datX, double datY, double datZ) {
        super(world, x, y, z, datX, datY, datZ);

        float f = this.rand.nextFloat() * 0.1F + 0.2F;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;

        this.setParticleTextureIndex(0);
        this.setSize(0.02F, 0.02F);

        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.5F;
        this.motionX *= 0.02D;
        this.motionY *= 0.02D;
        this.motionZ *= 0.02D;
        this.particleMaxAge = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
        this.canCollide = false;
    }

    @Override
    public void onUpdate() {
        this.setPosition(this.posX, this.posY, this.posZ);

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.move(this.motionX, this.motionY, this.motionZ);

        if( this.particleAge++ >= this.particleMaxAge ) {
            this.setExpired();
        }
    }
}
