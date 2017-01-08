/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class Sounds
{
    private static final ResourceLocation R_RICOCHET_SPLASH = new ResourceLocation(TmrConstants.ID, "ricochet.splash");
    private static final ResourceLocation R_RICOCHET_BULLET = new ResourceLocation(TmrConstants.ID, "ricochet.bullet");
    private static final ResourceLocation R_SHOOT_CRYOLATOR = new ResourceLocation(TmrConstants.ID, "shoot.cryolator");
    private static final ResourceLocation R_SHOOT_SHOTGUN = new ResourceLocation(TmrConstants.ID, "shoot.shotgun");
    private static final ResourceLocation R_SHOOT_REVOLVER = new ResourceLocation(TmrConstants.ID, "shoot.revolver");
    private static final ResourceLocation R_SHOOT_MINIGUN = new ResourceLocation(TmrConstants.ID, "shoot.minigun");
    private static final ResourceLocation R_SHOOT_LASER = new ResourceLocation(TmrConstants.ID, "shoot.laser");
    private static final ResourceLocation R_TURRET_HIT = new ResourceLocation(TmrConstants.ID, "hit.turrethit");
    private static final ResourceLocation R_TURRET_DEATH = new ResourceLocation(TmrConstants.ID, "hit.turretDeath");
    private static final ResourceLocation R_TURRET_COLLECT = new ResourceLocation(TmrConstants.ID, "collect.ia_get");

    public static final SoundEvent RICOCHET_SPLASH = new SoundEvent(R_RICOCHET_SPLASH);
    public static final SoundEvent RICOCHET_BULLET = new SoundEvent(R_RICOCHET_BULLET);
    public static final SoundEvent SHOOT_CRYOLATOR = new SoundEvent(R_SHOOT_CRYOLATOR);
    public static final SoundEvent SHOOT_SHOTGUN = new SoundEvent(R_SHOOT_SHOTGUN);
    public static final SoundEvent SHOOT_REVOLVER = new SoundEvent(R_SHOOT_REVOLVER);
    public static final SoundEvent SHOOT_MINIGUN = new SoundEvent(R_SHOOT_MINIGUN);
    public static final SoundEvent SHOOT_LASER = new SoundEvent(R_SHOOT_LASER);
    public static final SoundEvent TURRET_HIT = new SoundEvent(R_TURRET_HIT);
    public static final SoundEvent TURRET_DEATH = new SoundEvent(R_TURRET_DEATH);
    public static final SoundEvent TURRET_COLLECT = new SoundEvent(R_TURRET_COLLECT);

    public static void initialize() {
        register(RICOCHET_SPLASH, R_RICOCHET_SPLASH);
        register(RICOCHET_BULLET, R_RICOCHET_BULLET);
        register(SHOOT_CRYOLATOR, R_SHOOT_CRYOLATOR);
        register(SHOOT_SHOTGUN, R_SHOOT_SHOTGUN);
        register(SHOOT_REVOLVER, R_SHOOT_REVOLVER);
        register(SHOOT_MINIGUN, R_SHOOT_MINIGUN);
        register(SHOOT_LASER, R_SHOOT_LASER);
        register(TURRET_HIT, R_TURRET_HIT);
        register(TURRET_DEATH, R_TURRET_DEATH);
        register(TURRET_COLLECT, R_TURRET_COLLECT);

        EntityTurret.hurtSound = TURRET_HIT;
        EntityTurret.deathSound = TURRET_DEATH;
        EntityTurret.collectSound = TURRET_COLLECT;
    }

    private static void register(SoundEvent event, ResourceLocation regName) {
        event.setRegistryName(regName);
        GameRegistry.register(event);
    }
}
