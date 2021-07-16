package de.sanandrew.mods.turretmod.client.gui.tcu.info;

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

public class AmmoProvider
        implements ITcuInfoProvider
{
    private int ammo;
    private int maxAmmo;

    @Override
    public String getName() {
        return "ammo";
    }

    @Nullable
    @Override
    public ITextComponent getLabel() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.health.tooltip"));
    }

    @Override
    public void tick(ITurretEntity turret) {
        MiscUtils.accept(turret.getTargetProcessor(), e -> {
            this.ammo = e.getAmmoCount();
            this.maxAmmo = e.getMaxAmmoCapacity();
        });
    }

    @Nullable
    @Override
    public ITextComponent getValueStr() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.ammo.suffix"), this.ammo)
                   .withStyle(Style.EMPTY.withColor(Color.fromRgb(0xFFA03030)));
    }

    @Override
    public float getCurrValue() {
        return this.ammo;
    }

    @Override
    public float getMaxValue() {
        return this.maxAmmo;
    }

    @Nonnull
    @Override
    public ITexture buildIcon() {
        return (mw, mh) -> new int[] { 86, 32 };
    }

    @Nullable
    @Override
    public ITexture buildProgressBar() {
        return ITexture.progressBar((mw, mh) -> new int[] { 0, 155},
                                    (mw, mh) -> new int[] {0, 152});
    }
}
