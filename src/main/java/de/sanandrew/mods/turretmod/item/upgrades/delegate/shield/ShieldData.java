/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item.upgrades.delegate.shield;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.IForcefield;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeData;
import de.sanandrew.mods.turretmod.entity.turret.ForcefieldHelper;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

@IUpgradeData.Syncable
public final class ShieldData
    implements IUpgradeData<ShieldData>, IForcefield
{
    public static final float MAX_VALUE = 20.0F;

    private static final ColorObj      BASE_COLOR        = new ColorObj(0x40DD74FF);
    private static final float[]       BASE_CLR_HSL      = BASE_COLOR.calcHSL();
    private static final AxisAlignedBB BB                = new AxisAlignedBB(-0.5D, 0, -0.5D, 0.5D, 2, 0.5D);
    private static final float         CRIT_VALUE        = 5.0F;
    private static final float         RECOVERY_PER_TICK = 0.001F;

    private static final String NBT_VALUE = "Value";
    private static final String NBT_RECOVERY = "Recovery";

    float value;
    float recovery;

    ShieldData(float value, float recovery) {
        this.value = Math.min(Math.max(value, 0.0F), MAX_VALUE);
        this.recovery = recovery;
    }

    ShieldData(float value) {
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

    public float getRecoveryValue() {
        return this.recovery;
    }

    @Override
    public boolean isShieldActive() {
        return true;
    }

    @Override
    public AxisAlignedBB getShieldBoundingBox() {
        return BB;
    }

    @Override
    public int getShieldColor() {
        return ForcefieldHelper.getShieldColor(this.value, this.recovery, BASE_CLR_HSL.clone(), BASE_COLOR.fAlpha(), CRIT_VALUE);
    }

    @Override
    public boolean cullShieldFaces() {
        return true;
    }

    @Override
    public void load(ITurretEntity turretInst, @Nonnull CompoundNBT nbt) {
        this.value = nbt.getFloat(NBT_VALUE);
        this.recovery = nbt.getFloat(NBT_RECOVERY);
    }

    @Override
    public void save(ITurretEntity turretInst, @Nonnull CompoundNBT nbt) {
        nbt.putFloat(NBT_VALUE, this.value);
        nbt.putFloat(NBT_RECOVERY, this.recovery);
    }

    @Override
    public void onTick(ITurretEntity turretInst) {
        if( !turretInst.hasClientForcefield(ShieldData.class) ) {
            turretInst.addClientForcefield(this);
        }

        if( this.value <= 0.0F ) {
            this.recovery += RECOVERY_PER_TICK;
        }

        if( this.recovery >= 1.0F ) {
            this.value = MAX_VALUE;
            this.recovery = 0.0F;

            turretInst.getUpgradeProcessor().syncUpgrade(Upgrades.SHIELD_PERSONAL.getId());
        }
    }

    @Override
    public boolean renderFull() {
        return true;
    }
}
