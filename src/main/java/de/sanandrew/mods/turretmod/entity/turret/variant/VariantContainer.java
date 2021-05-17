package de.sanandrew.mods.turretmod.entity.turret.variant;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class VariantContainer
{
    public static final Map<ResourceLocation, Function<String, VariantContainer>> VARIANT_CONTAINERS = new HashMap<>();

    private IVariant              defaultVariant;
    final   Map<Object, IVariant> variants = new HashMap<>();

    public static VariantContainer buildInstance(ResourceLocation type, String texturesLocation) {
        if( VARIANT_CONTAINERS.containsKey(type) ) {
            return VARIANT_CONTAINERS.get(type).apply(texturesLocation);
        }

        return null;
    }

    public void register(IVariant variant) {
        this.variants.put(variant.getId(), variant);
        if( this.defaultVariant == null ) {
            this.defaultVariant = variant;
        }
    }

    public IVariant get(Object id) {
        return MiscUtils.defIfNull(this.variants.get(id), this.defaultVariant);
    }

    public boolean isDefault(IVariant variant) {
        return variant == null || this.defaultVariant.getId().equals(variant.getId());
    }

    public IVariant getDefault() {
        return this.defaultVariant;
    }

    public abstract IVariant get(IInventory inv);

    public abstract void register(JsonObject jobj) throws JsonParseException, IllegalArgumentException;

    public abstract IVariant get(String s);

    public static class Variant
            implements IVariant
    {
        protected final Object id;
        protected final ResourceLocation texture;

        Variant(Object id, ResourceLocation texture) {
            this.id = id;
            this.texture = texture;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getId() {
            return (T) this.id;
        }

        @Override
        public ResourceLocation getTexture() {
            return this.texture;
        }

        @Override
        public String getTranslatedName() {
            return LangUtils.translate(String.format("turret_variant.%s", this.id.toString()));
        }
    }

//    public static abstract class ItemVariants<T>
//            extends VariantContainer
//    {
//        public final T variantMap = buildVariantMap();
//
//        public abstract T buildVariantMap();
//
//        protected int getIdFromStack(ItemStack stack) {
//            return Objects.hashCode(stack.getItem());
//        }
//
//        public int checkType(int currType, int newType) {
//            if( currType >= 0L && newType >= 0L && currType != newType ) {
//                return -1L;
//            }
//
//            return newType >= 0L ? newType : currType;
//        }
//    }
}
