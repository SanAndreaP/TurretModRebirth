package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.api.assembly.ICountedIngredient;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class AssemblyComponentProcessor
        implements IComponentProcessor
{
    IAssemblyRecipe recipe;

    @Override
    public void setup(IVariableProvider provider) {
        this.recipe = AssemblyManager.INSTANCE.getRecipe(Minecraft.getInstance().level, new ResourceLocation(provider.get("recipe").asString()));
    }

    @Nonnull
    @Override
    public IVariable process(String s) {
        if( this.recipe != null ) {
            String langCode = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
            if( s.equals("output") ) {
                return IVariable.wrap(Ingredient.of(this.recipe.getResultItem()).toString());
            } else if( s.startsWith("input_") ) {
                try {
                    int                     i     = Integer.parseInt(s.substring("input_".length()));
                    NonNullList<ICountedIngredient> items = this.recipe.getCountedIngredients();

                    if( i < items.size() ) {
                        return IVariable.wrap(items.get(i).getIngredient().toJson());
//                        ICountedIngredient ingredient = items.get(i);
//
//                        final int cnt = ingredient.();
//                        ingredient = Ingredient.fromStacks(Arrays.stream(ingredient.getMatchingStacks())
//                                                                 .map(stack -> copyStackWithSize(stack, cnt))
//                                                                 .toArray(ItemStack[]::new));
//
//                        return ItemStackUtil.serializeIngredient(ingredient);
                    }
                } catch( NumberFormatException ignored ) {}
            } else if( s.equals("rf_cost") ) {
                return IVariable.wrap(MiscUtils.getNumberSiPrefixed(this.recipe.getEnergyConsumption() * this.recipe.getProcessTime(), 1, langCode));
            } else if( s.equals("time_processing") ) {
                return IVariable.wrap(MiscUtils.getTimeFromTicks(this.recipe.getProcessTime()));
            } else if( s.equals("rf_cost_full") ) {
                return IVariable.wrap(MiscUtils.getNumberFormat(0, true, langCode).format((long) this.recipe.getEnergyConsumption() * this.recipe.getProcessTime()));
            } else if( s.equals("time_processing_full") ) {
                return IVariable.wrap(MiscUtils.getNumberFormat(0, true, langCode).format(this.recipe.getProcessTime()));
            }
        }

        return IVariable.empty();
    }

    private static ItemStack copyStackWithSize(ItemStack stack, int size) {
        stack = stack.copy();
        stack.setCount(size);

        return stack;
    }
}
