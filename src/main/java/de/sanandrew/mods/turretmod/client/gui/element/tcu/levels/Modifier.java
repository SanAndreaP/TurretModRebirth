/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.client.gui.element.LoadElementFunction;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling.Stage;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Range;

import java.text.DecimalFormat;

public class Modifier
        extends ElementParent<String>
{
    public static final DecimalFormat MOD_FORMAT = new DecimalFormat("+#0.0;-#0.0");

    private static final String BACKGROUND = "background";
    private static final String LABEL = "label";
    private static final String VALUE = "value";

    public Modifier(Attribute attrib, Stage.ModifierInfo info, GuiElementInst background, GuiElementInst label, GuiElementInst value) {
        this.put(BACKGROUND, background);
        this.put(LABEL, label);
        this.put(VALUE, value);

        label.get(Text.class).setTextFunc((g, t) -> new TranslationTextComponent(attrib.getDescriptionId()));
        value.get(Text.class).setTextFunc((g, t) -> new TranslationTextComponent(Lang.TCU_TEXT.get("leveling.modValue"),
                                                                                 MOD_FORMAT.format(info.getModPercentage())));
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder
            implements IBuilder<Modifier>
    {
        private final Attribute attrib;
        private final Stage.ModifierInfo info;

        protected GuiElementInst background;
        protected GuiElementInst label;
        protected GuiElementInst value;

        public Builder(Attribute attrib, Stage.ModifierInfo info) {
            this.attrib = attrib;
            this.info = info;
        }

        public Builder background(GuiElementInst background) { this.background = background; return this; }
        public Builder label(GuiElementInst label) { this.label = label; return this; }
        public Builder value(GuiElementInst value) { this.value = value; return this; }

        @Override
        public void sanitize(IGui iGui) {
            if( this.background == null ) {
                this.background = GuiElementInst.EMPTY;
            }
            if( this.label == null ) {
                this.label = GuiElementInst.EMPTY;
            }
            if( this.value == null ) {
                this.value = GuiElementInst.EMPTY;
            }
        }

        @Override
        public Modifier get(IGui gui) {
            this.sanitize(gui);
            return new Modifier(this.attrib, this.info, this.background.initialize(gui), this.label.initialize(gui), this.value.initialize(gui));
        }

        protected GuiElementInst loadBackground(IGui gui, JsonElement bgData) {
            JsonObject bgDataObj = bgData.getAsJsonObject();
            return new GuiElementInst(JsonUtils.getIntArray(bgDataObj.get(ITcuScreen.OFFSET_JSON_ELEM), new int[2], Range.is(2)), Texture.Builder.fromJson(gui, bgDataObj));
        }

        protected GuiElementInst loadLabel(IGui gui, JsonElement lblData) {
            JsonObject lblDataObj = lblData.getAsJsonObject();
            GuiElementInst inst = new GuiElementInst(JsonUtils.getIntArray(lblDataObj.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] {3, 8}, Range.is(2)), Text.Builder.fromJson(gui, lblDataObj));
            inst.alignment = JsonUtils.getStringArray(lblDataObj.get("alignment"), new String[] {"left", "center"}, Range.between(1, 2));
            return inst;
        }

        protected GuiElementInst loadValue(IGui gui, JsonElement valData) {
            JsonObject valDataObj = valData.getAsJsonObject();
            GuiElementInst inst = new GuiElementInst(JsonUtils.getIntArray(valDataObj.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] {153, 8}, Range.is(2)), Text.Builder.fromJson(gui, valDataObj));
            inst.alignment = JsonUtils.getStringArray(valDataObj.get("alignment"), new String[] {"right", "center"}, Range.between(1, 2));
            return inst;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Attribute attrib, Stage.ModifierInfo info,
                                            LoadElementFunction<Modifier, Builder> loadBackground,
                                            LoadElementFunction<Modifier, Builder> loadLabel,
                                            LoadElementFunction<Modifier, Builder> loadValue)
        {
            Builder b = new Builder(attrib, info);

            MiscUtils.accept(loadBackground, f -> MiscUtils.accept(f.apply(b, gui, data.get(BACKGROUND)), b::background));
            MiscUtils.accept(loadLabel, f -> MiscUtils.accept(f.apply(b, gui, data.get(LABEL)), b::label));
            MiscUtils.accept(loadValue, f -> MiscUtils.accept(f.apply(b, gui, data.get(VALUE)), b::value));

            return b;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Attribute attrib, Stage.ModifierInfo info) {
            return buildFromJson(gui, data, attrib, info, b -> b::loadBackground, b -> b::loadLabel, b -> b::loadValue);
        }

        public static Modifier fromJson(IGui gui, JsonObject data, Attribute attrib, Stage.ModifierInfo info) {
            return buildFromJson(gui, data, attrib, info).get(gui);
        }
    }
}
