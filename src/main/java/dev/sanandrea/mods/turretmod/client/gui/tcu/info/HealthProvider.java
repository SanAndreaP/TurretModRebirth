/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.tcu.info;

import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.init.Lang;

import javax.annotation.Nonnull;

public class HealthProvider
        extends ValueProvider
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
        return new int[] { 0, 151 };
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
        return 0xFFFC5050;
    }

    @Override
    protected int getDefaultLabelBorderColor() {
        return 0xFF400000;
    }

    @Override
    protected String getNumberFormat(double value) {
        return String.format("%.1f", value / 2.0D);
    }
}
