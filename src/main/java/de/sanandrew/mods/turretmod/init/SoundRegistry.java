/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public final class SoundRegistry
{
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TmrConstants.ID);

    public static final SoundEvent RICOCHET_SPLASH    = new SoundEvent(Resources.SOUND_RICOCHET_SPLASH);
    public static final SoundEvent RICOCHET_BULLET    = new SoundEvent(Resources.SOUND_RICOCHET_BULLET);
    public static final SoundEvent SHOOT_CRYOLATOR    = new SoundEvent(Resources.SOUND_SHOOT_CRYOLATOR);
    public static final SoundEvent SHOOT_SHOTGUN      = new SoundEvent(Resources.SOUND_SHOOT_SHOTGUN);
    public static final SoundEvent SHOOT_REVOLVER     = new SoundEvent(Resources.SOUND_SHOOT_REVOLVER);
    public static final SoundEvent SHOOT_MINIGUN      = new SoundEvent(Resources.SOUND_SHOOT_MINIGUN);
    public static final SoundEvent SHOOT_LASER        = new SoundEvent(Resources.SOUND_SHOOT_LASER);
    public static final SoundEvent SHOOT_FLAMETHROWER = new SoundEvent(Resources.SOUND_SHOOT_FLAMETHROWER);
    public static final SoundEvent HIT_TURRETHIT      = new SoundEvent(Resources.SOUND_TURRET_HIT);
    public static final SoundEvent HIT_TURRETDEATH    = new SoundEvent(Resources.SOUND_TURRET_DEATH);
    public static final SoundEvent COLLECT_IA_GET     = new SoundEvent(Resources.SOUND_TURRET_PICKUP);

    private SoundRegistry() { /* no-op */ }

    public static void register(IEventBus bus) {
        Consumer<SoundEvent> r = e -> SOUNDS.register(e.getLocation().getPath(), () -> e);

        r.accept(RICOCHET_SPLASH);
        r.accept(RICOCHET_BULLET);
        r.accept(SHOOT_CRYOLATOR);
        r.accept(SHOOT_SHOTGUN);
        r.accept(SHOOT_REVOLVER);
        r.accept(SHOOT_MINIGUN);
        r.accept(SHOOT_LASER);
        r.accept(SHOOT_FLAMETHROWER);
        r.accept(HIT_TURRETHIT);
        r.accept(HIT_TURRETDEATH);
        r.accept(COLLECT_IA_GET);

        SOUNDS.register(bus);
    }
}
