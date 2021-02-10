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
import de.sanandrew.mods.turretmod.api.client.render.IRenderInst;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRender;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretBase;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretLaser;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretMinigun;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretRevolver;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretShotgun;
import de.sanandrew.mods.turretmod.client.render.layer.LayerTurretGlow;
import de.sanandrew.mods.turretmod.client.render.layer.LayerTurretUpgrades;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderTurret<E extends EntityLiving & ITurretInst>
        extends RenderLiving<E>
        implements ITurretRenderRegistry<E>, IRenderInst<E>
{
    private final Map<ITurret, ITurretRender<?, E>> turretRenders = new HashMap<>();
    private final Map<ITurret, List<LayerRenderer<E>>> turretLayers = new HashMap<>();

    public RenderTurret(RenderManager manager) {
        super(manager, new ModelBase() { }, 0.5F);

        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerTurretRenderer(this));
        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerTurretRenderLayers(this));
    }

    @Override
    public boolean register(@Nonnull ITurret key, @Nonnull ITurretRender<?, E> render) {
        if( this.turretRenders.containsKey(key) ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Cannot register renderer for turret %s since it already has one.", key.getId()));
            return false;
        }

        this.turretRenders.put(key, render);
        List<LayerRenderer<E>> layers = this.turretLayers.compute(key, (k, v) -> new ArrayList<>());
        render.addLayers(layers);

        return true;
    }

    @Override
    public ITurretRender<?, E> remove(ITurret key) {
        ITurretRender<?, E> oldRender = this.turretRenders.remove(key);
        this.turretLayers.remove(key);

        return oldRender;
    }

    @Override
    public void addUpgradeLayer(List<LayerRenderer<E>> layerList) {
        layerList.add(new LayerTurretUpgrades<>());
    }

    @Override
    public <T extends ModelBase> void addGlowLayer(List<LayerRenderer<E>> layerList, ITurretRender<T, E> render) {
        layerList.add(new LayerTurretGlow<>(this, render.getNewModel(0.005F)));
    }

    @Override
    public void addCustomLayer(ITurret turret, LayerRenderer<E> layer) {
        if( turret == null ) {
            TurretRegistry.INSTANCE.getObjects().forEach(t -> this.addCustomLayer(t, layer));
        } else {
            this.turretLayers.get(turret).add(layer);
        }
    }

    @Override
    public void doRender(E entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ITurret turret = entity.getTurret();
        ITurretRender<?, E> render = this.turretRenders.get(turret);

        if( render != null ) {
            this.mainModel = render.getModel();

            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            render.doRender(this, entity, x, y, z, entityYaw, partialTicks);

            renderTurretRange(entity, x, y, z);
        }
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
    protected boolean setScoreTeamColor(E entityLivingBaseIn) {
        return super.setScoreTeamColor(entityLivingBaseIn);
    }

    @Override
    protected boolean setBrightness(E entitylivingbaseIn, float partialTicks, boolean combineTextures) {
        return super.setBrightness(entitylivingbaseIn, partialTicks, combineTextures);
    }

    @Override
    protected void applyRotations(E turret, float x, float y, float z) {
        super.applyRotations(turret, x, y, z);
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

            float[] prevBright = ClientProxy.forceGlow();

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

            ClientProxy.resetGlow(prevBright);

            GlStateManager.enableTexture2D();
        }
    }

    @Override
    public RenderLivingBase<? extends EntityLiving> getRenderer() {
        return this;
    }

    public static <T extends EntityLiving & ITurretInst> void initialize(ITurretRenderRegistry<T> registry) {
        registry.register(Turrets.CROSSBOW, new TurretRenderBase<>(registry, ModelTurretBase::new));
        registry.register(Turrets.HARPOON, new TurretRenderBase<>(registry, scale -> new ModelTurretBase(scale, Resources.TURRET_T1_BASE_BUOY.resource)));
        registry.register(Turrets.SHOTGUN, new TurretRenderBase<>(registry, ModelTurretShotgun::new));
        registry.register(Turrets.CRYOLATOR, new TurretRenderBase<>(registry, ModelTurretBase::new));
        registry.register(Turrets.REVOLVER, new TurretRenderBase<>(registry, ModelTurretRevolver::new));
        registry.register(Turrets.MINIGUN, new TurretRenderBase<>(registry, ModelTurretMinigun::new));
        registry.register(Turrets.LASER, new TurretRenderBase<>(registry, ModelTurretLaser::new));
        registry.register(Turrets.FLAMETHROWER, new TurretRenderBase<>(registry, scale -> new ModelTurretBase(scale, Resources.TURRET_T3_FTHROWER_MODEL.resource)));
        registry.register(Turrets.FORCEFIELD, new TurretRenderShieldGen<>(registry));
    }

    public static <T extends EntityLiving & ITurretInst> void initializeLayers(ITurretRenderRegistry<T> registry) { }

    @Override
    public boolean bindRenderEntityTexture(E entity) {
        return this.bindEntityTexture(entity);
    }

    @Override
    public boolean renderOutlines() {
        return this.renderOutlines;
    }

    @Override
    public Render<?> getRender() {
        return this;
    }

    @Override
    public int getRenderTeamColor(E entity) {
        return this.getTeamColor(entity);
    }
}
