package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretVariant;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class VariantHolder
{
    private ITurretVariant defaultVariant;
    final Map<ResourceLocation, ITurretVariant> variants = new HashMap<>();

    public void register(ITurretVariant variant) {
        this.variants.put(variant.getId(), variant);
        if( this.defaultVariant == null ) {
            this.defaultVariant = variant;
        }
    }

    public ITurretVariant get(ResourceLocation id) {
        return this.variants.get(id);
    }

    public ITurretVariant getOrDefault(ResourceLocation id) {
        return MiscUtils.defIfNull(this.variants.get(id), this.defaultVariant);
    }

    public boolean isDefaultVariant(ITurretVariant variant) {
        return this.defaultVariant == variant;
    }

    public static class Variant
            implements ITurretVariant
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
