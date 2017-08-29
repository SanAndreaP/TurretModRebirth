/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.model.block.ModelTurretAssembly;
import de.sanandrew.mods.turretmod.client.shader.ShaderItemAlphaOverride;
import de.sanandrew.mods.turretmod.client.util.ShaderHelper;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTurretAssembly
        extends TileEntitySpecialRenderer<TileEntityTurretAssembly>
{
    private ModelTurretAssembly modelBlock = new ModelTurretAssembly();

    public float armX;
    public float armZ;

    private ShaderItemAlphaOverride shaderCallback = new ShaderItemAlphaOverride();

    @Override
    public void render(TileEntityTurretAssembly tile, double x, double y, double z, float partTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

        armX = Math.max(2.0F, Math.min(12.0F, tile.prevRobotArmX + (tile.robotArmX - tile.prevRobotArmX) * partTicks)) - 7.0F;
        armZ = Math.max(-11.0F, Math.min(-3.0F, tile.prevRobotArmY + (tile.robotArmY - tile.prevRobotArmY) * partTicks));

        this.bindTexture(Resources.TILE_TURRET_ASSEMBLY.getResource());
        this.modelBlock.render(0.0625F, tile, armX, armZ);

        renderItem(tile);

        if( tile.isActive && tile.robotArmX >= 4.0F && tile.robotArmX <= 10.0F && tile.robotArmY <= -3.5F && tile.robotArmY >= -9.5F ) {
            tile.spawnParticle = this.renderLaser(BlockRegistry.turret_assembly.getDirection(tile.getBlockMetadata()), tile.getPos());
        }

        GlStateManager.popMatrix();
    }

    private Tuple renderLaser(EnumFacing facing, BlockPos pos) {
        float laserX = ((this.armX) / 16.0F);
        float laserZ = ((this.armZ + 5.5F) / 16.0F);

        float lx;
        switch( facing ) {
            case WEST:
                lx = laserX;
                laserX = laserZ;
                laserZ = -lx;
                break;
            case NORTH:
                laserX = -laserX;
                laserZ = -laserZ;
                break;
            case EAST:
                lx = laserX;
                laserX = -laserZ;
                laserZ = lx;
                break;
        }

        int tileX = pos.getX();
        int tileY = pos.getY();
        int tileZ = pos.getZ();
        float dist = (float) Minecraft.getMinecraft().player.getDistance(tileX + 0.5F, tileY + 0.5F, tileZ + 0.5F);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.translate(laserX, 0.5F, laserZ);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float prevBrightX = OpenGlHelper.lastBrightnessX;
        float prevBrightY = OpenGlHelper.lastBrightnessY;
        int bright = 0xF0;
        int brightX = bright % 65536;
        int brightY = bright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

        GlStateManager.glLineWidth(Math.min(20.0F, 20.0F / (dist)));
        buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(0.0F, 0.1F, 0.0F).color(255, 0, 0, 64).endVertex();
        buf.pos(0.0F, 1.0F, 0.0F).color(255, 0, 0, 64).endVertex();
        tess.draw();

        GlStateManager.glLineWidth(Math.min(5.0F, 5.0F / (dist)));
        buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(0.0F, 0.1F, 0.0F).color(255, 0, 0, 128).endVertex();
        buf.pos(0.0F, 1.0F, 0.0F).color(255, 0, 0, 128).endVertex();
        tess.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        return new Tuple((double) (tileX + 0.50F + laserX), (double) (tileY + 0.65F), (double) (tileZ + 0.50F - laserZ));
    }

    private void renderItem(TileEntityTurretAssembly assembly) {
        int xShift = 0;
        ItemStack crfStack = assembly.currCrafting != null ? assembly.currCrafting.getValue(1) : assembly.getInventory().getStackInSlot(0);

        GlStateManager.pushMatrix();
        GlStateManager.rotate((float)(90.0D * BlockRegistry.turret_assembly.getDirection(assembly.getBlockMetadata()).getHorizontalIndex()), 0.0F, 1.0F, 0.0F);

        if( ItemStackUtils.isValid(crfStack) ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.shaderCallback.alphaMulti = Math.max(0.0F, (assembly.getTicksCrafted() - 15.0F) / (assembly.getMaxTicksCrafted() - 15.0F));
            ShaderHelper.useShader(ShaderHelper.alphaOverride, this.shaderCallback::call);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);
            RenderUtils.renderStackInWorld(crfStack, 0.0D, 0.802D, 0.0D, -90.0F, 180.0F, 0.0F, 0.35D);
            ShaderHelper.releaseShader();
            GlStateManager.disableBlend();
        }

        if( assembly.hasAutoUpgrade() ) {
            RenderUtils.renderStackInWorld(assembly.getInventory().getStackInSlot(1), -0.425D + xShift++ * 0.025D, 0.85D, -0.35D, 0.0F, 90.0F, 0.0F, 0.15D);
        }
        if( assembly.hasSpeedUpgrade() ) {
            RenderUtils.renderStackInWorld(assembly.getInventory().getStackInSlot(2), -0.425D + xShift++ * 0.025D, 0.85D, -0.35D, 0.0F, 90.0F, 0.0F, 0.15D);
        }
        if( assembly.hasFilterUpgrade() ) {
            RenderUtils.renderStackInWorld(assembly.getInventory().getStackInSlot(3), -0.425D + xShift * 0.025D, 0.85D, -0.35D, 0.0F, 90.0F, 0.0F, 0.15D);
        }
        GlStateManager.popMatrix();
    }
}
