package de.sanandrew.mods.turretmod.api.turret;

import java.util.List;
import java.util.UUID;

public interface ITurretRegistry
{
    List<ITurretInfo> getRegisteredInfos();

    ITurretInfo getInfo(UUID uuid);

    ITurretInfo getInfo(Class<? extends EntityTurret> clazz);

    boolean registerTurretInfo(ITurretInfo type);
}
