package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;

public interface IGuiTurretProvider
{
    /**
     * @return the turret instance associated with this GUI.
     */
    ITurretInst getTurretInst();
}
