/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.shader;

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

public class ShaderAlphaOverride
{
    private final ResourceLocation texture;

    public ShaderAlphaOverride(ResourceLocation texture) {
        this.texture = texture;
    }

    private void drawAlpha(int shader, float alpha, float brightness) {
        TextureManager texMgr = Minecraft.getInstance().getTextureManager();
        Texture tex = texMgr.getTexture(this.texture);
        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");
        int alphaUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "alpha");
        int brightnessUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "brightness");

        if( tex != null ) {
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
            RenderSystem.bindTexture(tex.getId());
            ARBShaderObjects.glUniform1iARB(imageUniform, 0);
            ARBShaderObjects.glUniform1fARB(alphaUniform, alpha);
            ARBShaderObjects.glUniform1fARB(brightnessUniform, brightness);
        }
    }

    public void render(Runnable renderer, float alpha) {
        render(renderer, alpha, 1.0F);
    }

    public void render(Runnable renderer, float alpha, float brightness) {
        int     textureId = 0;
        boolean shaders   = ShaderHelper.areShadersEnabled();

        if( shaders ) {
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB + ShaderHelper.getSecondaryTextureUnit());
            textureId = GlStateManager._getInteger(GL11.GL_TEXTURE_BINDING_2D);
            ShaderHelper.useShader(Shaders.alphaOverride, shader -> this.drawAlpha(shader, alpha, brightness));
        }

        renderer.run();

        if( shaders ) {
            ShaderHelper.releaseShader();
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB + ShaderHelper.getSecondaryTextureUnit());
            RenderSystem.bindTexture(textureId);
            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        }
    }
//    public float alphaMulti = 1.0F;
//    public int brightness = 0xF0;
//
//    public void call(int shader) {
//        Minecraft mc = Minecraft.getInstance();
//        float[] lightingColor = { 1.0F, 1.0F, 1.0F };
//
//        TextureManager texMgr = mc.textureManager;
//        Texture tex = texMgr.getTexture(PlayerContainer.BLOCK_ATLAS);
//        int alphaUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "alpha");
//        int lightingUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "lighting");
//        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");
//
//        if( tex != null ) {
//
////        int blockB = ((this.brightness % 0x10000) >> 4);
////        int skyB = ((this.brightness / 0x10000) >> 4);
////        int pixelID = (skyB * 16 + blockB) * 3;
////        byte[] pixels = new byte[16*16*3];
////        ByteBuffer buf = ByteBuffer.allocateDirect(pixels.length);
//
////        RenderSystem.activeTexture();
////        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);
////        buf.get(pixels);
////        lightingColor[0] = (pixels[pixelID] & 0xFF) / 255.0F;
////        lightingColor[1] = (pixels[pixelID + 1] & 0xFF) / 255.0F;
////        lightingColor[2] = (pixels[pixelID + 2] & 0xFF) / 255.0F;
//
//            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getId());
//            ARBShaderObjects.glUniform1iARB(imageUniform, 0);
//            ARBShaderObjects.glUniform3fARB(lightingUniform, lightingColor[0], lightingColor[1], lightingColor[2]);
//            ARBShaderObjects.glUniform1fARB(alphaUniform, this.alphaMulti);
//        }
//    }
}
