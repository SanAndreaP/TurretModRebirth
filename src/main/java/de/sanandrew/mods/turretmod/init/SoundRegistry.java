/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class SoundRegistry
{
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TmrConstants.ID);

    private static final Map<SoundEvent, ResourceLocation> REGISTRY_LOCATIONS = new HashMap<>();

    public static final SoundEvent RICOCHET_SPLASH    = newSoundEvent(Resources.SOUND_RICOCHET_SPLASH);
    public static final SoundEvent RICOCHET_BULLET    = newSoundEvent(Resources.SOUND_RICOCHET_BULLET);
    public static final SoundEvent SHOOT_CRYOLATOR    = newSoundEvent(Resources.SOUND_SHOOT_CRYOLATOR);
    public static final SoundEvent SHOOT_SHOTGUN      = newSoundEvent(Resources.SOUND_SHOOT_SHOTGUN);
    public static final SoundEvent SHOOT_REVOLVER     = newSoundEvent(Resources.SOUND_SHOOT_REVOLVER);
    public static final SoundEvent SHOOT_MINIGUN      = newSoundEvent(Resources.SOUND_SHOOT_MINIGUN);
    public static final SoundEvent SHOOT_LASER        = newSoundEvent(Resources.SOUND_SHOOT_LASER);
    public static final SoundEvent SHOOT_FLAMETHROWER = newSoundEvent(Resources.SOUND_SHOOT_FLAMETHROWER);
    public static final SoundEvent HIT_TURRETHIT      = newSoundEvent(Resources.SOUND_TURRET_HIT);
    public static final SoundEvent HIT_TURRETDEATH    = newSoundEvent(Resources.SOUND_TURRET_DEATH);
    public static final SoundEvent COLLECT_IA_GET     = newSoundEvent(Resources.SOUND_TURRET_PICKUP);

    private SoundRegistry() { /* no-op */ }

    private static SoundEvent newSoundEvent(ResourceLocation location) {
        SoundEvent se = new SoundEvent(location);
        REGISTRY_LOCATIONS.put(se, location);

        return se;
    }

    public static void register(IEventBus bus) {
        Consumer<SoundEvent> r = e -> SOUNDS.register(REGISTRY_LOCATIONS.get(e).getPath(), () -> e);

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

        REGISTRY_LOCATIONS.clear();

        SOUNDS.register(bus);
    }
}
