/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.turret.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Target
        extends Text
{
    private static final TranslationTextComponent LABEL = new TranslationTextComponent(Lang.TCU_LABEL.get("target"));

    public Target(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    @Override
    protected ITextComponent getTextLeft(ITurretEntity turret) {
        return LABEL;
    }

    @Override
    protected ITextComponent getTextRight(ITurretEntity turret) {
        return turret.getTargetProcessor().getTargetName();
    }

    @Override
    protected int getColorLeft(float opacity) {
        return new ColorObj(1.0F, 1.0F, 1.0F, opacity).getColorInt();
    }

    @Override
    protected int getColorRight(float opacity) {
        return new ColorObj(0.7F, 0.7F, 0.7F, opacity).getColorInt();
    }
}
