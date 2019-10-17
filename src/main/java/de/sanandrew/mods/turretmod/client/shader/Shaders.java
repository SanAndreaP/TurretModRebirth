/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.shader;

import de.sanandrew.mods.sanlib.lib.client.ShaderHelper;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("WeakerAccess")
public final class Shaders
{
    public static int categoryButton = 0;
    public static int grayscaleItem = 0;
    public static int alphaOverride = 0;

    public static void initShaders() {
        if( !ShaderHelper.areShadersEnabled() ) {
            return;
        }

        categoryButton = ShaderHelper.createProgram(null, Resources.SHADER_CATEGORY_BUTTON_FRAG.resource);
        grayscaleItem = ShaderHelper.createProgram(null, Resources.SHADER_GRAYSCALE_FRAG.resource);
        alphaOverride = ShaderHelper.createProgram(null, Resources.SHADER_ALPHA_OVERRIDE_FRAG.resource);
    }
}
