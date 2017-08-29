/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenerRegistry;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretRender;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretNew;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderTurret<E extends EntityLiving & ITurretInst>
        extends RenderLiving<E>
        implements ITurretRenerRegistry
{
    private static final ModelIntern NULL_MODEL = new ModelIntern();
    private static final EmptyRender NULL_RENDER = new EmptyRender();

    private final Map<ITurret, ITurretRender<?>> turretRenders = new HashMap<>();
    private final Map<ITurret, List<LayerRenderer<E>>> turretLayers = new HashMap<>();

    public RenderTurret(RenderManager manager) {
        super(manager, NULL_MODEL, 0.5F);

//        this.addLayer(new LayerTurretGlow<>(this, standardModel.getNewInstance(0.001F)));
//
        this.addLayer(new LayerTurretUpgrades<>());
    }

    @Override
    public <T extends ModelBase> boolean registerRender(@Nonnull ITurret turret, @Nonnull ITurretRender<T> render) {
        if( this.turretRenders.containsKey(turret) ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Cannot register renderer for turret %s since it already has one.", turret.getName()));
            return false;
        }

        this.turretRenders.put(turret, render);
        List<LayerRenderer<E>> layers = this.turretLayers.compute(turret, (key, val) -> new ArrayList<>());
        render.addLayers(layers);

        return true;
    }

    @Override
    public ITurretRender<?> removeRender(ITurret turret) {
        ITurretRender<?> oldRender = this.turretRenders.remove(turret);
        this.turretLayers.remove(turret);

        return oldRender;
    }

    @Override
    public <T extends ModelBase> void addStandardLayers(List<LayerRenderer<?>> layerList, ITurretRender<T> render) {
        layerList.add(new LayerTurretGlow<>(this, render.getNewModel(0.001F)));
        layerList.add(new LayerTurretUpgrades<>());
    }

    @Override
    public void doRender(E entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ITurret turret = entity.getTurret();
        this.mainModel = turretRenders.getOrDefault(turret, NULL_RENDER).getModel();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        turretRenders.getOrDefault(turret, NULL_RENDER).doRender(entity, x, y, z, entityYaw, partialTicks);

        renderTurretRange(entity, x, y, z);
    }

    @Override
    protected void renderLayers(E entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
                                float scaleIn)
    {
        if( this.turretLayers.containsKey(entity.getTurret()) ) {
            this.turretLayers.get(entity.getTurret()).forEach((layer) -> {
                boolean hasBrightnessSet = this.setBrightness(entity, partialTicks, layer.shouldCombineTextures());
                layer.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);

                if( hasBrightnessSet ) {
                    this.unsetBrightness();
                }
            });
        }
    }

    @Override
    protected void applyRotations(E turret, float x, float y, float z) {
        super.applyRotations(turret, x, y, z);

        if( turret.isUpsideDown() ) {
            GlStateManager.translate(0.0F, turret.height + 0.2F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(E entity) {
        return entity.getTurret().getStandardTexture(entity);
    }

    @Override
    protected boolean canRenderName(E entity) {
        return false;
    }

    private static void renderTurretRange(ITurretInst turret, double x, double y, double z) {
        if( turret.showRange() ) {
            GlStateManager.disableTexture2D();

            float prevBrightX = OpenGlHelper.lastBrightnessX;
            float prevBrightY = OpenGlHelper.lastBrightnessY;
            int brightness = 0xF0;
            int brightX = brightness % 65536;
            int brightY = brightness / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            AxisAlignedBB aabb = turret.getTargetProcessor().getAdjustedRange(false);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.glLineWidth(5.0F);
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(0, aabb.minY, 0).color(0, 0, 0, 255).endVertex();
            buf.pos(0, aabb.maxY, 0).color(0, 0, 0, 255).endVertex();
            tess.draw();
            GlStateManager.glLineWidth(3.0F);
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(0, aabb.minY, 0).color(255, 255, 255, 255).endVertex();
            buf.pos(0, aabb.maxY, 0).color(255, 255, 255, 255).endVertex();
            tess.draw();

            GlStateManager.glLineWidth(3.0F);
            for( double cx = aabb.minX; cx <= aabb.maxX; cx += 0.5F ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                buf.pos(cx, aabb.minY, aabb.minZ).color(0, 0, 0, 255).endVertex();
                buf.pos(cx, aabb.minY, aabb.maxZ).color(0, 0, 0, 255).endVertex();
                buf.pos(cx, aabb.maxY, aabb.maxZ).color(0, 0, 0, 255).endVertex();
                buf.pos(cx, aabb.maxY, aabb.minZ).color(0, 0, 0, 255).endVertex();
                tess.draw();
            }
            for( double cy = aabb.minY; cy <= aabb.maxY; cy += 0.5F ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                buf.pos(aabb.minX, cy, aabb.minZ).color(0, 0, 0, 255).endVertex();
                buf.pos(aabb.minX, cy, aabb.maxZ).color(0, 0, 0, 255).endVertex();
                buf.pos(aabb.maxX, cy, aabb.maxZ).color(0, 0, 0, 255).endVertex();
                buf.pos(aabb.maxX, cy, aabb.minZ).color(0, 0, 0, 255).endVertex();
                tess.draw();
            }
            for( double cz = aabb.minZ; cz <= aabb.maxZ; cz += 0.5F ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                buf.pos(aabb.minX, aabb.minY, cz).color(0, 0, 0, 255).endVertex();
                buf.pos(aabb.minX, aabb.maxY, cz).color(0, 0, 0, 255).endVertex();
                buf.pos(aabb.maxX, aabb.maxY, cz).color(0, 0, 0, 255).endVertex();
                buf.pos(aabb.maxX, aabb.minY, cz).color(0, 0, 0, 255).endVertex();
                tess.draw();
            }
            GlStateManager.glLineWidth(0.1F);
            for( double cx = aabb.minX; cx <= aabb.maxX; cx += 0.5F ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                buf.pos(cx, aabb.minY, aabb.minZ).color(255, 255, 255, 255).endVertex();
                buf.pos(cx, aabb.minY, aabb.maxZ).color(255, 255, 255, 255).endVertex();
                buf.pos(cx, aabb.maxY, aabb.maxZ).color(255, 255, 255, 255).endVertex();
                buf.pos(cx, aabb.maxY, aabb.minZ).color(255, 255, 255, 255).endVertex();
                tess.draw();
            }
            for( double cy = aabb.minY; cy <= aabb.maxY; cy += 0.5F ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                buf.pos(aabb.minX, cy, aabb.minZ).color(255, 255, 255, 255).endVertex();
                buf.pos(aabb.minX, cy, aabb.maxZ).color(255, 255, 255, 255).endVertex();
                buf.pos(aabb.maxX, cy, aabb.maxZ).color(255, 255, 255, 255).endVertex();
                buf.pos(aabb.maxX, cy, aabb.minZ).color(255, 255, 255, 255).endVertex();
                tess.draw();
            }
            for( double cz = aabb.minZ; cz <= aabb.maxZ; cz += 0.5F ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                buf.pos(aabb.minX, aabb.minY, cz).color(255, 255, 255, 255).endVertex();
                buf.pos(aabb.minX, aabb.maxY, cz).color(255, 255, 255, 255).endVertex();
                buf.pos(aabb.maxX, aabb.maxY, cz).color(255, 255, 255, 255).endVertex();
                buf.pos(aabb.maxX, aabb.minY, cz).color(255, 255, 255, 255).endVertex();
                tess.draw();
            }
            GlStateManager.popMatrix();

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);

            GlStateManager.enableTexture2D();
        }
    }

    @Override
    public RenderLivingBase<? extends EntityLiving> getRenderer() {
        return this;
    }

    private static class ModelIntern extends ModelBase {

    }

    private static class EmptyRender implements ITurretRender<ModelIntern> {
        @Override
        public ModelIntern getNewModel(float scale) {
            return new ModelIntern();
        }

        @Override
        public ModelIntern getModel() {
            return RenderTurret.NULL_MODEL;
        }
    }
}
