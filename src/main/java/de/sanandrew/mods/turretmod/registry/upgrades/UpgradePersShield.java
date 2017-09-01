/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class UpgradePersShield
        implements ITurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/pers_shield");
    private final String name;

    public UpgradePersShield() {
        this.name = "pers_shield";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResourceLocation getModel() {
        return ITEM_MODEL;
    }

    @Override
    public UUID getRecipeId() {
        return null;
    }

    @Override
    public boolean isTurretApplicable(ITurret turret) {
        return true;
    }

    @Override
    public void onApply(ITurretInst turretInst) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        Shield shield = new Shield(20);
        turretInst.getUpgradeProcessor().setUpgradeInstance(upgId, shield);
        TmrUtils.INSTANCE.addForcefield(turretInst.getEntity(), shield);
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        Shield shield = new Shield(20);
        shield.value = nbt.getFloat("shieldValue");
        shield.recovery = nbt.getFloat("shieldRecovery");
        turretInst.getUpgradeProcessor().setUpgradeInstance(upgId, shield);
        TmrUtils.INSTANCE.addForcefield(turretInst.getEntity(), shield);
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        Shield shield = turretInst.getUpgradeProcessor().getUpgradeInstance(upgId);
        if( shield != null ) {
            nbt.setFloat("shieldValue", shield.value);
            nbt.setFloat("shieldRecovery", shield.recovery);
        }
    }

    @Override
    public void onRemove(ITurretInst turretInst) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        turretInst.getUpgradeProcessor().<Shield>getUpgradeInstance(upgId).value = 0;
        turretInst.getUpgradeProcessor().delUpgradeInstance(upgId);
    }

    @IUpgradeInstance.UpgInstTickable
    public static final class Shield
            implements IUpgradeInstance<Shield>, IForcefieldProvider
    {
        public static final float MAX_VALUE = 20.0F;

        private static final ColorObj BASE_COLOR = new ColorObj(0x40DD74FF);
        private static final float[] BASE_CLR_HSL = BASE_COLOR.calcHSL();
        private static final AxisAlignedBB BB = new AxisAlignedBB(-0.5D, 0, -0.5D, 0.5D, 2, 0.5D);
        private static final float CRIT_VALUE = 5.0F;
        private static final float RECOVERY_PER_TICK = CRIT_VALUE / 1000.0F;

        float value;
        float recovery;

        public Shield(float value) {
            this.value = Math.min(Math.max(value, 0.0F), MAX_VALUE);
            this.recovery = 0.0F;
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
            return this.value > 0.0F || this.isInRecovery();
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
        }

        @Override
        public void toBytes(ObjectOutputStream stream) throws IOException {
            stream.writeFloat(this.value);
        }

        @Override
        public void onTick(ITurretInst turretInst) {
            boolean hadRecovery = this.isInRecovery();

            if( this.value <= 0.0F ) {
                this.recovery += RECOVERY_PER_TICK;
                if( !hadRecovery ) {
                    TmrUtils.INSTANCE.addForcefield(turretInst.getEntity(), new ShieldRecovery(this));
                }
            }

            if( this.recovery >= CRIT_VALUE ) {
                this.value = MAX_VALUE;
                this.recovery = 0.0F;
                TmrUtils.INSTANCE.addForcefield(turretInst.getEntity(), this);
            }
        }
    }

    static final class ShieldRecovery
            implements IForcefieldProvider
    {
        private static final ColorObj CRIT_COLOR = new ColorObj(0x40FF0000);
        private static final float[] CRIT_CLR_HSL = CRIT_COLOR.calcHSL();
        private static final float[] HSL_DIF = new float[] {wrap360(Shield.BASE_CLR_HSL[0] - CRIT_CLR_HSL[0]), Shield.BASE_CLR_HSL[1] - CRIT_CLR_HSL[1],
                                                            Shield.BASE_CLR_HSL[2] - CRIT_CLR_HSL[2]};

        final Shield delegate;

        ShieldRecovery(Shield delegate) {
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
            float perc = this.delegate.recovery / Shield.CRIT_VALUE;
            if( perc < 0.9F ) {
                perc = perc / 0.9F;

                ColorObj newClr = new ColorObj(CRIT_COLOR);
                newClr.setAlpha(Math.round((CRIT_COLOR.fAlpha() + (Shield.BASE_COLOR.fAlpha() - CRIT_COLOR.fAlpha()) * perc) * 255.0F * perc));

                return newClr.getColorInt();
            } else {
                perc = (perc - 0.9F) * 10.0F;

                float[] hslDif = HSL_DIF.clone();
                hslDif[0] = wrap360(CRIT_CLR_HSL[0] + (hslDif[0] > 180.0F ? -(360.0F - hslDif[0]) : hslDif[0]) * perc);
                hslDif[1] = CRIT_CLR_HSL[1] + hslDif[1] * perc;
                hslDif[2] = CRIT_CLR_HSL[2] + hslDif[2] * perc;

                return ColorObj.fromHSLA(hslDif[0], hslDif[1], hslDif[2], CRIT_COLOR.fAlpha() + (Shield.BASE_COLOR.fAlpha() - CRIT_COLOR.fAlpha()) * perc).getColorInt();
            }
        }

        @Override
        public boolean hasSmoothFadeOut() {
            return false;
        }

        private static float wrap360(float angle) {
            return angle > 360.0F ? wrap360(angle - 360.0F) : angle < 0 ? wrap360(angle + 360.0F) : angle;
        }
    }
}
