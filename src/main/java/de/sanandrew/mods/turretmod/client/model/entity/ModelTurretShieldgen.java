/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.model.entity;

import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.util.ResourceLocation;

public class ModelTurretShieldgen
        extends ModelTurretBase
{
    public ModelTurretShieldgen(float scale) {
        super(scale);
    }

    @Override
    public ResourceLocation getModelLocation() {
        return Resources.TURRET_T2_SHIELDGEN_MODEL.resource;
    }
}
