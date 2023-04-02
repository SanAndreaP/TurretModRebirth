/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class TurretTypeName
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_type_name");

    private final int textfieldWidth;

    private long marqueeTime;

    public TurretTypeName(boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors, boolean bordered, int textfieldWidth) {
        super(StringTextComponent.EMPTY, shadow, wrapWidth, lineHeight, fontRenderer, colors, bordered);

        this.textfieldWidth = textfieldWidth;
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        ITextComponent name = this.getDynamicText(gui, StringTextComponent.EMPTY);
        int strWidth = this.fontRenderer.width(name);
        if( strWidth > this.textfieldWidth ) {
            long currTime = System.currentTimeMillis();
            if( this.marqueeTime < 1L ) {
                this.marqueeTime = currTime;
            }
            int marquee = -this.textfieldWidth + (int) (currTime - this.marqueeTime) / 25;
            if( marquee > strWidth ) {
                this.marqueeTime = currTime;
            }
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GuiUtils.enableScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.textfieldWidth, 12);
            this.fontRenderer.draw(stack, name, x - (float) marquee, y, this.currColor);
            RenderSystem.disableScissor();
        } else {
            this.fontRenderer.draw(stack, name, x + (this.textfieldWidth - this.fontRenderer.width(name)) / 2.0F, y, this.currColor);
        }
    }

    @Override
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        return ((TcuScreen) gui).getTurret().getTurretTypeName();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder
            extends Text.Builder
    {
        protected int textfieldWidth = 144;

        public Builder() {
            super(StringTextComponent.EMPTY);
        }

        public Builder textfieldWidth(int width) { this.textfieldWidth = width; return this; }

        @Override
        public TurretTypeName get(IGui gui) {
            this.sanitize(gui);

            return new TurretTypeName(this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors, this.bordered, this.textfieldWidth);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Builder b = IBuilder.copyValues(Text.Builder.buildFromJson(gui, data), new Builder());

            JsonUtils.fetchInt(data.get("textfieldWidth"), b::textfieldWidth);

            return b;
        }

        public static TurretTypeName fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
