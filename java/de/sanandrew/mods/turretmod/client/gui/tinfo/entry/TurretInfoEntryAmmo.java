/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.client.shader.ShaderCallback;
import de.sanandrew.mods.turretmod.client.util.ShaderHelper;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TurretInfoEntryAmmo
        extends TurretInfoEntry
{
    private int drawHeight;
    private int shownAmmo;
    private TurretAmmo[] ammos;
    private List<GuiButtonAmmoItem> ammoBtn;
    private long lastTimestamp;

    public TurretInfoEntryAmmo(UUID groupId) {
        this(AmmoRegistry.INSTANCE.getTypes(groupId));
    }

    private TurretInfoEntryAmmo(TurretAmmo[] ammos) {
        super(ammos[0].getStoringAmmoItem(), String.format("%s.tinfo.ammo.%s.name", TurretModRebirth.ID, ammos[0].getInfoName()));
        this.ammos = ammos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initEntry(GuiTurretInfo gui) {
        super.initEntry(gui);

        this.ammoBtn = new ArrayList<>(MathHelper.ceiling_double_int(this.ammos.length * (1/0.75D)));
        this.shownAmmo = 0;
        for( int i = 0; i < this.ammos.length; i++ ) {
            GuiButtonAmmoItem btn = new GuiButtonAmmoItem(gui.getButtonList().size(), gui.entryX + i*16, gui.entryY, i);
            this.ammoBtn.add(btn);
            btn.enabled = i != this.shownAmmo;
            gui.getButtonList().add(btn);
        }
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        TurretAmmo ammo = this.ammos[shownAmmo];

        gui.mc.fontRenderer.drawString(EnumChatFormatting.ITALIC + StatCollector.translateToLocal(this.getTitle()), 2, 20, 0xFF0080BB);
        Gui.drawRect(2, 30, MAX_ENTRY_WIDTH - 2, 31, 0xFF0080BB);

        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(2, 34, 192, 18, 34, 34);

        drawItem(gui.mc, 3, 35, ItemRegistry.ammo.getAmmoItem(1, ammo), 2.0F);

        gui.mc.fontRenderer.drawString(StatCollector.translateToLocal(String.format("gui.%s.tinfo.rounds", TurretModRebirth.ID)), 42, 34, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(String.format("%d", ammo.getAmmoCapacity()), 45, 43, 0xFF000000, false);
        gui.mc.fontRenderer.drawString(StatCollector.translateToLocal(String.format("gui.%s.tinfo.dps", TurretModRebirth.ID)), 42, 54, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(String.format(StatCollector.translateToLocal(String.format("gui.%s.tinfo.healthVal", TurretModRebirth.ID)), ammo.getInfoDamage()), 45, 63, 0xFF000000, false);
        gui.mc.fontRenderer.drawString(StatCollector.translateToLocal(String.format("gui.%s.tinfo.turret", TurretModRebirth.ID)), 42, 74, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(StatCollector.translateToLocal(String.format("entity.%s.name", EntityList.classToStringMapping.get(ammo.getTurret()))), 45, 83, 0xFF000000, false);
        gui.mc.fontRenderer.drawString(StatCollector.translateToLocal(String.format("gui.%s.tinfo.crafting", TurretModRebirth.ID)), 42, 94, 0xFF6A6A6A, false);

        String text = StatCollector.translateToLocal(String.format("%s.tinfo.ammo.%s.desc", TurretModRebirth.ID, ammo.getInfoName())).replace("\\n", "\n");
        gui.mc.fontRenderer.drawSplitString(text, 2, 117, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = gui.mc.fontRenderer.splitStringWidth(text, MAX_ENTRY_WIDTH - 2) + 2;

        Gui.drawRect(2, 114, MAX_ENTRY_WIDTH - 2, 115, 0xFF0080BB);

        TurretAssemblyRecipes.RecipeEntry recipeEntry = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(ammo.getRecipeId());
        for( int i = 0; i < recipeEntry.resources.length; i++ ) {
            ItemStack[] stacks = recipeEntry.resources[i].getEntryItemStacks();
            drawMiniItem(gui, 45 + 10 * i, 103, mouseX, mouseY, scrollY, stacks[(int)(this.lastTimestamp / 1000L % stacks.length)], recipeEntry.resources[i].shouldDrawTooltip());
        }

        this.drawHeight += 116;

        Gui.drawRect(0, scrollY, MAX_ENTRY_WIDTH, 16 + scrollY, 0x80000000);

        long time = System.currentTimeMillis();
        if( this.lastTimestamp + 1000 < time ) {
            this.lastTimestamp = time;
        }
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }

    @Override
    public boolean actionPerformed(GuiButton btn) {
        if( btn instanceof GuiButtonAmmoItem ) {
            this.shownAmmo = ((GuiButtonAmmoItem) btn).ammoIndex;
            for( GuiButtonAmmoItem ammoBtn : this.ammoBtn ) {
                ammoBtn.enabled = ammoBtn.ammoIndex != this.shownAmmo;
            }
            return true;
        }

        return super.actionPerformed(btn);
    }

    private class GuiButtonAmmoItem
            extends GuiButton
    {
        public final int ammoIndex;
        public final ItemStack stack;

        private ShaderCallback shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
                TextureManager texMgr = Minecraft.getMinecraft().renderEngine;
                int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");

                OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texMgr.getTexture(texMgr.getResourceLocation(stack.getItemSpriteNumber())).getGlTextureId());
                ARBShaderObjects.glUniform1iARB(imageUniform, 0);
            }
        };

        public GuiButtonAmmoItem(int id, int x, int y, int index) {
            super(id, x, y, 16, 16, "");
            this.ammoIndex = index;
            this.stack = ItemRegistry.ammo.getAmmoItem(1,  TurretInfoEntryAmmo.this.ammos[this.ammoIndex]);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if( this.visible ) {
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, 0xA0000000);
                Gui.drawRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, 0xA0000000);
                Gui.drawRect(this.xPosition, this.yPosition + 1, this.xPosition + 1, this.yPosition + this.height - 1, 0x40000000);
                Gui.drawRect(this.xPosition + this.width - 1, this.yPosition + 1, this.xPosition + this.width, this.yPosition + this.height - 1, 0x40000000);
                if( this.enabled ) {
                    Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0x80000000);

                    int texture = 0;
                    boolean shaders = ShaderHelper.areShadersEnabled();

                    if(shaders) {
                        //TODO: add config for "7" <glSecondaryTextureUnit>
                        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 7);
                        texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    }

                    ShaderHelper.useShader(ShaderHelper.grayscaleItem, shaderCallback);
                    drawItem(mc, this.xPosition, this.yPosition, this.stack, 1.0F);
                    ShaderHelper.releaseShader();

                    if(shaders) {
                        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 7);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
                        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
                    }
                } else {
                    Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0x80FFFFFF);
                    drawItem(mc, this.xPosition, this.yPosition, this.stack, 1.0F);
                }
            }
        }
    }
}
