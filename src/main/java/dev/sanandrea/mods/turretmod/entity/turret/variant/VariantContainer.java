/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.entity.turret.variant;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.turretmod.api.turret.IVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class VariantContainer
{

    private IVariant              defaultVariant;
    private final List<IVariant> variantList = new ArrayList<>();
    final   Map<Object, IVariant> variants = new HashMap<>();

    public void register(IVariant variant) {
        this.variantList.add(variant);
        this.variants.put(variant.getId(), variant);
        if( this.defaultVariant == null ) {
            this.defaultVariant = variant;
        }
    }

    public IVariant get(Object id) {
        return MiscUtils.get(this.variants.get(id), this.defaultVariant);
    }

    public boolean isDefault(IVariant variant) {
        return variant == null || this.defaultVariant.getId().equals(variant.getId());
    }

    public IVariant getDefault() {
        return this.defaultVariant;
    }

    public IVariant[] getAll() {
        return this.variantList.toArray(new IVariant[0]);
    }

    public abstract IVariant get(IInventory inv);

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
}
