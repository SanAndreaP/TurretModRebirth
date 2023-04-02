/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.api.assembly.ICountedIngredient;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
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

    @Override
    @SuppressWarnings("NullableProblems")
    public IVariable process(@Nonnull String s) {
        if( this.recipe != null ) {
            String langCode = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
            if( s.equals("output") ) {
                return IVariable.wrap(PatchouliHelper.getItemStr(this.recipe.getResultItem()));
            } else if( s.startsWith("input_") ) {
                try {
                    int                     i     = Integer.parseInt(s.substring("input_".length()));
                    NonNullList<ICountedIngredient> items = this.recipe.getCountedIngredients();

                    if( i < items.size() ) {
                        return IVariable.wrap(PatchouliHelper.getItemStr(items.get(i).getItems()));
                    }
                } catch( NumberFormatException ignored ) { /* NO-OP */ }
            } else if( s.equals("rf_cost") ) {
                return IVariable.wrap(MiscUtils.getNumberSiPrefixed(this.recipe.getEnergyConsumption() * (double) this.recipe.getProcessTime(), 1, langCode));
            } else if( s.equals("time_processing") ) {
                return IVariable.wrap(MiscUtils.getTimeFromTicks(this.recipe.getProcessTime()));
            } else if( s.equals("rf_cost_full") ) {
                return IVariable.wrap(MiscUtils.getNumberFormat(0, true, langCode).format((long) this.recipe.getEnergyConsumption() * this.recipe.getProcessTime()));
            } else if( s.equals("time_processing_full") ) {
                return IVariable.wrap(MiscUtils.getNumberFormat(0, true, langCode).format(this.recipe.getProcessTime()));
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
