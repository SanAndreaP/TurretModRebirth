/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.turret.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.renderer.turret.ForcefieldRender;
import de.sanandrew.mods.turretmod.client.renderer.turret.TurretRenderBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

/**
 * This class is only there to fix "Fabulous" graphics quirks.
 * This will get removed once I figure out how to render it properly there!
 */
public class TurretShieldLayer<E extends LivingEntity & ITurretEntity, M extends EntityModel<E>>
        extends LayerRenderer<E, M>
{
    public TurretShieldLayer(TurretRenderBase<E, M> turretRenderer) {
        super(turretRenderer);
    }

    public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int packedLight, @Nonnull E turret,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if( Minecraft.useShaderTransparency() ) {
            ForcefieldRender.INSTANCE.renderEntityField(turret, Minecraft.getInstance(), stack, buffer);
        }
    }
}
