/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.projectile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class TurretProjectileBaseRenderer<E extends Entity & IProjectileEntity>
        extends EntityRenderer<E>
{
    public TurretProjectileBaseRenderer(EntityRendererManager manager) {
        super(manager);
    }

    public void render(E projectileInst, float yaw, float partialTicks, MatrixStack mStack, @Nonnull IRenderTypeBuffer buffer, int light) {
        float scale = 0.05625F / 2.0F;

        mStack.pushPose();

        mStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, projectileInst.yRotO, projectileInst.yRot) - 90.0F));
        mStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, projectileInst.xRotO, projectileInst.xRot)));
        mStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
        mStack.scale(scale, scale, scale);
        mStack.translate(-4.0D, 0.0D, 0.0D);

        IVertexBuilder    builder    = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(projectileInst)));
        MatrixStack.Entry lastStack = mStack.last();
        Matrix4f          pose          = lastStack.pose();
        Matrix3f          normal          = lastStack.normal();


        // fletching back
        this.vertex(projectileInst, pose, normal, builder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
        this.vertex(projectileInst, pose, normal, builder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
        this.vertex(projectileInst, pose, normal, builder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
        this.vertex(projectileInst, pose, normal, builder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
        // fletching front
        this.vertex(projectileInst, pose, normal, builder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
        this.vertex(projectileInst, pose, normal, builder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
        this.vertex(projectileInst, pose, normal, builder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
        this.vertex(projectileInst, pose, normal, builder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);

        // arrow
        for(int j = 0; j < 4; ++j) {
            mStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.vertex(projectileInst, pose, normal, builder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
            this.vertex(projectileInst, pose, normal, builder, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
            this.vertex(projectileInst, pose, normal, builder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
            this.vertex(projectileInst, pose, normal, builder, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
        }

        mStack.popPose();

        super.render(projectileInst, yaw, partialTicks, mStack, buffer, light);
    }

    public void vertex(E projectileInst, Matrix4f pose, Matrix3f normal, IVertexBuilder builder, int x, int y, int z, float u, float v, int nrmX, int nrmZ, int nrmY, int light) {
        builder.vertex(pose, (float)x, (float)y, (float)z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY)
               .uv2(light).normal(normal, (float)nrmX, (float)nrmY, (float)nrmZ).endVertex();
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull E projectileInst) {
        return projectileInst.getDelegate().getTexture(projectileInst);
    }
}
