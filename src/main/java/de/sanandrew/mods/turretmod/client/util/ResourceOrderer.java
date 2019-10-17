package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ResourceOrderer
        implements ISelectiveResourceReloadListener
{
    public static final ResourceOrderer INSTANCE = new ResourceOrderer();

    private static final List<ResourceLocation> ORDERED_LIST = new ArrayList<>();

    private ResourceOrderer() { }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if( resourcePredicate.test(VanillaResourceType.TEXTURES) ) {
            try( IResource r = resourceManager.getResource(Resources.ITEM_ORDER_LIST.resource); InputStream is = r.getInputStream() ) {
                ORDERED_LIST.clear();
                IOUtils.readLines(is, StandardCharsets.UTF_8).forEach(s -> { if( !s.startsWith("#") ) ORDERED_LIST.add(new ResourceLocation(s)); });
            } catch( IOException ignored ) { }
        }
    }

    public static void orderItems(List<ItemStack> input) {
        input.sort(Comparator.comparingInt((ItemStack i) -> {
            int ind = ORDERED_LIST.indexOf(i.getItem().getRegistryName());
            return ind == -1 ? Integer.MAX_VALUE : ind;
        }).thenComparing(ItemStack::getDisplayName));
    }

    public static <T> Comparator<T> getOrderComparator(Function<T, ResourceLocation> reslocGetter, Function<T, String> nameGetter) {
        return Comparator.comparingInt((T i) -> {
            int ind = ORDERED_LIST.indexOf(reslocGetter.apply(i));
            return ind == -1 ? Integer.MAX_VALUE : ind;
        }).thenComparing(nameGetter);
    }
}
