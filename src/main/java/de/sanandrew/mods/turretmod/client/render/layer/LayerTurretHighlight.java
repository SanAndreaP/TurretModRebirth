package de.sanandrew.mods.turretmod.client.render.layer;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;

public class LayerTurretHighlight<E extends EntityLiving & ITurretInst>
        implements LayerRenderer<E>
{
    @Override
    public void doRenderLayer(E entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        GlStateManager.enableColorMaterial();
        GlStateManager.enableOutlineMode(0xFF0000);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
