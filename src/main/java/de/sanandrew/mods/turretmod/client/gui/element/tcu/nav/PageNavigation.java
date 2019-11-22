package de.sanandrew.mods.turretmod.client.gui.element.tcu.nav;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Label;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.registry.Lang;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PageNavigation
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_page_nav");

    public BakedData data;

    private int tabStartIdx = 0;

    private Map<GuiElementInst, ResourceLocation> shownTabs = Collections.emptyMap();

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();

            this.data.maxTabsShown = JsonUtils.getIntVal(data.get("tabsShown"), 7);

            int currIdx = 0;
            for( ResourceLocation page : GuiTcuRegistry.GUI_ENTRIES ) {
                GuiElementInst btn = new GuiElementInst();
                btn.element = new ButtonNav(currIdx++, page);

                this.data.pages.put(btn, page);
                btn.data = data.getAsJsonObject("buttonData");
                gui.getDefinition().initElement(btn);
                btn.element.bakeData(gui, btn.data);
            }

            this.data.tabScrollL = new GuiElementInst();
            this.data.tabScrollL.element = new ButtonTabScroll(0);
            this.data.tabScrollL.data = data.getAsJsonObject("tabScrollLeft");
            gui.getDefinition().initElement(this.data.tabScrollL);
            this.data.tabScrollL.get().bakeData(gui, this.data.tabScrollL.data);
            this.data.tabScrollR = new GuiElementInst();
            this.data.tabScrollR.element = new ButtonTabScroll(1);
            this.data.tabScrollR.data = data.getAsJsonObject("tabScrollRight");
            gui.getDefinition().initElement(this.data.tabScrollR);
            this.data.tabScrollR.get().bakeData(gui, this.data.tabScrollR.data);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        final IGuiTcuInst guiTcuInst = (IGuiTcuInst) gui;
        final ResourceLocation currEntry = guiTcuInst.getCurrentEntryKey();

        int shownId = 0;
        for( Map.Entry<GuiElementInst, ResourceLocation> entry : this.data.pages.entrySet() ) {
            GuiElementInst btn = entry.getKey();
            ButtonNav btnNav = btn.get(ButtonNav.class);
            if( GuiTcuRegistry.INSTANCE.getGuiEntry(btnNav.page).showTab(guiTcuInst)
                && btnNav.pageIdx >= this.tabStartIdx && btnNav.pageIdx <= this.tabStartIdx + this.data.maxTabsShown )
            {
                btnNav.setVisible(true);
                btn.pos[0] = this.data.tabScrollL.get().getWidth() + shownId++ * 18;
                btnNav.setEnabled(!currEntry.equals(btnNav.page));
            } else {
                btnNav.setVisible(false);
            }
        }
        this.data.tabScrollR.pos[0] = this.data.tabScrollL.get().getWidth() + shownId * 18;

        this.shownTabs = this.fetchShownPageButtons();
    }

    private Map<GuiElementInst, ResourceLocation> fetchShownPageButtons() {
        return this.data.pages.entrySet().stream()
                              .filter(e -> e.getKey().get(ButtonNav.class).isVisible())
                              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, () -> new TreeMap<>(new ComparatorTabButton())));
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        this.data.tabScrollL.get().render(gui, partTicks, x, y, mouseX, mouseY, data);
        this.data.tabScrollR.get().render(gui, partTicks, x + this.data.tabScrollR.pos[0], y, mouseX, mouseY, data);
        this.shownTabs.forEach((btn, page) -> btn.get().render(gui, partTicks, x + btn.pos[0], y, mouseX, mouseY, btn.data));
    }

    @Override
    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        for( Map.Entry<GuiElementInst, ResourceLocation> e : this.shownTabs.entrySet() ) {
            if( e.getKey().get().mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
                return true;
            }
        }

        if( this.data.tabScrollL.get(ButtonTabScroll.class).mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
            return true;
        }

        return this.data.tabScrollR.get(ButtonTabScroll.class).mouseClicked(gui, mouseX, mouseY, mouseButton);
    }

    @Override
    public int getWidth() {
        return 18 * Math.min(this.data.maxTabsShown, this.data.pages.size()) - 2;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @SuppressWarnings("WeakerAccess")
    public static class BakedData
    {
        public Map<GuiElementInst, ResourceLocation> pages = new TreeMap<>(new ComparatorTabButton());
        public GuiElementInst                        tabScrollL;
        public GuiElementInst                        tabScrollR;
        public int                                   maxTabsShown;
    }

    private static final class ComparatorTabButton
            implements Comparator<GuiElementInst>
    {
        @Override
        public int compare(GuiElementInst o1, GuiElementInst o2) {
            return Integer.compare(o1.get(ButtonNav.class).pageIdx, o2.get(ButtonNav.class).pageIdx);
        }
    }

    private final class ButtonTabScroll
            extends Button
    {
        private final int direction;

        private ButtonTabScroll(int direction) {
            this.direction = direction;
        }

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            JsonUtils.addDefaultJsonProperty(data, "buttonFunction", -1);

            super.bakeData(gui, data);
        }

        @Override
        public void performAction(IGui gui, int id) {
            if( PageNavigation.this.tabStartIdx > 0 && this.direction == 0 ) {
                PageNavigation.this.tabStartIdx--;
            } else if( PageNavigation.this.tabStartIdx < PageNavigation.this.data.pages.size() - PageNavigation.this.data.maxTabsShown && this.direction == 1 ) {
                PageNavigation.this.tabStartIdx++;
            }
        }
    }

    public static class PageNavigationLabel
            extends Label
    {
        public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_page_nav_label");

        private GuiElementInst pageNavigation;

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] {16, 16});

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
                this.data.content.get(LabelText.class).text = LangUtils.translate(Lang.TCU_PAGE_TITLE.get(p));
                super.render(gui, partTicks, e.pos[0], e.pos[1], mouseX, mouseY, e.data);
            });
        }

        private static class LabelText
                extends Text
        {
            private String text = "";

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
}
