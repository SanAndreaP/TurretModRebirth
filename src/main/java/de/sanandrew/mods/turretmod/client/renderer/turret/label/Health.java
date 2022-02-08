package de.sanandrew.mods.turretmod.client.renderer.turret.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Health
        extends ValueBar
{
    public Health(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getSortOrder() {
        return 1;
    }

    @Override
    protected ITextComponent getLabelTxt() {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("health.text"));
    }

    @Override
    protected ITextComponent getValueTxt(ITurretEntity turret) {
        float hp = turret.get().getHealth();

        return new TranslationTextComponent(Lang.TCU_LABEL.get("health.value"), String.format("%.1f", hp / 2.0F));
    }

    @Override
    protected float getValue(ITurretEntity turret) {
        return turret.get().getHealth();
    }

    @Override
    protected float getMaxValue(ITurretEntity turret) {
        return turret.get().getMaxHealth();
    }

    @Override
    protected ColorObj getFgColor(float opacity) {
        return new ColorObj(1.0F, 0.2F, 0.2F, opacity);
    }

    @Override
    protected ColorObj getBgColor(float opacity) {
        return new ColorObj(0.3F, 0.0F, 0.0F, opacity);
    }
}
