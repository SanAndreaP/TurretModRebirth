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
import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public final class RenderTurretPointed
        implements ILabelRegistry
{
    public static final RenderTurretPointed INSTANCE = new RenderTurretPointed();

    private final WeakHashMap<EntityTurret, LabelEntry> labels = new WeakHashMap<>();
    private final List<ILabelElement> elements = new ArrayList<>();

    private static final float FADE_TIME = 2.0F;

    public void render(Minecraft mc, double x, double y, double z, double partTicks) {
        if( mc.player != mc.getRenderViewEntity() ) {
            return;
        }

        this.labels.forEach((turret, tp) -> {
            tp.wasActive = tp.active;
            tp.active = false;
            if( tp.wasActive ) {
                tp.endTick = ClientTickHandler.ticksInGame;
            }
        });

        if( mc.pointedEntity instanceof EntityTurret) {
            EntityTurret turret = (EntityTurret) mc.pointedEntity;
            renderTurretBB(turret, x, y, z);

            if( isItemTCU(mc.player.getHeldItemMainhand()) || isItemTCU(mc.player.getHeldItemOffhand()) ) {
                LabelEntry tp = this.labels.computeIfAbsent(turret, t -> new LabelEntry(mc.getRenderManager()));
                tp.active = true;
                if( !tp.wasActive ) {
                    tp.beginTick = tp.endTick < 0 ? ClientTickHandler.ticksInGame : tp.endTick;
                }
            }
        }

        cleanupRenderers(false);

        this.labels.forEach((turret, lbl) -> {
            if( turret != null ) {
                boolean lblActive = lbl.active;
                lbl.angleY += (-mc.getRenderManager().playerViewY - lbl.angleY) / 16.0F;
                lbl.angleX += (mc.getRenderManager().playerViewX - lbl.angleX) / 16.0F;

                if( lblActive ) {
                    if( lbl.progress < 2.0F ) {
                        lbl.progress = ((ClientTickHandler.ticksInGame - lbl.beginTick + (float) partTicks) / FADE_TIME);
                    }
                } else {
                    if( lbl.progress > 0.0F ) {
                        lbl.progress = ((lbl.endTick + FADE_TIME * 2.0F - ClientTickHandler.ticksInGame - (float) partTicks) / FADE_TIME);
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
    public void register(ILabelElement element) {
        Objects.requireNonNull(element);
        this.elements.add(element);
    }

    public void cleanupRenderers(boolean clearAll) {
        if( clearAll ) {
            this.labels.clear();
        } else {
            this.labels.entrySet().removeIf(entry -> entry.getValue() != null && !entry.getValue().active && entry.getValue().progress <= 0.0F);
        }
    }

    private static boolean isItemTCU(@Nonnull ItemStack stack) {
        return ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT);
    }

    private void renderLabel(EntityTurret turret, double x, double y, double z, LabelEntry lbl) {
        final Minecraft mc = Minecraft.getMinecraft();
        final FontRenderer fontrenderer = mc.fontRenderer;
        final float scale = 0.010F;
        final List<ILabelElement> fltElem = this.elements.stream().filter(el -> el.showElement(turret))
                                                         .sorted((el1, el2) -> Integer.compare(el2.getPriority(), el1.getPriority())).collect(Collectors.toList());

        lbl.maxWidth = fltElem.stream().collect(() -> new MutableFloat(MIN_WIDTH),
                                                (f, l) -> f.setValue(Math.max(f.getValue(), l.getWidth(turret, fontrenderer))),
                                                (f1, f2) -> f1.setValue(Math.max(f1.getValue(), f2.getValue()))).floatValue();
        lbl.maxHeight = fltElem.stream().collect(() -> new MutableFloat(0.0F),
                                                 (f, l) -> f.add(l.getHeight(turret, fontrenderer)),
                                                 (f1, f2) -> f1.add(f2.getValue())).floatValue();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + (turret.isBuoy() ? 1.4F : 0.7F), z);
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
        ColorObj clrMain = new ColorObj(0x00001000 | (Math.max(Math.round(0xA0 * alphaMulti), 4) << 24));

        // main bg
        ClientProxy.addQuad(buffer, -2.0D, -2.0D, lbl.maxWidth + 2.0D, lbl.maxHeight + 2.0D, clrMain);

        // inner frame [top, bottom, left, right]
        ClientProxy.addQuad(buffer, -3.0D,               -3.0D,                lbl.maxWidth + 3.0D, -2.0D,                clrTop);
        ClientProxy.addQuad(buffer, -3.0D,               lbl.maxHeight + 2.0D, lbl.maxWidth + 3.0D, lbl.maxHeight + 3.0D, clrBottom);
        ClientProxy.addQuad(buffer, -3.0D,               -2.0D,                -2.0D,               lbl.maxHeight + 2.0D, clrTop, clrBottom);
        ClientProxy.addQuad(buffer, lbl.maxWidth + 2.0D, -2.0D,                lbl.maxWidth + 3.0D, lbl.maxHeight + 2.0D, clrTop, clrBottom);

        // outer frame [top, bottom, left, right]
        ClientProxy.addQuad(buffer, -3.0D,               -4.0D,                lbl.maxWidth + 3.0D, -3.0D,                clrMain);
        ClientProxy.addQuad(buffer, -3.0D,               lbl.maxHeight + 3.0D, lbl.maxWidth + 3.0D, lbl.maxHeight + 4.0D, clrMain);
        ClientProxy.addQuad(buffer, -4.0D,               -3.0D,                -3.0D,               lbl.maxHeight + 3.0D, clrMain);
        ClientProxy.addQuad(buffer, lbl.maxWidth + 3.0D, -3.0D,                lbl.maxWidth + 4.0D, lbl.maxHeight + 3.0D, clrMain);

        if( lbl.progress >= 1.0F ) {
            final MutableFloat currHeight = new MutableFloat(0.0F);
            fltElem.forEach(elem -> {
                elem.renderQuads(turret, lbl.maxWidth, lbl.progress - 1.0F, fontrenderer, currHeight.floatValue(), buffer);
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
                elem.renderTextured(turret, lbl.maxWidth, lbl.progress - 1.0F, fontrenderer);
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

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        RenderGlobal.drawSelectionBoundingBox(renderBB, 0.0F, 0.0F, 0.0F, 0.4F);

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private static class LabelEntry
    {
        long beginTick;
        long endTick;
        boolean wasActive;
        boolean active;
        float angleY;
        float angleX;
        float maxHeight;
        float maxWidth;
        float progress;

        LabelEntry(RenderManager rMan) {
            this.active = true;
            this.wasActive = false;
            this.beginTick = -1;
            this.endTick = -1;
            this.angleY = -rMan.playerViewY;
            this.angleX = rMan.playerViewX;
            this.maxWidth = MIN_WIDTH;
            this.maxHeight = 0.0F;
            this.progress = 0.0F;
        }
    }
}
