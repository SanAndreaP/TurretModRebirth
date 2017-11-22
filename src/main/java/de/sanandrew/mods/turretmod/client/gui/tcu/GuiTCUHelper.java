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
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiItemTab;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class GuiTCUHelper
{
    static final int X_SIZE = 176;
    static final int Y_SIZE = 236;

    final Map<GuiButton, ResourceLocation> tabs = new HashMap<>();

    GuiTCUHelper() {}

    private long marqueeTime;

    @SuppressWarnings("unchecked")
    void initGui(IGuiTcuInst<?> gui) {
        this.tabs.clear();
        MutableInt tabPos = new MutableInt(0);
        GuiTcuRegistry.GUI_RESOURCES.forEach(location -> {
            GuiTcuRegistry.GuiEntry entry = GuiTcuRegistry.INSTANCE.getGuiEntry(location);
            if( entry != null && entry.showTab(gui) ) {
                GuiButton btn = gui.addNewButton(new GuiItemTab(gui.getNewButtonId(), gui.getPosX() - 23,
                                                                gui.getPosY() + 5 + tabPos.getAndIncrement() * 28, entry.getIcon(),
                                                                Lang.translate(Lang.TCU_PAGE_TITLE.get(location.getResourceDomain(), location.getResourcePath())), false));
                btn.enabled = !location.equals(gui.getRegistryKey());
                this.tabs.put(btn, location);
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
        FontRenderer fRender = gui.getFontRenderer();
        fRender.drawString(Lang.translate(Lang.TCU_PAGE_TITLE.get(gui.getRegistryKey().getResourceDomain(), gui.getRegistryKey().getResourcePath())), 8, 28, 0xFF404040);
        String turretName = Lang.translate(Lang.TURRET_NAME.get(gui.getTurretInst().getTurret().getName()));
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
            TurretModRebirth.proxy.openGui(gui.getGui().mc.player, EnumGui.GUI_TCU, gui.getTurretInst().getEntity().getEntityId(), GuiTcuRegistry.GUI_RESOURCES.indexOf(location), 0);
        }
    }

}
