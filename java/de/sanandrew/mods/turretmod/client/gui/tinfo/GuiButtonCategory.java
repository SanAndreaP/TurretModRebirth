/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.client.shader.ShaderCallback;
import de.sanandrew.mods.turretmod.client.util.ShaderHelper;
import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class GuiButtonCategory
        extends GuiButton
{
    public final int catIndex;

    private ResourceLocation texture;
    private GuiTurretInfo tinfo;

    private ShaderCallback shaderCallback = new ShaderCallback() {
        @Override
        public void call(int shader) {
            TextureManager texMgr = Minecraft.getMinecraft().renderEngine;
            int heightMatchUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "heightMatch");
            int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");
            int maskUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "mask");

            float heightMatch = GuiButtonCategory.this.ticksHovered / GuiButtonCategory.this.time;
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texMgr.getTexture(GuiButtonCategory.this.texture).getGlTextureId());
            ARBShaderObjects.glUniform1iARB(imageUniform, 0);

            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + TmrConfiguration.glSecondaryTextureUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            ResourceLocation stencil = Resources.TINFO_GRP_STENCIL.getResource();
            texMgr.getTexture(stencil);
            ITextureObject stencilTex;
            texMgr.bindTexture(stencil);
            stencilTex = texMgr.getTexture(stencil);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, stencilTex.getGlTextureId());
            ARBShaderObjects.glUniform1iARB(maskUniform, 7);

            ARBShaderObjects.glUniform1fARB(heightMatchUniform, heightMatch);
        }
    };

    float ticksHovered = 0.0F;
    float time = 6.0F;

    public GuiButtonCategory(int id, int catId, int x, int y, GuiTurretInfo gui) {
        super(id, x, y, 32, 32, "");
        this.tinfo = gui;
        this.catIndex = catId;
        this.texture = TurretInfoCategory.getCategories()[catId].getIcon();
    }

    @Override
    public void drawButton(Minecraft mc, int mx, int my) {
        boolean inside = mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height;
        if( inside ) {
            this.ticksHovered = Math.min(this.time, this.ticksHovered + this.tinfo.timeDelta);
        } else {
            this.ticksHovered = Math.max(-1.0F, this.ticksHovered - this.tinfo.timeDelta);
        }

        float s = 1.0F / 32.0F;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.renderEngine.bindTexture(this.texture);

        int texture = 0;
        boolean shaders = ShaderHelper.areShadersEnabled();

        if(shaders) {
            OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + TmrConfiguration.glSecondaryTextureUnit);
            texture = GlStateManager.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        }

        ShaderHelper.useShader(ShaderHelper.categoryButton, shaderCallback);
        TmrClientUtils.drawTexturedModalRect(xPosition, yPosition, zLevel * 2, 0, 0, 32, 32, s, s);
        ShaderHelper.releaseShader();

        if(shaders) {
            GlStateManager.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + TmrConfiguration.glSecondaryTextureUnit);
            GlStateManager.bindTexture(texture);
            GlStateManager.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        }

        GlStateManager.popMatrix();

        if(inside) {
            this.tinfo.categoryHighlight = TurretInfoCategory.getCategory(this.catIndex);
        }
    }
}
