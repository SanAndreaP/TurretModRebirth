package de.sanandrew.mods.turretmod.api.client.render;


import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

/**
 * <p>A registry, holding and maintaining {@link IRender} objects.</p>
 *
 * @param <K> The type of the key to be used for accessing the IRender objects.
 * @param <E> The type of entity to be rendered.
 * @param <R> The type of IRender instance.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IRenderRegistry<K, E extends Entity, R extends IRender<E>>
{
    /**
     * <p>Registers a new {@link IRender} object to this registry.</p>
     *
     * @param key The key of the new render object to be registered.
     * @param render The new render object to be registered.
     * @return <tt>true</tt>, if registration was successful; <tt>false</tt> otherwise.
     */
    boolean register(@Nonnull K key, @Nonnull R render);

    /**
     * <p>Removes an {@link IRender} object associated with the given key.</p>
     *
     * @param key The key of the object to be removed.
     * @return the object removed.
     */
    R remove(K key);

    /**
     * <p>Returns the {@link Render} instance associated with this registry.</p>
     *
     * @return the Render instance.
     */
    Render<?> getRenderer();
}
