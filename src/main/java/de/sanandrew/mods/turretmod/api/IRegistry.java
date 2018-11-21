package de.sanandrew.mods.turretmod.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings({"unused", "unchecked"})
public interface IRegistry<T extends IRegistryType>
{
    void register(T type);

    default void registerAll(T... types) {
        Arrays.stream(types).forEach(this::register);
    }

    @Nonnull
    Collection<T> getTypes();

    @Nonnull
    T getType(ResourceLocation id);

    @Nonnull
    T getType(ItemStack item);

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
}
