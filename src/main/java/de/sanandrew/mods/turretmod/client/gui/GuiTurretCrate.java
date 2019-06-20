/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import de.sanandrew.mods.turretmod.inventory.container.ContainerTurretCrate;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiTurretCrate
        extends GuiContainer
        implements IGui
{
    private final TileEntityTurretCrate crate;

    private GuiDefinition guiDef;
    private FontRenderer smallFr;
    private ContainerTurretCrate.SlotAmmo ammoSlot;
    private float currPartTicks;

    public GuiTurretCrate(InventoryPlayer invPlayer, TileEntityTurretCrate crate) {
        super(new ContainerTurretCrate(invPlayer, crate));

        this.crate = crate;

        try {
            this.guiDef = GuiDefinition.getNewDefinition(Resources.GUI_STRUCT_TCRATE.resource);

            this.xSize = this.guiDef.width;
            this.ySize = this.guiDef.height;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        this.smallFr = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.renderEngine, true);
        this.ammoSlot = ((ContainerTurretCrate) this.inventorySlots).getAmmoSlot();

        GuiHelper.initGuiDef(this.guiDef, this);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.ammoSlot.isRendering = true;

        drawDefaultBackground();
        this.currPartTicks = partialTicks;
        GuiHelper.drawGDBackground(this.guiDef, this, partialTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.ammoSlot.isRendering = false;

        int ammoCount = this.crate.getAmmoCount();
        if( ammoCount > 0 ) {
            FontRenderer fr = ammoCount > 99 ? this.smallFr : this.fontRenderer;
            String s = String.format("%d", ammoCount);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.0D, 100.0D);
            fr.drawString(s, this.ammoSlot.xPos + 17 - fr.getStringWidth(s), this.ammoSlot.yPos + 9, 0xFFFFFFFF, true);
            GlStateManager.popMatrix();
        }

        RenderHelper.disableStandardItemLighting();
        this.guiDef.drawForeground(this, mouseX, mouseY, this.currPartTicks);
        RenderHelper.enableGUIStandardItemLighting();
    }

    @Override
    public GuiScreen get() {
        return this;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getScreenPosX() {
        return this.guiLeft;
    }

    @Override
    public int getScreenPosY() {
        return this.guiTop;
    }
}
