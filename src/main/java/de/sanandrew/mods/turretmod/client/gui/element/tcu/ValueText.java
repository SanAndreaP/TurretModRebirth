package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.levels.BorderedText;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

public class ValueText
        extends BorderedText
{
    private Supplier<String> valSupplier = () -> "";
    private Supplier<String> maxSupplier = () -> "";

    private String currVal = "";
    private String currMax = "";

    public ValueText(@Nonnull ITextComponent text, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors) {
        super(text, wrapWidth, lineHeight, fontRenderer, colors);
    }

    public void setValueSuppliers(@Nonnull Supplier<String> valueSupplier, @Nonnull Supplier<String> maxSupplier) {
        this.valSupplier = valueSupplier;
        this.maxSupplier = maxSupplier;
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.currVal = this.valSupplier.get();
        this.currMax = this.maxSupplier.get();

        super.tick(gui, inst);
    }

    @Override
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        if( originalText instanceof TranslationTextComponent ) {
            return new TranslationTextComponent(((TranslationTextComponent) originalText).getKey(), this.currVal, this.currMax);
        }

        return new StringTextComponent(String.format(originalText.getString(), this.currVal, this.currMax));
    }

    public static class Builder
            extends BorderedText.Builder
    {
        public Builder(ITextComponent text) {
            super(text);
        }

        @Override
        public ValueText get(IGui gui) {
            this.sanitize(gui);

            return new ValueText(this.text, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            BorderedText.Builder b = BorderedText.Builder.buildFromJson(gui, data);
            return IBuilder.copyValues(b, new Builder(b.text));
        }

        public static ValueText fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
