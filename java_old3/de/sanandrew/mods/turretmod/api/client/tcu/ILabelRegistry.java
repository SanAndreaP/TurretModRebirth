package de.sanandrew.mods.turretmod.api.client.tcu;

/**
 * <p>A registry, holding and maintaining {@link ILabelElement} objects.</p>
 */
public interface ILabelRegistry
{
    /**
     * <p>The minimum width a label element can be.</p>
     */
    float MIN_WIDTH = 128.0F;

    /**
     * <p>Registers a new {@link ILabelElement} object to this registry.</p>
     *
     * @param element The new label element to be registered.
     */
    void register(ILabelElement element);
}
