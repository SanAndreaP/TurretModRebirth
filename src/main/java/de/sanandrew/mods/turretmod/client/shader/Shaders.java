/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.shader;

import de.sanandrew.mods.sanlib.lib.client.ShaderHelper;
import de.sanandrew.mods.turretmod.api.Resources;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("WeakerAccess")
public final class Shaders
{
    public static int grayscaleItem = 0;
    public static int alphaOverride = 0;

    public static void initShaders() {
        if( !ShaderHelper.areShadersEnabled() ) {
            return;
        }

        grayscaleItem = ShaderHelper.createProgram(null, Resources.SHADER_GRAYSCALE_FRAG);
        alphaOverride = ShaderHelper.createProgram(null, Resources.SHADER_ALPHA_OVERRIDE_FRAG);
    }
}
