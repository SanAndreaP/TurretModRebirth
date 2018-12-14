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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public final class Sounds
{

    private static final ResourceLocation R_RICOCHET_SPLASH = new ResourceLocation(TmrConstants.ID, "ricochet.splash");
    private static final ResourceLocation R_RICOCHET_BULLET = new ResourceLocation(TmrConstants.ID, "ricochet.bullet");
    private static final ResourceLocation R_SHOOT_CRYOLATOR = new ResourceLocation(TmrConstants.ID, "shoot.cryolator");
    private static final ResourceLocation R_SHOOT_SHOTGUN = new ResourceLocation(TmrConstants.ID, "shoot.shotgun");
    private static final ResourceLocation R_SHOOT_REVOLVER = new ResourceLocation(TmrConstants.ID, "shoot.revolver");
    private static final ResourceLocation R_SHOOT_MINIGUN = new ResourceLocation(TmrConstants.ID, "shoot.minigun");
    private static final ResourceLocation R_SHOOT_LASER = new ResourceLocation(TmrConstants.ID, "shoot.laser");
    private static final ResourceLocation R_SHOOT_FTHROWER = new ResourceLocation(TmrConstants.ID, "shoot.flamethrower");
    private static final ResourceLocation R_TURRET_HIT = new ResourceLocation(TmrConstants.ID, "hit.turrethit");
    private static final ResourceLocation R_TURRET_DEATH = new ResourceLocation(TmrConstants.ID, "hit.turretdeath");
    private static final ResourceLocation R_TURRET_COLLECT = new ResourceLocation(TmrConstants.ID, "collect.ia_get");

    public static final SoundEvent RICOCHET_SPLASH = new SoundEvent(R_RICOCHET_SPLASH).setRegistryName(getRegistryName(R_RICOCHET_SPLASH));
    public static final SoundEvent RICOCHET_BULLET = new SoundEvent(R_RICOCHET_BULLET).setRegistryName(getRegistryName(R_RICOCHET_BULLET));
    public static final SoundEvent SHOOT_CRYOLATOR = new SoundEvent(R_SHOOT_CRYOLATOR).setRegistryName(getRegistryName(R_SHOOT_CRYOLATOR));
    public static final SoundEvent SHOOT_SHOTGUN = new SoundEvent(R_SHOOT_SHOTGUN).setRegistryName(getRegistryName(R_SHOOT_SHOTGUN));
    public static final SoundEvent SHOOT_REVOLVER = new SoundEvent(R_SHOOT_REVOLVER).setRegistryName(getRegistryName(R_SHOOT_REVOLVER));
    public static final SoundEvent SHOOT_MINIGUN = new SoundEvent(R_SHOOT_MINIGUN).setRegistryName(getRegistryName(R_SHOOT_MINIGUN));
    public static final SoundEvent SHOOT_LASER = new SoundEvent(R_SHOOT_LASER).setRegistryName(getRegistryName(R_SHOOT_LASER));
    public static final SoundEvent SHOOT_FLAMETHROWER = new SoundEvent(R_SHOOT_FTHROWER).setRegistryName(getRegistryName(R_SHOOT_FTHROWER));
    public static final SoundEvent HIT_TURRETHIT = new SoundEvent(R_TURRET_HIT).setRegistryName(getRegistryName(R_TURRET_HIT));
    public static final SoundEvent HIT_TURRETDEATH = new SoundEvent(R_TURRET_DEATH).setRegistryName(getRegistryName(R_TURRET_DEATH));
    public static final SoundEvent COLLECT_IA_GET = new SoundEvent(R_TURRET_COLLECT).setRegistryName(getRegistryName(R_TURRET_COLLECT));

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(RICOCHET_SPLASH,
                                        RICOCHET_BULLET,
                                        SHOOT_CRYOLATOR,
                                        SHOOT_SHOTGUN,
                                        SHOOT_REVOLVER,
                                        SHOOT_MINIGUN,
                                        SHOOT_LASER,
                                        SHOOT_FLAMETHROWER,
                                        HIT_TURRETHIT,
                                        HIT_TURRETDEATH,
                                        COLLECT_IA_GET
        );
    }

    private static ResourceLocation getRegistryName(ResourceLocation resLoc) {
        return new ResourceLocation(resLoc.getResourceDomain(), resLoc.getResourcePath().replace('.', '_'));
    }
}
