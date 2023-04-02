/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.turret.label;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRenderer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public abstract class Text
        implements ILabelRenderer
{
    private final ResourceLocation id;

    public Text(ResourceLocation id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean isVisible(ITurretEntity turret) {
        return true;
    }

    @Override
    public int getMinWidth(ILabelRegistry registry, ITurretEntity turret) {
        return getTextWidth(getTextLeft(turret), registry.getFontRenderer(), 0) + getTextWidth(getTextRight(turret), registry.getFontRenderer(), 12);
    }

    @Override
    public int getHeight(ILabelRegistry registry, ITurretEntity turret) {
        return registry.getFontRenderer().lineHeight;
    }

    protected abstract ITextComponent getTextLeft(ITurretEntity turret);

    protected ITextComponent getTextRight(ITurretEntity turret) {
        return null;
    }

    private static int getTextWidth(ITextComponent text, FontRenderer fr, int pad) {
        return text != null ? fr.width(text) + pad : 0;
    }

    protected abstract int getColorLeft(float opacity);

    protected int getColorRight(float opacity) {
        return 0x0;
    }

    @Override
    public void render(ILabelRegistry registry, ITurretEntity turret, WorldRenderer context, MatrixStack mat, float totalWidth, float totalHeight, float partialTicks, float opacity) {
        RenderSystem.enableTexture();

        registry.drawFont(this.getTextLeft(turret), 0, 0, this.getColorLeft(opacity), mat);

        ITextComponent rt = this.getTextRight(turret);
        if( rt != null ) {
            registry.drawFont(rt, totalWidth - getTextWidth(rt, registry.getFontRenderer(), 0), 0, this.getColorRight(opacity), mat);
        }

        RenderSystem.disableTexture();
    }
}
