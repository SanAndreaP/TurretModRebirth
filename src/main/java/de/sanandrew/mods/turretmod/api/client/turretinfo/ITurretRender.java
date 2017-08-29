package de.sanandrew.mods.turretmod.api.client.turretinfo;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;

import java.util.List;

public interface ITurretRender<T extends ModelBase>
{
    T getNewModel(float scale);

    T getModel();

    default <E extends EntityLiving & ITurretInst> void addLayers(List<LayerRenderer<E>> layerList) { }

    default void doRender(ITurretInst entity, double x, double y, double z, float entityYaw, float partialTicks) { }
}
