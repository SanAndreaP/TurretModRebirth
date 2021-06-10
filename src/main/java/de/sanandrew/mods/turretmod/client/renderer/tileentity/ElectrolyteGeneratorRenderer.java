package de.sanandrew.mods.turretmod.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.ResourceLocations;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteGeneratorTileEntity;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteInventory;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteProcess;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class ElectrolyteGeneratorRenderer
        extends TileEntityRenderer<ElectrolyteGeneratorTileEntity>
{

    private static final Parabole[] PARABOLES = new Parabole[ElectrolyteInventory.INPUT_SLOT_COUNT];

    public ElectrolyteGeneratorRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(@Nonnull ElectrolyteGeneratorTileEntity tile, float partialTicks, @Nonnull MatrixStack pose, @Nonnull IRenderTypeBuffer buffer,
                       int combinedLight, int combinedOverlay)
    {
        pose.pushPose();
        pose.translate(0.5F, 1.5F, 0.5F);
        pose.mulPose(Vector3f.XP.rotationDegrees(180.0F));

        for( int i = 0, max = ElectrolyteInventory.INPUT_SLOT_COUNT; i < max; i++ ) {
            ElectrolyteProcess proc = tile.processes.get(i);
            if( ItemStackUtils.isValid(proc.processStack) ) {
                drawElectrolyteItem(i, proc.processStack, pose, buffer, combinedLight, combinedOverlay);
            }
        }

        pose.popPose();
    }

    private static void drawElectrolyteItem(int index, @Nonnull ItemStack item, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        pose.pushPose();
        pose.mulPose(Vector3f.YP.rotationDegrees(40.0F * index));
        pose.translate(0.4F, 0.0F, 0.0F);
        for( int i = 0, max = 3; i < max; i++ ) {
            RenderUtils.renderStackInWorld(item, pose, new Vector3f(0.0F, 0.0001F * i, 0.0F), new Vector3f(0.0F, (180.0F / max) * i, 0.0F), 0.3F, buffer, combinedLight, combinedOverlay);
        }

        Parabole p = PARABOLES[index];
        if( p == null ) {
            p = new Parabole();
            PARABOLES[index] = p;
        }
        p.draw(pose, buffer, combinedLight);

        pose.popPose();
    }

    private static class Parabole {
        private static final int        STEPS         = 10;
        private static final float      SCALE         = 0.01F;
        private static final double     PERPEND_ANGLE = 90.0D * Math.PI / 180.0D;
        private static final RenderType TYPE_WIRE     = RenderType.create("electrolyte_wire", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256,
                                                                          RenderType.State.builder().setTextureState(new RenderState.TextureState(ResourceLocations.TEXTURE_TILE_ELECTROLYTE_GEN_WIRE, false, false))
                                                                                          .setCullState(new RenderState.CullState(false))
                                                                                          .setLightmapState(new RenderState.LightmapState(true))
                                                                                          .createCompositeState(false));

        private final Vector3d[] vecsA = new Vector3d[STEPS + 1];
        private final Vector3d[] vecsB = new Vector3d[STEPS + 1];
        private final float[]    texsU = new float[STEPS];

        Parabole() {
            Vector3d[] builtVecMain = new Vector3d[STEPS + 1];

            for( int i = 0; i <= STEPS; i++ ) {
                double x = i / (double) STEPS;
                double y = Math.pow(x, 4.0D);
                builtVecMain[i] = new Vector3d(x * -0.35D, y * -0.49D, 0.00D);
                this.vecsA[i] = builtVecMain[i].scale(1D);
                this.vecsB[i] = builtVecMain[i].scale(1D);
            }

            this.vecsA[0] = builtVecMain[0].add(rotateVecXY(builtVecMain[1].subtract(builtVecMain[0]).normalize().scale(SCALE),  PERPEND_ANGLE));
            this.vecsB[0] = builtVecMain[0].add(rotateVecXY(builtVecMain[1].subtract(builtVecMain[0]).normalize().scale(SCALE), -PERPEND_ANGLE));

            for( int i = 1; i < vecsA.length - 1; i++) {
                Vector3d vecBtwPre = builtVecMain[i].subtract(builtVecMain[i-1]);
                Vector3d vecBtwPost = builtVecMain[i+1].subtract(builtVecMain[i]);
                double btwAngle = Math.acos(Math.min(vecBtwPre.dot(vecBtwPost) / vecBtwPre.lengthSqr(), 1.0D));
                this.vecsA[i] = builtVecMain[i].add(rotateVecXY(vecBtwPre.normalize().scale(SCALE),  PERPEND_ANGLE + btwAngle / 2.0D));
                this.vecsB[i] = builtVecMain[i].add(rotateVecXY(vecBtwPre.normalize().scale(SCALE), -PERPEND_ANGLE - btwAngle / 2.0D));
            }

            Vector3d vecBtw = builtVecMain[builtVecMain.length-1].subtract(builtVecMain[builtVecMain.length-2]).normalize().scale(SCALE);
            this.vecsA[this.vecsA.length - 1] = builtVecMain[builtVecMain.length - 1].add(rotateVecXY(vecBtw,  PERPEND_ANGLE));
            this.vecsB[this.vecsA.length - 1] = builtVecMain[builtVecMain.length - 1].add(rotateVecXY(vecBtw, -PERPEND_ANGLE));

            for( int i = 0; i < this.vecsA.length - 1; i++ ) {
                this.texsU[i] = (float) (Math.abs(this.vecsA[i + 1].subtract(this.vecsA[i]).length()) * 4.1D);
            }
        }

        void draw(MatrixStack stack, IRenderTypeBuffer buffer, int light) {
            Matrix4f pose = stack.last().pose();
            IVertexBuilder buf = buffer.getBuffer(TYPE_WIRE);
            for( int i = 0; i < this.vecsA.length - 1; i++ ) {
                drawQuad(i, buf, pose, this.vecsA, this.vecsB, -SCALE, SCALE, this.texsU[i], light);
                drawQuad(i, buf, pose, this.vecsA, this.vecsB, SCALE, -SCALE, this.texsU[i], light);
            }
        }

        private static void drawQuad(int i, IVertexBuilder buf, Matrix4f matrix, Vector3d[] vecA, Vector3d[] vecB, float zA, float zB, float u, int light) {
            buf.vertex(matrix, (float) vecA[i].x, (float) vecA[i].y, zA)        .color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(light).endVertex();
            buf.vertex(matrix, (float) vecA[i + 1].x, (float) vecA[i + 1].y, zA).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u, 1.0F)   .uv2(light).endVertex();
            buf.vertex(matrix, (float) vecB[i + 1].x, (float) vecB[i + 1].y, zB).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u, 0.0F)   .uv2(light).endVertex();
            buf.vertex(matrix, (float) vecB[i].x, (float) vecB[i].y, zB)        .color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(light).endVertex();
        }

        private static Vector3d rotateVecXY(Vector3d vec, double yaw) {
            double cos = Math.cos(yaw);
            double sin = Math.sin(yaw);
            double d0 = vec.x * cos - vec.y * sin;
            double d1 = vec.y * cos + vec.x * sin;
            double d2 = vec.z;
            return new Vector3d(d0, d1, d2);
        }
    }
}
