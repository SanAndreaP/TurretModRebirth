package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HealthProvider
        implements ITcuInfoProvider
{
    private float health;
    private float maxHealth;

    @Override
    public String getName() {
        return "health";
    }

    @Nullable
    @Override
    public ITextComponent getLabel() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.health.tooltip"));
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        MiscUtils.accept(turret.get(), e -> {
            this.health = e.getHealth();
            this.maxHealth = e.getMaxHealth();
        });
    }

    @Nullable
    @Override
    public ITextComponent getValueStr() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.health.suffix"), this.health / 2.0F)
                   .withStyle(Style.EMPTY.withColor(Color.fromRgb(0xFFA03030)));
    }

    @Override
    public float getCurrValue() {
        return this.health;
    }

    @Override
    public float getMaxValue() {
        return this.maxHealth;
    }

    @Nonnull
    @Override
    public ITexture buildIcon() {
        return ITexture.icon((mw, mh) -> new int[] { 86, 16 });
    }

    @Nullable
    @Override
    public ITexture buildProgressBar() {
        return ITexture.progressBar((mw, mh) -> new int[] { 0, 149 },
                                    (mw, mh) -> new int[] { 0, 146 });
    }
}
