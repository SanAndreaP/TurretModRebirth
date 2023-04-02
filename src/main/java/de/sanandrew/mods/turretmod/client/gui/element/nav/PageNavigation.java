/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.element.nav;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.TcuTabEvent;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.Range;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class PageNavigation
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_page_nav");

    protected static int            tabStartIdx = 0;

    protected final Map<GuiElementInst, ResourceLocation> pages = new TreeMap<>(new ComparatorTabButton());

    protected GuiElementInst tabScrollL;
    protected GuiElementInst tabScrollR;
    protected int            maxTabsShown;

    private int currAvailableTabs = 0;
    private int currWidth = 0;
    private int currHeight = 0;

    Map<GuiElementInst, ResourceLocation> shownTabs = Collections.emptyMap();

    public PageNavigation(int maxTabsShown, GuiElementInst tabScrollL, GuiElementInst tabScrollR, Map<GuiElementInst, ResourceLocation> pages) {
        this.maxTabsShown = maxTabsShown;
        this.tabScrollL = tabScrollL;
        this.tabScrollR = tabScrollR;

        this.pages.putAll(pages);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        if( ((TcuScreen) gui).getMenu().initial ) {
            resetIndex();
        }

        this.tabScrollL.get().setup(gui, this.tabScrollL);
        this.tabScrollR.get().setup(gui, this.tabScrollR);

        this.pages.keySet().forEach(t -> t.get().setup(gui, t));

        this.tabScrollL.get(ButtonSL.class).setActive(false);
        this.tabScrollR.get(ButtonSL.class).setActive(false);

        this.tabScrollL.get(ButtonSL.class).setFunction(btn -> decrIndex());
        this.tabScrollR.get(ButtonSL.class).setFunction(btn -> incrIndex(this.currAvailableTabs - this.maxTabsShown));

        this.setupVisuals(gui);
    }

    private void setupVisuals(IGui gui) {
        final TcuScreen     tcu      = (TcuScreen) gui;
        final ResourceLocation currPage = tcu.getCurrPage();

        final int tabScrollElemLWidth = this.tabScrollL.get().getWidth();
        final int tabScrollElemRWidth = this.tabScrollR.get().getWidth();

        int prevAvailableTabs = this.currAvailableTabs;
        this.currAvailableTabs = 0;
        for( Map.Entry<GuiElementInst, ResourceLocation> entry : this.pages.entrySet() ) {
            GuiElementInst btn    = entry.getKey();
            ButtonNav      btnNav = btn.get(ButtonNav.class);

            if( !MinecraftForge.EVENT_BUS.post(new TcuTabEvent.TabIconShow(gui, tcu.getTurret(), btnNav.pageKey)) ) {
                this.currAvailableTabs++;

                if( btnNav.order >= tabStartIdx && btnNav.order < tabStartIdx + this.maxTabsShown ) {
                    btn.setVisible(true);
                    btnNav.setActive(!currPage.equals(btnNav.pageKey));

                    continue;
                }
            }

            btn.setVisible(false);
        }
        if( prevAvailableTabs > this.currAvailableTabs ) {
            decrIndex();
        }
        this.shownTabs = this.fetchShownPageButtons();

        int tabWidth = this.getTabWidth() * this.shownTabs.size();
        int tabLeft  = (tcu.getDefinition().width - tabWidth - tabScrollElemLWidth - tabScrollElemRWidth - 4) / 2;

        this.tabScrollL.pos[0] = tabLeft;
        this.tabScrollL.get(ButtonSL.class).setActive(tabStartIdx > 0);
        this.tabScrollR.pos[0] = tabLeft + tabScrollElemLWidth + tabWidth + 4;
        this.tabScrollR.get(ButtonSL.class).setActive(tabStartIdx < this.currAvailableTabs - shownTabs.size());

        int shownId = 0;
        for( Map.Entry<GuiElementInst, ResourceLocation> tab : this.shownTabs.entrySet() ) {
            tab.getKey().pos[0] = getTabX(shownId++);
        }

        this.currWidth = tabWidth + 4 + tabScrollElemLWidth + tabScrollElemRWidth;
        this.currHeight = this.shownTabs.keySet().stream().map(elem -> elem.get().getHeight()).reduce(Math::max).orElse(0);
    }

    int getTabWidth() {
        GuiElementInst firstTab = this.pages.keySet().stream().findFirst().orElse(null);
        return firstTab != null ? firstTab.get().getWidth() : 0;
    }

    int getTabHeight() {
        GuiElementInst firstTab = this.pages.keySet().stream().findFirst().orElse(null);
        return firstTab != null ? firstTab.get().getHeight() : 0;
    }

    int getTabX(int index) {
        return this.tabScrollL.pos[0] + this.tabScrollL.get().getWidth() + 2 + index * this.getTabWidth();
    }

    @Override
    public void tick(IGui gui, GuiElementInst e) {
        this.setupVisuals(gui);
    }

    private Map<GuiElementInst, ResourceLocation> fetchShownPageButtons() {
        return this.pages.entrySet().stream()
                         .filter(e -> e.getKey().isVisible())
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, () -> new TreeMap<>(new ComparatorTabButton())));
    }

    @Override
    public void render(IGui gui, MatrixStack mStack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        GuiDefinition.renderElement(gui, mStack, x + this.tabScrollL.pos[0], y + this.tabScrollL.pos[1], mouseX, mouseY, partTicks, this.tabScrollL);
        GuiDefinition.renderElement(gui, mStack, x + this.tabScrollR.pos[0], y + this.tabScrollR.pos[1], mouseX, mouseY, partTicks, this.tabScrollR);
        this.shownTabs.forEach((btn, page) -> GuiDefinition.renderElement(gui, mStack, x + btn.pos[0], y, mouseX, mouseY, partTicks, btn));
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int mouseButton) {
        for( Map.Entry<GuiElementInst, ResourceLocation> e : this.shownTabs.entrySet() ) {
            if( e.getKey().get().mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
                return true;
            }
        }

        if( this.tabScrollL.get(ButtonSL.class).mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
            return true;
        }

        return this.tabScrollR.get(ButtonSL.class).mouseClicked(gui, mouseX, mouseY, mouseButton);
    }

    @Override
    public int getWidth() {
        return this.currWidth;
    }

    @Override
    public int getHeight() {
        return this.currHeight;
    }

    private static final class ComparatorTabButton
            implements Comparator<GuiElementInst>
    {
        @Override
        public int compare(GuiElementInst o1, GuiElementInst o2) {
            return Integer.compare(o1.get(ButtonNav.class).order, o2.get(ButtonNav.class).order);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder
            implements IBuilder<PageNavigation>
    {
        public final Map<GuiElementInst, ResourceLocation> btnPages;
        public final GuiElementInst                         btnTabScrollL;
        public final GuiElementInst                         btnTabScrollR;

        protected int maxTabsShown = 7;

        public Builder(GuiElementInst btnTabScrollL, GuiElementInst btnTabScrollR, Map<GuiElementInst, ResourceLocation> btnPages) {
            this.btnTabScrollL = btnTabScrollL;
            this.btnTabScrollR = btnTabScrollR;
            this.btnPages = btnPages;
        }

        @Override
        public void sanitize(IGui gui) {
            if( this.maxTabsShown <= 0 ) { this.maxTabsShown = 1; }
        }

        public Builder maxTabsShown(int shownTabs) { this.maxTabsShown = shownTabs; return this; }

        @Override
        public PageNavigation get(IGui gui) {
            this.sanitize(gui);

            Map<GuiElementInst, ResourceLocation> pages = new HashMap<>();
            this.btnPages.forEach((b, k) -> pages.put(b.initialize(gui), k));

            return new PageNavigation(this.maxTabsShown, this.btnTabScrollL.initialize(gui), this.btnTabScrollR.initialize(gui), pages);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            JsonObject tsl = MiscUtils.get(data.getAsJsonObject("tabScrollLeft"), JsonObject::new);
            JsonObject tsr = MiscUtils.get(data.getAsJsonObject("tabScrollRight"), JsonObject::new);
            JsonObject btn = MiscUtils.get(data.getAsJsonObject("buttonData"), JsonObject::new);

            Map<GuiElementInst, ResourceLocation> btnList = new HashMap<>();
            TurretControlUnit.forEachPage(pgKey -> btnList.put(new GuiElementInst(ButtonNav.Builder.buildFromJson(gui, btn, pgKey).get(gui)), pgKey));

            int tsPosY = JsonUtils.getIntArray(btn.get("size"), new int[] {0, 18}, Range.is(2))[1] / 2;
            GuiElementInst tsle = new GuiElementInst(new int[] {0, tsPosY}, ButtonSL.Builder.buildFromJson(gui, tsl).get(gui));
            tsle.alignment = JsonUtils.getStringArray(tsl.get("alignment"), new String[] {"left", "center"}, Range.between(0, 2));
            GuiElementInst tsre = new GuiElementInst(new int[] {0, tsPosY}, ButtonSL.Builder.buildFromJson(gui, tsr).get(gui));
            tsre.alignment = JsonUtils.getStringArray(tsr.get("alignment"), new String[] {"left", "center"}, Range.between(0, 2));

            Builder b = new Builder(tsle, tsre, btnList);

            JsonUtils.fetchInt(data.get("maxTabsShown"), b::maxTabsShown);

            return b;
        }

        public static PageNavigation fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }

    private static void resetIndex() {
        tabStartIdx = 0;
    }

    private static void decrIndex() {
        tabStartIdx = Math.max(0, tabStartIdx - 1);
    }

    private static void incrIndex(int max) {
        tabStartIdx = Math.min(max, tabStartIdx + 1);
    }
}
