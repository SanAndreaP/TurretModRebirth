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
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiTCUHelper
{
    static final int X_SIZE = 176;
    static final int Y_SIZE = 236;

    GuiTCUHelper() {}

    @SuppressWarnings("unchecked")
    void initGui(IGuiTcuInst<?> gui) {
//        pageInfo = gui.addNewButton(new GuiItemTab(gui.getNewButtonId(), gui.getPosX() - 23, gui.getPosY() + 5, new ItemStack(Items.SIGN), Lang.translate(Lang.TCU_PAGE_TAB.get("info")), false));

//        if( gui.hasPermision() ) {
//            pageEntityTargets = gui.addNewButton(new GuiItemTab(gui.getNewButtonId(), gui.getPosX() - 23, gui.getPosY() + 33, new ItemStack(Items.SKULL, 1, 2), Lang.translate(Lang.TCU_PAGE_TAB.get("targetsEntity")), false));
//            pagePlayerTargets = gui.addNewButton(new GuiItemTab(gui.getNewButtonId(), gui.getPosX() - 23, gui.getPosY() + 61, new ItemStack(Items.SKULL, 1, 3), Lang.translate(Lang.TCU_PAGE_TAB.get("targetsPlayer")), false));
//            pageUpgrades = gui.addNewButton(new GuiItemTab(gui.getNewButtonId(), gui.getPosX() - 23, gui.getPosY() + 89, new ItemStack(ItemRegistry.TURRET_UPGRADE), Lang.translate(Lang.TCU_PAGE_TAB.get("upgrades")), false));
//        }
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
