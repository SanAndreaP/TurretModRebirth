package de.sanandrew.mods.turretmod.client.gui.element.tcu.nav;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.registry.Resources;
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

    private Map<GuiElementInst, String> pages = new TreeMap<>(new ComparatorTabButton());
    private GuiElementInst                        tabScrollL;
    private GuiElementInst                        tabScrollR;
    private int                                   maxTabsShown;

    private boolean initialized = false;
    private int     tabStartIdx = 0;
    private boolean visible = true;

    Map<GuiElementInst, String> shownTabs = Collections.emptyMap();

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !this.initialized ) {
            this.initialized = true;

            this.maxTabsShown = JsonUtils.getIntVal(data.get("tabsShown"), 7);

            int currIdx = 0;
            for( String page : GuiTcuRegistry.GUI_ENTRIES ) {
                GuiElementInst btn = new GuiElementInst();
                btn.element = new ButtonNav(currIdx++, page);

                this.pages.put(btn, page);
                btn.data = data.getAsJsonObject("buttonData");
                gui.getDefinition().initElement(btn);
                btn.element.bakeData(gui, btn.data);
            }

            this.tabScrollL = new GuiElementInst();
            this.tabScrollL.element = new ButtonTabScroll(0);
            this.tabScrollL.data = data.getAsJsonObject("tabScrollLeft");
            gui.getDefinition().initElement(this.tabScrollL);
            this.tabScrollL.get().bakeData(gui, this.tabScrollL.data);
            this.tabScrollL.get(ButtonTabScroll.class).setVisible(false);

            this.tabScrollR = new GuiElementInst();
            this.tabScrollR.element = new ButtonTabScroll(1);
            this.tabScrollR.data = data.getAsJsonObject("tabScrollRight");
            gui.getDefinition().initElement(this.tabScrollR);
            this.tabScrollR.get().bakeData(gui, this.tabScrollR.data);
            this.tabScrollR.get(ButtonTabScroll.class).setVisible(false);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        final IGuiTcuInst<?> guiTcuInst = (IGuiTcuInst<?>) gui;
        final String currEntry = guiTcuInst.getCurrentEntryKey();

        final int tabScrollElemLWidth = this.tabScrollL.get().getWidth();
        final IGuiElement tabScrollElemR = this.tabScrollR.get();

        int cntAvailableTabs = 0;
        for( Map.Entry<GuiElementInst, String> entry : this.pages.entrySet() ) {
            GuiElementInst btn = entry.getKey();
            ButtonNav btnNav = btn.get(ButtonNav.class);
            if( GuiTcuRegistry.INSTANCE.getGuiEntry(btnNav.page).showTab(guiTcuInst) ) {
                cntAvailableTabs++;

                if( btnNav.pageIdx >= this.tabStartIdx && btnNav.pageIdx <= this.tabStartIdx + this.maxTabsShown ) {
                    btnNav.setVisible(true);
                    btnNav.setEnabled(!currEntry.equals(btnNav.page));

                    continue;
                }
            }

            btnNav.setVisible(false);
        }
        this.shownTabs = this.fetchShownPageButtons();

        int tabWidth = this.shownTabs.keySet().stream().map(elem -> elem.get().getWidth()).reduce((e1, e2) -> e1 + e2 + 2).orElse(0);
        int tabLeft = (guiTcuInst.getWidth() - tabWidth - tabScrollElemLWidth - tabScrollElemR.getWidth() - 4) / 2;

        this.tabScrollL.pos[0] = tabLeft;
        this.tabScrollL.get().setVisible(this.tabStartIdx > 0);
        this.tabScrollR.pos[0] = tabLeft + tabScrollElemLWidth + tabWidth + 4;
        this.tabScrollR.get().setVisible(this.tabStartIdx < cntAvailableTabs - shownTabs.size());

        int shownId = 0;
        for( Map.Entry<GuiElementInst, String> entry : this.shownTabs.entrySet() ) {
            entry.getKey().pos[0] = tabLeft + tabScrollElemLWidth + 2 + shownId++ * 16;
        }
    }

    private Map<GuiElementInst, String> fetchShownPageButtons() {
        return this.pages.entrySet().stream()
                         .filter(e -> e.getKey().get(ButtonNav.class).isVisible())
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, () -> new TreeMap<>(new ComparatorTabButton())));
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        this.tabScrollL.get().render(gui, partTicks, x + this.tabScrollL.pos[0], y, mouseX, mouseY, data);
        this.tabScrollR.get().render(gui, partTicks, x + this.tabScrollR.pos[0], y, mouseX, mouseY, data);
        this.shownTabs.forEach((btn, page) -> btn.get().render(gui, partTicks, x + btn.pos[0], y, mouseX, mouseY, btn.data));
    }

    @Override
    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        for( Map.Entry<GuiElementInst, String> e : this.shownTabs.entrySet() ) {
            if( e.getKey().get().mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
                return true;
            }
        }

        if( this.tabScrollL.get(ButtonTabScroll.class).mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
            return true;
        }

        return this.tabScrollR.get(ButtonTabScroll.class).mouseClicked(gui, mouseX, mouseY, mouseButton);
    }

    @Override
    public int getWidth() {
        return 18 * Math.min(this.maxTabsShown, this.pages.size()) - 2;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
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
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] {16, 16});
            JsonUtils.addDefaultJsonProperty(data, "uvSize", new int[] {16, 16});
            JsonUtils.addDefaultJsonProperty(data, "texture", Resources.GUI_TCU_BUTTONS.resource.toString());
            JsonUtils.addDefaultJsonProperty(data, "buttonFunction", -1);

            super.bakeData(gui, data);
        }

        @Override
        public void performAction(IGui gui, int id) {
            if( PageNavigation.this.tabStartIdx > 0 && this.direction == 0 ) {
                PageNavigation.this.tabStartIdx--;
            } else if( PageNavigation.this.tabStartIdx < PageNavigation.this.pages.size() - PageNavigation.this.maxTabsShown && this.direction == 1 ) {
                PageNavigation.this.tabStartIdx++;
            }
        }
    }
}
