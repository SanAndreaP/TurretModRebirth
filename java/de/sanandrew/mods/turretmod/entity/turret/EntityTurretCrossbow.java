/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.util.Textures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTurretCrossbow
        extends EntityTurret
{
    public EntityTurretCrossbow(World world) {
        super(world);
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Textures.TURRET_T1_CROSSBOW.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Textures.TURRET_T1_CROSSBOW_GLOW.getResource();
    }
}
