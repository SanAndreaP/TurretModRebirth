package de.sanandrew.mods.turretmod.client.renderer.turret.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.LevelStorage;
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
        return 4;
    }

    @Override
    protected ITextComponent getLabelTxt() {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("level.text"));
    }

    @Override
    protected ITextComponent getValueTxt(ITurretEntity turret) {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("level.value"), MiscUtils.apply(this.getLevelStorage(turret), ls -> ls.getLevel(), 0));
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

    private LevelStorage getLevelStorage(ITurretEntity turret) {
        IUpgradeProcessor up = turret.getUpgradeProcessor();
        if( up.hasUpgrade(Upgrades.LEVELING) ) {
            return up.getUpgradeData(Upgrades.LEVELING.getId());
        }

        return null;
    }
}
