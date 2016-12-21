/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ModelTurretLaser
        extends ModelTurretBase
{
    public ModelTurretLaser(float scale) {
        super(scale);
    }

    @Override
    public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase, ModelJsonLoader.ModelJson> loader) {
        loader.addCustomModelRenderer("barrelLeftI", ModelRendererCulled.class);
        loader.addCustomModelRenderer("barrelLeftII", ModelRendererCulled.class);
        loader.addCustomModelRenderer("barrelRightI", ModelRendererCulled.class);
        loader.addCustomModelRenderer("barrelRightII", ModelRendererCulled.class);

        super.onReload(iResourceManager, loader);
    }

    @Override
    public ResourceLocation getModelLocation() {
        return Resources.TURRET_T3_LASER_MODEL.getResource();
    }
}
