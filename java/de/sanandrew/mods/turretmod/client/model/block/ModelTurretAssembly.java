package de.sanandrew.mods.turretmod.client.model.block;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * TurretConstructor - SanAndreasP
 * Created using Tabula 4.1.1
 */
public class ModelTurretAssembly
        extends ModelBase
{
    public ModelRenderer base;
    public ModelRenderer robotStand1;
    public ModelRenderer robotStand2;
    public ModelRenderer robotAxis;
    public ModelRenderer plate;
    public ModelRenderer robotBinding;
    public ModelRenderer robotArm;
    public ModelRenderer robotHead;

    public ModelTurretAssembly() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.robotStand1 = new ModelRenderer(this, 0, 0);
        this.robotStand1.setRotationPoint(-8.0F, -6.0F, 3.0F);
        this.robotStand1.addBox(0.0F, 0.0F, 0.0F, 2, 6, 3, 0.0F);
        this.robotBinding = new ModelRenderer(this, 0, 9);
        this.robotBinding.setRotationPoint(2.0F, 1.0F, 1.0F);
        this.robotBinding.addBox(-1.0F, -1.5F, -1.5F, 2, 3, 2, 0.0F);
        this.robotStand2 = new ModelRenderer(this, 0, 0);
        this.robotStand2.mirror = true;
        this.robotStand2.setRotationPoint(6.0F, -6.0F, 3.0F);
        this.robotStand2.addBox(0.0F, 0.0F, 0.0F, 2, 6, 3, 0.0F);
        this.robotHead = new ModelRenderer(this, 8, 10);
        this.robotHead.setRotationPoint(0.5F, 0.5F, 0.5F);
        this.robotHead.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(robotHead, 0.7853981633974483F, 0.0F, 0.0F);
        this.robotArm = new ModelRenderer(this, 0, 39);
        this.robotArm.setRotationPoint(-0.5F, 0.0F, -9.0F);
        this.robotArm.addBox(0.0F, 0.0F, 0.0F, 1, 1, 12, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.base.addBox(-8.0F, 0.0F, -8.0F, 16, 10, 16, 0.0F);
        this.plate = new ModelRenderer(this, 0, 28);
        this.plate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.plate.addBox(-5.0F, -1.0F, -6.0F, 10, 1, 10, 0.0F);
        this.robotAxis = new ModelRenderer(this, 0, 26);
        this.robotAxis.setRotationPoint(-7.0F, -5.0F, 4.0F);
        this.robotAxis.addBox(0.0F, 0.0F, 0.0F, 14, 1, 1, 0.0F);
        this.base.addChild(this.robotStand1);
        this.robotAxis.addChild(this.robotBinding);
        this.base.addChild(this.robotStand2);
        this.robotArm.addChild(this.robotHead);
        this.robotBinding.addChild(this.robotArm);
        this.base.addChild(this.plate);
        this.base.addChild(this.robotAxis);
    }

    public void render(float f5, float partTicks, TileEntityTurretAssembly te) {
        int meta = te.hasWorldObj() ? BlockRegistry.assemblyTable.getDirection(te.getBlockMetadata()) - 2 : 0;
        this.base.rotateAngleY = (float)(90.0D * meta / 180.0D * Math.PI);

        if( te.isItemRendered ) {
            this.robotBinding.rotationPointX = 3.0F;
            this.robotArm.rotationPointZ = -9.0F;

            this.base.render(f5);
        } else {
            this.robotBinding.rotationPointX = Math.max(2.0F, Math.min(12.0F, te.prevRobotArmX + (te.robotArmX - te.prevRobotArmX) * partTicks));
            this.robotArm.rotationPointZ = Math.max(-11.0F, Math.min(-3.0F, te.prevRobotArmY + (te.robotArmY - te.prevRobotArmY) * partTicks));

            float laserX = ((this.robotBinding.rotationPointX - 7.0F) / 16.0F);
            float laserZ = ((this.robotArm.rotationPointZ + 5.5F) / 16.0F);

            if( meta == 1 ) {
                float lx = laserX;
                laserX = laserZ;
                laserZ = -lx;
            } else if( meta == -2 ) {
                laserX = -laserX;
                laserZ = -laserZ;
            } else if( meta == -1 ) {
                float lx = laserX;
                laserX = -laserZ;
                laserZ = lx;
            }

            this.base.render(f5);

            if( te.isActive && te.robotArmX >= 4.0F && te.robotArmX <= 10.0F && te.robotArmY <= -3.5F && te.robotArmY >= -9.5F ) {
                int tileX = te.getPos().getX();
                int tileY = te.getPos().getY();
                int tileZ = te.getPos().getZ();
                float dist = (float) Minecraft.getMinecraft().thePlayer.getDistance(tileX + 0.5F, tileY + 0.5F, tileZ + 0.5F);
                Tessellator tess = Tessellator.getInstance();
                VertexBuffer buf = tess.getBuffer();

                GlStateManager.pushMatrix();
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.translate(laserX, 0.5F, laserZ);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                float prevBrightX = OpenGlHelper.lastBrightnessX;
                float prevBrightY = OpenGlHelper.lastBrightnessY;
                int bright = 0xF0F0;
                int brightX = bright % 65536;
                int brightY = bright / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

                GlStateManager.glLineWidth(Math.min(20.0F, 20.0F / (dist)));
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
                buf.pos(0.0F, 0.1F, 0.0F).color(255, 0, 0, 64).endVertex();
                buf.pos(0.0F, 1.0F, 0.0F).color(255, 0, 0, 64).endVertex();
                tess.draw();

                GlStateManager.glLineWidth(Math.min(5.0F, 5.0F / (dist)));
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
                buf.pos(0.0F, 0.1F, 0.0F).color(255, 0, 0, 128).endVertex();
                buf.pos(0.0F, 1.0F, 0.0F).color(255, 0, 0, 128).endVertex();
                tess.draw();

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);

                GlStateManager.disableBlend();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();

                te.spawnParticle = Triplet.with(tileX + 0.50F + laserX, tileY + 0.65F, tileZ + 0.50F - laserZ);
            }
        }
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
