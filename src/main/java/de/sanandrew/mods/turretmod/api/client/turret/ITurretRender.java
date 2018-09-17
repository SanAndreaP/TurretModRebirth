/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.turret;

import de.sanandrew.mods.turretmod.api.client.render.IRender;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

import java.util.List;

public interface ITurretRender<T extends ModelBase, E extends EntityLivingBase>
        extends IRender<E>
{
    T getNewModel(float scale);

    T getModel();

    default void addLayers(List<LayerRenderer<E>> layerList) { }
}
