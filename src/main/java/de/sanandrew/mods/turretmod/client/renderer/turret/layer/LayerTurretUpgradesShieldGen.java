/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.renderer.turret.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

public class LayerTurretUpgradesShieldGen<E extends LivingEntity & ITurretInst, M extends EntityModel<E>>
        extends LayerRenderer<E, M>
{
    public LayerTurretUpgradesShieldGen(IEntityRenderer<E, M> entityRendererIn) {
        super(entityRendererIn);
    }

    //TODO: reimplement upgrades
    @Override
    public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int packedLightIn, @Nonnull E turretInst,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//        if( TmrConfig.Client.renderUpgrades ) {
//            IUpgradeProcessor proc = turretInst.getUpgradeProcessor();
//            int cnt = proc.getSizeInventory();
//            for( int i = 0; i < cnt; i++ ) {
//                ItemStack slotStack = proc.getStackInSlot(i);
//                if( ItemStackUtils.isValid(slotStack) ) {
//                    int x = i % 18;
////                    int y = i / 18;
//
//                    Vector3f pos = new Vector3f(-0.2375F + 0.01765F * x, 0.125F, -0.075F);
//                    Vector3f rot = new Vector3f(90.0F, 90.0F, 0.0F);
////                    stack.push();
////                    stack.rotate(netHeadYaw + 90.0F + 180.0F * y, 0.0F, 1.0F, 0.0F);
////                    GlStateManager.rotate(-headPitch, 1.0F, 0.0F, 0.0F);
//                    RenderUtils.renderStackInWorld(slotStack, stack, pos, rot, 0.2F,
//                                                   ItemCameraTransforms.TransformType.FIXED, buffer, turretInst, packedLightIn, 0);
////                    GlStateManager.popMatrix();
//                }
//            }
//        }
    }
}
