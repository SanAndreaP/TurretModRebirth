package de.sanandrew.mods.turretmod.client.gui.element.nav;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

public class PageNavigationTooltip
        extends Tooltip
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_page_nav_ttip");

    private int[] tabPos = null;

    public PageNavigationTooltip(int[] mouseOverSize, int backgroundColor, int borderTopColor, int borderBottomColor, int[] padding, String visibleForId, GuiElementInst content) {
        super(mouseOverSize, backgroundColor, borderTopColor, borderBottomColor, padding, visibleForId, content);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        this.setupVisuals();
    }

    @SuppressWarnings("java:S1854")
    private void setupVisuals() {
        PageNavigation pgn = this.visibleFor.get(PageNavigation.class);
        Label lbl = this.get(CONTENT).get(Label.class);
        this.tabPos = null;

        pgn.shownTabs.forEach((b, p) -> {
            if( b.get(ButtonNav.class).isHovering() ) {
                lbl.text = new TranslationTextComponent(Lang.TCU_TEXT.get(p));
                this.tabPos = b.pos;
            }
        });
    }

    @Override
    public void tick(IGui gui, GuiElementInst e) {
        this.setupVisuals();

        super.tick(gui, e);
    }

    @Override
    public void render(IGui gui, MatrixStack mStack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        if( this.tabPos != null ) {
            super.render(gui, mStack, partTicks, this.tabPos[0] + x, this.tabPos[1] + y, mouseX, mouseY, e);
        }
    }

    private static class Label
            extends Text
    {
        private ITextComponent text = StringTextComponent.EMPTY;

        public Label(boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors) {
            super(StringTextComponent.EMPTY, shadow, wrapWidth, lineHeight, fontRenderer, colors);
        }

        @Override
        public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
            return this.text;
        }

        public static class Builder
                extends Text.Builder
        {
            public Builder() {
                super(StringTextComponent.EMPTY);
            }

            @Override
            public Label get(IGui gui) {
                return new Label(this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors);
            }

            public static Builder buildFromJson(IGui gui, JsonObject data) {
                return IBuilder.copyValues(Text.Builder.buildFromJson(gui, JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF")), new Builder());
            }

            public static Label fromJson(IGui gui, JsonObject data) {
                return buildFromJson(gui, data).get(gui);
            }
        }
    }

    public static class Builder
            extends Tooltip.Builder
    {
        public Builder(int[] mouseOverSize) {
            super(mouseOverSize);
        }

        @Override
        public PageNavigationTooltip get(IGui gui) {
            this.sanitize(gui);

            return new PageNavigationTooltip(this.mouseOverSize, this.backgroundColor, this.borderTopColor, this.borderBottomColor, this.padding, this.visibleForId, this.content);
        }

        @Override
        protected GuiElementInst loadContent(IGui gui, JsonObject data) {
            return new GuiElementInst(Label.Builder.fromJson(gui, MiscUtils.get(data.getAsJsonObject("label"), JsonObject::new)));
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Tooltip.Builder tb = Tooltip.Builder.buildFromJson(gui, JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16 }), b -> (g, j) -> null);

            Builder b = IBuilder.copyValues(tb, new Builder(tb.mouseOverSize));
            b.content(b.loadContent(gui, data));

            return b;
        }

        public static PageNavigationTooltip fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
