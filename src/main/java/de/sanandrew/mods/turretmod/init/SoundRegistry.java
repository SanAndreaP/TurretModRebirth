/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.ResourceLocations;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SoundRegistry
{
    public static final SoundEvent RICOCHET_SPLASH    = getSoundEvent(ResourceLocations.SOUND_RICOCHET_SPLASH);
    public static final SoundEvent RICOCHET_BULLET    = getSoundEvent(ResourceLocations.SOUND_RICOCHET_BULLET);
    public static final SoundEvent SHOOT_CRYOLATOR    = getSoundEvent(ResourceLocations.SOUND_SHOOT_CRYOLATOR);
    public static final SoundEvent SHOOT_SHOTGUN      = getSoundEvent(ResourceLocations.SOUND_SHOOT_SHOTGUN);
    public static final SoundEvent SHOOT_REVOLVER     = getSoundEvent(ResourceLocations.SOUND_SHOOT_REVOLVER);
    public static final SoundEvent SHOOT_MINIGUN      = getSoundEvent(ResourceLocations.SOUND_SHOOT_MINIGUN);
    public static final SoundEvent SHOOT_LASER        = getSoundEvent(ResourceLocations.SOUND_SHOOT_LASER);
    public static final SoundEvent SHOOT_FLAMETHROWER = getSoundEvent(ResourceLocations.SOUND_SHOOT_FLAMETHROWER);
    public static final SoundEvent HIT_TURRETHIT      = getSoundEvent(ResourceLocations.SOUND_TURRET_HIT);
    public static final SoundEvent HIT_TURRETDEATH    = getSoundEvent(ResourceLocations.SOUND_TURRET_DEATH);
    public static final SoundEvent COLLECT_IA_GET     = getSoundEvent(ResourceLocations.SOUND_TURRET_PICKUP);

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(RICOCHET_SPLASH, RICOCHET_BULLET, SHOOT_CRYOLATOR, SHOOT_SHOTGUN, SHOOT_REVOLVER, SHOOT_MINIGUN, SHOOT_LASER, SHOOT_FLAMETHROWER,
                                        HIT_TURRETHIT, HIT_TURRETDEATH, COLLECT_IA_GET);
    }

    private static SoundEvent getSoundEvent(ResourceLocation rl) {
        return new SoundEvent(rl).setRegistryName(rl);
    }
}
