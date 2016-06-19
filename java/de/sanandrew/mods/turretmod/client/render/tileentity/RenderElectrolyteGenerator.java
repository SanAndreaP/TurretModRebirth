/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.mods.turretmod.client.model.block.ModelElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.Resources;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class RenderElectrolyteGenerator
        extends TileEntitySpecialRenderer
{
    private ModelElectrolyteGenerator modelBlock = new ModelElectrolyteGenerator();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partTicks) {
        TileEntityElectrolyteGenerator te = ((TileEntityElectrolyteGenerator) tile);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

        this.bindTexture(Resources.TILE_ELECTROLYTE_GEN.getResource());

        short renderBits = 0;
        ItemStack[] stacks = new ItemStack[TileEntityElectrolyteGenerator.SLOTS_PROCESSING.length];

        for( int i = 0; i < TileEntityElectrolyteGenerator.SLOTS_PROCESSING.length; i++ ) {
            int slot = TileEntityElectrolyteGenerator.SLOTS_PROCESSING[i];
            ItemStack stack = te.getStackInSlot(slot);

            if( ItemStackUtils.isValidStack(stack) ) {
                renderBits |= 1 << i;
                stacks[i] = stack;
            } else {
                renderBits &= ~(1 << i);
            }
        }

        this.modelBlock.render(0.0625F, renderBits);

        for( int i = 0; i < stacks.length; i++ ) {
            ItemStack stack = stacks[i];
            if( stack != null ) {
                GL11.glPushMatrix();
                float s = te.progress[i] / (float) te.maxProgress[i] * 0.5F;
                GL11.glRotatef(24.0F + 160.0F + 40.0F * i, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(1.0F, 0.5F + s, 1.0F);
                renderItem(stack, te);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
    }

    private static void renderItem(ItemStack stack, TileEntityElectrolyteGenerator tile) {
        if( !ItemStackUtils.isValidStack(stack) ) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.35F, 0.2F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(0.50F, 0.50F, 4.0F);

        RenderItem.renderInFrame = true;

        EntityItem entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, stack.copy());
        entityitem.getEntityItem().stackSize = 1;
        entityitem.hoverStart = 0.0F;

        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

        RenderItem.renderInFrame = false;
        GL11.glPopMatrix();
    }
}
