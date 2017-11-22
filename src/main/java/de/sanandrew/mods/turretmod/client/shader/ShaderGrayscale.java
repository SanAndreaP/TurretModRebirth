/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.shader;

import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiButtonTcuTab;
import de.sanandrew.mods.turretmod.client.util.ShaderHelper;
import de.sanandrew.mods.turretmod.util.Procedure;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
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

    private void drawGrayscale(int shader) {
        TextureManager texMgr = Minecraft.getMinecraft().renderEngine;
        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");

        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        GlStateManager.bindTexture(texMgr.getTexture(this.texture).getGlTextureId());
        ARBShaderObjects.glUniform1iARB(imageUniform, 0);
    }

    public void render(Procedure renderer) {
        int texture = 0;
        boolean shaders = ShaderHelper.areShadersEnabled();

        if(shaders) {
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + TmrConfiguration.glSecondaryTextureUnit);
            texture = GlStateManager.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        }

        ShaderHelper.useShader(ShaderHelper.grayscaleItem, this::drawGrayscale);
        renderer.work();
        ShaderHelper.releaseShader();

        if(shaders) {
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + TmrConfiguration.glSecondaryTextureUnit);
            GlStateManager.bindTexture(texture);
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        }
    }
}
