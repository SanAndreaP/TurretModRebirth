/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretBase;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretLaser;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretMinigun;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretRevolver;
import de.sanandrew.mods.turretmod.client.model.entity.ModelTurretShotgun;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class TurretRenderer<E extends LivingEntity & ITurretInst>
        extends LivingRenderer<E, EntityModel<E>>
{
    private final Map<ITurret, TurretRenderBase<E, ?>> delegates = new HashMap<>();

    public void register(ITurret turret, TurretRenderBase<E, ?> renderer) {
        this.delegates.put(turret, renderer);
    }

    public TurretRenderer(EntityRendererManager manager) {
        super(manager, new EmptyModel<>(), 0.5F);
    }

    @Override
    public void render(E turretInst, float yaw, float partialTicks, @Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light) {
        this.delegates.computeIfPresent(turretInst.getTurret(), (t, r) -> {
            r.render(turretInst, yaw, partialTicks, stack, buffer, light);
            return r;
        });
    }

    @Nonnull
    @Override
    @Deprecated
    public ResourceLocation getEntityTexture(@Nonnull E turretInst) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    protected boolean canRenderName(@Nonnull E entity) {
        return false;
    }

    public void initialize() {
        register(Turrets.CROSSBOW, new TurretRenderBase<>(this.renderManager, ModelTurretBase::new));
        register(Turrets.HARPOON, new TurretRenderBase<>(this.renderManager, () -> new ModelTurretBase<>(Resources.TURRET_T1_BASE_BUOY.resource)));
        register(Turrets.SHOTGUN, new TurretRenderBase<>(this.renderManager, ModelTurretShotgun::new));
        register(Turrets.CRYOLATOR, new TurretRenderBase<>(this.renderManager, ModelTurretBase::new));
        register(Turrets.REVOLVER, new TurretRenderBase<>(this.renderManager, ModelTurretRevolver::new));
        register(Turrets.MINIGUN, new TurretRenderBase<>(this.renderManager, ModelTurretMinigun::new));
        register(Turrets.LASER, new TurretRenderBase<>(this.renderManager, ModelTurretLaser::new));
        register(Turrets.FLAMETHROWER, new TurretRenderBase<>(this.renderManager, () -> new ModelTurretBase<>(Resources.TURRET_T3_FTHROWER_MODEL.resource)));
        register(Turrets.FORCEFIELD, new TurretRenderShieldGen<>(this.renderManager));
    }

    private static final class EmptyModel<E extends LivingEntity>
            extends EntityModel<E>
    {
        @Override
        public void setRotationAngles(@Nonnull E entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { }

        @Override
        public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { }
    }
}
