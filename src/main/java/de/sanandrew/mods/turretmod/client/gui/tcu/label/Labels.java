/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.label;

import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;

public final class Labels
{
    public static void initialize(ILabelRegistry registry) {
        registry.register(new LabelTurretName());
        registry.register(new LabelTurretHealth());
        registry.register(new LabelTurretAmmo());
        registry.register(new LabelTurretTarget());
        registry.register(new LabelTurretPersShield());
        registry.register(new LabelTurretShield());
    }
}
