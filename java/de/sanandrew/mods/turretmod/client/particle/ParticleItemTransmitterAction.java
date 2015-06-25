/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.particle;

import de.sanandrew.core.manpack.util.client.*;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ParticleItemTransmitterAction
        extends EntityParticle
{
    private static final IIcon ICON = new IconParticle("itemTransmitter", 256, 256, 0, 0, 32, 32);

    private int lastParticleAge;

    public ParticleItemTransmitterAction(World world, double x, double y, double z) {
        super(world, x, y, z);

        this.particleIcon = ICON;
        this.particleScale = 0.0F;
        this.particleMaxAge = 40;

        this.particleRed = 0.0F;
        this.particleGreen = 0.75F;
        this.particleBlue = 0.5F;

        this.motionY = 0.01F;
    }

    @Override
    public void onUpdate() {
        this.lastParticleAge = this.particleAge;

        super.onUpdate();
    }

    @Override
    public int getBrightnessForRender(float partTicks) {
        return 0xF0;
    }

    @Override
    public void renderParticle(Tessellator tess, float partTicks, float viewRotX, float viewRotXZ, float viewRotZ, float viewRotYZ, float viewRotXY) {
        double x = this.lastParticleAge + ((this.particleAge - this.lastParticleAge) * partTicks);

        this.particleScale = (float) Math.sin(Math.pow(10.0F, 0.49715988F - (x / 20.0F)));
        this.particleScale *= 4.0F;
        //this.particleScale = sin(10^(0.49715988-(x/4)));

        super.renderParticle(tess, partTicks, viewRotX, viewRotXZ, viewRotZ, viewRotYZ, viewRotXY);
    }

    @Override
    public int getFXLayer() {
        return ClientProxy.PARTICLE_FX_LAYER_1;
    }
}
