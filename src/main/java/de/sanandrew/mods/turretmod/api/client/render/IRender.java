package de.sanandrew.mods.turretmod.api.client.render;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public interface IRender<E extends Entity>
{

    default void doRender(IRenderInst<E> render, E entity, double x, double y, double z, float entityYaw, float partialTicks) { }

    default ResourceLocation getRenderTexture(E entity) {
        return null;
    }
}
