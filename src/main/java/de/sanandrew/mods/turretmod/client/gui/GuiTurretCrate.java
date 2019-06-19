/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.turretmod.inventory.container.ContainerTurretCrate;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiTurretCrate
        extends GuiContainer
{
    private final TileEntityTurretCrate crate;

    public GuiTurretCrate(InventoryPlayer invPlayer, TileEntityTurretCrate crate) {
        super(new ContainerTurretCrate(invPlayer, crate));

        this.crate = crate;

        this.width = 256;
        this.height = 256;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();

        for( Slot s : this.inventorySlots.inventorySlots ) {
            if( s instanceof ContainerTurretCrate.SlotAmmo ) {
                ((ContainerTurretCrate.SlotAmmo) s).isRendering = true;
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        FontRenderer fr = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.renderEngine, true);
        for( Slot slot : this.inventorySlots.inventorySlots ) {
            if( slot instanceof ContainerTurretCrate.SlotAmmo ) {
                ((ContainerTurretCrate.SlotAmmo) slot).isRendering = false;

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0D, 0.0D, 100.0D);
                String s = String.format("%d", this.crate.getAmmoCount());
                fr.drawString(s, slot.xPos + 17 - fr.getStringWidth(s), slot.yPos + 9, 0xFFFFFFFF, true);
                GlStateManager.popMatrix();
            }
        }

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
