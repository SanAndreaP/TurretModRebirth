/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiItemTab;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@SideOnly(Side.CLIENT)
public final class GuiTCUHelper
{
    static final int X_SIZE = 176;
    static final int Y_SIZE = 236;

    final Map<GuiButton, ResourceLocation> tabs = new HashMap<>();

    GuiTCUHelper() {}

    @SuppressWarnings("unchecked")
    void initGui(IGuiTcuInst<?> gui) {
        this.tabs.clear();
        MutableInt tabPos = new MutableInt(0);
        GuiTcuRegistry.GUI_RESOURCES.forEach(location -> {
            GuiTcuRegistry.GuiEntry entry = GuiTcuRegistry.INSTANCE.getGuiEntry(location);
            if( entry.showTab(gui) ) {
                this.tabs.put(gui.addNewButton(new GuiItemTab(gui.getNewButtonId(), gui.getPosX() - 23, gui.getPosY() + 5 + tabPos.getAndIncrement() * 28, entry.icon, "", false)), location);
            }
        });
    }

    boolean hasPermission(Minecraft mc, ITurretInst turretInst) {
        return ItemStackUtils.isItem(mc.player.getHeldItemMainhand(), ItemRegistry.TURRET_CONTROL_UNIT) && turretInst.hasPlayerPermission(mc.player);
    }

    void updateScreen(Minecraft mc, IGuiTcuInst<?> gui) {
        if( !gui.hasPermision() || gui.getTurretInst().getEntity().isDead ) {
            mc.player.closeScreen();
        }
    }

    void drawScreen(IGuiTcuInst<?> gui) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderHelper.disableStandardItemLighting();

//        FontRenderer fRender = gui.getFontRenderer();
//        String pageName = "";
//        if( !pageInfo.enabled ) {
//            pageName = "info";
//        } else if( !pageEntityTargets.enabled ) {
//            pageName = "targetsEntity";
//        } else if( !pagePlayerTargets.enabled ) {
//            pageName = "targetsPlayer";
//        } else if( !pageUpgrades.enabled ) {
//            pageName = "upgrades";
//        }
//        pageName = Lang.translate(Lang.TCU_PAGE_TITLE.get(pageName));
//        fRender.drawString(pageName, gui.getGuiLeft() + 8, gui.getGuiTop() + 6, 0xFF404040);

//        String turretName = Lang.translate(Lang.TURRET_NAME.get(gui.getTurretInst().getTurret().getName()));
//        fRender.drawString(turretName, gui.getGuiLeft() + (X_SIZE - fRender.getStringWidth(turretName)) / 2.0F, gui.getGuiTop() + Y_SIZE - 15, 0xFF00FF00, false);
//        RenderHelper.enableGUIStandardItemLighting();
    }

    void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) {
        ResourceLocation location = this.tabs.get(button);
        if( location != null ) {
            TurretModRebirth.proxy.openGui(gui.getGui().mc.player, EnumGui.GUI_TCU, gui.getTurretInst().getEntity().getEntityId(), GuiTcuRegistry.GUI_RESOURCES.indexOf(location), 0);
        }
    }

//    static boolean actionPerformed(GuiButton button, GuiTurretCtrlUnit gui) {
//        if( button == null ) {
//            return false;
//        }
//        Minecraft mc = gui.getMc();
//
//        if( button == pageInfo ) {
//            TurretModRebirth.proxy.openGui(mc.player, EnumGui.GUI_TCU_INFO, gui.getTurretInst().getEntity().getEntityId(), gui.hasPermision() ? 1 : 0, 0);
//            return true;
//        } else if( button == pageEntityTargets ) {
//            TurretModRebirth.proxy.openGui(mc.player, EnumGui.GUI_TCU_ENTITY_TARGETS, gui.getTurretInst().getEntity().getEntityId(), 0, 0);
//            return true;
//        } else if( button == pagePlayerTargets ) {
//            TurretModRebirth.proxy.openGui(mc.player, EnumGui.GUI_TCU_PLAYER_TARGETS, gui.getTurretInst().getEntity().getEntityId(), 0, 0);
//            return true;
//        } else if( button == pageUpgrades ) {
//            TurretModRebirth.proxy.openGui(mc.player, EnumGui.GUI_TCU_UPGRADES, gui.getTurretInst().getEntity().getEntityId(), 0, 0);
//            return true;
//        }
//        return false;
//    }

}
