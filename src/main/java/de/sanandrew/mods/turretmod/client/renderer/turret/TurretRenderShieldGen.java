/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.turret;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretForcefield;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;

public class TurretRenderShieldGen<E extends LivingEntity & ITurretEntity>
        extends TurretRenderBase<E, ModelTurretForcefield<E>>
{
    public TurretRenderShieldGen(EntityRendererManager manager) {
        super(manager, ModelTurretForcefield::new);
    }

    //TODO: reimplement forcefield
    @Override
    protected void addTurretLayers() {
//        this.addLayer(new LayerTurretShieldLightning<>(this));
//        this.addLayer(new LayerTurretUpgradesShieldGen<>(this));
//        this.addLayer(new LayerTurretGlow<>(this));
    }

//    @Override
//    public void addLayers(List<LayerRenderer<T>> layerList) {
//        layerList.add(new LayerTurretShieldLightning<>());
//        layerList.add(new LayerTurretUpgradesShieldGen<>());
//        this.renderRegistry.addGlowLayer(layerList, this);
//    }

}
