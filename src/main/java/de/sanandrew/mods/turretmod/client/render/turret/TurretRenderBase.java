/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.turret;

import de.sanandrew.mods.turretmod.api.client.turret.ITurretRender;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.model.ModelTurretBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class TurretRenderBase<T extends EntityLiving & ITurretInst>
        implements ITurretRender<ModelTurretBase, T>
{
    private final ModelTurretBase mainModel;
    final ITurretRenderRegistry<T> renderRegistry;
    private final Function<Float, ModelTurretBase> modelConst;

    public TurretRenderBase(ITurretRenderRegistry<T> registry, Function<Float, ModelTurretBase> modelConst) {
        this.renderRegistry = registry;
        this.modelConst = modelConst;
        this.mainModel = this.getNewModel(0.0F);
    }

    @Override
    public ModelTurretBase getNewModel(float scale) {
        return this.modelConst.apply(scale);
    }

    @Override
    public ModelTurretBase getModel() {
        return this.mainModel;
    }

    @Override
    public void addLayers(List<LayerRenderer<T>> layerList) {
        this.renderRegistry.addUpgradeLayer(layerList);
        this.renderRegistry.addGlowLayer(layerList, this);
    }
}
