package de.sanandrew.mods.turretmod.api.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IRenderInst<E extends Entity>
{
    boolean bindRenderEntityTexture(E entity);

    boolean renderOutlines();

    Render<?> getRender();

    int getRenderTeamColor(E entity);
}
