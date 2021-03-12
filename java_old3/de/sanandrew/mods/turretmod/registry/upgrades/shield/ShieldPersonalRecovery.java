/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades.shield;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import net.minecraft.util.math.AxisAlignedBB;

public final class ShieldPersonalRecovery
        implements IForcefieldProvider
{
    private static final ColorObj CRIT_COLOR = new ColorObj(0x40FF0000);
    private static final float[] CRIT_CLR_HSL = CRIT_COLOR.calcHSL();
    private static final float[] HSL_DIF = new float[] {
            MiscUtils.wrap360(ShieldPersonal.BASE_CLR_HSL[0] - CRIT_CLR_HSL[0]),
            ShieldPersonal.BASE_CLR_HSL[1] - CRIT_CLR_HSL[1],
            ShieldPersonal.BASE_CLR_HSL[2] - CRIT_CLR_HSL[2]
    };

    private final ShieldPersonal delegate;

    ShieldPersonalRecovery(ShieldPersonal delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isShieldActive() {
        return this.delegate.isInRecovery();
    }

    @Override
    public AxisAlignedBB getShieldBoundingBox() {
        return this.delegate.getShieldBoundingBox();
    }

    @Override
    public int getShieldColor() {
        float perc = this.delegate.recovery / ShieldPersonal.CRIT_VALUE;
        if( perc < 0.9F ) {
            perc = perc / 0.9F;

            ColorObj newClr = new ColorObj(CRIT_COLOR);
            newClr.setAlpha(Math.round((CRIT_COLOR.fAlpha() + (ShieldPersonal.BASE_COLOR.fAlpha() - CRIT_COLOR.fAlpha()) * perc) * 255.0F * perc));

            return newClr.getColorInt();
        } else {
            perc = (perc - 0.9F) * 10.0F;

            float[] hslDif = HSL_DIF.clone();
            hslDif[0] = MiscUtils.wrap360(CRIT_CLR_HSL[0] + (hslDif[0] > 180.0F ? -(360.0F - hslDif[0]) : hslDif[0]) * perc);
            hslDif[1] = CRIT_CLR_HSL[1] + hslDif[1] * perc;
            hslDif[2] = CRIT_CLR_HSL[2] + hslDif[2] * perc;

            return ColorObj.fromHSLA(hslDif[0], hslDif[1], hslDif[2], CRIT_COLOR.fAlpha() + (ShieldPersonal.BASE_COLOR.fAlpha() - CRIT_COLOR.fAlpha()) * perc).getColorInt();
        }
    }

    @Override
    public boolean cullShieldFaces() {
        return true;
    }

    @Override
    public boolean hasSmoothFadeOut() {
        return false;
    }

    @Override
    public boolean renderFull() {
        return true;
    }
}
