package de.sanandrew.mods.turretmod.client.gui.element.tcu.nav;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
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
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.page_nav");

    private final Map<GuiElementInst, String> pages = new TreeMap<>(new ComparatorTabButton());

    private GuiElementInst tabScrollL;
    private GuiElementInst tabScrollR;
    private int            maxTabsShown;
    private int            tabStartIdx = 0;

    Map<GuiElementInst, String> shownTabs = Collections.emptyMap();

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.maxTabsShown = JsonUtils.getIntVal(data.get("tabsShown"), 7);

        int currIdx = 0;
        for( String page : GuiTcuRegistry.GUI_ENTRIES ) {
            GuiElementInst btn = new GuiElementInst(new ButtonNav(currIdx++, page), data.getAsJsonObject("buttonData")).initialize(gui);
            btn.get().bakeData(gui, btn.data, btn);

            this.pages.put(btn, page);
        }

        this.tabScrollL = new GuiElementInst(new ButtonTabScroll(0), data.getAsJsonObject("tabScrollLeft")).initialize(gui);
        this.tabScrollL.get().bakeData(gui, this.tabScrollL.data, this.tabScrollL);
        this.tabScrollL.setVisible(false);

        this.tabScrollR = new GuiElementInst(new ButtonTabScroll(1), data.getAsJsonObject("tabScrollRight")).initialize(gui);
        this.tabScrollR.get().bakeData(gui, this.tabScrollR.data, this.tabScrollR);
        this.tabScrollR.setVisible(false);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        final IGuiTcuInst<?> guiTcuInst = (IGuiTcuInst<?>) gui;
        final String         currEntry  = guiTcuInst.getCurrentEntryKey();

        final int tabScrollElemLWidth = this.tabScrollL.get().getWidth();
        final int tabScrollElemRWidth = this.tabScrollR.get().getWidth();

        int cntAvailableTabs = 0;
        for( Map.Entry<GuiElementInst, String> entry : this.pages.entrySet() ) {
            GuiElementInst btn    = entry.getKey();
            ButtonNav      btnNav = btn.get(ButtonNav.class);
            if( GuiTcuRegistry.INSTANCE.getGuiEntry(btnNav.page).showTab(guiTcuInst) ) {
                cntAvailableTabs++;

                if( btnNav.pageIdx >= this.tabStartIdx && btnNav.pageIdx <= this.tabStartIdx + this.maxTabsShown ) {
                    btn.setVisible(true);
                    btnNav.setEnabled(!currEntry.equals(btnNav.page));

                    continue;
                }
            }

            btn.setVisible(false);
        }
        this.shownTabs = this.fetchShownPageButtons();

        int tabWidth = this.shownTabs.keySet().stream().map(elem -> elem.get().getWidth()).reduce((e1, e2) -> e1 + e2 + 2).orElse(2);
        int tabLeft  = (guiTcuInst.getWidth() - tabWidth - tabScrollElemLWidth - tabScrollElemRWidth - 4) / 2;

        this.tabScrollL.pos[0] = tabLeft;
        this.tabScrollL.setVisible(this.tabStartIdx > 0);
        this.tabScrollR.pos[0] = tabLeft + tabScrollElemLWidth + tabWidth + 4;
        this.tabScrollR.setVisible(this.tabStartIdx < cntAvailableTabs - shownTabs.size());

        int shownId = 0;
        for( Map.Entry<GuiElementInst, String> entry : this.shownTabs.entrySet() ) {
            entry.getKey().pos[0] = tabLeft + tabScrollElemLWidth + 2 + shownId++ * 18;
        }
    }

    private Map<GuiElementInst, String> fetchShownPageButtons() {
        return this.pages.entrySet().stream()
                         .filter(e -> e.getKey().isVisible())
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, () -> new TreeMap<>(new ComparatorTabButton())));
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GuiDefinition.renderElement(gui, x + this.tabScrollL.pos[0], y, mouseX, mouseY, partTicks, this.tabScrollL);
        GuiDefinition.renderElement(gui, x + this.tabScrollR.pos[0], y, mouseX, mouseY, partTicks, this.tabScrollR);
        this.shownTabs.forEach((btn, page) -> GuiDefinition.renderElement(gui, x + btn.pos[0], y, mouseX, mouseY, partTicks, btn));
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
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16 });
            JsonUtils.addDefaultJsonProperty(data, "uvSize", new int[] { 16, 16 });
            JsonUtils.addDefaultJsonProperty(data, "texture", Resources.GUI_TCU_BUTTONS.toString());
            JsonUtils.addDefaultJsonProperty(data, "buttonFunction", -1);

            super.bakeData(gui, data, inst);
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
