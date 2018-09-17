/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret.shieldgen;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRAM;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public final class ShieldTurret
        implements ITurretRAM, IForcefieldProvider
{
    static final ColorObj BASE_COLOR = new ColorObj(0x40FFFFFF);
    static final ColorObj CRIT_COLOR = new ColorObj(0x40FF0000);
    static final float[] BASE_CLR_HSL = BASE_COLOR.calcHSL();
    static final float[] CRIT_CLR_HSL = CRIT_COLOR.calcHSL();
    static final float[] HSL_DIF = new float[] {
            TmrUtils.wrap360(BASE_CLR_HSL[0] - CRIT_CLR_HSL[0]),
            BASE_CLR_HSL[1] - CRIT_CLR_HSL[1],
            BASE_CLR_HSL[2] - CRIT_CLR_HSL[2]
    };

    static final float RECOVERY_PER_TICK = 0.0005F;
    static final float MAX_VALUE_RECOVERED = 100.0F;

    float value;
    float recovery;
    final ITurretInst turretInst;

    public ShieldTurret(ITurretInst turretInst) {
        this.turretInst = turretInst;
        this.value = 0.0F;
        this.recovery = 0.0F;
    }

    public void damage(float dmg) {
        if( dmg <= 0.0F ) {
            return;
        }

        if( this.value < dmg ) {
            this.value = 0.0F;

            return;
        }

        this.value -= dmg;
    }

    public float getMaxValue() {
        IUpgradeProcessor upgProc = this.turretInst.getUpgradeProcessor();
        return upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_II) ? 250.0F : upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_I) ? 150.0F : 100.0F;
    }

    public float getCritValue() {
        return this.getMaxValue() / 10.0F;
    }

    public float getValue() {
        return this.value;
    }

    public boolean isInRecovery() {
        return this.recovery > 0.0F && this.turretInst.isActive();
    }

    public float getRecovery() {
        return this.recovery;
    }

    @Override
    public boolean isShieldActive() {
        return this.value > 0.0F && this.turretInst.isActive();
    }

    @Override
    public AxisAlignedBB getShieldBoundingBox() {
        return turretInst.getRangeBB();
    }

    @Override
    public int getShieldColor() {
        float critVal = this.getCritValue();
        if( this.value < critVal ) {
            return getCritColor(this.value / critVal);
        } else {
            return BASE_COLOR.getColorInt();
        }
    }

    public void onTick() {
        float maxVal = this.getMaxValue();

        if( this.turretInst.isActive() && this.turretInst.getTargetProcessor().hasAmmo() && this.value < maxVal ) {
            double speedMulti = this.turretInst.get().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).getAttributeValue();
            if( this.value <= 0.0F ) {
                int prevRecoveryPerc = MathHelper.floor(this.recovery * 100.0F);
                this.recovery += RECOVERY_PER_TICK * (2.0F - speedMulti);
                if( prevRecoveryPerc != MathHelper.floor(this.recovery * 100.0F) ) {
                    this.turretInst.getTargetProcessor().decrAmmo();
                }
            } else if( this.turretInst.get().ticksExisted % Math.round(40 * speedMulti) == 0 ) {
                this.value += 1.0F;
                if( this.value > maxVal ) {
                    this.value = maxVal;
                }
                this.turretInst.getTargetProcessor().decrAmmo();
            }
        }

        if( this.recovery >= 1.0F ) {
            this.value = MAX_VALUE_RECOVERED;
            this.recovery = 0.0F;
        }

        if( this.value > maxVal ) {
            this.value = maxVal;
        }

        if( this.turretInst.get().world.isRemote ) {
            if( this.isInRecovery() && !TmrUtils.INSTANCE.hasForcefield(this.turretInst.get(), ShieldTurretRecovery.class) ) {
                TmrUtils.INSTANCE.addForcefield(this.turretInst.get(), new ShieldTurretRecovery(this));
            }

            if( this.isShieldActive() && !TmrUtils.INSTANCE.hasForcefield(this.turretInst.get(), this.getClass()) ) {
                TmrUtils.INSTANCE.addForcefield(turretInst.get(), this);
            }
        }
    }

    static int getCritColor(float relation) {
        float alpha = ShieldTurret.CRIT_COLOR.fAlpha() + (ShieldTurret.BASE_COLOR.fAlpha() - ShieldTurret.CRIT_COLOR.fAlpha()) * relation;
        float[] hslDif = ShieldTurret.HSL_DIF.clone();
        hslDif[0] = TmrUtils.wrap360(ShieldTurret.CRIT_CLR_HSL[0] + (hslDif[0] > 180.0F ? -(360.0F - hslDif[0]) : hslDif[0]) * relation);
        hslDif[1] = ShieldTurret.CRIT_CLR_HSL[1] + hslDif[1] * relation;
        hslDif[2] = ShieldTurret.CRIT_CLR_HSL[2] + hslDif[2] * relation;

        return ColorObj.fromHSLA(hslDif[0], hslDif[1], hslDif[2], alpha).getColorInt();
    }
}
