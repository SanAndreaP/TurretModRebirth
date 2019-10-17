/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.layer;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;

public class LayerTurretGlow<E extends EntityLiving & ITurretInst>
        implements LayerRenderer<E>
{
    private final RenderLiving<E> turretRenderer;
    private final ModelBase glowModel;

    public LayerTurretGlow(RenderLiving<E> turretRenderer, ModelBase glowModel) {
        this.turretRenderer = turretRenderer;
        this.glowModel = glowModel;
    }

    @Override
    public void doRenderLayer(E turret, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if( !turret.isActive() ) {
            return;
        }

        this.turretRenderer.bindTexture(turret.getTurret().getGlowTexture(turret));
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        GlStateManager.depthMask(false);

        float[] prevBright = ClientProxy.forceGlow();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.glowModel.setLivingAnimations(turret, limbSwing, limbSwingAmount, partialTicks);
        this.glowModel.render(turret, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        ClientProxy.resetGlow(prevBright);

        GlStateManager.depthMask(true);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
