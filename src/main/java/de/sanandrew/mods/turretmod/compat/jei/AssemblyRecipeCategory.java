package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class AssemblyRecipeCategory<T extends IRecipeWrapper>
        implements IRecipeCategory<T>
{
    static final String UID = TurretModRebirth.ID + ".assemblytbl";
    private static final String TITLE = "TM Assembly Table";
    private final IDrawableStatic background;
    private final IDrawableStatic icon = null;

    public AssemblyRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation resLoc = new ResourceLocation(TurretModRebirth.ID, "/textures/gui/nei_blank.png");
        this.background = guiHelper.createBlankDrawable(166, 65);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Override
    public void drawAnimations(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper, IIngredients ingredients) {
        if( !(recipeWrapper instanceof AssemblyRecipeWrapper) ) {
            return;
        }

        AssemblyRecipeWrapper assemblyWrapper = (AssemblyRecipeWrapper) recipeWrapper;

        List inputs = assemblyWrapper.getInputs();

        int inputSize = inputs.size();

        if( inputSize > 0 ) {
            recipeLayout.getItemStacks().init(0, true, 39, 41);

            Object fstInput = inputs.get(0);
            if (fstInput instanceof ItemStack) {
                recipeLayout.getItemStacks().set(0, (ItemStack) fstInput);
            } else if (fstInput instanceof List) {
                recipeLayout.getItemStacks().set(0, ReflectionUtils.<List<ItemStack>>getCasted(fstInput));
            }

            int index = 1;
            int posX = 60;
            for (int i = 1; i < inputSize; i++) {
                Object o = inputs.get(i);
                recipeLayout.getItemStacks().init(index, true, posX, 6);
                if (o instanceof ItemStack) {
                    recipeLayout.getItemStacks().set(index, (ItemStack) o);
                } else if (o instanceof List) {
                    recipeLayout.getItemStacks().set(index, ReflectionUtils.<List<ItemStack>>getCasted(o));
                }
                index++;
                posX += 18;
            }
        }

        recipeLayout.getItemStacks().init(inputSize, false, 87, 41);
        recipeLayout.getItemStacks().set(inputSize, ReflectionUtils.<List<ItemStack>>getCasted(assemblyWrapper.getOutputs()));
    }
}
