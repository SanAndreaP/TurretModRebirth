/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.turret.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.init.Lang;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import dev.sanandrea.mods.turretmod.item.upgrades.delegate.shield.ShieldData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Function;

public class PersonalShield
        extends ValueBar
{
    public PersonalShield(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    protected ITextComponent getLabelTxt() {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("personal_shield.text"));
    }

    private static <T> T callShieldDataValue(ITurretEntity turret, Function<ShieldData, T> onRecovery, Function<ShieldData, T> onNormal, T defValue) {
        ShieldData sd = turret.getUpgradeProcessor().getUpgradeData(Upgrades.SHIELD_PERSONAL.getId());
        if( sd != null ) {
            if( sd.isInRecovery() ) {
                return onRecovery.apply(sd);
            } else {
                return onNormal.apply(sd);
            }
        }

        return defValue;
    }

    @Override
    protected ITextComponent getValueTxt(ITurretEntity turret) {
        return callShieldDataValue(turret,
                                   sd -> new TranslationTextComponent(Lang.TCU_LABEL.get("personal_shield.recovery"), String.format("%.0f", sd.getRecoveryValue() * 100.0F)),
                                   sd -> new TranslationTextComponent(Lang.TCU_LABEL.get("personal_shield.value"), String.format("%.1f", sd.getValue())),
                                   StringTextComponent.EMPTY);
    }

    @Override
    protected float getValue(ITurretEntity turret) {
        return callShieldDataValue(turret, ShieldData::getRecoveryValue, ShieldData::getValue, 0.0F);
    }

    @Override
    protected float getMaxValue(ITurretEntity turret) {
        return callShieldDataValue(turret, sd -> 1.0F, sd -> ShieldData.MAX_VALUE, 1.0F);
    }

    @Override
    protected ColorObj getFgColor(float opacity) {
        return new ColorObj(1.0F, 0.2F, 1.0F, opacity);
    }

    @Override
    protected ColorObj getBgColor(float opacity) {
        return new ColorObj(0.3F, 0.0F, 0.3F, opacity);
    }

    @Override
    public boolean isVisible(ITurretEntity turret) {
        return turret.getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_PERSONAL);
    }
}
