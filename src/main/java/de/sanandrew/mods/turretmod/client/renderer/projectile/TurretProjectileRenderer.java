package de.sanandrew.mods.turretmod.client.renderer.projectile;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileInst;
import de.sanandrew.mods.turretmod.entity.projectile.ProjectileRegistry;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class TurretProjectileRenderer<E extends Entity & IProjectileInst>
        extends EntityRenderer<E>
{
    private final Map<IProjectile, EntityRenderer<E>> delegates = new HashMap<>();

    public void register(IProjectile projectile, EntityRenderer<E> renderer) {
        this.delegates.put(projectile, renderer);
    }

    public TurretProjectileRenderer(EntityRendererManager manager) {
        super(manager);

        this.initialize();
    }

    @Override
    public void render(E projectileInst, float yaw, float partialTicks, @Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light) {
        this.delegates.computeIfPresent(projectileInst.getProjectile(), (t, r) -> {
            r.render(projectileInst, yaw, partialTicks, stack, buffer, light);
            return r;
        });
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull E turretInst) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }

    @Override
    protected boolean shouldShowName(@Nonnull E entity) {
        return false;
    }

    public void initialize() {
        for( IProjectile projectile : ProjectileRegistry.INSTANCE.getAll() ) {
            EntityRenderer<E> prb;

            String customRenderClassName = projectile.getCustomRenderClass();
            if( customRenderClassName != null ) {
                try {
                    final Constructor<?> c = Class.forName(customRenderClassName).getConstructor(EntityRendererManager.class);
                    prb = ReflectionUtils.getCasted(c.newInstance(this.entityRenderDispatcher));
                } catch( NoSuchMethodException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e ) {
                    throw new RuntimeException(e);
                }
            } else {
                prb = new TurretProjectileBaseRenderer<>(this.entityRenderDispatcher);
            }

            register(projectile, prb);
        }
    }
}
