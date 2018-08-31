/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.assembly;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

public class RecipeKeyEntry
{
    public final UUID id;
    @Nonnull
    public final ItemStack stack;

    public RecipeKeyEntry(UUID id, @Nonnull ItemStack stack) {
        this.id = id;
        this.stack = stack;
    }
}
