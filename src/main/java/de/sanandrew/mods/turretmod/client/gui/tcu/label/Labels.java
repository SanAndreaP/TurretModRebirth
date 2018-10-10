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
        registry.registerLabelElement(new LabelTurretName());
        registry.registerLabelElement(new LabelTurretHealth());
        registry.registerLabelElement(new LabelTurretAmmo());
        registry.registerLabelElement(new LabelTurretTarget());
        registry.registerLabelElement(new LabelTurretPersShield());
        registry.registerLabelElement(new LabelTurretShield());
    }
}
