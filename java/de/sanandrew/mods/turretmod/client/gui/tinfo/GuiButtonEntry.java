/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class GuiButtonEntry
        extends GuiButton
{
    public final int entIndex;

    private ItemStack icon;
    private GuiTurretInfo tinfo;

    float ticksHovered = 0.0F;
    float time = 6.0F;

    public GuiButtonEntry(int id, int entryId, int x, int y, GuiTurretInfo gui) {
        super(id, x, y, 156, 14, "");
        this.tinfo = gui;
        this.entIndex = entryId;
        TurretInfoEntry entry = gui.category.getEntry(id);
        this.icon = entry.getIcon();
        this.displayString = Lang.translate(entry.getTitle());
    }

    @Override
    public void drawButton(Minecraft mc, int mx, int my) {
        if( this.visible ) {
            boolean inside = this.enabled && mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height;
            if( inside ) {
                this.ticksHovered = Math.min(this.time, this.ticksHovered + this.tinfo.timeDelta);
            } else {
                this.ticksHovered = Math.max(0.0F, this.ticksHovered - this.tinfo.timeDelta);
            }

            if( this.icon == null ) {
                this.icon = new ItemStack(Blocks.FIRE);
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            float alphaMulti = this.ticksHovered / this.time;
            int color1 = 0x0066cc66 | ((Math.max(0x00, Math.min(0xC0, StrictMath.round(0xC0 * alphaMulti))) << 24) & 0xFF000000);
            int color2 = 0x0066cc66 | ((Math.max(0x00, Math.min(0x80, StrictMath.round(0x80 * alphaMulti))) << 24) & 0xFF000000);

            this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, color1, color2);
            this.drawGradientRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, color1, color2);
            this.drawGradientRect(this.xPosition, this.yPosition + 1, this.xPosition + 1, this.yPosition + this.height - 1, color1, color1);
            this.drawGradientRect(this.xPosition + this.width - 1, this.yPosition + 1, this.xPosition + this.width, this.yPosition + this.height - 1, color2, color2);

            RenderUtils.renderStackInGui(this.icon, this.xPosition + 2, this.yPosition + 3, 0.5D);

            mc.fontRendererObj.drawString(this.displayString, this.xPosition + 12, this.yPosition + 3, 0xFF000000, false);

            GlStateManager.popMatrix();
        }
    }
}
