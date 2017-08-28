/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class UpgradePrsShield
        implements ITurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/ender_medium");
    private final String name;

    public UpgradePrsShield() {
        this.name = "prs_shield";
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
    public boolean isTurretApplicable(Class<? extends EntityTurret> turretCls) {
        return true;
    }

    @Override
    public void onApply(EntityTurret turret) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        Shield shield = new Shield(20);
        turret.getUpgradeProcessor().setUpgradeInstance(upgId, shield);
        EntityTurret.utils.addForcefield(turret, shield);
    }

    @Override
    public void onLoad(EntityTurret turret, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        Shield shield = new Shield(20);
        shield.value = nbt.getFloat("shieldValue");
        shield.recovery = nbt.getFloat("shieldRecovery");
        turret.getUpgradeProcessor().setUpgradeInstance(upgId, shield);
        EntityTurret.utils.addForcefield(turret, shield);
    }

    @Override
    public void onSave(EntityTurret turret, NBTTagCompound nbt) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        Shield shield = turret.getUpgradeProcessor().getUpgradeInstance(upgId);
        if( shield != null ) {
            nbt.setFloat("shieldValue", shield.value);
            nbt.setFloat("shieldRecovery", shield.recovery);
        }
    }

    @Override
    public void onRemove(EntityTurret turret) {
        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeId(this);
        turret.getUpgradeProcessor().<Shield>getUpgradeInstance(upgId).value = 0;
        turret.getUpgradeProcessor().delUpgradeInstance(upgId);
    }

    @IUpgradeInstance.UpgInstTickable
    public static final class Shield
            implements IUpgradeInstance<Shield>, IForcefieldProvider
    {
        private static final ColorObj BASE_COLOR = new ColorObj(0x8080FFA0);
        private static final AxisAlignedBB BB = new AxisAlignedBB(-0.5D, 0, -0.5D, 0.5D, 2, 0.5D);
        private static final float MAX_VALUE = 20.0F;
        private static final float CRIT_VALUE = 5.0F;
        private static final float RECOVERY_PER_TICK = CRIT_VALUE / 100.0F;

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

        @Override
        public boolean isShieldActive() {
            return this.value > 0.0F || this.recovery > 0.0F;
        }

        @Override
        public AxisAlignedBB getShieldBoundingBox() {
            return BB;
        }

        @Override
        public int getShieldColor() {
            if( this.value < CRIT_VALUE ) {
                float[] hsl = BASE_COLOR.calcHSL();
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
        public void onTick(EntityTurret turret) {
            boolean hadRecovery = this.recovery > 0.0F;

            if( this.value <= 0.0F ) {
                this.recovery += RECOVERY_PER_TICK;
                if( !hadRecovery ) {
                    EntityTurret.utils.addForcefield(turret, new ShieldRecovery(this));
                }
            }

            if( this.recovery >= CRIT_VALUE ) {
                this.value = MAX_VALUE;
                this.recovery = 0.0F;
                EntityTurret.utils.addForcefield(turret, this);
            }
        }
    }

    static final class ShieldRecovery
            implements IForcefieldProvider
    {
        final Shield delegate;

        ShieldRecovery(Shield delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isShieldActive() {
            return this.delegate.recovery > 0.0F;
        }

        @Override
        public AxisAlignedBB getShieldBoundingBox() {
            return this.delegate.getShieldBoundingBox();
        }

        @Override
        public int getShieldColor() {
            ColorObj newClr = new ColorObj(Shield.BASE_COLOR);
            newClr.setAlpha(Math.round(this.delegate.recovery / Shield.CRIT_VALUE * newClr.alpha()));

            return newClr.getColorInt();
        }

        @Override
        public boolean hasSmoothFadeOut() {
            return false;
        }
    }
}
