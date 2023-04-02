/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.client.tcu;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.IRegistry;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ILabelRegistry
        extends IRegistry<ILabelRenderer>
{
    FontRenderer getFontRenderer();

    int drawFont(String s, float x, float y, int color, MatrixStack matrixStack);

    int drawFont(ITextComponent t, float x, float y, int color, MatrixStack matrixStack);

    void quadPC(BufferBuilder buf, Matrix4f pose, Vector2f pos, Vector2f size, int color);

    void quadPT(BufferBuilder buf, Matrix4f pose, Vector2f pos, Vector2f size, Vector2f uv, Vector2f uvSize);

    void quadPCT(BufferBuilder buf, Matrix4f pose, Vector2f pos, Vector2f size, int color, Vector2f uv, Vector2f uvSize);
}
