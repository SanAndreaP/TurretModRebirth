/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.element.tcu.targets;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.init.config.Targets;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class CreatureIcon
        extends Texture
{
    protected int[] uvMobs;
    protected int[] uvAnimals;
    protected int[] uvMisc;

    @Nonnull
    private Supplier<EntityClassification> entityTypeSupplier = () -> null;

    public CreatureIcon(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, int[] uvMobs, int[] uvAnimals, float[] scale, ColorObj color) {
        super(txLocation, size, textureSize, uv, scale, color);

        this.uvMobs = uvMobs;
        this.uvAnimals = uvAnimals;
        this.uvMisc = uv;
    }

    public void setEntityTypeSupplier(@Nonnull Supplier<EntityClassification> entityTypeSupplier) {
        this.entityTypeSupplier = entityTypeSupplier;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        EntityClassification cls = Targets.getCondensedType(this.entityTypeSupplier.get());
        if( cls == EntityClassification.MONSTER ) {
            this.uv = this.uvMobs;
        } else if( cls == EntityClassification.CREATURE ) {
            this.uv = this.uvAnimals;
        } else {
            this.uv = this.uvMisc;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder
            extends Texture.Builder
    {
        protected int[] uvMobs;
        protected int[] uvAnimals;

        public Builder(int[] size) {
            super(size);
        }

        public Builder uvMobs(int[] uv)    { this.uvMobs = uv;    return this; }
        public Builder uvAnimals(int[] uv) { this.uvAnimals = uv; return this; }

        @Override
        public void sanitize(IGui gui) {
            super.sanitize(gui);

            if( this.uvMobs == null ) {
                this.uvMobs = new int[] { 172, 0 };
            }

            if( this.uvAnimals == null ) {
                this.uvAnimals = new int[] { 172, 8 };
            }
        }

        @Override
        public CreatureIcon get(IGui gui) {
            this.sanitize(gui);
            return new CreatureIcon(this.texture, this.size, this.textureSize, this.uv, this.uvMobs, this.uvAnimals, this.scale, this.color);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            JsonUtils.addJsonProperty(data, "uv", JsonUtils.getIntArray(data.get("uvMisc"), new int[] { 172, 16 }, Range.is(2)));
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 8, 8 });

            Texture.Builder    tb = Texture.Builder.buildFromJson(gui, data);
            Builder b  = IBuilder.copyValues(tb, new Builder(tb.size));

            JsonUtils.fetchIntArray(data.get("uvMobs"), b::uvMobs);
            JsonUtils.fetchIntArray(data.get("uvAnimals"), b::uvAnimals);

            return b;
        }

        public static CreatureIcon fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
