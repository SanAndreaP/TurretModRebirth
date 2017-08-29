package de.sanandrew.mods.turretmod.api.turret;

import java.util.List;
import java.util.UUID;

public interface ITurretRegistry
{
    List<ITurret> getTurrets();

    ITurret getTurret(UUID uuid);

    ITurret getTurret(Class<? extends ITurret> clazz);

    boolean registerTurret(ITurret type);
}
