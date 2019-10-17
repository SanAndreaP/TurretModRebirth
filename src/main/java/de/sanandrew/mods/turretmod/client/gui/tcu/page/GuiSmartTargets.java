/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiButtonIcon;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.smarttargeting.AdvTargetSettings;
import de.sanandrew.mods.turretmod.registry.Lang;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiSmartTargets
        implements IGuiTCU
{
    private GuiButton turretIgnore;
    private GuiButton turretCheckSame;
    private GuiButton turretCheckAll;

    private GuiButton tamedAll;
    private GuiButton tamedPlayers;
    private GuiButton tamedNone;

    private GuiButton childOnly;
    private GuiButton adultOnly;
    private GuiButton childAndAdult;

    private GuiButton noCount;
    private GuiButton countGlobalLess;
    private GuiButton countGlobalMore;
    private GuiButton countIndivLess;
    private GuiButton countIndivMore;

    private GuiTextField countEntities;

    @Override
    public void initialize(IGuiTcuInst<?> gui) {
        AdvTargetSettings settings = getSettings(gui);
        if( settings != null ) {
            ResourceLocation texture = Resources.GUI_TCU_SMARTTGT.resource;
            this.turretIgnore = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 10, gui.getPosY() + 48, 212, 18, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_turret_ignore"))));
            this.turretCheckSame = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 29, gui.getPosY() + 48, 176, 18, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_turret_same"))));
            this.turretCheckAll = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 48, gui.getPosY() + 48, 194, 18, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_turret_all"))));

            this.tamedAll = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 10, gui.getPosY() + 82, 194, 72, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_tamed_all"))));
            this.tamedPlayers = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 29, gui.getPosY() + 82, 176, 72, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_tamed_players"))));
            this.tamedNone = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 48, gui.getPosY() + 82, 230, 18, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_tamed_none"))));

            this.childAndAdult = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 10, gui.getPosY() + 116, 176, 126, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_child_adult"))));
            this.childOnly = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 29, gui.getPosY() + 116, 212, 72, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_child_only"))));
            this.adultOnly = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 48, gui.getPosY() + 116, 230, 72, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_adult_only"))));

            this.noCount = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 10, gui.getPosY() + 150, 194, 126, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_count_no"))));
            this.countGlobalLess = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 29, gui.getPosY() + 150, 212, 126, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_count_global_less"))));
            this.countGlobalMore = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 48, gui.getPosY() + 150, 230, 126, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_count_global_more"))));
            this.countIndivLess = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 67, gui.getPosY() + 150, 176, 180, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_count_individual_less"))));
            this.countIndivMore = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), gui.getPosX() + 86, gui.getPosY() + 150, 194, 180, texture, LangUtils.translate(Lang.TCU_BTN.get("adv_count_individual_more"))));

            this.countEntities = new GuiTextField(0, gui.getFontRenderer(), gui.getPosX() + 110, gui.getPosY() + 157, 25, 10);
            this.countEntities.setMaxStringLength(3);
            this.countEntities.setValidator(s -> {
                if( Strings.isNullOrEmpty(s) ) {
                    return true;
                }
                Integer val = getInteger(s, -1);
                return val >= 0 && val <= 256;
            });
            this.countEntities.setText(String.format("%d", settings.getCountEntities()));
        }
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        AdvTargetSettings settings = getSettings(gui);
        if( settings != null ) {
            this.turretIgnore.enabled = settings.getTurretAwareness() != AdvTargetSettings.TurretAwareness.UNAWARE;
            this.turretCheckSame.enabled = settings.getTurretAwareness() != AdvTargetSettings.TurretAwareness.SAME_TYPE;
            this.turretCheckAll.enabled = settings.getTurretAwareness() != AdvTargetSettings.TurretAwareness.ALL_TYPES;

            this.tamedAll.enabled = settings.getTamedAwareness() != AdvTargetSettings.TamedAwareness.UNAWARE;
            this.tamedPlayers.enabled = settings.getTamedAwareness() != AdvTargetSettings.TamedAwareness.IGNORE_UNTARGETED_PLAYERS;
            this.tamedNone.enabled = settings.getTamedAwareness() != AdvTargetSettings.TamedAwareness.IGNORE_ALL_TAMED;

            this.childAndAdult.enabled = settings.getChildAwareness() != AdvTargetSettings.ChildAwareness.UNAWARE;
            this.childOnly.enabled = settings.getChildAwareness() != AdvTargetSettings.ChildAwareness.CHILDREN_ONLY;
            this.adultOnly.enabled = settings.getChildAwareness() != AdvTargetSettings.ChildAwareness.ADULTS_ONLY;

            this.noCount.enabled = settings.getCountAwareness() != AdvTargetSettings.CountAwareness.NO_COUNT;
            this.countGlobalLess.enabled = settings.getCountAwareness() != AdvTargetSettings.CountAwareness.IGNORE_IF_BELOW_GLOBAL;
            this.countGlobalMore.enabled = settings.getCountAwareness() != AdvTargetSettings.CountAwareness.IGNORE_IF_ABOVE_GLOBAL;
            this.countIndivLess.enabled = settings.getCountAwareness() != AdvTargetSettings.CountAwareness.IGNORE_IF_BELOW_INDIVIDUAL;
            this.countIndivMore.enabled = settings.getCountAwareness() != AdvTargetSettings.CountAwareness.IGNORE_IF_ABOVE_INDIVIDUAL;

            this.countEntities.setEnabled(this.noCount.enabled);

            this.countEntities.updateCursorCounter();
        }
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        gui.getGui().mc.renderEngine.bindTexture(Resources.GUI_TCU_SMARTTGT.resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gui.getGui().drawTexturedModalRect(gui.getPosX(), gui.getPosY(), 0, 0, gui.getWidth(), gui.getHeight());

        drawGroupBox(gui.getFontRenderer(), LangUtils.translate(Lang.TCU_SMARTTGT_GBOX.get("turret")), gui.getPosX() + 7, gui.getPosY() + 39);
        drawGroupBox(gui.getFontRenderer(), LangUtils.translate(Lang.TCU_SMARTTGT_GBOX.get("tamed")), gui.getPosX() + 7, gui.getPosY() + 73);
        drawGroupBox(gui.getFontRenderer(), LangUtils.translate(Lang.TCU_SMARTTGT_GBOX.get("age")), gui.getPosX() + 7, gui.getPosY() + 107);
        drawGroupBox(gui.getFontRenderer(), LangUtils.translate(Lang.TCU_SMARTTGT_GBOX.get("count")), gui.getPosX() + 7, gui.getPosY() + 141);

        if( this.countEntities != null ) {
            this.countEntities.drawTextBox();
        }
    }

    @Override
    public void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) {
        AdvTargetSettings settings = getSettings(gui);
        if( settings != null ) {
            if( button == this.turretIgnore ) {
                settings.setTurretAwareness(AdvTargetSettings.TurretAwareness.UNAWARE);
                syncSettings(gui.getTurretInst());
            } else if( button == this.turretCheckSame ) {
                settings.setTurretAwareness(AdvTargetSettings.TurretAwareness.SAME_TYPE);
                syncSettings(gui.getTurretInst());
            } else if( button == this.turretCheckAll ) {
                settings.setTurretAwareness(AdvTargetSettings.TurretAwareness.ALL_TYPES);
                syncSettings(gui.getTurretInst());
            } else if( button == this.tamedAll ) {
                settings.setTamedAwareness(AdvTargetSettings.TamedAwareness.UNAWARE);
                syncSettings(gui.getTurretInst());
            } else if( button == this.tamedPlayers ) {
                settings.setTamedAwareness(AdvTargetSettings.TamedAwareness.IGNORE_UNTARGETED_PLAYERS);
                syncSettings(gui.getTurretInst());
            } else if( button == this.tamedNone ) {
                settings.setTamedAwareness(AdvTargetSettings.TamedAwareness.IGNORE_ALL_TAMED);
                syncSettings(gui.getTurretInst());
            } else if( button == this.childAndAdult ) {
                settings.setChildAwareness(AdvTargetSettings.ChildAwareness.UNAWARE);
                syncSettings(gui.getTurretInst());
            } else if( button == this.childOnly ) {
                settings.setChildAwareness(AdvTargetSettings.ChildAwareness.CHILDREN_ONLY);
                syncSettings(gui.getTurretInst());
            } else if( button == this.adultOnly ) {
                settings.setChildAwareness(AdvTargetSettings.ChildAwareness.ADULTS_ONLY);
                syncSettings(gui.getTurretInst());
            } else if( button == this.noCount ) {
                settings.setCountAwareness(AdvTargetSettings.CountAwareness.NO_COUNT);
                syncSettings(gui.getTurretInst());
            } else if( button == this.countGlobalLess ) {
                settings.setCountAwareness(AdvTargetSettings.CountAwareness.IGNORE_IF_BELOW_GLOBAL);
                syncSettings(gui.getTurretInst());
            } else if( button == this.countGlobalMore ) {
                settings.setCountAwareness(AdvTargetSettings.CountAwareness.IGNORE_IF_ABOVE_GLOBAL);
                syncSettings(gui.getTurretInst());
            } else if( button == this.countIndivLess ) {
                settings.setCountAwareness(AdvTargetSettings.CountAwareness.IGNORE_IF_BELOW_INDIVIDUAL);
                syncSettings(gui.getTurretInst());
            } else if( button == this.countIndivMore ) {
                settings.setCountAwareness(AdvTargetSettings.CountAwareness.IGNORE_IF_ABOVE_INDIVIDUAL);
                syncSettings(gui.getTurretInst());
            }
        }
    }

    @Override
    public void onMouseClick(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton) {
        this.countEntities.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doKeyIntercept(IGuiTcuInst<?> gui, char typedChar, int keyCode) {
        if( this.countEntities.textboxKeyTyped(typedChar, keyCode) ) {
            AdvTargetSettings settings = getSettings(gui);
            if( settings != null ) {
                Integer val = getInteger(this.countEntities.getText(), null);
                if( val != null ) {
                    settings.setCountEntities(val);
                    syncSettings(gui.getTurretInst());
                }
            }

            return true;
        }

        return false;
    }

    private static void drawGroupBox(FontRenderer fontRenderer, String title, int x, int y) {
        final int width2 = 162;
        final int height2 = 30;
        final int frameColor2 = 0x30000000;

        int strWidth = fontRenderer.getStringWidth(title);
        GlStateManager.enableBlend();
        fontRenderer.drawString(title, x + 5, y, 0x80000000, false);

        Gui.drawRect(x,                y + 4,          x + 3,     y + 5,          frameColor2);
        Gui.drawRect(x + strWidth + 6, y + 4,          x + width2, y + 5,          frameColor2);
        Gui.drawRect(x,                y + height2 - 1, x + width2, y + height2,     frameColor2);
        Gui.drawRect(x,                y + 5,          x + 1,     y + height2 - 1, frameColor2);
        Gui.drawRect(x + width2 - 1,    y + 5,          x + width2, y + height2 - 1, frameColor2);
    }

    public static boolean showTab(IGuiTcuInst<?> gui) {
        return gui.hasPermision() && gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.SMART_TGT);
    }

    private static AdvTargetSettings getSettings(IGuiTcuInst<?> gui) {
        AdvTargetSettings settings = gui.getTurretInst().getUpgradeProcessor().getUpgradeInstance(Upgrades.SMART_TGT.getId());
        if( settings == null ) {
            gui.getGui().mc.player.closeScreen();
        }
        return settings;
    }

    private static void syncSettings(ITurretInst turretInst) {
        UpgradeRegistry.INSTANCE.syncWithServer(turretInst, Upgrades.SMART_TGT.getId());
    }

    private static Integer getInteger(String s, Integer def) {
        try {
            return Integer.decode(s);
        } catch( NumberFormatException ex ) {
            return def;
        }
    }
}
