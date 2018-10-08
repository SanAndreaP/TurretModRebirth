package de.sanandrew.mods.turretmod.api.client.render;


import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IRenderRegistry<K, E extends Entity, R extends IRender<E>, D extends Render<E>>
{
    boolean registerRender(@Nonnull K key, @Nonnull R render);

    R removeRender(K key);

    Render<?> getRenderer();
}
