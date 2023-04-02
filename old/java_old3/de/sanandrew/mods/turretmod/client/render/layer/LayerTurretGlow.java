/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.render.turret.TurretRenderBase;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

public class LayerTurretGlow<E extends LivingEntity & ITurretInst, M extends EntityModel<E>>
        extends LayerRenderer<E, M>
{
    public LayerTurretGlow(TurretRenderBase<E, M> turretRenderer) {
        super(turretRenderer);
    }

    public void render(@Nonnull MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, E turret,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.getEyes(turret.getTurret().getGlowTexture(turret)));
        this.getEntityModel().render(stack, ivertexbuilder, 0xF00000, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
