/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;

public class GuiTurretCtrlUnitPg1
        extends GuiScreen
{
    protected int guiLeft;
    protected int guiTop;
    protected int xSize;
    protected int ySize;

    private final AEntityTurretBase myTurret;

    public GuiTurretCtrlUnitPg1(AEntityTurretBase turret) {
        this.myTurret = turret;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.xSize = 176;
        this.ySize = 222;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_PG1.getResource());

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        int offsetY = 0;
        for( Class<? extends EntityLiving> cls : this.myTurret.getTargetList().keySet() ) {
            this.fontRendererObj.drawString(cls.toString(), this.guiLeft + 10, this.guiTop + 20 + offsetY, 0xFFFFFF, false);
            offsetY += 10;
        }

        super.drawScreen(mouseX, mouseY, partTicks);
    }
}
