package de.sanandrew.mods.turretmod.client.gui.element.nav;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.ResourceLocation;

public class PageNavigationTooltip
        extends Tooltip
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_page_nav_ttip");

    private GuiElementInst pageNavigation;

    private int[] tabPos = null;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16});

        super.bakeData(gui, data, inst);

        this.pageNavigation = gui.getDefinition().getElementById(JsonUtils.getStringVal(data.get("for")));
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        PageNavigation pgn = this.pageNavigation.get(PageNavigation.class);
        Label lbl = this.getChild(CONTENT).get(Label.class);
        this.tabPos = null;

        pgn.shownTabs.forEach((e, p) -> {
            if( e.get(ButtonNav.class).isHovering() ) {
                lbl.text = LangUtils.translate(Lang.TCU_PAGE_TITLE.get(p));
                this.tabPos = e.pos;
            }
        });

        super.update(gui, data);
    }

    @Override
    public GuiElementInst getContent(IGui gui, JsonObject data) {
        return new GuiElementInst(new Label()).initialize(gui);
    }

    @Override
    public void render(IGui gui, MatrixStack mStack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        if( this.tabPos != null ) {
            super.render(gui, mStack, partTicks, this.tabPos[0] + x, this.tabPos[1] + y, mouseX, mouseY, data);
        }
    }

    private static class Label
            extends Text
    {
        private String text = "";

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF");

            super.bakeData(gui, data, inst);
        }

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            return this.text;
        }
    }
}
