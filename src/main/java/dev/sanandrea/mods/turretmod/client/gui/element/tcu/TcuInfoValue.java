/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;

import javax.annotation.Nonnull;

public final class TcuInfoValue
        extends ElementParent<Integer>
{
    @Nonnull
    public final ITcuInfoProvider provider;

    private final int              w;
    private final int              h;
    private final ITurretEntity turret;

    TcuInfoValue(@Nonnull ITcuInfoProvider provider, IGui gui, ITurretEntity turret, int w, int h) {
        this.provider = provider;
        this.turret = turret;
        this.w = w;
        this.h = h;

        this.provider.load(gui, turret, w, h, this);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        this.provider.setup(gui, this.turret, this.w, this.h);
    }

    @Override
    public void tick(IGui gui, GuiElementInst e) {
        if( this.isVisible() ) {
            this.provider.tick(gui, this.turret);
        }

        super.tick(gui, e);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        if( this.isVisible() ) {
            super.render(gui, stack, partTicks, x, y, mouseX, mouseY, e);

            this.provider.renderContent(gui, this.turret, stack, partTicks, x, y, mouseX, mouseY, this.w, this.h);
        }
    }

    public void renderOutside(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY) {
        if( this.isVisible() ) {
            this.provider.renderOutside(gui, this.turret, stack, partTicks, x, y, mouseX, mouseY, this.w, this.h);
        }
    }

    @Override
    public void onClose(IGui gui) {
        super.onClose(gui);

        this.provider.onClose(gui, this.turret);
    }

    @Override
    public int getWidth() {
        return this.w;
    }

    @Override
    public int getHeight() {
        return this.h;
    }

    @Override
    public boolean isVisible() {
        return this.provider.isVisible(this.turret);
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) {
        return this.provider.mouseScrolled(gui, mouseX, mouseY, mouseScroll)
               || super.mouseScrolled(gui, mouseX, mouseY, mouseScroll);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.provider.mouseClicked(gui, mouseX, mouseY, button)
               || super.mouseClicked(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return this.provider.mouseReleased(gui, mouseX, mouseY, button)
               || super.mouseReleased(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.provider.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY)
               || super.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.provider.keyPressed(gui, keyCode, scanCode, modifiers)
               || super.keyPressed(gui, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.provider.keyReleased(gui, keyCode, scanCode, modifiers)
               || super.keyReleased(gui, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return this.provider.charTyped(gui, typedChar, keyCode)
               || super.charTyped(gui, typedChar, keyCode);
    }

    public static class Builder
            implements IBuilder<TcuInfoValue>
    {
        final ITurretEntity turret;
        final ITcuInfoProvider provider;
        final int              w;
        final int              h;

        public Builder(ITcuInfoProvider provider, ITurretEntity turret, int w, int h) {
            this.provider = provider;
            this.turret = turret;
            this.w = w;
            this.h = h;
        }

        @Override
        public void sanitize(IGui gui) { /* no-op */ }

        @Override
        public TcuInfoValue get(IGui gui) {
            this.sanitize(gui);

            return new TcuInfoValue(this.provider, gui, this.turret, this.w, this.h);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, @Nonnull ITcuInfoProvider provider, ITurretEntity turret, int w, int h) {
            JsonObject valData = JsonUtils.deepCopy(MiscUtils.get(data.getAsJsonObject(provider.getName()), () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new)));

            provider.loadJson(gui, valData, w, h);

            return new Builder(provider, turret, w, h);
        }
    }
}
