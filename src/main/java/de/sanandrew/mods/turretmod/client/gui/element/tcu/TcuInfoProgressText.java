package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

public class TcuInfoProgressText
        extends Text
{
    private Supplier<String> getVal = () -> "";
    private Supplier<String>   getMax = () -> "";

    private String currVal = "";
    private String currMax = "";

    public TcuInfoProgressText(@Nonnull ITextComponent text, boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors) {
        super(text, shadow, wrapWidth, lineHeight, fontRenderer, colors);
    }

    public void setValueFunc(@Nonnull Supplier<String> getValue, @Nonnull Supplier<String> getMax) {
        this.getVal = getValue;
        this.getMax = getMax;
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.currVal = this.getVal.get();
        this.currMax = this.getMax.get();

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
            extends Text.Builder
    {
        public Builder(ITextComponent text) {
            super(text);
        }

        @Override
        public TcuInfoProgressText get(IGui gui) {
            this.sanitize(gui);

            return new TcuInfoProgressText(this.text, this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Text.Builder b = Text.Builder.buildFromJson(gui, data);
            return IBuilder.copyValues(b, new Builder(b.text));
        }

        public static TcuInfoProgressText fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
