/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.renderer.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretBase;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TurretRenderer<E extends LivingEntity & ITurretEntity>
        extends LivingRenderer<E, EntityModel<E>>
{
    private final Map<ITurret, LivingRenderer<E, ?>> delegates = new HashMap<>();

    public void register(ITurret turret, LivingRenderer<E, ?> renderer) {
        this.delegates.put(turret, renderer);
    }

    public TurretRenderer(EntityRendererManager manager) {
        super(manager, new EmptyModel<>(), 0.5F);

        this.initialize();
    }

    @Override
    public void render(E turretInst, float yaw, float partialTicks, @Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light) {
        this.delegates.computeIfPresent(turretInst.getDelegate(), (t, r) -> {
            r.render(turretInst, yaw, partialTicks, stack, buffer, light);
            return r;
        });
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull E turretInst) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }

    public void initialize() {
        for( ITurret turret : TurretRegistry.INSTANCE.getAll() ) {
            Function<ResourceLocation, EntityModel<E>> s;
            LivingRenderer<E, EntityModel<E>> trb;

            String customModelClassName = turret.getCustomModelClass();
            if( customModelClassName != null ) {
                try {
                    final Constructor<?> c = Class.forName(customModelClassName).getConstructor(ResourceLocation.class);
                    s = modelLocation -> {
                        try {
                            return ReflectionUtils.getCasted(c.newInstance(modelLocation));
                        } catch( InstantiationException | IllegalAccessException | InvocationTargetException e ) {
                            throw new RuntimeException(e);
                        }
                    };
                } catch( NoSuchMethodException | ClassNotFoundException e ) {
                    throw new RuntimeException(e);
                }
            } else {
                s = ModelTurretBase::new;
            }


            String customRenderClassName = turret.getCustomRenderClass();
            if( customRenderClassName != null ) {
                try {
                    final Constructor<?> c = Class.forName(customRenderClassName).getConstructor(EntityRendererManager.class, Supplier.class);
                    trb = ReflectionUtils.getCasted(c.newInstance(this.entityRenderDispatcher, (Supplier<? extends EntityModel<?>>) () -> s.apply(turret.getModelLocation())));
                } catch( NoSuchMethodException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e ) {
                    throw new RuntimeException(e);
                }
            } else {
                trb = new TurretRenderBase<>(this.entityRenderDispatcher, () -> s.apply(turret.getModelLocation()));
            }

            register(turret, trb);
        }
    }

    private static final class EmptyModel<E extends LivingEntity>
            extends EntityModel<E>
    {
        @Override
        public void setupAnim(@Nonnull E entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { }

        @Override
        public void renderToBuffer(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { }
    }
}
