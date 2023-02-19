package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.JsonArray;
import de.sanandrew.mods.turretmod.item.repairkits.RepairKitRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class PatchouliHelper
{
    public static void preInit() {
        if( ModList.get().isLoaded("patchouli") ) {
//            PatchouliMouseEventHandler.register();
//            PageCustomCrafting.registerPage();
        }
    }

    public static <T> JsonArray toJsonArray(T[] a, BiConsumer<JsonArray, T> addElem) {
        return toJsonArray(Arrays.asList(a), addElem);
    }

    public static <T> JsonArray toJsonArray(List<T> l, BiConsumer<JsonArray, T> addElem) {
        JsonArray a = new JsonArray();
        l.forEach(i -> addElem.accept(a, i));

        return a;
    }

    public static String getItemStr(ItemStack stack) {
        if( stack == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(stack.getItem().getRegistryName());
        if( stack.getCount() > 1 ) {
            sb.append("#").append(stack.getCount());
        }
        if( stack.hasTag() ) {
            sb.append(stack.getTag());
        }

        return sb.toString();
    }

    public static String getItemStr(ItemStack... stacks) {
        if( stacks == null ) {
            return "";
        }

        return getItemStr(Arrays.asList(stacks));
    }

    public static String getItemStr(Collection<ItemStack> stacks) {
        if( stacks == null || stacks.isEmpty() ) {
            return "";
        }

        return stacks.stream().map(PatchouliHelper::getItemStr)
                     .collect(StringBuilder::new, (sb, i) -> sb.append(i).append(","), StringBuilder::append)
                     .toString()
                     .replaceAll(",$", "");
    }

    public static ItemStack[] getRepairKitSpotlightItems() {
        return RepairKitRegistry.INSTANCE.getAll().stream().map(RepairKitRegistry.INSTANCE::getItem).toArray(ItemStack[]::new);
    }

//    public static vazkii.patchouli.common.util.ItemStackUtil.StackWrapper getWrapper(IRegistry<?> r, IRegistryObject o) {
//        return new vazkii.patchouli.common.util.ItemStackUtil.StackWrapper(r.getItem(o.getId()));
//    }
}
