package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HealthProvider
        implements ITcuInfoProvider
{
    private float health;
    private float maxHealth;

    @Nullable
    @Override
    public ITextComponent getLabel() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.health.tooltip"));
    }

    @Override
    public void tick(ITurretEntity turret) {
        MiscUtils.accept(turret.get(), e -> {
            this.health = e.getHealth();
            this.maxHealth = e.getMaxHealth();
        });
    }

    @Nullable
    @Override
    public ITextComponent getValueStr() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.health.suffix"), this.health / 2.0F);
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
        return new ITexture() {
            @Override
            public int[] getSize(int maxWidth, int maxHeight) {
                return DEFAULT_ICON_SIZE;
            }

            @Override
            public int[] getUV(int maxWidth, int maxHeight) {
                return new int[] { 240, 0 };
            }
        };
    }

    @Nullable
    @Override
    public ITexture getProgressBar() {
        return null;
    }
}
