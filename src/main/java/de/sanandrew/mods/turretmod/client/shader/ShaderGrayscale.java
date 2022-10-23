/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.ShaderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
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
        TextureManager texMgr = Minecraft.getInstance().getTextureManager();
        Texture tex = texMgr.getTexture(this.texture);
        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");
        int brightnessUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "brightness");

        if( tex != null ) {
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
            RenderSystem.bindTexture(tex.getId());
            ARBShaderObjects.glUniform1iARB(imageUniform, 0);
            ARBShaderObjects.glUniform1fARB(brightnessUniform, brightness);
        }
    }

    public void render(Runnable renderer, float brightness) {
        int     textureId = 0;
        boolean shaders   = ShaderHelper.areShadersEnabled();

        if( shaders ) {
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB + ShaderHelper.getSecondaryTextureUnit());
            textureId = GlStateManager._getInteger(GL11.GL_TEXTURE_BINDING_2D);
            ShaderHelper.useShader(Shaders.grayscaleItem, shader -> this.drawGrayscale(shader, brightness));
        }

        renderer.run();

        if( shaders ) {
            ShaderHelper.releaseShader();
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB + ShaderHelper.getSecondaryTextureUnit());
            RenderSystem.bindTexture(textureId);
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        }
    }
}
