package de.sanandrew.mods.turretmod.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>A registry, holding and maintaining objects with a common function/purpose.</p>
 *
 * @param <T> the type of objects registered by this registry.
 */
public interface IRegistry<T extends IRegistryObject>
{
    /**
     * <p>Registers a new object to this registry.</p>
     *
     * @param obj The new object to be registered.
     * @throws NullPointerException if the object to be registered is <tt>null</tt>
     */
    void register(@Nonnull T obj);

    /**
     * <p>Returns an unmodifiable view of the objects registered in this registry.</p>
     *
     * @return an unmodifiable view of registered objects.
     */
    @Nonnull
    Collection<T> getObjects();

    /**
     * <p>Fetches an object that has the given ID.</p>
     * <p>If no object is found, this returns the {@link #getDefaultObject() default object}.</p>
     *
     * @param id The ID of the object requested.
     * @return the object with the ID given or the default object, if none is found.
     */
    @Nonnull
    T getObject(ResourceLocation id);

    /**
     * <p>Reads the object from the given ItemStack.</p>
     * <p>If no object can be found either in this registry or in the ItemStack itself, or this registry doesn't support items,
     *    this returns the {@link #getDefaultObject() default object}.</p>
     *
     * @param stack The ItemStack that may contain a registered object.
     * @return the object contained in the ItemStack or the default object, if none is found.
     */
    @Nonnull
    T getObject(ItemStack stack);

    /**
     * <p>Returns the default object, an unregistered empty object that returns <tt>false</tt> in {@link IRegistryObject#isValid()}.</p>
     *
     * @return the default object.
     */
    @Nonnull
    T getDefaultObject();

    /**
     * <p>Creates a new ItemStack instance with a stack size of 1, containing the object given by its ID.</p>
     * <p>If no object can be fetched with the ID, this returns {@link ItemStack#EMPTY}.</p>
     * <p>If this registry doesn't support items, this throws an {@link UnsupportedOperationException}.</p>
     *
     * @param id the ID of the object whose ItemStack should be created.
     * @return a new ItemStack containing the (default) object or <tt>ItemStack.EMPTY</tt>, if no object can be found.
     * @throws UnsupportedOperationException if this registry doesn't support items.
     */
    @Nonnull
    default ItemStack getItem(ResourceLocation id) {
        throw new UnsupportedOperationException("Cannot create item! This registry does not register items.");
    }

    /**
     * <p>Indicates wether the first object is "equal to" the second object and neither are invalid.</p>
     *
     * @param obj1 The first object to be compared.
     * @param obj2 The second object to be compared.
     * @return <tt>true</tt>, if both types are valid and considered the same; <tt>false</tt> otherwise.
     * @throws NullPointerException if either the first or second object is <tt>null</tt>
     *
     * @see Object#equals(Object)
     */
    default boolean isEqual(@Nonnull T obj1, @Nonnull T obj2) {
        return obj1.isValid() && obj2.isValid() && obj1.getId().equals(obj2.getId());
    }

    /**
     * <p>Indicates wether the first ItemStack contains the same object as the second ItemStack.</p>
     *
     * @param stack1 The first ItemStack to be compared.
     * @param stack2 The second ItemStack to be compared.
     * @return <tt>true</tt>, if both objects from the ItemStacks are valid and considered the same; <tt>false</tt> otherwise.
     *
     * @see IRegistry#isEqual(IRegistryObject, IRegistryObject)
     */
    default boolean isEqual(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        return this.isEqual(this.getObject(stack1), this.getObject(stack2));
    }

    /**
     * <p>A helper method to register multiple objects at once.</p>
     *
     * @param registry The registry to be filled.
     * @param objects The object(s) to be registered.
     * @param <T> The type of the registry objects.
     * @throws NullPointerException if the registry or one of the objects is <tt>null</tt>
     */
    @SafeVarargs
    static <T extends IRegistryObject> void registerAll(IRegistry<T> registry, T... objects) {
        Arrays.stream(objects).forEach(registry::register);
    }
}
