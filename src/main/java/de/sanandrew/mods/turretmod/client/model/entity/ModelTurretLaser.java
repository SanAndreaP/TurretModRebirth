/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model.entity;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResourceManager;

public class ModelTurretLaser<E extends LivingEntity & ITurretEntity>
        extends ModelTurretBase<E>
{
    @Override
    public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase<E>, ModelJsonLoader.JsonBase> loader) {
        loader.addCustomModelRenderer("barrelLeftI", ModelRendererCulled.class);
        loader.addCustomModelRenderer("barrelLeftII", ModelRendererCulled.class);
        loader.addCustomModelRenderer("barrelRightI", ModelRendererCulled.class);
        loader.addCustomModelRenderer("barrelRightII", ModelRendererCulled.class);

        super.onReload(iResourceManager, loader);
    }
}
