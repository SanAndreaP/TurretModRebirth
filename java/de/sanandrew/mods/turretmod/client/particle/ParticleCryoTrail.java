/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleCryoTrail
        extends Particle
{
    public ParticleCryoTrail(World world, double x, double y, double z, double datX, double datY, double datZ) {
        super(world, x, y, z, datX, datY, datZ);

        this.motionX = datX;
        this.motionY = datY;
        this.motionZ = datZ;

        float f = this.rand.nextFloat() * 0.1F + 0.2F;
        this.particleRed = f + 0.2F;
        this.particleGreen = f + 0.5F;
        this.particleBlue = f + 0.7F;

        this.setParticleTextureIndex(65);
        this.setSize(0.02F, 0.02F);

        this.particleScale = 1.25F - (float) this.rand.nextGaussian() * 0.25F;
        this.particleMaxAge = 30;
    }

    @Override
    public void onUpdate() {
        this.setPosition(this.posX, this.posY, this.posZ);

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionY -= 0.01F;

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.particleScale -= 0.1F;

        if( this.particleAge++ > this.particleMaxAge || this.particleScale <= 0 ) {
            this.setExpired();
        }
    }

    @Override
    public int getBrightnessForRender(float partTicks) {
        float ageDelta = (this.particleAge + partTicks) / this.particleMaxAge;

        if( ageDelta < 0.0F ) {
            ageDelta = 0.0F;
        }

        if( ageDelta > 1.0F ) {
            ageDelta = 1.0F;
        }

        int currBrightness = super.getBrightnessForRender(partTicks);
        int blockLight = currBrightness & 255;
        int skyLight = currBrightness >> 16 & 255;

        blockLight += 240 - (int)(240.0F * ageDelta);


        if( blockLight > 240 ) {
            blockLight = 240;
        }

        return blockLight | skyLight << 16;
    }
}
