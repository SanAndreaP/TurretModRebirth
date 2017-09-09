package de.sanandrew.mods.turretmod.api.client.turretinfo;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public interface ITurretRender<T extends ModelBase, E extends EntityLiving & ITurretInst>
{
    T getNewModel(float scale);

    T getModel();

    default void addLayers(List<LayerRenderer<E>> layerList) { }

    default void doRender(ITurretInst turretInst, double x, double y, double z, float entityYaw, float partialTicks) { }
}
