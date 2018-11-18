/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades.shield;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.util.math.AxisAlignedBB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@IUpgradeInstance.UpgInstTickable
public final class ShieldPersonal
        implements IUpgradeInstance<ShieldPersonal>, IForcefieldProvider
{
    public static final float MAX_VALUE = 20.0F;

    static final ColorObj BASE_COLOR = new ColorObj(0x40DD74FF);
    static final float[] BASE_CLR_HSL = BASE_COLOR.calcHSL();
    private static final AxisAlignedBB BB = new AxisAlignedBB(-0.5D, 0, -0.5D, 0.5D, 2, 0.5D);
    static final float CRIT_VALUE = 5.0F;
    private static final float RECOVERY_PER_TICK = CRIT_VALUE / 1000.0F;

    float value;
    float recovery;

    ShieldPersonal(float value, float recovery) {
        this.value = Math.min(Math.max(value, 0.0F), MAX_VALUE);
        this.recovery = recovery;
    }

    ShieldPersonal(float value) {
        this(value, 0.0F);
    }

    public float damage(float dmg) {
        if( dmg <= 0.0F ) {
            return 0.0F;
        }

        if( this.value < dmg ) {
            float rest = dmg - this.value;
            this.value = 0.0F;

            return rest;
        }

        this.value -= dmg;
        return 0.0F;
    }

    public float getValue() {
        return this.value;
    }

    public boolean isInRecovery() {
        return this.recovery > 0.0F;
    }

    public float getRecoveryPercentage() {
        return this.recovery / CRIT_VALUE * 100.0F;
    }

    @Override
    public boolean isShieldActive() {
        return this.value > 0.0F;
    }

    @Override
    public AxisAlignedBB getShieldBoundingBox() {
        return BB;
    }

    @Override
    public int getShieldColor() {
        if( this.value < CRIT_VALUE ) {
            float[] hsl = BASE_CLR_HSL.clone();
            if( hsl[0] <= 180.0F ) {
                hsl[0] = (this.value / CRIT_VALUE) * hsl[0];
            } else {
                hsl[0] = 360.0F - (this.value / CRIT_VALUE) * (360 - hsl[0]);
            }

            return ColorObj.fromHSLA(hsl[0], hsl[1], hsl[2], BASE_COLOR.fAlpha()).getColorInt();
        } else {
            return BASE_COLOR.getColorInt();
        }
    }

    @Override
    public void fromBytes(ObjectInputStream stream) throws IOException {
        this.value = stream.readFloat();
        this.recovery = stream.readFloat();
    }

    @Override
    public void toBytes(ObjectOutputStream stream) throws IOException {
        stream.writeFloat(this.value);
        stream.writeFloat(this.recovery);
    }

    @Override
    public void onTick(ITurretInst turretInst) {
        if( this.value <= 0.0F ) {
            this.recovery += RECOVERY_PER_TICK;
        }

        if( this.recovery >= CRIT_VALUE ) {
            this.value = MAX_VALUE;
            this.recovery = 0.0F;
        }

        if( turretInst.get().world.isRemote ) {
            if( this.isInRecovery() && !TmrUtils.INSTANCE.hasForcefield(turretInst.get(), ShieldPersonalRecovery.class) ) {
                TmrUtils.INSTANCE.addForcefield(turretInst.get(), new ShieldPersonalRecovery(this));
            }

            if( this.isShieldActive() && !TmrUtils.INSTANCE.hasForcefield(turretInst.get(), this.getClass()) ) {
                TmrUtils.INSTANCE.addForcefield(turretInst.get(), this);
            }
        }
    }

    @Override
    public boolean renderFull() {
        return true;
    }
}
