package de.sanandrew.mods.turretmod.client.gui.element.tcu.nav;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Label;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.registry.Lang;
import net.minecraft.util.ResourceLocation;

public class PageNavigationLabel
        extends Label
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_page_nav_label");

    private GuiElementInst pageNavigation;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16});

        super.bakeData(gui, data);

        this.pageNavigation = gui.getDefinition().getElementById(JsonUtils.getStringVal(data.get("for")));
    }

    @Override
    public GuiElementInst getLabel(IGui gui, JsonObject data) {
        GuiElementInst lbl = new GuiElementInst();
        lbl.element = new LabelText();
        gui.getDefinition().initElement(lbl);
        lbl.get().bakeData(gui, data);

        return lbl;
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        PageNavigation pgn = this.pageNavigation.get(PageNavigation.class);
        pgn.shownTabs.forEach((e, p) -> {
            if( e.get(ButtonNav.class).isHovering() ) {
                this.data.content.get(LabelText.class).text = LangUtils.translate(Lang.TCU_PAGE_TITLE.get(p));
                super.render(gui, partTicks, e.pos[0] + x, e.pos[1] + y, mouseX, mouseY, data);
            }
        });
    }

    private static class LabelText
            extends Text
    {
        private String text = "";

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF");

            super.bakeData(gui, data);
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
