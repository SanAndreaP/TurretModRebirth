/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.container.PlayerContainer;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class ShaderItemAlphaOverride
{
    public float alphaMulti = 1.0F;
    public int brightness = 0xF0;

    public void call(int shader) {
        Minecraft mc = Minecraft.getInstance();
        float[] lightingColor = { 1.0F, 1.0F, 1.0F };

        TextureManager texMgr = mc.textureManager;
        Texture tex = texMgr.getTexture(PlayerContainer.BLOCK_ATLAS);
        int alphaUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "alpha");
        int lightingUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "lighting");
        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");

        if( tex != null ) {

//        int blockB = ((this.brightness % 0x10000) >> 4);
//        int skyB = ((this.brightness / 0x10000) >> 4);
//        int pixelID = (skyB * 16 + blockB) * 3;
//        byte[] pixels = new byte[16*16*3];
//        ByteBuffer buf = ByteBuffer.allocateDirect(pixels.length);

//        RenderSystem.activeTexture();
//        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);
//        buf.get(pixels);
//        lightingColor[0] = (pixels[pixelID] & 0xFF) / 255.0F;
//        lightingColor[1] = (pixels[pixelID + 1] & 0xFF) / 255.0F;
//        lightingColor[2] = (pixels[pixelID + 2] & 0xFF) / 255.0F;

            RenderSystem.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getId());
            ARBShaderObjects.glUniform1iARB(imageUniform, 0);
            ARBShaderObjects.glUniform3fARB(lightingUniform, lightingColor[0], lightingColor[1], lightingColor[2]);
            ARBShaderObjects.glUniform1fARB(alphaUniform, this.alphaMulti);
        }
    }
}
