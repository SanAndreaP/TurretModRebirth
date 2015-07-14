/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api;

import java.util.List;

public interface UpgradeHandler<T extends Turret>
{
    void registerUpgradeToUpdateQueue(TurretUpgrade upgrade, UpgradeQueueData queueData);

    UpgradeQueueData getUpgradeQueueData(TurretUpgrade upgrade);

    boolean hasUpgrade(TurretUpgrade upg);

    void removeUpgrade(T turret, TurretUpgrade upg);

    List<TurretUpgrade> getUpgradeList();

    boolean applyUpgrade(T turret, TurretUpgrade upg);

    int getMaxUpgradeSlots(T turret);
}
