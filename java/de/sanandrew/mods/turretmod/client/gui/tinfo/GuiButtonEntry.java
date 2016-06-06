/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiButtonEntry
        extends GuiButton
{
    protected static RenderItem itemRender = new RenderItem();

    public final int entIndex;

    private ItemStack icon;
    private GuiTurretInfo tinfo;

    float ticksHovered = 0.0F;
    float time = 6.0F;

    public GuiButtonEntry(int id, int entryId, int x, int y, GuiTurretInfo gui) {
        super(id, x, y, 156, 20, "");
        this.tinfo = gui;
        this.entIndex = entryId;
        TurretInfoEntry entry = gui.category.getEntry(id);
        this.icon = entry.getIcon();
        this.displayString = StatCollector.translateToLocal(entry.getTitle());
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
                this.icon = new ItemStack(Blocks.fire);
            }


            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            float alphaMulti = this.ticksHovered / this.time;
            int color1 = 0x0066cc66 | ((Math.max(0x00, Math.min(0xC0, StrictMath.round(0xC0 * alphaMulti))) << 24) & 0xFF000000);
            int color2 = 0x0066cc66 | ((Math.max(0x00, Math.min(0x80, StrictMath.round(0x80 * alphaMulti))) << 24) & 0xFF000000);

            this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, color1, color2);
            this.drawGradientRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, color1, color2);
            this.drawGradientRect(this.xPosition, this.yPosition + 1, this.xPosition + 1, this.yPosition + this.height - 1, color1, color1);
            this.drawGradientRect(this.xPosition + this.width - 1, this.yPosition + 1, this.xPosition + this.width, this.yPosition + this.height - 1, color2, color2);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            this.drawItemStack(mc, this.icon, xPosition + 2, yPosition + 2);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);

            mc.fontRenderer.drawString(this.displayString, this.xPosition + 20, this.yPosition + 6, 0xFF000000, false);

            GL11.glPopMatrix();
        }
    }

    private void drawItemStack(Minecraft mc, ItemStack stack, int x, int y) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
        GL11.glTranslatef(0.0F, 0.0F, -32.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }
}
