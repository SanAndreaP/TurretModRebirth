package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;

import javax.annotation.Nonnull;

//TODO: render personal shield
public class HealthProvider
        extends IndicatorProvider
{
    @Nonnull
    @Override
    public String getName() {
        return "health";
    }

    protected void calcValues(ITurretEntity turret) {
        this.currValue = turret.get().getHealth();
        this.maxValue = turret.get().getMaxHealth();
    }

    @Override
    protected int[] getDefaultIconUV() {
        return new int[] { 88, 16 };
    }

    @Override
    protected int[] getDefaultIndicatorUV() {
        return new int[] { 0, 149 };
    }

    @Override
    protected String getDefaultTooltipText() {
        return Lang.TCU_TEXT.get("info.health.tooltip");
    }

    @Override
    protected String getDefaultLabelText() {
        return Lang.TCU_TEXT.get("info.health.value");
    }

    @Override
    protected int getDefaultLabelColor() {
        return 0xFFA03030;
    }

    @Override
    protected String getNumberFormat(double value) {
        return String.format("%.1f", value / 2.0D);
    }
}
