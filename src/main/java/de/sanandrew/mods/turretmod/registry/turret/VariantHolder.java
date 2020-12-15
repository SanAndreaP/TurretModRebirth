package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class VariantHolder
{
    private IVariant                        defaultVariant;
    final   Map<ResourceLocation, IVariant> variants = new HashMap<>();

    public void register(IVariant variant) {
        this.variants.put(variant.getId(), variant);
        if( this.defaultVariant == null ) {
            this.defaultVariant = variant;
        }
    }

    public IVariant get(ResourceLocation id) {
        return this.variants.get(id);
    }

    public IVariant getOrDefault(ResourceLocation id) {
        return MiscUtils.defIfNull(this.variants.get(id), this.defaultVariant);
    }

    public boolean isDefaultVariant(IVariant variant) {
        return variant == null || this.defaultVariant.getId().equals(variant.getId());
    }

    public static class Variant
            implements IVariant
    {
        private final ResourceLocation id;
        private final ResourceLocation texture;

        Variant(ResourceLocation id, ResourceLocation texture) {
            this.id = id;
            this.texture = texture;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public ResourceLocation getTexture() {
            return this.texture;
        }
    }
}
