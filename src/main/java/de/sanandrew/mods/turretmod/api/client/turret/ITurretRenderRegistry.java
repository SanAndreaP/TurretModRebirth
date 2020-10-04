package de.sanandrew.mods.turretmod.api.client.turret;

import de.sanandrew.mods.turretmod.api.client.render.IRenderRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ITurretRenderRegistry<E extends EntityLiving & ITurretInst>
        extends IRenderRegistry<ITurret, E, ITurretRender<?, E>>
{
    void addUpgradeLayer(List<LayerRenderer<E>> layerList);

    <T extends ModelBase> void addGlowLayer(List<LayerRenderer<E>> layerList, ITurretRender<T, E> render);
}
