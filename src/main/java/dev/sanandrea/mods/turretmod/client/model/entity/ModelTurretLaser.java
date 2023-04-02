/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.model.entity;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
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
