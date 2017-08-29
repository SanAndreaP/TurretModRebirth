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
import net.minecraftforge.fml.common.registry.GameRegistry;

@SuppressWarnings("ConstantNamingConvention")
@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(TmrConstants.ID)
public final class Sounds
{

    public static final SoundEvent ricochet_splash = nil();//new SoundEvent(R_RICOCHET_SPLASH);
    public static final SoundEvent ricochet_bullet = nil();// = new SoundEvent(R_RICOCHET_BULLET);
    public static final SoundEvent shoot_cryolator = nil();// = new SoundEvent(R_SHOOT_CRYOLATOR);
    public static final SoundEvent shoot_shotgun = nil();// = new SoundEvent(R_SHOOT_SHOTGUN);
    public static final SoundEvent shoot_revolver = nil();// = new SoundEvent(R_SHOOT_REVOLVER);
    public static final SoundEvent shoot_minigun = nil();// = new SoundEvent(R_SHOOT_MINIGUN);
    public static final SoundEvent shoot_laser = nil();// = new SoundEvent(R_SHOOT_LASER);
    public static final SoundEvent shoot_flamethrower = nil();// = new SoundEvent(R_SHOOT_FTHROWER);
    public static final SoundEvent hit_turrethit = nil();// = new SoundEvent(R_TURRET_HIT);
    public static final SoundEvent hit_turretdeath = nil();// = new SoundEvent(R_TURRET_DEATH);
    public static final SoundEvent collect_ia_get = nil();// = new SoundEvent(R_TURRET_COLLECT);

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

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                new SoundEvent(R_RICOCHET_SPLASH).setRegistryName(getRegistryName(R_RICOCHET_SPLASH)),
                new SoundEvent(R_RICOCHET_BULLET).setRegistryName(getRegistryName(R_RICOCHET_BULLET)),
                new SoundEvent(R_SHOOT_CRYOLATOR).setRegistryName(getRegistryName(R_SHOOT_CRYOLATOR)),
                new SoundEvent(R_SHOOT_SHOTGUN).setRegistryName(getRegistryName(R_SHOOT_SHOTGUN)),
                new SoundEvent(R_SHOOT_REVOLVER).setRegistryName(getRegistryName(R_SHOOT_REVOLVER)),
                new SoundEvent(R_SHOOT_MINIGUN).setRegistryName(getRegistryName(R_SHOOT_MINIGUN)),
                new SoundEvent(R_SHOOT_LASER).setRegistryName(getRegistryName(R_SHOOT_LASER)),
                new SoundEvent(R_SHOOT_FTHROWER).setRegistryName(getRegistryName(R_SHOOT_FTHROWER)),
                new SoundEvent(R_TURRET_HIT).setRegistryName(getRegistryName(R_TURRET_HIT)),
                new SoundEvent(R_TURRET_DEATH).setRegistryName(getRegistryName(R_TURRET_DEATH)),
                new SoundEvent(R_TURRET_COLLECT).setRegistryName(getRegistryName(R_TURRET_COLLECT))
        );
    }

    private static ResourceLocation getRegistryName(ResourceLocation resLoc) {
        return new ResourceLocation(resLoc.getResourceDomain(), resLoc.getResourcePath().replace('.', '_'));
    }

    private static SoundEvent nil() {
        return null;
    }
//    private static int registryID = 0;
//
//    public static void initialize() {
//        register(RICOCHETs_SPLASH, R_RICOCHET_SPLASH);
//        register(RICOCsHET_BULLET, R_RICOCHET_BULLET);
//        register(SHOOsT_CRYOLATOR, R_SHOOT_CRYOLATOR);
//        register(SHOOsT_SHOTGUN, R_SHOOT_SHOTGUN);
//        register(SHOOsT_REVOLVER, R_SHOOT_REVOLVER);
//        register(SHOOsT_MINIGUN, R_SHOOT_MINIGUN);
//        register(SHOOTs_LASER, R_SHOOT_LASER);
//        register(SHOOTs_FTHROWER, R_SHOOT_FTHROWER);
//        register(TURREsT_HIT, R_TURRET_HIT);
//        register(TURREsT_DEATH, R_TURRET_DEATH);
//        register(TURREsT_COLLECT, R_TURRET_COLLECT);
//
    //        EntityTurret.hurtSound = TURRsET_HIT;
    //        EntityTurret.deathSound = TURRsET_DEATH;
    //        EntityTurret.collectSound = TURRsET_COLLECT;
//    }
//
//    private static void register(SoundEvent event, ResourceLocation regName) {
//        event.setRegistryName(regName);
//
//        SoundEvent.REGISTRY.register(registryID++, regName, event);
//    }
}
