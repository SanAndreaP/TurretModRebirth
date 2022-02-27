package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.DynamicText;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Map;

//todo: put in SanLib
public class BorderedText
        extends DynamicText
{
    public static final ResourceLocation ID = new ResourceLocation("bordered_text");

    private static final String BORDER_COLOR = "borderColor";

    public BorderedText(@Nonnull ITextComponent text, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors) {
        super(text, false, wrapWidth, lineHeight, fontRenderer, colors);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        this.setColor(BORDER_COLOR);
        super.render(gui, stack, partTicks, x-1, y, mouseX, mouseY, inst);
        super.render(gui, stack, partTicks, x+1, y, mouseX, mouseY, inst);
        super.render(gui, stack, partTicks, x, y-1, mouseX, mouseY, inst);
        super.render(gui, stack, partTicks, x, y+1, mouseX, mouseY, inst);
        this.setColor(null);
        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst);
    }

    public static class Builder
            extends DynamicText.Builder
    {
        public Builder(ITextComponent text) {
            super(text);
        }

        @Override
        public void sanitize(IGui gui) {
            this.colors.computeIfAbsent("default", k -> this.colors.values().stream().findFirst().orElse(0xFF80FF20));
            this.colors.computeIfAbsent(BORDER_COLOR, k -> this.colors.values().stream().skip(1).findFirst().orElse(0xFF000000));

            super.sanitize(gui);
        }

        @Override
        public BorderedText get(IGui gui) {
            this.sanitize(gui);
            return new BorderedText(this.text, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            DynamicText.Builder db = DynamicText.Builder.buildFromJson(gui, data);
            return IBuilder.copyValues(db, new Builder(db.text));
        }

        public static BorderedText fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
