/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.turret;

import java.util.UUID;

public interface ITurretInfo
{
    UUID getRecipeId();

    float getHealth();

    int getAmmoCapacity();

    String getRange();
}
