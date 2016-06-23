/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.Resources;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderElectrolyteGenerator
        extends TileEntitySpecialRenderer<TileEntityElectrolyteGenerator>
{
    @Override
    public void renderTileEntityAt(TileEntityElectrolyteGenerator tile, double x, double y, double z, float partTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

//        this.bindTexture(Resources.TILE_ELECTROLYTE_GEN.getResource());

//        short renderBits = 0;
//        ItemStack[] stacks = new ItemStack[TileEntityElectrolyteGenerator.SLOTS_PROCESSING.length];

        for( int i = 0; i < TileEntityElectrolyteGenerator.SLOTS_PROCESSING.length; i++ ) {
            int slot = TileEntityElectrolyteGenerator.SLOTS_PROCESSING[i];
            ItemStack stack = tile.getStackInSlot(slot);

            if( ItemStackUtils.isValidStack(stack) ) {
//                renderBits |= 1 << i;
//                stacks[i] = stack;
                drawElectrolyteItem(i, stack);
            } else {
//                renderBits &= ~(1 << i);
            }
        }

//        for( int i = 0; i < stacks.length; i++ ) {
//            ItemStack stack = stacks[i];
//            if( stack != null ) {
//                GlStateManager.pushMatrix();
////                float s = tile.progress[i] / (float) tile.maxProgress[i] * 0.5F;
////                GlStateManager.rotate(24.0F + 160.0F + 40.0F * i, 0.0F, 1.0F, 0.0F);
////                GlStateManager.scale(1.0F, 0.5F + s, 1.0F);
////                renderItem(stack, tile);
//////                GL11.glDisable(GL11.GL_BLEND);
//                GlStateManager.popMatrix();
//            }
//        }

        GlStateManager.popMatrix();
    }

    private static void drawElectrolyteItem(int index, ItemStack stack) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(40.0F * index, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.4F, 0.0F, 0.0F);
        for( int i = 0, max = 4; i < max; i++ ) {
            TmrClientUtils.renderStackInWorld(stack, 0.0D, 0.0001D * i, 0.0D, 0.0F, (180.0F / max) * i, 0.0F, 0.3D);
        }

        GlStateManager.disableTexture2D();
        GlStateManager.enableCull();
        GlStateManager.disableLighting();

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buf = tess.getBuffer();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(5.0F);
        buf.pos(0.0, 0.0, 0.0).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        buf.pos(-0.4, -0.5, 0.0).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        tess.draw();

        GlStateManager.enableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }

    private static void drawParaboleWire(float start, float stop, int steps) {

    }
}
