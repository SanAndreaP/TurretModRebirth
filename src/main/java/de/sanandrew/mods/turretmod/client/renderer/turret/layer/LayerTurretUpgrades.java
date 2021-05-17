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

public class LayerTurretUpgrades<E extends LivingEntity & ITurretInst, M extends EntityModel<E>>
        extends LayerRenderer<E, M>
{
    public LayerTurretUpgrades(IEntityRenderer<E, M> entityRendererIn) {
        super(entityRendererIn);
    }

    //TODO: reimplement upgrades
    @Override
    public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int packedLight, @Nonnull E turret,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
//        if( TmrConfig.Client.renderUpgrades ) {
//            IUpgradeProcessor proc = turret.getUpgradeProcessor();
//            int cnt = proc.getSizeInventory();
//            double shiftY = 1.53D - turret.getEyeHeight();
//
//            stack.push();
//            stack.translate(0.0D, shiftY, 0.0D);
//            stack.rotate(new Quaternion(netHeadYaw + 180.0F, 0.0F, 1.0F, 0.0F));
//            stack.rotate(new Quaternion(-headPitch, 1.0F, 0.0F, 0.0F));
//            for( int i = 0; i < cnt; i++ ) {
//                ItemStack slotStack = proc.getStackInSlot(i);
//                if( ItemStackUtils.isValid(slotStack) ) {
//                    int x = i % 18;
//                    int y = i / 18;
//
//                    Vector3f pos = new Vector3f(-0.15F + 0.01765F * x, -0.09375F + 0.1875F * y, -0.21F);
//                    Vector3f rot = new Vector3f(90.0F, 90.0F, 0.0F);
//                    RenderUtils.renderStackInWorld(slotStack, stack, pos, rot, 0.2F, ItemCameraTransforms.TransformType.FIXED,
//                                                   buffer, turret, packedLight, 0);
//                }
//            }
//            stack.pop();
//        }
    }
}
