/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.turret;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretForcefield;
import de.sanandrew.mods.turretmod.client.render.layer.LayerTurretGlow;
import de.sanandrew.mods.turretmod.client.render.layer.LayerTurretShieldLightning;
import de.sanandrew.mods.turretmod.client.render.layer.LayerTurretUpgradesShieldGen;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;

public class TurretRenderShieldGen<E extends LivingEntity & ITurretInst>
        extends TurretRenderBase<E, ModelTurretForcefield<E>>
{
    public TurretRenderShieldGen(EntityRendererManager manager) {
        super(manager, ModelTurretForcefield::new);
    }

    @Override
    protected void addTurretLayers() {
        this.addLayer(new LayerTurretShieldLightning<>(this));
        this.addLayer(new LayerTurretUpgradesShieldGen<>(this));
        this.addLayer(new LayerTurretGlow<>(this));
    }

//    @Override
//    public void addLayers(List<LayerRenderer<T>> layerList) {
//        layerList.add(new LayerTurretShieldLightning<>());
//        layerList.add(new LayerTurretUpgradesShieldGen<>());
//        this.renderRegistry.addGlowLayer(layerList, this);
//    }

}
