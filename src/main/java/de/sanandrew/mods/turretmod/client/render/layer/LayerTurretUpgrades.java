/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.layer;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerTurretUpgrades<E extends EntityLiving & ITurretInst>
        implements LayerRenderer<E>
{
    @Override
    public void doRenderLayer(E turret, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if( TmrConfiguration.renderUpgrades ) {
            IUpgradeProcessor proc = turret.getUpgradeProcessor();
            int cnt = proc.getSizeInventory();
            for( int i = 0; i < cnt; i++ ) {
                ItemStack slotStack = proc.getStackInSlot(i);
                if( ItemStackUtils.isValid(slotStack) ) {
                    int x = i % 18;
                    int y = i / 18;

                    GlStateManager.pushMatrix();
                    GlStateManager.rotate(netHeadYaw + 180.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-headPitch, 1.0F, 0.0F, 0.0F);
                    RenderUtils.renderStackInWorld(slotStack, -0.15D + 0.01765F * x, -0.09375D + 0.1875F * y, -0.25D, 90.0F, 90.0F, 0.0F, 0.2D);
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
