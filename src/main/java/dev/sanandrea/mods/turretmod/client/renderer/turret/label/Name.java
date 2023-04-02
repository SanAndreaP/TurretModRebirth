/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.turret.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class Name
        extends Text
{
    public Name(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getSortOrder() {
        return -1;
    }

    @Override
    protected ITextComponent getTextLeft(ITurretEntity turret) {
        return turret.get().getDisplayName();
    }

    @Override
    protected int getColorLeft(float opacity) {
        return new ColorObj(1.0F, 1.0F, 1.0F, opacity).getColorInt();
    }
}
