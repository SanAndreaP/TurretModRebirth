/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.world;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelElement;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public final class RenderTurretPointed
        implements ILabelRegistry
{
    public static final RenderTurretPointed INSTANCE = new RenderTurretPointed();

    private final WeakHashMap<EntityTurret, LabelEntry> labels = new WeakHashMap<>();
    private final List<ILabelElement> elements = new ArrayList<>();

    public void render(Minecraft mc, double x, double y, double z, double partTicks) {
        if( mc.player != mc.getRenderViewEntity() ) {
            return;
        }

        labels.forEach((turret, tp) -> tp.active = false);

        if( mc.pointedEntity instanceof EntityTurret) {
            EntityTurret turret = (EntityTurret) mc.pointedEntity;
            renderTurretBB(turret, x, y, z);

            if( isItemTCU(mc.player.getHeldItemMainhand()) || isItemTCU(mc.player.getHeldItemOffhand()) ) {
                LabelEntry tp = labels.computeIfAbsent(turret, t -> new LabelEntry(mc.getRenderManager()));
                tp.active = true;
            }
        }

        cleanupRenderers(false);

        labels.forEach((turret, lbl) -> {
            if( turret != null ) {
                boolean lblActive = lbl.active;
                lbl.angleY += (-mc.getRenderManager().playerViewY - lbl.angleY) / 16.0F;
                lbl.angleX += (mc.getRenderManager().playerViewX - lbl.angleX) / 16.0F;

                if( lblActive ) {
                    if( lbl.progress < 2.0F ) {
                        lbl.progress += 0.01F;
                    }
                } else {
                    if( lbl.progress > 0.0F ) {
                        lbl.progress -= 0.01F;
                        lblActive = true;
                    }
                }

                if( lblActive ) {
                    double entityX = turret.lastTickPosX + (turret.posX - turret.lastTickPosX) * partTicks;
                    double entityY = turret.lastTickPosY + (turret.posY - turret.lastTickPosY) * partTicks;
                    double entityZ = turret.lastTickPosZ + (turret.posZ - turret.lastTickPosZ) * partTicks;
                    renderLabel(turret, entityX - x, entityY - y, entityZ - z, lbl);
                }
            }
        });
    }

    @Override
    public void registerLabelElement(ILabelElement element) {
        Objects.requireNonNull(element);
        this.elements.add(element);
    }

    public void cleanupRenderers(boolean clearAll) {
        if( clearAll ) {
            labels.clear();
        } else {
            labels.entrySet().removeIf(entry -> entry.getValue() != null && !entry.getValue().active && entry.getValue().progress <= 0.0F);
        }
    }

    private static boolean isItemTCU(@Nonnull ItemStack stack) {
        return ItemStackUtils.isItem(stack, ItemRegistry.turret_control_unit);
    }

    private void renderLabel(EntityTurret turret, double x, double y, double z, LabelEntry lbl) {
        final Minecraft mc = Minecraft.getMinecraft();
        final FontRenderer fontrenderer = mc.fontRenderer;
        final float scale = 0.010F;
        final List<ILabelElement> fltElem = this.elements.stream().filter(el -> el.showElement(turret)).collect(Collectors.toList());

        lbl.maxWidth = fltElem.stream().collect(() -> new MutableFloat(MIN_WIDTH),
                                                (f, l) -> f.setValue(Math.max(f.getValue(), l.getWidth(turret, fontrenderer))),
                                                (f1, f2) -> f1.setValue(Math.max(f1.getValue(), f2.getValue()))).floatValue();
        lbl.maxHeight = fltElem.stream().collect(() -> new MutableFloat(0.0F),
                                                 (f, l) -> f.add(l.getHeight(turret, fontrenderer)),
                                                 (f1, f2) -> f1.add(f2.getValue())).floatValue();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + (turret.isUpsideDown() ? 1.4F : 0.7F), z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lbl.angleY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lbl.angleX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.translate(-lbl.maxWidth / 2.0D, -32.0D, 0.0D);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        float alphaMulti = Math.min(1.0F, lbl.progress);
        ColorObj clrTop = new ColorObj(0x0050FF00 | (Math.max(Math.round(0xCC * alphaMulti), 4) << 24));
        ColorObj clrBottom = new ColorObj(0x00288000 | (Math.max(Math.round(0xCC * alphaMulti), 4) << 24));
        ColorObj clrMain = new ColorObj(0x00001000 | (Math.max(Math.round(0x80 * alphaMulti), 4) << 24));

        // main bg
        addQuad(buffer, 0.0D, 0.0D, lbl.maxWidth, lbl.maxHeight, clrMain);

        // inner frame [top, bottom, left, right]
        addQuad(buffer, -1.0D,        -1.0D,         lbl.maxWidth + 1.0D, 0.0D,                 clrTop);
        addQuad(buffer, -1.0D,        lbl.maxHeight, lbl.maxWidth + 1.0D, lbl.maxHeight + 1.0D, clrBottom);
        addQuad(buffer, -1.0D,        0.0D,          0.0D,                lbl.maxHeight,        clrTop, clrBottom);
        addQuad(buffer, lbl.maxWidth, 0.0D,          lbl.maxWidth + 1.0D, lbl.maxHeight,        clrTop, clrBottom);

        // outer frame [top, bottom, left, right]
        addQuad(buffer, -1.0D,               -2.0D,                lbl.maxWidth + 1.0D, -1.0D,                clrMain);
        addQuad(buffer, -1.0D,               lbl.maxHeight + 1.0D, lbl.maxWidth + 1.0D, lbl.maxHeight + 2.0D, clrMain);
        addQuad(buffer, -2.0D,               -1.0D,                -1.0D,               lbl.maxHeight + 1.0D, clrMain);
        addQuad(buffer, lbl.maxWidth + 1.0D, -1.0D,                lbl.maxWidth + 2.0D, lbl.maxHeight + 1.0D, clrMain);

        if( lbl.progress >= 1.0F ) {
            final MutableFloat currHeight = new MutableFloat(0.0F);
            fltElem.forEach(elem -> {
                elem.doRenderQuads(turret, lbl.maxWidth, lbl.progress - 1.0F, fontrenderer, currHeight.floatValue(), buffer);
                currHeight.add(elem.getHeight(turret, fontrenderer));
            });
        }

        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();

        if( lbl.progress >= 1.0F ) {
            final MutableFloat currHeight = new MutableFloat(0.0F);
            fltElem.forEach(elem -> {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, currHeight.floatValue(), 0.0F);
                elem.doRenderTextured(turret, lbl.maxWidth, lbl.progress - 1.0F, fontrenderer);
                GlStateManager.popMatrix();
                currHeight.add(elem.getHeight(turret, fontrenderer));
            });
        }

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void renderTurretBB(EntityTurret turret, double renderX, double renderY, double renderZ) {
        AxisAlignedBB renderBB = turret.getEntityBoundingBox().offset(-renderX, -renderY, -renderZ);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();

        GlStateManager.glLineWidth(1.0F);
        buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        addLine(buf, renderBB.minX, renderBB.minY, renderBB.minZ, renderBB.minX, renderBB.maxY, renderBB.minZ, new ColorObj(0xFF000000));
        addLine(buf, renderBB.maxX, renderBB.maxY, renderBB.minZ, renderBB.maxX, renderBB.minY, renderBB.minZ, new ColorObj(0xFF000000));
        addLine(buf, renderBB.maxX, renderBB.minY, renderBB.maxZ, renderBB.maxX, renderBB.maxY, renderBB.maxZ, new ColorObj(0xFF000000));
        addLine(buf, renderBB.minX, renderBB.maxY, renderBB.maxZ, renderBB.minX, renderBB.minY, renderBB.maxZ, new ColorObj(0xFF000000));

        addLine(buf, renderBB.minX, renderBB.minY, renderBB.minZ, renderBB.minX, renderBB.maxY, renderBB.minZ, new ColorObj(0xFF000000));
        addLine(buf, renderBB.minX, renderBB.maxY, renderBB.maxZ, renderBB.minX, renderBB.minY, renderBB.maxZ, new ColorObj(0xFF000000));
        addLine(buf, renderBB.maxX, renderBB.minY, renderBB.maxZ, renderBB.maxX, renderBB.maxY, renderBB.maxZ, new ColorObj(0xFF000000));
        addLine(buf, renderBB.maxX, renderBB.maxY, renderBB.minZ, renderBB.maxX, renderBB.minY, renderBB.minZ, new ColorObj(0xFF000000));
        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void addLine(BufferBuilder buf, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, ColorObj clr) {
        buf.pos(minX, minY, minZ).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, maxY, maxZ).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
    }

    private static void addQuad(BufferBuilder buf, double minX, double minY, double maxX, double maxY, ColorObj clr1, ColorObj clr2) {
        buf.pos(minX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
        buf.pos(minX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
    }

    private static void addQuad(BufferBuilder buf, double minX, double minY, double maxX, double maxY, ColorObj clr) {
        addQuad(buf, minX, minY, maxX, maxY, clr, clr);
    }

    private static class LabelEntry
    {
        protected boolean active;
        protected float angleY;
        protected float angleX;
        protected float maxHeight;
        protected float maxWidth;
        protected float progress;

        protected LabelEntry(RenderManager rMan) {
            this.active = true;
            this.angleY = -rMan.playerViewY;
            this.angleX = rMan.playerViewX;
            this.maxWidth = MIN_WIDTH;
            this.maxHeight = 0.0F;
            this.progress = 0.0F;
        }
    }
}
