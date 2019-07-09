/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.render.IRender;
import de.sanandrew.mods.turretmod.api.client.render.IRenderInst;
import de.sanandrew.mods.turretmod.api.client.render.IRenderRegistry;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.registry.projectile.Projectiles;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class RenderProjectile
        extends Render<EntityTurretProjectile>
        implements IRenderRegistry<ResourceLocation, EntityTurretProjectile, IRender<EntityTurretProjectile>, Render<EntityTurretProjectile>>, IRenderInst<EntityTurretProjectile>
{
    private final Map<ResourceLocation, IRender<EntityTurretProjectile>> renders = new HashMap<>();

    public RenderProjectile(RenderManager renderManager) {
        super(renderManager);

        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerProjectileRenderer(this));
    }

    @Override
    public void doRender(EntityTurretProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ResourceLocation delegateId = entity.delegate.getId();
        if( this.renders.containsKey(delegateId) ) {
            this.renders.get(delegateId).doRender(this, entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTurretProjectile entity) {
        if( this.renders.containsKey(entity.delegate.getId()) ) {
            return this.renders.get(entity.delegate.getId()).getRenderTexture(entity);
        }

        return null;
    }

    @Override
    public boolean registerRender(@Nonnull ResourceLocation key, @Nonnull IRender<EntityTurretProjectile> render) {
        if( this.renders.containsKey(key) ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Cannot register renderer for projectile ID %s since it already has one.", key));
            return false;
        }

        this.renders.put(key, render);

        return true;
    }

    @Override
    public IRender<EntityTurretProjectile> removeRender(ResourceLocation key) {
        return this.renders.remove(key);
    }

    @Override
    public Render<?> getRenderer() {
        return this;
    }

    @Override
    public boolean bindRenderEntityTexture(EntityTurretProjectile entity) {
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
    public int getRenderTeamColor(EntityTurretProjectile entity) {
        return this.getTeamColor(entity);
    }

    public static <T extends Entity> void initialize(IRenderRegistry<ResourceLocation, T, IRender<T>, Render<T>> registry) {
        registry.registerRender(Projectiles.CB_BOLT.getId(), new RenderCrossbowBolt<>());
        registry.registerRender(Projectiles.PEBBLE.getId(), new RenderPebble<>());
        registry.registerRender(Projectiles.CRYO_BALL_I.getId(), new RenderNothingness<>());
        registry.registerRender(Projectiles.CRYO_BALL_II.getId(), new RenderNothingness<>());
        registry.registerRender(Projectiles.CRYO_BALL_III.getId(), new RenderNothingness<>());
        registry.registerRender(Projectiles.BULLET.getId(), new RenderBullet<>());
        registry.registerRender(Projectiles.MG_PEBBLE.getId(), new RenderPebble<>());
        registry.registerRender(Projectiles.LASER_NORMAL.getId(), new RenderNothingness<>());
        registry.registerRender(Projectiles.FLAME_NORMAL.getId(), new RenderFlame<>(false));
        registry.registerRender(Projectiles.FLAME_PURIFY.getId(), new RenderFlame<>(true));
    }
}
