/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.render.world.RenderTurretCam;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class GuiCameras
        extends GuiScreen
{
    private final Entity e;

    public GuiCameras(Entity rendered) {
        e = rendered;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if( e instanceof ITurretInst ) {
            RenderTurretCam.bindTurretCamTx((ITurretInst) e, 64);
            GlStateManager.pushMatrix();
            GlStateManager.translate(128, 128, 0);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.5F, 0.5F, 1.0F);
            this.drawTexturedModalRect(-128, -128, 0, 0, 256, 256);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
