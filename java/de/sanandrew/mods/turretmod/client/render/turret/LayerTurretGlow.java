/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.turret;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerTurretGlow<T extends EntityTurret>
        implements LayerRenderer<T>
{
    private RenderTurret turretRenderer;

    public LayerTurretGlow(RenderTurret turretRenderer) {
        this.turretRenderer = turretRenderer;
    }

    @Override
    public void doRenderLayer(T turret, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.turretRenderer.bindTexture(turret.getGlowTexture());
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        if( turret.isInvisible() ) {
            GlStateManager.depthMask(false);
        } else {
            GlStateManager.depthMask(true);
        }

        int brightness = 0xF0F0;
        int brightX = brightness % 65536;
        int brightY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.turretRenderer.getMainModel().render(turret, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        brightness = turret.getBrightnessForRender(partialTicks);
        brightX = brightness % 65536;
        brightY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
        this.turretRenderer.setLightmap(turret, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
