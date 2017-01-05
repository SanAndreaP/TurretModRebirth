/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.compat.jei;

import com.google.common.collect.ImmutableList;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeEntry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AssemblyRecipeWrapper
        implements IRecipeWrapper
{
    private final List<List<ItemStack>> input;
    private final List<ItemStack> output;
    private final int fluxPerTick;
    private final int timeInTicks;

    public AssemblyRecipeWrapper(TurretAssemblyRegistry.RecipeKeyEntry keyEntry) {
        TurretAssemblyRegistry.RecipeEntry entry = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(keyEntry.id);
        assert entry != null : "Recipe Entry should not be null!";

        ImmutableList.Builder<List<ItemStack>> inputBuilder = ImmutableList.builder();
        for( IRecipeEntry item : entry.resources ) {
            inputBuilder.add(Arrays.asList(item.getEntryItemStacks()));
        }
        this.input = inputBuilder.build();
        this.fluxPerTick = entry.fluxPerTick;
        this.timeInTicks = entry.ticksProcessing;
        this.output = ImmutableList.of(keyEntry.stack);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, this.input);
        ingredients.setOutputs(ItemStack.class, this.output);
    }

    @Override
    @Deprecated
    public List getInputs() {
        return this.input;
    }

    @Override
    @Deprecated
    public List getOutputs() {
        return this.output;
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidInputs() {
        return ImmutableList.of();
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidOutputs() {
        return ImmutableList.of();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String s = Lang.translate(Lang.JEI_ASSEMBLY_ENERGY) + ' ' + this.fluxPerTick * this.timeInTicks + " RF";
        minecraft.fontRendererObj.drawString(s, 0, 90, 0xFF808080);
        s = Lang.translate(Lang.JEI_ASSEMBLY_TIME) + ' ' + MiscUtils.getTimeFromTicks(this.timeInTicks);
        minecraft.fontRendererObj.drawString(s, 0, 100, 0xFF808080);
    }

    @Override
    @Deprecated
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
