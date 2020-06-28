package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ComponentAssemblyProcessor
        implements IComponentProcessor
{
    IAssemblyRecipe recipe;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.recipe = AssemblyManager.INSTANCE.getRecipe(new ResourceLocation(provider.get("recipe")));
    }

    @Override
    public String process(String s) {
        if( this.recipe != null ) {
            if( s.equals("output") ) {
                return ItemStackUtil.serializeStack(this.recipe.getRecipeOutput());
            } else if( s.startsWith("input_") ) {
                try {
                    int                     i     = Integer.parseInt(s.substring("input_".length()));
                    NonNullList<Ingredient> items = this.recipe.getIngredients();

                    if( i < items.size() ) {
                        return ItemStackUtil.serializeIngredient(items.get(i));
                    }
                } catch( NumberFormatException ignored ) {}
            }
        }

        return null;
    }
}
