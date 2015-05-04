/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTcuInfo
        extends AGuiTurretControlUnit
{
    private int specOwnerHead;

    private static RenderItem itemRenderer = new RenderItem();

    public GuiTcuInfo(AEntityTurretBase turret) {
        super(turret);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.pageInfo.enabled = false;
        this.specOwnerHead = SAPUtils.RNG.nextInt(3) == 0 ? SAPUtils.RNG.nextInt(3) : -1;
    }

    @Override
    public void drawScreenPostBkg(int mouseX, int mouseY, float partTicks) {
        this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_INFO.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if( this.specOwnerHead >= 0 ) {
            this.drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 95, this.xSize, this.specOwnerHead * 8, 10, 8);
        }

        String value = this.myTurret.hasCustomNameTag() ? this.myTurret.getCustomNameTag() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 23, 0x000000);

        value = String.format("%.1f / %.1f HP", this.myTurret.getHealth(), this.myTurret.getMaxHealth());
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 35, 0x000000);

        value = String.format("%d", this.myTurret.getAmmo());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawIcon(this.myTurret.getAmmoType().getTypeItem().getIconIndex(), this.guiLeft + 21, this.guiTop + 49);
        GL11.glTranslated(0.0D, 0.0D, 300.0D);
        this.fontRendererObj.drawStringWithShadow(value, this.guiLeft + 38 - this.fontRendererObj.getStringWidth(value), this.guiTop + 58, 0xFFFFFFFF);
        GL11.glTranslated(0.0D, 0.0D, -300.0D);
//        this.zLevel = 0.0F;
//        value = this.myTurret.getAmmoType() != null ? this.myTurret.getAmmoType().getTypeItem().getDisplayName() : "-n/a-";
//        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 59, 0x000000);
//
        value = this.myTurret.hasTarget() ? SAPUtils.translatePreFormat("entity.%s.name", this.myTurret.getTargetName()) : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 71, 0x000000);

        value = this.myTurret.getOwnerName();
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 95, 0x000000);
    }

    private void drawIcon(IIcon icon, int posX, int posY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        ResourceLocation resourcelocation = this.mc.getTextureManager().getResourceLocation(1);
        mc.getTextureManager().bindTexture(resourcelocation);

        if( icon == null ) {
            icon = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        itemRenderer.renderIcon(posX, posY, icon, 16, 16);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
        GL11.glPopMatrix();
    }
}
