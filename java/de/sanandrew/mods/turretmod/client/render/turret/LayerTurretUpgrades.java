/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.turret;

import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;

public class LayerTurretUpgrades<T extends EntityTurret>
        implements LayerRenderer<T>
{
    @Override
    public void doRenderLayer(T turret, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if( TmrConfiguration.renderUpgrades ) {
            int cnt = turret.getUpgradeProcessor().getSizeInventory();
            for( int i = 0; i < cnt; i++ ) {
                ItemStack slotStack = turret.getUpgradeProcessor().getStackInSlot(i);
                if( slotStack != null ) {
                    int x = i % 18;
                    int y = i / 18;

                    GlStateManager.pushMatrix();
                    GlStateManager.rotate(netHeadYaw + 180.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(headPitch, 0.0F, 0.0F, 1.0F);
                    TmrClientUtils.renderStackInWorld(slotStack, -0.1915D + 0.0225F * x, -0.1D + 0.2F * y, -0.25D, 90.0D, 90.0D, 0.0D, 0.2D);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
