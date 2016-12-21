/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ModelRendererCulled
        extends ModelRenderer
{
    public ModelRendererCulled(ModelBase model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

    @Override
    public void render(float scale) {
        GlStateManager.enableCull();
        super.render(scale);
        GlStateManager.disableCull();
    }
}
