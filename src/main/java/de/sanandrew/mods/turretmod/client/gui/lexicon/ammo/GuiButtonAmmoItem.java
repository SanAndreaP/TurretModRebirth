/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.ammo;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class GuiButtonAmmoItem
        extends GuiButton
{
    private static final ShaderGrayscale SHADER_GRAYSCALE = new ShaderGrayscale(TextureMap.LOCATION_BLOCKS_TEXTURE);

    final IAmmunition<?> ammo;
    @Nonnull
    public final ItemStack stack;
    boolean inactive;

    public GuiButtonAmmoItem(IAmmunition<?> ammo, int id, int x, int y) {
        super(id, x, y, 16, 16, "");
        this.ammo = ammo;
        this.stack = AmmunitionRegistry.INSTANCE.getAmmoItem(ammo);
        this.inactive = true;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
        if( this.visible ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 100.0F);
            Gui.drawRect(this.x, this.y, this.x + this.width, this.y + 1, 0xA0000000);
            Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xA0000000);
            Gui.drawRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0x40000000);
            Gui.drawRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0x40000000);
            if( this.inactive ) {
                Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x80000000);
                SHADER_GRAYSCALE.render(() ->  RenderUtils.renderStackInGui(this.stack, this.x, this.y, 1.0F), 0.75F);
            } else {
                Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x80FFFFFF);
                RenderUtils.renderStackInGui(this.stack, this.x, this.y, 1.0F);
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return super.mousePressed(mc, mouseX, mouseY) && this.inactive;
    }
}
