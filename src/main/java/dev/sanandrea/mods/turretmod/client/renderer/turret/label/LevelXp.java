/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.turret.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.turret.IUpgradeProcessor;
import dev.sanandrea.mods.turretmod.init.Lang;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import dev.sanandrea.mods.turretmod.item.upgrades.delegate.leveling.LevelData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LevelXp
        extends ValueBar
{
    public LevelXp(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getSortOrder() {
        return 5;
    }

    @Override
    protected ITextComponent getLabelTxt() {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("level.text"));
    }

    @Override
    protected ITextComponent getValueTxt(ITurretEntity turret) {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("level.value"), MiscUtils.apply(this.getLevelStorage(turret), LevelData::getLevel, 0));
    }

    @Override
    protected float getValue(ITurretEntity turret) {
        return MiscUtils.apply(this.getLevelStorage(turret), ls -> ls.getXp() - ls.getCurrentLevelMinXp(), 0);
    }

    @Override
    protected float getMaxValue(ITurretEntity turret) {
        return MiscUtils.apply(this.getLevelStorage(turret), ls -> ls.getNextLevelMinXp() - ls.getCurrentLevelMinXp(), 1);
    }

    @Override
    protected ColorObj getFgColor(float opacity) {
        return new ColorObj(2.0F, 1.0F, 0.2F, opacity);
    }

    @Override
    protected ColorObj getBgColor(float opacity) {
        return new ColorObj(0.0F, 0.3F, 0.0F, opacity);
    }

    @Override
    public boolean isVisible(ITurretEntity turret) {
        return this.getLevelStorage(turret) != null;
    }

    private LevelData getLevelStorage(ITurretEntity turret) {
        IUpgradeProcessor up = turret.getUpgradeProcessor();
        if( up.hasUpgrade(Upgrades.LEVELING) ) {
            return up.getUpgradeData(Upgrades.LEVELING.getId());
        }

        return null;
    }
}
