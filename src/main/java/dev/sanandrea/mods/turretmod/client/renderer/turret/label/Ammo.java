/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.turret.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.init.Lang;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Ammo
        extends ValueBar
{
    public Ammo(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    protected ITextComponent getLabelTxt() {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("ammo.text"));
    }

    @Override
    protected ITextComponent getValueTxt(ITurretEntity turret) {
        int ammo = turret.getTargetProcessor().getAmmoCount();

        return new TranslationTextComponent(Lang.TCU_LABEL.get("ammo.value"), ammo);
    }

    @Override
    protected float getValue(ITurretEntity turret) {
        return turret.getTargetProcessor().getAmmoCount();
    }

    @Override
    protected float getMaxValue(ITurretEntity turret) {
        return turret.getTargetProcessor().getMaxAmmoCapacity();
    }

    @Override
    protected ColorObj getFgColor(float opacity) {
        return new ColorObj(0.5F, 0.5F, 1.0F, opacity);
    }

    @Override
    protected ColorObj getBgColor(float opacity) {
        return new ColorObj(0.0F, 0.0F, 0.3F, opacity);
    }
}
