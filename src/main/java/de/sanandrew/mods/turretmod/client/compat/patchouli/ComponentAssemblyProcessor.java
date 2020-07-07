package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.Arrays;
import java.util.stream.Stream;

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
            String langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
            if( s.equals("output") ) {
                return ItemStackUtil.serializeStack(this.recipe.getRecipeOutput());
            } else if( s.startsWith("input_") ) {
                try {
                    int                     i     = Integer.parseInt(s.substring("input_".length()));
                    NonNullList<Ingredient> items = this.recipe.getIngredients();

                    if( i < items.size() ) {
                        Ingredient ingredient = items.get(i);
                        
                        if( ingredient instanceof AssemblyIngredient ) {
                            final int cnt = ((AssemblyIngredient) ingredient).getCount();
                            ingredient = Ingredient.fromStacks(Arrays.stream(ingredient.getMatchingStacks())
                                                                     .map(stack -> copyStackWithSize(stack, cnt))
                                                                     .toArray(ItemStack[]::new));
                        }

                        return ItemStackUtil.serializeIngredient(ingredient);
                    }
                } catch( NumberFormatException ignored ) {}
            } else if( s.equals("rf_cost") ) {
                return TmrUtils.getNumberSiPrefixed(this.recipe.getFluxPerTick() * this.recipe.getProcessTime(), 1, langCode);
            } else if( s.equals("time_processing") ) {
                return MiscUtils.getTimeFromTicks(this.recipe.getProcessTime());
            } else if( s.equals("rf_cost_full") ) {
                return TmrUtils.getNumberFormat(0, true, langCode).format((long) this.recipe.getFluxPerTick() * this.recipe.getProcessTime());
            } else if( s.equals("time_processing_full") ) {
                return TmrUtils.getNumberFormat(0, true, langCode).format(this.recipe.getProcessTime());
            }
        }

        return null;
    }

    private static ItemStack copyStackWithSize(ItemStack stack, int size) {
        stack = stack.copy();
        stack.setCount(size);

        return stack;
    }
}
