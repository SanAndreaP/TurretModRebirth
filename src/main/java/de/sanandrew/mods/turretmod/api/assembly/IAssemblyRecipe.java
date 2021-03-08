package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

/**
 * <p>An object defining a turret assembly table recipe.</p>
 * <p>This is a custom declaration (pre 1.14) / extension (1.14) of {@link net.minecraft.item.crafting.IRecipe}</p>
 */
public interface IAssemblyRecipe
        extends IRecipe<IAssemblyInventory>
{
    /**
     * @return the amount of Redstone Flux (RF) used per tick (1/20th of a second) during crafting.
     */
    int getFluxPerTick();

    /**
     * @return the duration in ticks (1/20th of a second) the crafting process takes for this recipe.
     */
    int getProcessTime();
}
