package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.entity.turret.delegate.Crossbow;
import net.minecraft.util.ResourceLocation;

public final class Turrets
{
    public static final ITurret CROSSBOW = new Crossbow(new ResourceLocation(TmrConstants.ID, "crossbow_turret"));

    public static void register(ITurretRegistry registry) {
        registry.registerAll(CROSSBOW);
    }
}
