/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.particle;

import de.sanandrew.core.manpack.mod.client.particle.EntityParticle;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ParticleItemTransmitterAction
        extends EntityParticle
{
    private static final IIcon ICON = new IconParticle("itemTransmitter", 256, 256, 0, 0, 32, 32);

    public ParticleItemTransmitterAction(World world, double x, double y, double z) {
        super(world, x, y, z);

        this.particleIcon = ICON;

        this.particleScale = 0.0F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public int getFXLayer() {
        return ClientProxy.PARTICLE_FX_LAYER_1;
    }
}
