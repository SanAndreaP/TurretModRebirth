/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret.shieldgen;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import net.minecraft.util.math.AxisAlignedBB;

public final class ShieldTurretRecovery
        implements IForcefieldProvider
{
    private final ShieldTurret delegate;

    ShieldTurretRecovery(ShieldTurret delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isShieldActive() {
        return this.delegate.isInRecovery() && this.delegate.turretInst.isActive();
    }

    @Override
    public AxisAlignedBB getShieldBoundingBox() {
        return this.delegate.getShieldBoundingBox();
    }

    @Override
    public int getShieldColor() {
        float perc = this.delegate.recovery;
        if( perc < 0.9F ) {
            perc = perc / 0.9F;

            ColorObj newClr = new ColorObj(ShieldTurret.CRIT_COLOR);
            newClr.setAlpha(Math.round(ShieldTurret.CRIT_COLOR.fAlpha() * 255.0F * perc));

            return newClr.getColorInt();
        } else {
            return ShieldTurret.getCritColor((perc - 0.9F) * 10.0F);
        }
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
