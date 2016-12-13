/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.world;

import de.sanandrew.mods.sanlib.lib.client.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public final class RenderTurretPointed
{
    static WeakHashMap<EntityTurret, LabelEntry> labels = new WeakHashMap<>();

    public static void render(Minecraft mc, double x, double y, double z, double partTicks) {
        labels.forEach((turret, tp) -> tp.active = false);

        if( mc.pointedEntity instanceof EntityTurret ) {
            renderTurretBB((EntityTurret) mc.pointedEntity, x, y, z);

            if( isItemTCU(mc.player.getHeldItemMainhand()) || isItemTCU(mc.player.getHeldItemOffhand()) ) {
                EntityTurret turret = (EntityTurret) mc.pointedEntity;
                LabelEntry tp = labels.get(turret);
                if( tp != null ) {
                    tp.active = true;
                } else {
                    labels.put(turret, new LabelEntry(mc.getRenderManager()));
                }
            }
        }

        labels.entrySet().removeIf(entry -> entry.getValue() != null && !entry.getValue().active && entry.getValue().progress <= 0.0F);

        labels.forEach((turret, lbl) -> {
            if( turret != null ) {
                boolean lblActive = lbl.active;

                if( lblActive ) {
                    lbl.minHeight = -2.0F;
                    if( lbl.progress < 1.5F ) {
                        lbl.progress += 0.05F;
                    } else {
                        lbl.angleY += (-mc.getRenderManager().playerViewY - lbl.angleY) / 16.0F;
                        lbl.angleX += (mc.getRenderManager().playerViewX - lbl.angleX) / 16.0F;
                    }
                    if( lbl.progress <= 1.0F ) {
                        lbl.maxHeight = -2.0F + lbl.progress * 68.0F;
                    } else {
                        lbl.maxHeight = 66.0F;
                    }
                } else {
                    lbl.maxHeight = 66.0F;
                    if( lbl.progress > 0.0F ) {
                        lbl.progress -= 0.05F;
                        lblActive = true;
                    }
                    if( lbl.progress <= 1.0F ) {
                        lbl.minHeight = 66.0F - lbl.progress * 68.0F;
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

    private static boolean isItemTCU(ItemStack stack) {
        return ItemStackUtils.isItem(stack, ItemRegistry.turret_control_unit);
    }

    private static void renderLabel(EntityTurret turret, double x, double y, double z, LabelEntry lbl) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontrenderer = mc.fontRendererObj;
        float scale = 0.010F;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + (turret.isUpsideDown ? 1.4F : 0.7F), z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lbl.angleY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lbl.angleX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.translate(-64.0D, -32.0D, 0.0D);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addQuad(buffer, 0.0D, Math.max(0.0D, lbl.minHeight), 128.0D, Math.min(64.0D, lbl.maxHeight), new ColorObj(0x80001000));

        addQuad(buffer, -1.0D, Math.max(-1.0D, lbl.minHeight), 129.0D, Math.min(0.0D, lbl.maxHeight), new ColorObj(0xCC50FF00));
        addQuad(buffer, -1.0D, Math.max(64.0D, lbl.minHeight), 129.0D, Math.min(65.0D, lbl.maxHeight), new ColorObj(0xCC288000));
        addQuad(buffer, -1.0D, Math.max(0.0D, lbl.minHeight), 0.0D, Math.min(64.0D, lbl.maxHeight), new ColorObj(0xCC50FF00), new ColorObj(0xCC288000));
        addQuad(buffer, 128.0D, Math.max(0.0D, lbl.minHeight), 129.0D, Math.min(64.0D, lbl.maxHeight), new ColorObj(0xCC50FF00), new ColorObj(0xCC288000));

        addQuad(buffer, -1.0D, Math.max(-2.0D, lbl.minHeight), 129.0D, Math.min(-1.0D, lbl.maxHeight), new ColorObj(0x80001000));
        addQuad(buffer, -1.0D, Math.max(65.0D, lbl.minHeight), 129.0D, Math.min(66.0D, lbl.maxHeight), new ColorObj(0x80001000));
        addQuad(buffer, -2.0D, Math.max(-1.0D, lbl.minHeight), -1.0D, Math.min(65.0D, lbl.maxHeight), new ColorObj(0x80001000));
        addQuad(buffer, 129.0D, Math.max(-1.0D, lbl.minHeight), 130.0D, Math.min(65.0D, lbl.maxHeight), new ColorObj(0x80001000));

        float txtAlpha = Math.min(0.1F + Math.max(0.0F, (lbl.progress - 1.0F) * 2.0F), 1.0F);

        if( lbl.progress >= 1.0F ) {
            float healthRel = turret.getHealth() / turret.getMaxHealth();
            float ammoRel = turret.getTargetProcessor().getAmmoCount() / (float)turret.getTargetProcessor().getMaxAmmoCapacity();
            ColorObj clrBkg = new ColorObj(0.0F, 0.0F, 0.0F, txtAlpha);
            //health
            addQuad(buffer, (1.0D + 126.0D * healthRel), 22.0D, 127.0D, 24.0D, clrBkg);
            addQuad(buffer, 1.0D, 22.0D, (1.0D + 126.0D * healthRel), 24.0D, new ColorObj(1.0F, 0.0F, 0.0F, txtAlpha));

            //turret_ammo
            addQuad(buffer, (1.0D + 126.0D * ammoRel), 38.0D, 127.0D, 40.0D, clrBkg);
            addQuad(buffer, 1.0D, 38.0D, (1.0D + 126.0D * ammoRel), 40.0D, new ColorObj(0.625F, 0.625F, 1.0F, txtAlpha));
        }

        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();

        if( lbl.progress >= 1.0F ) {
            String s = turret.hasCustomName() ? turret.getCustomNameTag() : Lang.translateEntityCls(turret.getClass());
            fontrenderer.drawString(s, 1, 1, new ColorObj(1.0F, 1.0F, 1.0F, txtAlpha).getColorInt());
            s = String.format("Health: %.2f/%.2f", turret.getHealth(), turret.getMaxHealth());
            fontrenderer.drawString(s, 1, 12, new ColorObj(1.0F, 0.5F, 0.5F, txtAlpha).getColorInt());
            s = String.format("Ammo: %d/%d", turret.getTargetProcessor().getAmmoCount(), turret.getTargetProcessor().getMaxAmmoCapacity());
            fontrenderer.drawString(s, 1, 28, new ColorObj(0.625F, 0.625F, 1.0F, txtAlpha).getColorInt());
            s = String.format("Target: %s", turret.getTargetProcessor().getTarget() == null ? "n/a" : Lang.translateEntityCls(turret.getTargetProcessor().getTarget().getClass()));
            fontrenderer.drawString(s, 1, 44, new ColorObj(1.0F, 1.0F, 0.625F, txtAlpha).getColorInt());
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
        VertexBuffer buf = tess.getBuffer();

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

    private static void addLine(VertexBuffer buf, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, ColorObj clr) {
        buf.pos(minX, minY, minZ).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, maxY, maxZ).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
    }

    private static void addQuad(VertexBuffer buf, double minX, double minY, double maxX, double maxY, ColorObj clr1, ColorObj clr2) {
        buf.pos(minX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
        buf.pos(minX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
    }

    private static void addQuad(VertexBuffer buf, double minX, double minY, double maxX, double maxY, ColorObj clr) {
        addQuad(buf, minX, minY, maxX, maxY, clr, clr);
    }

    private static class LabelEntry
    {
        protected boolean active;
        protected float angleY;
        protected float angleX;
        protected float minHeight;
        protected float maxHeight;
        protected float progress;

        protected LabelEntry(RenderManager rMan) {
            this.active = true;
            this.angleY = -rMan.playerViewY;
            this.angleX = rMan.playerViewX;
            this.minHeight = -2.0F;
            this.maxHeight = 66.0F;
            this.progress = 0.0F;
        }
    }
}
