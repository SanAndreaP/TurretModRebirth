/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.turretmod.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.List;

public class TmrClientUtils
{
    private static Minecraft mc;
    private static RenderItem renderItem;

    public static Minecraft getMc() {
        if( mc == null ) {
            mc = Minecraft.getMinecraft();
        }

        return mc;
    }

    public static void doGlScissor(int x, int y, int width, int height) {
        Minecraft mc = getMc();
        int scaleFactor = 1;
        int guiScale = mc.gameSettings.guiScale;

        if( guiScale == 0 ) {
            guiScale = 1000;
        }

        while( scaleFactor < guiScale && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240 ) {
            ++scaleFactor;
        }

        GL11.glScissor(x * scaleFactor, mc.displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }

    public static String getTimeFromTicks(int ticks) {
        int hours = ticks / 72_000;
        int minutes = (ticks - hours * 72_000) / 1_200;
        float seconds = (ticks - hours * 72_000 - minutes * 1_200) / 20.0F;

        StringBuilder sb = new StringBuilder();
        if( hours > 0 ) {
            sb.append(String.format("%dh", hours));
        }
        if( minutes > 0 ) {
            if( sb.length() > 0 ) {
                sb.append(' ');
            }
            sb.append(String.format("%dm", minutes));
        }
        if( seconds > 0.0F ) {
            if( sb.length() > 0 ) {
                sb.append(' ');
            }
            sb.append(String.format("%.1fs", seconds));
        }

        return sb.toString();
    }

    public static List<?> getTooltipWithoutShift(ItemStack stack) {
        ByteBuffer keyDownBuffer = ReflectionUtils.getCachedFieldValue(Keyboard.class, null, "keyDownBuffer", "keyDownBuffer");
        byte lShift = keyDownBuffer.get(Keyboard.KEY_LSHIFT);
        byte rShift = keyDownBuffer.get(Keyboard.KEY_RSHIFT);
        keyDownBuffer.put(Keyboard.KEY_LSHIFT, (byte) 0);
        keyDownBuffer.put(Keyboard.KEY_RSHIFT, (byte) 0);
        List<?> tooltip = stack.getTooltip(getMc().thePlayer, false);
        keyDownBuffer.put(Keyboard.KEY_LSHIFT, lShift);
        keyDownBuffer.put(Keyboard.KEY_RSHIFT, rShift);

        return tooltip;
    }

    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale) {
        renderStackInGui(stack, posX, posY, scale, null, null);
    }

    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale, @Nullable FontRenderer fontRenderer) {
        renderStackInGui(stack, posX, posY, scale, fontRenderer, null);
    }

    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale, @Nullable FontRenderer fontRenderer, @Nullable String customTxt) {
        if( renderItem == null ) {
            renderItem = getMc().getRenderItem();
        }

        renderItem.zLevel -= 50.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        if( fontRenderer != null ) {
            renderItem.renderItemOverlayIntoGUI(fontRenderer, stack, 0, 0, customTxt);
            GlStateManager.disableLighting();
        }
        GlStateManager.popMatrix();
        renderItem.zLevel += 50.0F;
    }

    public static void renderStackInWorld(ItemStack stack, double posX, double posY, double posZ, float rotateX, float rotateY, float rotateZ, double scale) {
        if( renderItem == null ) {
            renderItem = getMc().getRenderItem();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotateX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotateY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotateZ, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(scale, scale, scale);

        renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

        GlStateManager.popMatrix();
    }

    public static void drawTexturedModalRect(int xPos, int yPos, float z, int u, int v, int width, int height) {
        drawTexturedModalRect(xPos, yPos, z, u, v, width, height, 0.00390625F, 0.00390625F);
    }

    public static void drawTexturedModalRect(int xPos, int yPos, float z, int u, int v, int width, int height, float resScaleX, float resScaleY) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(xPos, yPos + height, z).tex(u * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(xPos + width, yPos + height, z).tex((u + width) * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(xPos + width, yPos, z).tex((u + width) * resScaleX, v * resScaleY).endVertex();
        buffer.pos(xPos, yPos, z).tex(u * resScaleX, v * resScaleY).endVertex();
        tessellator.draw();
    }
}
