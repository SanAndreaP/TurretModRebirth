package de.sanandrew.mods.turretmod.api.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

/**
 * <p>A wrapper interface for the {@link Render} object of the entity to access protected/private methods/fields.</p>
 * @param <E> The entity type rendered.
 */
@SuppressWarnings({"UnusedReturnValue", "JavadocReference"})
public interface IRenderInst<E extends Entity>
{
    /**
     * <p>Binds the current entity texture.</p>
     * @param entity The entity rendered.
     * @return the current texture.
     *
     * @see IRender#getRenderTexture(Entity)
     * @see Render#bindEntityTexture(Entity)
     */
    boolean bindRenderEntityTexture(E entity);

    /**
     * <p>Indicates wether outlines should be rendered on the entity.</p>
     *
     * @return <tt>true</tt>, if outlines should be rendered; <tt>false</tt> otherwise.
     * @see Render#renderOutlines
     */
    boolean renderOutlines();

    /**
     * <p>Returns the actual {@link Render} instance without need for casting this object.</p>
     *
     * @return this instance as Render.
     */
    Render<?> getRender();

    /**
     * <p>Returns the color from the team of this entity.</p>
     *
     * @param entity The entity rendered.
     * @return the team color of this entity.
     * @see Render#getTeamColor(Entity)
     */
    int getRenderTeamColor(E entity);
}
