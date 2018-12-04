/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.turret;

import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretShieldgen;
import de.sanandrew.mods.turretmod.client.render.layer.LayerTurretShieldLightning;
import de.sanandrew.mods.turretmod.client.render.layer.LayerTurretUpgradesShieldGen;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;

import java.util.List;

public class TurretRenderShieldGen<T extends EntityLiving & ITurretInst>
        extends TurretRenderBase<T>
{
    public TurretRenderShieldGen(ITurretRenderRegistry<T> registry) {
        super(registry, ModelTurretShieldgen::new);
    }

    @Override
    public void addLayers(List<LayerRenderer<T>> layerList) {
        layerList.add(new LayerTurretShieldLightning<>());
        layerList.add(new LayerTurretUpgradesShieldGen<>());
        this.renderRegistry.addGlowLayer(layerList, this);
    }
}
