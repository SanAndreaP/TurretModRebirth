/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.IGuiTcuRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiButtonIcon;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiButtonTcuTab;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiInfo;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiSmartTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiTargetCreatures;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiTargetPlayers;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiUpgrades;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.PlayerHeads;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public final class GuiTcuHelper
{
    private static final int MAX_TABS = 5;

    static final int X_SIZE = 176;
    static final int Y_SIZE = 236;

    private final Map<GuiButton, ResourceLocation> tabs = new TreeMap<>(new ComparatorTabButton());
    private GuiButton tabNavLeft;
    private GuiButton tabNavRight;

    private static int currTabScroll = 0;

    GuiTcuHelper() {}

    private long marqueeTime;

    void initGui(IGuiTcuInst<?> gui) {
        this.tabs.clear();

        MutableInt currIndex = new MutableInt(0);
        GuiTcuRegistry.GUI_RESOURCES.forEach(location -> {
            GuiTcuRegistry.GuiEntry entry = GuiTcuRegistry.INSTANCE.getGuiEntry(location);
            if( entry != null && entry.showTab(gui) ) {
                GuiButton btn = new GuiButtonTcuTab(gui.getNewButtonId(), 0, gui.getPosY() + 213, entry.getIcon(),
                                                    LangUtils.translate(Lang.TCU_PAGE_TITLE.get(location.getResourceDomain(), location.getResourcePath())));
                btn.visible = false;
                btn.enabled = !location.equals(gui.getRegistryKey());
                if( btn.enabled && (currIndex.getValue() < currTabScroll || currIndex.getValue() >= currTabScroll + MAX_TABS) ) {
                    currTabScroll = Math.min(Math.max(currIndex.getValue() - MathHelper.floor(MAX_TABS / 2.0F), 0), this.tabs.size() - MAX_TABS + 1);
                }
                gui.addNewButton(btn);
                this.tabs.put(btn, location);
                currIndex.increment();
            }
        });
        this.tabNavLeft = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), 0, gui.getPosY() + 213, 18, 0, Resources.GUI_TCU_BUTTONS.resource, ""));
        this.tabNavLeft.visible = false;
        this.tabNavRight = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), 0, gui.getPosY() + 213, 36, 0, Resources.GUI_TCU_BUTTONS.resource, ""));
        this.tabNavRight.visible = false;
    }

    boolean hasPermission(Minecraft mc, ITurretInst turretInst) {
        return ItemStackUtils.isItem(mc.player.getHeldItemMainhand(), ItemRegistry.TURRET_CONTROL_UNIT) && turretInst.hasPlayerPermission(mc.player);
    }

    void updateScreen(Minecraft mc, IGuiTcuInst<?> gui) {
        if( !gui.hasPermision() || gui.getTurretInst().get().isDead || gui.getTurretInst().get().getDistance(mc.player) > 36.0D ) {
            mc.player.closeScreen();
        }

        int maxTabs = Math.min(this.tabs.size(), MAX_TABS);
        int minXTabs = gui.getPosX() + (gui.getWidth() - maxTabs * 19 + 1) / 2;
        MutableInt currIndex = new MutableInt(0);
        this.tabs.forEach((btn, location) -> {
            int cInd = currIndex.getAndIncrement();
            btn.visible = cInd >= currTabScroll && cInd < currTabScroll + MAX_TABS;
            btn.enabled = !gui.getRegistryKey().equals(location);
            btn.x = minXTabs + (cInd - currTabScroll) * 19;
        });
        this.tabNavLeft.x = minXTabs - 19;
        this.tabNavRight.x = minXTabs + maxTabs * 19;
        this.tabNavLeft.visible = currTabScroll > 0;
        this.tabNavRight.visible = currTabScroll + MAX_TABS < this.tabs.size();
    }

    void drawScreen(IGuiTcuInst<?> gui) {
        FontRenderer fRender = gui.getFontRenderer();
        fRender.drawString(LangUtils.translate(Lang.TCU_PAGE_TITLE.get(gui.getRegistryKey().getResourceDomain(), gui.getRegistryKey().getResourcePath())), 8, 28, 0xFF404040);
        String turretName = LangUtils.translate(Lang.ENTITY_NAME.get(gui.getTurretInst().getTurret().getRegistryId()));
        int strWidth = fRender.getStringWidth(turretName);
        if( strWidth > 144 ) {
            long currTime = System.currentTimeMillis();
            if( this.marqueeTime < 1 ) {
                this.marqueeTime = currTime;
            }
            int marquee = -144 + (int) (currTime - this.marqueeTime) / 25;
            if( marquee > strWidth ) {
                this.marqueeTime = currTime;
            }
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GuiUtils.glScissor(gui.getPosX() + 16, gui.getPosY() + 6, 144, 12);
            fRender.drawString(turretName, 17 - marquee, 9, 0xFFAAAAFF, false);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            fRender.drawString(turretName, (X_SIZE - fRender.getStringWidth(turretName)) / 2.0F, 9, 0xFFAAAAFF, false);
        }
    }

    void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) {
        ResourceLocation location = this.tabs.get(button);
        if( location != null ) {
            TurretModRebirth.proxy.openGui(gui.getGui().mc.player, EnumGui.GUI_TCU, gui.getTurretInst().get().getEntityId(), GuiTcuRegistry.GUI_RESOURCES.indexOf(location), 0);
        } else if( button == this.tabNavLeft && currTabScroll > 0 ) {
            currTabScroll--;
        } else if( button == this.tabNavRight && currTabScroll < this.tabs.size() - MAX_TABS ) {
            currTabScroll++;
        }
    }

    public static void initialize(IGuiTcuRegistry registry) {
        registry.registerGui(GuiTcuRegistry.GUI_INFO, new ItemStack(Items.BOOK), GuiInfo::new, null);
        registry.registerGui(GuiTcuRegistry.GUI_TARGETS_MOB, new ItemStack(Items.SKULL, 1, 2), GuiTargetCreatures::new, IGuiTcuInst::hasPermision);
        registry.registerGui(GuiTcuRegistry.GUI_TARGETS_PLAYER, PlayerHeads::getRandomSkull, GuiTargetPlayers::new, IGuiTcuInst::hasPermision);
        registry.registerGui(GuiTcuRegistry.GUI_TARGETS_SMART, UpgradeRegistry.INSTANCE.getUpgradeItem(Upgrades.SMART_TGT), GuiSmartTargets::new, GuiSmartTargets::showTab);
        registry.registerGui(GuiTcuRegistry.GUI_UPGRADES, new ItemStack(ItemRegistry.TURRET_UPGRADE), GuiUpgrades::new, IGuiTcuInst::hasPermision);
    }

    private static final class ComparatorTabButton
            implements Comparator<GuiButton>
    {
        @Override
        public int compare(GuiButton o1, GuiButton o2) {
            return Integer.compare(o1.id, o2.id);
        }
    }
}
