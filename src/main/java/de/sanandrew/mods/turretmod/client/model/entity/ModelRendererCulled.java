/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.client.ModelBoxBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
class ModelRendererCulled
        extends ModelBoxBuilder.NamedModelRenderer
{
    public ModelRendererCulled(Model model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

    @Override
    public void render(@Nonnull MatrixStack stack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay,
                       float red, float green, float blue, float alpha)
    {
        RenderSystem.enableCull();
        super.render(stack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        RenderSystem.disableCull();
    }
}
