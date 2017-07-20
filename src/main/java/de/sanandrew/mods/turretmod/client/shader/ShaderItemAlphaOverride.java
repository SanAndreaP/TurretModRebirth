/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ShaderItemAlphaOverride
{
    public float alphaMulti = 1.0F;

    public void call(int shader) {
        TextureManager texMgr = Minecraft.getMinecraft().renderEngine;
        int alphaUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "alpha");
        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");

        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texMgr.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).getGlTextureId());
        ARBShaderObjects.glUniform1iARB(imageUniform, 0);
        ARBShaderObjects.glUniform1fARB(alphaUniform, this.alphaMulti);
    }
}
