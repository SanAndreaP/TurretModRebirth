/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.turret.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.renderer.turret.TurretRenderBase;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

public class TurretGlowLayer<E extends LivingEntity & ITurretEntity, M extends EntityModel<E>>
        extends LayerRenderer<E, M>
{
    public TurretGlowLayer(TurretRenderBase<E, M> turretRenderer) {
        super(turretRenderer);
    }

    public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int packedLight, E turret,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if( turret.isActive() ) {
            IVertexBuilder vb = buffer.getBuffer(RenderType.eyes(turret.getDelegate().getGlowTexture(turret)));
            this.getParentModel().renderToBuffer(stack, vb, 0xF00000, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
