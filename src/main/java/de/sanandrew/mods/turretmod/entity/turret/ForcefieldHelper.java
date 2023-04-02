/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.ColorObj;

public final class ForcefieldHelper
{
    private static final float RECOVERY_FADE_OUT = 0.01F;
    private static final float RECOVERY_FADE_MUL = 1.0F / (1.0F - RECOVERY_FADE_OUT);

    private ForcefieldHelper() { }

    public static int getShieldColor(final float value, final float recovery, float[] hsl, float alpha, final float criticalValue) {
        if( recovery > 0.0F ) {
            if( recovery >= 1.0F - RECOVERY_FADE_OUT ) {
                float recoveryFadeIn = (recovery - 1.0F + RECOVERY_FADE_OUT) / RECOVERY_FADE_OUT;

                alpha = 1.0F - (1.0F - alpha) * recoveryFadeIn;
                redshift(hsl, 1.0F - recoveryFadeIn);
            } else {
                alpha = (recovery < RECOVERY_FADE_OUT
                         ? 1.0F - (recovery / RECOVERY_FADE_OUT)
                         : Math.min((recovery - RECOVERY_FADE_OUT) * RECOVERY_FADE_MUL, 1.0F));
                hsl[0] = 0.0F;
                hsl[1] = 1.0F;
                hsl[2] = 0.5F;
            }
        } else if( value < criticalValue ) {
            float critPerc = 1.0F - value / criticalValue;
            alpha = Math.min(alpha + critPerc * (1.0F - alpha), 1.0F);

            redshift(hsl, critPerc);
        }

        return ColorObj.fromHSLA(hsl[0], hsl[1], hsl[2], alpha).getColorInt();
    }

    private static void redshift(float[] hsl, float shiftToRed) {
        if( shiftToRed >= 1.0F ) {
            hsl[0] = 0.0F;
            hsl[1] = 1.0F;
            hsl[2] = 0.5F;
        } else if( shiftToRed > 0.0F ) {
            // shift hue
            hsl[0] = hsl[0] <= 180.0F
                     ? hsl[0] - shiftToRed * hsl[0]
                     : hsl[0] + shiftToRed * (360.0F - hsl[0]);

            // shift saturation
            hsl[1] = hsl[1] + (1.0F - hsl[1]) * shiftToRed;

            // shift luminance
            hsl[2] = hsl[2] + (0.5F - hsl[2]) * shiftToRed;
        }
    }
}
