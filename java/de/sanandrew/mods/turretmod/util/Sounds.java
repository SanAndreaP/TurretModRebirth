/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class Sounds
{
    public static final SoundEvent RICOCHET_SPLASH = new SoundEvent(new ResourceLocation(TurretModRebirth.ID, "ricochet.splash"));
    public static final SoundEvent RICOCHET_BULLET = new SoundEvent(new ResourceLocation(TurretModRebirth.ID, "ricochet.bullet"));
    public static final SoundEvent SHOOT_CRYOLATOR = new SoundEvent(new ResourceLocation(TurretModRebirth.ID, "shoot.cryolator"));
    public static final SoundEvent SHOOT_SHOTGUN = new SoundEvent(new ResourceLocation(TurretModRebirth.ID, "shoot.shotgun"));
    public static final SoundEvent TURRET_HIT = new SoundEvent(new ResourceLocation(TurretModRebirth.ID, "hit.turrethit"));
    public static final SoundEvent TURRET_DEATH = new SoundEvent(new ResourceLocation(TurretModRebirth.ID, "hit.turretDeath"));
    public static final SoundEvent TURRET_COLLECT = new SoundEvent(new ResourceLocation(TurretModRebirth.ID, "collect.ia_get"));

    public static void initialize() {
        register(RICOCHET_SPLASH);
        register(RICOCHET_BULLET);
        register(SHOOT_CRYOLATOR);
        register(TURRET_HIT);
        register(TURRET_DEATH);
        register(TURRET_COLLECT);
    }

    private static void register(SoundEvent event) {
        GameRegistry.register(event, new ResourceLocation(TurretModRebirth.ID, event.getSoundName().getResourcePath()));
    }
}
