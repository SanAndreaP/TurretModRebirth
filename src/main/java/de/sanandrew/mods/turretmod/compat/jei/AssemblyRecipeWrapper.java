package de.sanandrew.mods.turretmod.compat.jei;

import com.google.common.collect.ImmutableList;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntryItem;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AssemblyRecipeWrapper
        implements IRecipeWrapper
{
    private final List<List<ItemStack>> input;
    private final List<ItemStack> output;
    private final int fluxPerTick;
    private final int timeInTicks;

    public AssemblyRecipeWrapper(TurretAssemblyRecipes.RecipeKeyEntry keyEntry) {
        TurretAssemblyRecipes.RecipeEntry entry = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(keyEntry.key());
        assert entry != null : "Recipe Entry should not be null!";

        ImmutableList.Builder<List<ItemStack>> inputBuilder = ImmutableList.builder();
        for( RecipeEntryItem item : entry.resources ) {
            inputBuilder.add(Arrays.asList(item.getEntryItemStacks()));
        }
        this.input = inputBuilder.build();
        this.fluxPerTick = entry.fluxPerTick;
        this.timeInTicks = entry.ticksProcessing;
        this.output = ImmutableList.of(keyEntry.stack());
    }

    @Override
    public void getIngredients(IIngredients ingredients) {

    }

    @Override
    public List getInputs() {
        return this.input;
    }

    @Override
    public List getOutputs() {
        return this.output;
    }

    @Override
    public List<FluidStack> getFluidInputs() {
        return ImmutableList.of();
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return ImmutableList.of();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRendererObj.drawString("RF/t used: " + this.fluxPerTick, 0, 0, 0xFF000000);
        minecraft.fontRendererObj.drawString("processing time: " + MiscUtils.getTimeFromTicks(this.timeInTicks), 0, 10, 0xFF000000);
    }

    @Override
    public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {

    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return ImmutableList.of();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
