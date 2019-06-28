package de.sanandrew.mods.turretmod.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>A common interface used by registry objects in the Turret Mod API.</p>
 * @param <T> the type of objects registered by this registry
 */
public interface IRegistry<T extends IRegistryType>
{
    /**
     * <p>Registers a new type to this registry</p>
     * @param type The new type to be registered, cannot be <tt>null</tt>
     */
    void register(@Nonnull T type);

    /**
     * @return a collection of registered types, usually and preferably immutable, cannot be <tt>null</tt>
     */
    @Nonnull
    Collection<T> getTypes();

    /**
     * <p>Fetches a type that has the given ID.</p>
     * <p>If no type is found, this needs to return an "unknown" type (a type that returns <tt>false</tt> in {@link IRegistryType#isValid()}).</p>
     * @param id The ID of the type needed
     * @return the type matching the ID given or an "unknown" type, if no type is found, cannot be <tt>null</tt>
     */
    @Nonnull
    T getType(ResourceLocation id);

    @Nonnull
    T getType(ItemStack stack);

    @Nonnull
    default ItemStack getItem(ResourceLocation id) {
        throw new UnsupportedOperationException("Cannot create item! This registry does not register items.");
    }

    default boolean isEqual(ItemStack s1, ItemStack s2) {
        return this.isEqual(this.getType(s1), this.getType(s2));
    }

    default boolean isEqual(T t1, T t2) {
        return t1.isValid() && t2.isValid() && t1.getId().equals(t2.getId());
    }

    @SafeVarargs
    static <T extends IRegistryType> void registerAll(IRegistry<T> registry, T... types) {
        Arrays.stream(types).forEach(registry::register);
    }
}
