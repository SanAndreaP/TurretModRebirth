/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.client.render.IRender;
import de.sanandrew.mods.turretmod.api.client.render.IRenderInst;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RenderPebble<T extends Entity>
        implements IRender<T>
{
    @Nonnull
    private ItemStack gravelItem = ItemStack.EMPTY;

    @Override
    public void doRender(IRenderInst<T> render, T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if( !ItemStackUtils.isValid(this.gravelItem) ) {
            this.gravelItem = new ItemStack(Blocks.GRAVEL, 1);
        }

        RenderUtils.renderStackInWorld(this.gravelItem, x, y, z, 0.0F, 0.0F, 0.0F, 0.1D);
    }
}
