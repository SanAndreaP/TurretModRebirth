/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.mods.turretmod.client.model.block.ModelTurretAssembly;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.Textures;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class RenderTurretAssembly
        extends TileEntitySpecialRenderer
{
    private ModelTurretAssembly modelBlock = new ModelTurretAssembly();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partTicks) {
        TileEntityTurretAssembly te = ((TileEntityTurretAssembly) tile);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

        this.bindTexture(Textures.TILE_TURRET_ASSEMBLY.getResource());
        this.modelBlock.render(0.0625F, partTicks, te);

        renderItem(te);

        GL11.glPopMatrix();
    }

    private static void renderItem(TileEntityTurretAssembly assembly) {
        ItemStack itemstack = assembly.currentlyCraftingItem;
        if( itemstack == null ) {
            itemstack = assembly.getStackInSlot(0);
        }

        if( itemstack != null ) {
            EntityItem entityitem = new EntityItem(assembly.getWorldObj(), 0.0D, 0.0D, 0.0D, itemstack.copy());

            entityitem.getEntityItem().stackSize = 1;
            entityitem.hoverStart = 0.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.795F, -0.2125F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(0.73F, 0.73F, 1.0F);

            RenderItem.renderInFrame = true;
            RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            RenderItem.renderInFrame = false;

            GL11.glPopMatrix();
        }
    }
}
