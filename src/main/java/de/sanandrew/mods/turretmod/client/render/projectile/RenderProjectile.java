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
import java.util.UUID;

public class RenderProjectile
        extends Render<EntityTurretProjectile>
        implements IRenderRegistry<UUID, Entity, IRender<Entity>, Render<Entity>>, IRenderInst<Entity>
{
    private final Map<UUID, IRender<Entity>> renders = new HashMap<>();

    public RenderProjectile(RenderManager renderManager) {
        super(renderManager);

        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerProjectileRenderer(this));
    }

    @Override
    public void doRender(EntityTurretProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if( this.renders.containsKey(entity.delegate.getId()) ) {
            this.renders.get(entity.delegate.getId()).doRender(this, entity, x, y, z, entityYaw, partialTicks);
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
    public boolean registerRender(@Nonnull UUID key, @Nonnull IRender<Entity> render) {
        if( this.renders.containsKey(key) ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Cannot register renderer for projectile ID %s since it already has one.", key));
            return false;
        }

        this.renders.put(key, render);

        return true;
    }

    @Override
    public IRender<Entity> removeRender(UUID key) {
        return this.renders.remove(key);
    }

    @Override
    public Render<?> getRenderer() {
        return this;
    }

    @Override
    public boolean bindRenderEntityTexture(Entity entity) {
        return this.bindEntityTexture((EntityTurretProjectile) entity);
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
    public int getRenderTeamColor(Entity entity) {
        return this.getTeamColor((EntityTurretProjectile) entity);
    }

    public static <T extends Entity> void initialize(IRenderRegistry<UUID, Entity, ?, ?> registry) {
//        registry.registerRender(Turrets.CROSSBOW, new TurretRenderBase<>(registry, ModelTurretBase::new));
//        registry.registerRender(Turrets.SHOTGUN, new TurretRenderBase<>(registry, ModelTurretShotgun::new));
//        registry.registerRender(Turrets.CRYOLATOR, new TurretRenderBase<>(registry, ModelTurretBase::new));
//        registry.registerRender(Turrets.REVOLVER, new TurretRenderBase<>(registry, ModelTurretRevolver::new));
//        registry.registerRender(Turrets.MINIGUN, new TurretRenderBase<>(registry, ModelTurretMinigun::new));
//        registry.registerRender(Turrets.LASER, new TurretRenderBase<>(registry, ModelTurretLaser::new));
//        registry.registerRender(Turrets.FLAMETHROWER, new TurretRenderBase<>(registry, ModelTurretFlamethrower::new));
//        registry.registerRender(Turrets.SHIELDGEN, new TurretRenderShieldGen<>(registry));
    }
}
