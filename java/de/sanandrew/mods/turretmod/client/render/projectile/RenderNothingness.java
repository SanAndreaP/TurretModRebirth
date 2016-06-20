/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderNothingness
        extends Render<Entity>
{
    public RenderNothingness() {
        super(Minecraft.getMinecraft().getRenderManager());
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partTicks) { }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
