/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.element.nav;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ButtonNav
        extends ButtonSL
{
    private static final ShaderGrayscale SHADER_GRAYSCALE = new ShaderGrayscale(PlayerContainer.BLOCK_ATLAS);

    public final ResourceLocation pageKey;
    public final int order;

    private ItemStack pageStack = ItemStack.EMPTY;

    @SuppressWarnings("java:S107")
    ButtonNav(ResourceLocation texture, int[] size, int[] textureSize, int[] uvEnabled, int[] uvHover, int[] uvDisabled, int[] uvSize, int[] centralTextureSize, ResourceLocation pageId) {
        super(texture, size, textureSize, uvEnabled, uvHover, uvDisabled, uvSize, centralTextureSize, GuiElementInst.EMPTY);

        GuiElementInst lbl = new GuiElementInst(new int[] {size[0] / 2, size[1] / 2}, new Label());
        lbl.alignment = new String[] {"center", "center"};
        this.put(LABEL, lbl);

        this.pageKey = pageId;
        this.order = TurretControlUnit.getPageOrder(pageId);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        this.pageStack = TcuScreen.getIcon(this.pageKey);
        this.setFunction(btn -> {
            PlayerEntity player = Minecraft.getInstance().player;
            ITurretEntity turret = gui instanceof TcuScreen ? ((TcuScreen) gui).getTurret() : null;
            if( player != null && turret != null ) {
                TurretControlUnit.openTcu(null, TurretControlUnit.getHeldTcu(player), turret, this.pageKey, false);
            }
        });
    }

    boolean isHovering() {
        return this.isCurrHovering;
    }

    public class Label
            extends Item
    {
        Label() {
            super(ItemStack.EMPTY, 1.0F);
        }

        @Override
        protected ItemStack getDynamicStack(IGui gui) {
            return ButtonNav.this.pageStack;
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void render(IGui gui, MatrixStack mStack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
            if( ButtonNav.this.pageStack == null ) {
                ButtonNav.this.pageStack = new ItemStack(Blocks.BARRIER, 1);
            }

            if( !ButtonNav.this.isActive() ) {
                super.render(gui, mStack, partTicks, x, y, mouseX, mouseY, e);
            } else if( !ButtonNav.this.isCurrHovering ) {
                SHADER_GRAYSCALE.render(() -> super.render(gui, mStack, partTicks, x, y, mouseX, mouseY, e), 1.0F);
            } else {
                super.render(gui, mStack, partTicks, x, y, mouseX, mouseY, e);
            }
        }
    }

    public static class Builder
            extends ButtonSL.Builder
    {
        public final ResourceLocation pageKey;

        public Builder(int[] size, ResourceLocation pageKey) {
            super(size);

            this.pageKey = pageKey;
        }

        @Override
        public void sanitize(IGui gui) {
            if( this.uvSize == null ) { this.uvSize = new int[] { 18, 18 }; }

            super.sanitize(gui);
        }

        @Override
        protected GuiElementInst loadLabel(IGui gui, JsonElement lblData) {
            return null;
        }

        @Override
        public ButtonNav get(IGui gui) {
            this.sanitize(gui);

            return new ButtonNav(this.texture, this.size, this.textureSize, this.uvEnabled, this.uvHover, this.uvDisabled, this.uvSize, this.centralTextureSize, this.pageKey);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, ResourceLocation pageKey) {
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 18, 18 });

            ButtonSL.Builder sb = ButtonSL.Builder.buildFromJson(gui, data);

            return IBuilder.copyValues(sb, new Builder(sb.size, pageKey));
        }

        public static ButtonNav fromJson(IGui gui, JsonObject data, ResourceLocation pageKey) {
            return buildFromJson(gui, data, pageKey).get(gui);
        }
    }
}
