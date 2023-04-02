/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.client.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiReference;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoValue;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface ITcuInfoProvider
        extends IGuiReference
{

    @Nonnull
    String getName();

    default void load(IGui gui, ITurretEntity turret, int w, int h, TcuInfoValue container) { }

    default void setup(IGui gui, ITurretEntity turret, int w, int h) { }

    default void tick(IGui gui, ITurretEntity turret) { }

    @SuppressWarnings("java:S107")
    default void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) { }

    @SuppressWarnings("java:S107")
    default void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) { }

    default void onClose(IGui gui, ITurretEntity turret) {
        this.onClose(gui);
    }

    default boolean isVisible(ITurretEntity turret) {
        return true;
    }

    default void loadJson(IGui gui, JsonObject data, int w, int h) { }
}
