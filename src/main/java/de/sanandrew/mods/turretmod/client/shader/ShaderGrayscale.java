/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.shader;

import de.sanandrew.mods.sanlib.lib.client.ShaderHelper;
import de.sanandrew.mods.turretmod.client.util.Shaders;
import de.sanandrew.mods.turretmod.util.Procedure;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class ShaderGrayscale
{
    private final ResourceLocation texture;

    public ShaderGrayscale(ResourceLocation texture) {
        this.texture = texture;
    }

    private void drawGrayscale(int shader, float brightness) {
        TextureManager texMgr = Minecraft.getMinecraft().renderEngine;
        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");
        int brightnessUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "brightness");

        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        GlStateManager.bindTexture(texMgr.getTexture(this.texture).getGlTextureId());
        ARBShaderObjects.glUniform1iARB(imageUniform, 0);
        ARBShaderObjects.glUniform1fARB(brightnessUniform, brightness);
    }

    public void render(Procedure renderer, float brightness) {
        int texture = 0;
        boolean shaders = ShaderHelper.areShadersEnabled();

        if(shaders) {
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + TmrConfiguration.glSecondaryTextureUnit);
            texture = GlStateManager.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        }

        ShaderHelper.useShader(Shaders.grayscaleItem, shader -> this.drawGrayscale(shader, brightness));
        renderer.work();
        ShaderHelper.releaseShader();

        if(shaders) {
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + TmrConfiguration.glSecondaryTextureUnit);
            GlStateManager.bindTexture(texture);
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        }
    }
}
