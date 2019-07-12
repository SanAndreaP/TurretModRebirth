package de.sanandrew.mods.turretmod.api.client.render;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * <p>An object defining an entity renderer for an entity delegate.</p>
 * <p>This is most commonly used by turrets and their projectiles.</p>
 *
 * @param <E> The entity type rendered.
 */
@SideOnly(Side.CLIENT)
public interface IRender<E extends Entity>
{
    /**
     * <p>Renders the entity at the given position.</p>
     *
     * @param render The {@link net.minecraft.client.renderer.entity.Render} instance of the entity.
     * @param entity The entity to be rendered.
     * @param x The X position of the entity for rendering.
     * @param y The Y position of the entity for rendering.
     * @param z The Z position of the entity for rendering.
     * @param entityYaw The yaw angle of the entity.
     * @param partialTicks The partial render tick amount.
     */
    default void doRender(IRenderInst<E> render, E entity, double x, double y, double z, float entityYaw, float partialTicks) { }

    /**
     * <p>Returns the current texture to be bound for rendering.</p>
     *
     * @param entity The entity to be rendered.
     * @return the current entity texture.
     */
    default ResourceLocation getRenderTexture(E entity) {
        return null;
    }
}
