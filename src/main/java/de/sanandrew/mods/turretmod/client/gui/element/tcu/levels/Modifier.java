package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.DynamicText;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.Stage;
import net.minecraft.entity.ai.attributes.Attribute;
import org.apache.commons.lang3.Range;

public class Modifier
        extends ElementParent<String>
{
    private static final String BACKGROUND = "background";
    private static final String LABEL = "label";
    private static final String VALUE = "value";

//    private final Attribute attrib;

    public static class Builder
            implements IBuilder<Modifier>
    {
        @Override
        public void sanitize(IGui iGui) { }

        protected GuiElementInst loadBackground(IGui gui, JsonObject bgData) {
            return new GuiElementInst(JsonUtils.getIntArray(bgData.get("offset"), new int[2], Range.is(2)), Texture.Builder.fromJson(gui, bgData));
        }

        protected GuiElementInst loadLabel(IGui gui, JsonObject lblData) {
            return new GuiElementInst(JsonUtils.getIntArray(lblData.get("offset"), new int[2], Range.is(2)), DynamicText.Builder.fromJson(gui, lblData));
        }

        @Override
        public Modifier get(IGui iGui) {
            return null;
        }
    }
}
