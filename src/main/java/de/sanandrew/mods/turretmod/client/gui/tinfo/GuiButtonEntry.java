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
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class GuiButtonEntry
        extends GuiButton
{
    private static final float TIME = 6.0F;
    public final int entIndex;

    @Nonnull
    private ItemStack icon;

    private float ticksHovered = 0.0F;
    private float lastTime;

    public GuiButtonEntry(int id, int entryId, int x, int y, GuiTurretInfo gui) {
        super(id, x, y, 156, 14, "");
        this.entIndex = entryId;
        ITurretInfoEntry entry = gui.category.getEntry(id);
        this.icon = entry.getIcon();
        this.displayString = Lang.translate(entry.getTitle());
    }

    @Override
    public void drawButton(Minecraft mc, int mx, int my, float partTicks) {
        float time = ClientTickHandler.ticksInGame + partTicks;
        float timeDelta = time - this.lastTime;
        this.lastTime = time;

        if( this.visible ) {
            boolean inside = this.enabled && mx >= x && my >= y && mx < x + width && my < y + height;
            if( inside ) {
                this.ticksHovered = Math.min(TIME, this.ticksHovered + timeDelta);
            } else {
                this.ticksHovered = Math.max(0.0F, this.ticksHovered - timeDelta);
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            float alphaMulti = this.ticksHovered / TIME;
            int color1 = 0x0066cc66 | ((Math.max(0x00, Math.min(0xC0, StrictMath.round(0xC0 * alphaMulti))) << 24) & 0xFF000000);
            int color2 = 0x0066cc66 | ((Math.max(0x00, Math.min(0x80, StrictMath.round(0x80 * alphaMulti))) << 24) & 0xFF000000);

            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + 1, color1, color2);
            this.drawGradientRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, color1, color2);
            this.drawGradientRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, color1, color1);
            this.drawGradientRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, color2, color2);

            RenderUtils.renderStackInGui(this.icon, this.x + 2, this.y + 3, 0.5D);

            mc.fontRenderer.drawString(this.displayString, this.x + 12, this.y + 3, 0xFF000000, false);

            GlStateManager.popMatrix();
        }
    }
}
