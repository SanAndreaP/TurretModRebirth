/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.client.model.block.TurretAssemblyModel;
import de.sanandrew.mods.turretmod.client.renderer.TmrRenderTypes;
import de.sanandrew.mods.turretmod.client.shader.ShaderAlphaOverride;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyInventory;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.tileentity.assembly.RobotArm;
import de.sanandrew.mods.turretmod.tileentity.assembly.TurretAssemblyEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;

import java.util.function.IntFunction;

public class TurretAssemblyRenderer
        extends TileEntityRenderer<TurretAssemblyEntity>
{
    private final TurretAssemblyModel modelBlock = new TurretAssemblyModel();

    private float armX;
    private float armZ;

    public TurretAssemblyRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    private final ShaderAlphaOverride shaderCallback = new ShaderAlphaOverride(PlayerContainer.BLOCK_ATLAS);


    @Override
    public void render(TurretAssemblyEntity tile, float partTicks, MatrixStack mStack, IRenderTypeBuffer buffer,
                       int combinedLight, int combinedOverlay)
    {
        mStack.pushPose();
        mStack.translate(0.5F, 1.5F, 0.5F);
        mStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));

        RobotArm arm = tile.getRobotArm();

        this.armX = arm.getArmX(partTicks);
        this.armZ = arm.getArmZ(partTicks);

        this.modelBlock.setupAnim(tile, armX, armZ);
        this.modelBlock.renderToBuffer(mStack, buffer.getBuffer(modelBlock.renderType(Resources.TEXTURE_TILE_TURRET_ASSEMBLY)),
                                       combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        mStack.popPose();

        renderItems(tile, mStack, buffer, combinedLight, combinedOverlay);

        if( tile.isActive() && arm.isInBuildVicinity() ) {
            Direction direction = BlockRegistry.TURRET_ASSEMBLY.getDirection(tile.getBlockState());
            Vector2f laserPos = getLocalLaserPos(direction);
            this.renderLaser(direction, laserPos, mStack, buffer.getBuffer(TmrRenderTypes.ASSEMBLY_LASER));

            BlockPos pos = tile.getBlockPos();
            tile.getRobotArm().spawnParticle(pos.getX() + laserPos.x, pos.getY() + 0.7D, pos.getZ() + laserPos.y);
        }
    }

    private Vector2f getLocalLaserPos(Direction direction) {
        float laserX = ((this.armX) / 16.0F);
        float laserZ = ((-this.armZ) / 16.0F) - 5.5F/16.0F;

        float lx = laserX * direction.getStepZ() + laserZ * direction.getStepX();
        float lz = laserX * -direction.getStepX() + laserZ * direction.getStepZ();

        return new Vector2f(0.5F + lx, 0.5F + lz);
    }

    private void renderLaser(Direction direction, Vector2f laserPos, MatrixStack mStack, IVertexBuilder builder) {
        mStack.pushPose();
        mStack.translate(laserPos.x, 0.0F, laserPos.y);

        final float cw = direction.getAxis() == Direction.Axis.X ? 0.022F : 0.03F;
        final float cw2 = direction.getAxis() == Direction.Axis.X ? 0.03F : 0.022F;
        Matrix4f pose = mStack.last().pose();

        Vector3f start = new Vector3f(cw, 0.85F, cw2);
        Vector3f end = new Vector3f(-cw, 0.6F, -cw2);
        quad(builder, pose, start, end, new ColorObj(0x0AFF0000), 1.0F);
        quad(builder, pose, start, end, new ColorObj(0x10FF0000), 0.75F);
        quad(builder, pose, start, end, new ColorObj(0x20FF0000), 0.5F);
        quad(builder, pose, start, end, new ColorObj(0x80FF0000), 0.25F);

        mStack.popPose();
    }

    @SuppressWarnings("DuplicatedCode")
    private static void quad(IVertexBuilder builder, Matrix4f pose, Vector3f start, Vector3f end, ColorObj color, float scaleH) {
        start = start.copy();
        end = end.copy();

        start.mul(scaleH, 1, scaleH);
        end.mul(scaleH, 1, scaleH);

        rect(builder, pose, new Vector3f(start.x(), start.y(), start.z()), new Vector3f(end.x(), end.y(), start.z()), color);
        rect(builder, pose, new Vector3f(end.x(), start.y(), end.z()), new Vector3f(start.x(), end.y(), end.z()), color);
        rect(builder, pose, new Vector3f(start.x(), start.y(), end.z()), new Vector3f(start.x(), end.y(), start.z()), color);
        rect(builder, pose, new Vector3f(end.x(), start.y(), start.z()), new Vector3f(end.x(), end.y(), end.z()), color);
    }

    private static void rect(IVertexBuilder builder, Matrix4f pose, Vector3f start, Vector3f end, ColorObj color) {
        float r = color.fRed();
        float g = color.fGreen();
        float b = color.fBlue();
        float a = color.fAlpha(0.0F);

        builder.vertex(pose, start.x(), start.y(), start.z()).color(r, g, b, a).endVertex();
        builder.vertex(pose, end.x(),   start.y(), end.z())  .color(r, g, b, a).endVertex();
        builder.vertex(pose, end.x(),   end.y(),   end.z())  .color(r, g, b, a).endVertex();
        builder.vertex(pose, start.x(), end.y(),   start.z()).color(r, g, b, a).endVertex();
    }

    private void renderItems(TurretAssemblyEntity tile, MatrixStack mStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        mStack.pushPose();
        mStack.translate(0.5F, 0.64F, 0.5F);
        mStack.mulPose(BlockRegistry.TURRET_ASSEMBLY.getDirection(tile.getBlockState()).getRotation());

        IAssemblyRecipe recipe = MiscUtils.apply(tile.getCurrentRecipeId(), r -> AssemblyManager.INSTANCE.getRecipe(tile.getLevel(), r));
        ItemStack crfStack = recipe != null ? recipe.getResultItem() : ItemStack.EMPTY;
        if( ItemStackUtils.isValid(crfStack) ) {
            this.shaderCallback.render(() -> {
                IRenderTypeBuffer.Impl cstBuf = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
                RenderUtils.renderStackInWorld(crfStack, mStack, new Vector3f(0.0F, 0.0F, -0.05F), new Vector3f(0.0F, 180.0F, 0.0F), 0.25F, cstBuf, combinedLight, combinedOverlay);
                cstBuf.endBatch();
            }, Math.max(0.0F, tile.getTicksCrafted() / (float) tile.getMaxTicksCrafted()), ClientProxy.getTrueBrightness(combinedLight, tile.getLevel()));
        }

        ItemStack outStack = tile.getInventory().getStackInSlot(AssemblyInventory.SLOT_OUTPUT);
        if( ItemStackUtils.isValid(outStack) ) {
            RenderUtils.renderStackInWorld(outStack, mStack, new Vector3f(0.39F, 0.39F, -0.06F), new Vector3f(90.0F, 180.0F, 0.0F), 0.15F, buffer, combinedLight, combinedOverlay);
        }

        mStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        mStack.translate(0.4F, 0.0F, 0.0F);
        mStack.translate(-0.01F, 0.0F, 0.0F);

        final IntFunction<Vector3f> itmPos = shf -> new Vector3f(0.0F, 0.28F - 0.1F / 3.0F * shf, 0.0F);
        final Vector3f rot = new Vector3f(30.0F, 0.0F, 0.0F);
        if( tile.hasAutoUpgrade() ) {
            RenderUtils.renderStackInWorld(tile.getInventory().getStackInSlot(AssemblyInventory.SLOT_UPGRADE_AUTO), mStack, itmPos.apply(0), rot, 0.15F, buffer, combinedLight, combinedOverlay);
        }
        if( tile.hasSpeedUpgrade() ) {
            RenderUtils.renderStackInWorld(tile.getInventory().getStackInSlot(AssemblyInventory.SLOT_UPGRADE_SPEED), mStack, itmPos.apply(1), rot, 0.15F, buffer, combinedLight, combinedOverlay);
        }
        if( tile.hasFilterUpgrade() ) {
            RenderUtils.renderStackInWorld(tile.getInventory().getStackInSlot(AssemblyInventory.SLOT_UPGRADE_FILTER), mStack, itmPos.apply(2), rot, 0.15F, buffer, combinedLight, combinedOverlay);
        }
        if( tile.hasRedstoneUpgrade() ) {
            RenderUtils.renderStackInWorld(tile.getInventory().getStackInSlot(AssemblyInventory.SLOT_UPGRADE_REDSTONE), mStack, itmPos.apply(3), rot, 0.15F, buffer, combinedLight, combinedOverlay);
        }

        mStack.popPose();
    }
}
