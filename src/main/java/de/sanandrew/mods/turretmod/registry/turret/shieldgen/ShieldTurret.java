/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret.shieldgen;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.*;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.ShieldColorizer;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public final class ShieldTurret
        implements ITurretRAM, IForcefieldProvider
{
    static final ColorObj CRIT_COLOR = new ColorObj(0x40FF0000);
    private static final float[] CRIT_CLR_HSL = CRIT_COLOR.calcHSL();

    float value;
    float recovery;
    final ITurretInst turretInst;
    private float prevValue;
    private float attackTime;
    private ColorObj baseColor = new ColorObj(0x40FFFFFF);
    private boolean cullFaces = false;
    private float[] baseColorHsl = this.baseColor.calcHSL();
    private float[] hslDiff = getHslDiff();

    ShieldTurret(ITurretInst turretInst) {
        this.turretInst = turretInst;
        this.prevValue = 0.0F;
        this.value = 0.0F;
        this.attackTime = 0.0F;
        this.recovery = 0.0F;
    }

    public void recalcBaseColor() {
        ShieldColorizer colorizer = this.turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.SHIELD_COLORIZER.getId());
        if( colorizer != null ) {
            this.baseColor = new ColorObj(colorizer.getColor());
            this.cullFaces = colorizer.doCullFaces();
        } else {
            this.baseColor = new ColorObj(0x40FFFFFF);
            this.cullFaces = false;
        }

        this.baseColorHsl = this.baseColor.calcHSL();
        this.hslDiff = getHslDiff();
    }

    private float[] getHslDiff() {
        return new float[] {
                TmrUtils.wrap360(baseColorHsl[0] - CRIT_CLR_HSL[0]),
                baseColorHsl[1] - CRIT_CLR_HSL[1],
                baseColorHsl[2] - CRIT_CLR_HSL[2]
        };
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
        return upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_II)
               ? TurretForcefield.shieldValueThird
               : (upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_I) ? TurretForcefield.shieldValueSecond : TurretForcefield.shieldValueFirst);
    }

    private float getCritValue() {
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
            return this.getCritColor(this.value / critVal);
        } else {
            return (MathHelper.floor(Math.max(this.baseColor.alpha(), 0x40 * this.attackTime)) << 24) | (this.baseColor.getColorInt() & 0xFFFFFF);
        }
    }

    @Override
    public boolean cullShieldFaces() {
        return this.cullFaces;
    }

    void onTick() {
        if( this.attackTime > 0.0F ) {
            this.attackTime -= 0.1F;
        }
        if( this.prevValue > this.value ) {
            this.attackTime = 1.0F;
        }
        this.prevValue = this.value;
        float maxVal = this.getMaxValue();

        if( this.turretInst.isActive() && this.turretInst.getTargetProcessor().hasAmmo() && this.value < maxVal ) {
            double speedMulti = this.turretInst.get().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).getAttributeValue();
            if( this.value <= 0.0F ) {
                int prevRecoveryPerc = MathHelper.floor(this.recovery * 100.0F);
                this.recovery += TurretForcefield.shieldRecoveryPerTick * (2.0F - speedMulti);
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
            this.value = TurretForcefield.maxShieldRecoveryValue;
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

    int getCritColor(float relation) {
        float alpha = CRIT_COLOR.fAlpha() + (this.baseColor.fAlpha() - CRIT_COLOR.fAlpha()) * relation;
        float[] hslDif = this.hslDiff.clone();
        hslDif[0] = TmrUtils.wrap360(ShieldTurret.CRIT_CLR_HSL[0] + (hslDif[0] > 180.0F ? -(360.0F - hslDif[0]) : hslDif[0]) * relation);
        hslDif[1] = ShieldTurret.CRIT_CLR_HSL[1] + hslDif[1] * relation;
        hslDif[2] = ShieldTurret.CRIT_CLR_HSL[2] + hslDif[2] * relation;

        return ColorObj.fromHSLA(hslDif[0], hslDif[1], hslDif[2], alpha).getColorInt();
    }
}
